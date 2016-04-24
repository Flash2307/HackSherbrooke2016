package com.evalwithin.olook.Data;


import android.content.Context;
import android.os.Process;
import android.util.Log;

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

    private final long WAIT_TIME = 300000; //7200000;

    private String attraitName;
    private String parkingName;
    private String zapName;

    Map<String, ArrayList<AreaOfInterest>> areaOfInterestMap;

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

        areaOfInterestMap = new HashMap<>();
        areaOfInterestMap.put(attraitName, new ArrayList<AreaOfInterest>());
        areaOfInterestMap.put(parkingName, new ArrayList<AreaOfInterest>());
        areaOfInterestMap.put(zapName, new ArrayList<AreaOfInterest>());
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

    @Override
    public void run()
    {
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
        Context context = OLookApp.getAppContext();

        if (areaOfInterestMap.get(attraitName).isEmpty())
        {
            ArrayList<AreaOfInterest> fileAttrait = readFile(Attrait.ATTRAIT_FILENAME);
            if (fileAttrait != null)
            {
                areaOfInterestMap.put(attraitName, fileAttrait);
            }
        }

        if (areaOfInterestMap.get(parkingName).isEmpty())
        {
            ArrayList<AreaOfInterest> fileParking = readFile(Parking.PARKING_FILENAME);
            if (fileParking != null)
            {
                areaOfInterestMap.put(parkingName, fileParking);
            }
        }

        if (areaOfInterestMap.get(zapName).isEmpty())
        {
            ArrayList<AreaOfInterest> fileZap = readFile(Zap.ZAP_FILENAME);
            if (fileZap != null)
            {
                areaOfInterestMap.put(zapName, fileZap);
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

            ArrayList<AreaOfInterest> listAttrait = updateAttrait();
            ArrayList<AreaOfInterest> listParking = updateParking();
            ArrayList<AreaOfInterest> listZap = updateZAP();

            areaOfInterestMap.put(attraitName, listAttrait);
            areaOfInterestMap.put(parkingName, listParking);
            areaOfInterestMap.put(zapName, listZap);
        }
    }

    public Map<String, ArrayList<AreaOfInterest>> getAreaOfInterestValues(double locX, double locY, double radius)
    {
        return areaOfInterestMap;
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
}


