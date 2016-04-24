package com.evalwithin.olook.Data;


import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.evalwithin.olook.FilterItems;
import com.evalwithin.olook.MapUtils;
import com.evalwithin.olook.OLookApp;
import com.evalwithin.olook.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pascal on 23/04/2016.
 */

public class DataManager extends Thread
{
    private static DataManager instance = null;

    private final long WAIT_TIME = 300000; //7200000;

    private String attraitName;
    private String parkingName;
    private String zapName;
    private String parcometreName;
    private String[] filterNames;

    private boolean dataFetched;

    Map<String, ArrayList<AreaOfInterest>> areaOfInterestMap;

    protected DataManager()
    {
        Context context = OLookApp.getAppContext();

        filterNames = context.getResources().getStringArray(R.array.filter_array);
        areaOfInterestMap = new HashMap<>();

        for(String filterName : filterNames)
        {
            areaOfInterestMap.put(filterName, new ArrayList<AreaOfInterest>());
        }

        dataFetched = false;
    }

    public static DataManager getInstance()
    {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    public ArrayList<AreaOfInterest> getLocationList(String listName)
    {
        return areaOfInterestMap.get(listName);
    }

    public ArrayList<String> getMapKeys()
    {
        ArrayList<String> keys = new ArrayList<>();
        for(String key : areaOfInterestMap.keySet())
        {
            keys.add(key);
        }
        return keys;
    }

    @Override
    public void run()
    {
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
        readFromFile();
        dataFetched = true;

        while(true)
        {
            try
            {
                Thread.sleep(WAIT_TIME);
            }
            catch (InterruptedException e)
            {
            }

            ArrayList<AreaOfInterest> listAttrait = updateAttrait();
            ArrayList<AreaOfInterest> listParking = updateParking();
            ArrayList<AreaOfInterest> listZap = updateZAP();
            ArrayList<AreaOfInterest> listParcometre = updateParcometre();

            areaOfInterestMap.put(attraitName, listAttrait);
            areaOfInterestMap.put(parkingName, listParking);
            areaOfInterestMap.put(zapName, listZap);
            areaOfInterestMap.put(parcometreName, listParcometre);
        }
    }

    private void readFromFile()
    {
        for(int i = 0; i < filterNames.length; i++)
        {
            String filterName = filterNames[i];
            String fileName = "";
            String url = "";
            String className = "";

            switch(i)
            {
                case 0:
                    fileName = Attrait.ATTRAIT_FILENAME;
                    url = Attrait.URL_ATTRAIT;
                    className = Attrait.class.toString();
                    break;
                case 1:
                    fileName = Parking.PARKING_FILENAME;
                    url = Parking.URL_PARKING;
                    className = Parking.class.toString();
                    break;
                case 2:
                    fileName = Zap.ZAP_FILENAME;
                    url = Zap.URL_ZAP;
                    className = Zap.class.toString();
                    break;
                case 3:
                    fileName = Parcometre.PARCOMETRE_FILENAME;
                    url = Parcometre.URL_PARCOMETRE;
                    className = Parcometre.class.toString();
                    break;
                default:
                    assert(false);
            }

            if (areaOfInterestMap.get(filterName).isEmpty())
            {


                ArrayList<AreaOfInterest> file = readFile(fileName);
                if (file != null)
                {
                    areaOfInterestMap.put(filterName, file);
                }
                else
                {

                    ArrayList<AreaOfInterest> list = updateData(url, fileName, className);
                    areaOfInterestMap.put(filterName, list);
                }
            }
        }

        /*if (areaOfInterestMap.get(attraitName).isEmpty())
        {
            ArrayList<AreaOfInterest> fileAttrait = readFile(Attrait.ATTRAIT_FILENAME);
            if (fileAttrait != null)
            {
                areaOfInterestMap.put(attraitName, fileAttrait);
            }
            else
            {
                ArrayList<AreaOfInterest> listAttrait = updateAttrait();
                areaOfInterestMap.put(attraitName, listAttrait);
            }
        }

        if (areaOfInterestMap.get(parkingName).isEmpty())
        {
            ArrayList<AreaOfInterest> fileParking = readFile(Parking.PARKING_FILENAME);
            if (fileParking != null)
            {
                areaOfInterestMap.put(parkingName, fileParking);
            }
            else
            {
                ArrayList<AreaOfInterest> listParking = updateParking();
                areaOfInterestMap.put(parkingName, listParking);
            }
        }

        if (areaOfInterestMap.get(zapName).isEmpty())
        {
            ArrayList<AreaOfInterest> fileZap = readFile(Zap.ZAP_FILENAME);
            if (fileZap != null)
            {
                areaOfInterestMap.put(zapName, fileZap);
            }
            else
            {
                ArrayList<AreaOfInterest> listZap = updateZAP();
                areaOfInterestMap.put(zapName, listZap);
            }
        }

        if (areaOfInterestMap.get(parcometreName).isEmpty())
        {
            ArrayList<AreaOfInterest>  fileParcometre = readFile(Parcometre.PARCOMETRE_FILENAME);
            if (fileParcometre != null)
            {
                areaOfInterestMap.put(parcometreName, fileParcometre);
            }
            else
            {
                ArrayList<AreaOfInterest> listParcometre = updateParcometre();
                areaOfInterestMap.put(parcometreName, listParcometre);
            }
        }*/
    }

    public Map<String, ArrayList<AreaOfInterest>> getAreaOfInterestValues(double locX, double locY, double radius)
    {
        while (!dataFetched)
        {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e){}
        }

        Map<String, ArrayList<AreaOfInterest>> sortedLocationMap = new HashMap<>();

        for(String key : areaOfInterestMap.keySet())
        {
            ArrayList<AreaOfInterest> locations = areaOfInterestMap.get(key);
            ArrayList<AreaOfInterest> sortedLocations = new ArrayList<>();

            for(AreaOfInterest location : locations)
            {
                if(radius >= MapUtils.getDistance(location.getLocX(), locX, location.getLocY(), locY))
                {
                    sortedLocations.add(location);
                }
            }

            if(sortedLocations.size() > 0)
            {
                sortedLocationMap.put(key, sortedLocations);
            }
        }

        return sortedLocationMap;
    }

    private String getDataString(String url)
    {
        DataFetcher fetcher = new DataFetcher();
        fetcher.execute(url);
        while (!fetcher.isFinished())
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
            }
        }
        return fetcher.getDataString();
    }

    private ArrayList<AreaOfInterest> readFile(String fileName)
    {
        Context context = OLookApp.getAppContext();

        File file = new File(context.getFilesDir() + File.separator + fileName);
        if (!file.exists())
        {
            return null;
        }

        ArrayList<AreaOfInterest> newList;
        try
        {
            InputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            newList = (ArrayList<AreaOfInterest>) ois.readObject();
            ois.close();
            fis.close();
            return newList;
        }
        catch (FileNotFoundException e)
        {
            Log.e("FileNotFoundException", e.toString());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.e("IOException", e.toString());
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            Log.e("ClassNotFoundException", e.toString());
            e.printStackTrace();
        }
        return null;
    }

    private void writeFile(ArrayList<AreaOfInterest> listToWrite, String fileName)
    {
        Context context = OLookApp.getAppContext();
        try
        {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(listToWrite);
            fos.close();
            oos.close();
        }
        catch (IOException e)
        {
            Log.e("IOException", e.toString());
            e.printStackTrace();
        }
    }

    private ArrayList<AreaOfInterest> updateData(String filename, String url, String className)
    {
        String stringData = getDataString(url);

        try
        {
            Class clazz = Class.forName(className);
            Method method = clazz.getMethod("parseString", String.class);
            ArrayList<AreaOfInterest> data = (ArrayList<AreaOfInterest>) method.invoke(null, stringData);

            writeFile(data, filename);

            return data;
        } catch(Exception e)
        {

        }

        return null;
    }

    private ArrayList<AreaOfInterest> updateAttrait()
    {
        String jsonData = getDataString(Attrait.URL_ATTRAIT);
        ArrayList<AreaOfInterest> attraitData = Attrait.parseJSON(jsonData);

        writeFile(attraitData, Attrait.ATTRAIT_FILENAME);

        return attraitData;
    }

    private ArrayList<AreaOfInterest> updateParking()
    {
        String jsonData = getDataString(Parking.URL_PARKING);
        ArrayList<AreaOfInterest> parkingData = Parking.parseJSON(jsonData);

        writeFile(parkingData, Parking.PARKING_FILENAME);

        return parkingData;
    }

    private ArrayList<AreaOfInterest> updateZAP()
    {
        String csvData = getDataString(Zap.URL_ZAP);
        ArrayList<AreaOfInterest> zapData = Zap.parseCSV(csvData);

        writeFile(zapData, Zap.ZAP_FILENAME);

        return zapData;
    }

    private ArrayList<AreaOfInterest> updateParcometre()
    {
        String jsonData = getDataString(Parcometre.URL_PARCOMETRE);
        ArrayList<AreaOfInterest> parcometreData = Parcometre.parseJSON(jsonData);

        writeFile(parcometreData, Parcometre.PARCOMETRE_FILENAME);

        return  parcometreData;
    }
}


