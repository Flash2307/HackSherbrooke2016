package com.evalwithin.olook.Data;


import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.evalwithin.olook.FilterItems;
import com.evalwithin.olook.OLookApp;
import com.evalwithin.olook.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pascal on 23/04/2016.
 */

public class DataManager extends Thread
{
    private static DataManager instance = null;

    private final long WAIT_TIME = 30000; //7200000;

    private String attraitName;
    private String parkingName;
    private String zapName;

    //ArrayList<ArrayList<Location>> locationsList;
    Map<String, ArrayList<Location>> locationMap;

    /*
     * Arraylist ids - 0 : Attraits
     *                 1 : Parkings
     *                 2 : Zap
     */

    protected DataManager()
    {
        Context context = OLookApp.getAppContext();

        attraitName = context.getResources().getString(R.string.filter_name_attrait);
        parkingName = context.getResources().getString(R.string.filter_name_parking);
        zapName = context.getResources().getString(R.string.filter_name_zap);

        locationMap = new HashMap<>();
        locationMap.put(attraitName, new ArrayList<Location>());
        locationMap.put(parkingName, new ArrayList<Location>());
        locationMap.put(zapName, new ArrayList<Location>());
    }

    public static DataManager getInstance()
    {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    public ArrayList<Location> getLocationList(String listName)
    {
        return locationMap.get(listName);
    }

    @Override
    public void run()
    {
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
        Context context = OLookApp.getAppContext();

        if (locationMap.get(attraitName).isEmpty())
        {
            ArrayList<Location> fileAttrait = readFile(Attrait.ATTRAIT_FILENAME);
            if (fileAttrait != null)
            {
                locationMap.put(attraitName, fileAttrait);
            }
        }

        if (locationMap.get(parkingName).isEmpty())
        {
            ArrayList<Location> fileParking = readFile(Parking.PARKING_FILENAME);
            if (fileParking != null)
            {
                locationMap.put(parkingName, fileParking);
            }
        }

        if (locationMap.get(zapName).isEmpty())
        {
            ArrayList<Location> fileZap = readFile(Zap.ZAP_FILENAME);
            if (fileZap != null)
            {
                locationMap.put(zapName, fileZap);
            }
        }

        while(true)
        {
            try
            {
                Thread.sleep(WAIT_TIME);
            }
            catch (InterruptedException e)
            {
            }

            ArrayList<Location> listAttrait = updateAttrait();
            ArrayList<Location> listParking = updateParking();
            ArrayList<Location> listZap = updateZAP();

            locationMap.put(attraitName, listAttrait);
            locationMap.put(parkingName, listParking);
            locationMap.put(zapName, listZap);
        }
    }

    public Map<String, ArrayList<Location>> getLocationValues(double locX, double locY, double radius)
    {
        return locationMap;
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

    private ArrayList<Location> readFile(String fileName)
    {
        Context context = OLookApp.getAppContext();

        File file = new File(context.getFilesDir() + File.separator + fileName);
        if (!file.exists())
        {
            return null;
        }

        ArrayList<Location> newList;
        try
        {
            InputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            newList = (ArrayList<Location>) ois.readObject();
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

    private void writeFile(ArrayList<Location> listToWrite, String fileName)
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

    private ArrayList<Location> updateAttrait()
    {
        String jsonData = getDataString(Attrait.URL_ATTRAIT);
        ArrayList<Location> attraitData = Attrait.parseJSON(jsonData);

        writeFile(attraitData, Attrait.ATTRAIT_FILENAME);

        return attraitData;
    }

    private ArrayList<Location> updateParking()
    {
        String jsonData = getDataString(Parking.URL_PARKING);
        ArrayList<Location> parkingData = Parking.parseJSON(jsonData);

        writeFile(parkingData, Parking.PARKING_FILENAME);

        return parkingData;
    }

    private ArrayList<Location> updateZAP()
    {
        String csvData = getDataString(Zap.URL_ZAP);
        ArrayList<Location> zapData = Zap.parseCSV(csvData);

        writeFile(zapData, Zap.ZAP_FILENAME);

        return zapData;
    }
}


