package com.evalwithin.olook.Data;


import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.evalwithin.olook.OLookApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Pascal on 23/04/2016.
 */

public class DataManager extends Thread
{
    private static DataManager instance = null;

    public final int INDEX_ATTRAIT = 0;
    public final int INDEX_PARKING = 1;
    public final int INDEX_ZAP = 2;

    ArrayList<ArrayList<Location>> locationsList;

    /*
     * Arraylist ids - 0 : Attraits
     *                 1 : Parkings
     *                 2 : Zap
     */

    protected DataManager()
    {
        locationsList = new ArrayList<>();
        locationsList.add(new ArrayList<Location>());
        locationsList.add(new ArrayList<Location>());
        locationsList.add(new ArrayList<Location>());
    }

    public static DataManager getInstance()
    {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    public ArrayList<Location> getLocationList(int listIndex)
    {
        return locationsList.get(listIndex);
    }

    @Override
    public void run()
    {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while(true)
        {
            //TODO: Verify if file exists --> Create/Update if not existing
            if (locationsList.get(INDEX_ATTRAIT).isEmpty())
            {
                ArrayList<Location> fileAttrait = readFile(Attrait.ATTRAIT_FILENAME);
                if (fileAttrait != null)
                {
                    locationsList.set(INDEX_ATTRAIT, fileAttrait);
                }
            }

            if (locationsList.get(INDEX_PARKING).isEmpty())
            {
                ArrayList<Location> fileParking = readFile(Parking.PARKING_FILENAME);
                if (fileParking != null)
                {
                    locationsList.set(INDEX_PARKING, fileParking);
                }
            }

            if (locationsList.get(INDEX_ZAP).isEmpty())
            {
                ArrayList<Location> fileZap = readFile(Zap.ZAP_FILENAME);
                if (fileZap != null)
                {
                    locationsList.set(INDEX_ZAP, fileZap);
                }
            }


            //TODO: Verify last modified date of file --> Update if too old
            ArrayList<Location> listAttrait = updateAttrait();
            ArrayList<Location> listParking = updateParking();
            ArrayList<Location> listZap = updateZAP();

            //TODO: Update to ram
            locationsList.set(INDEX_ATTRAIT, listAttrait);
            locationsList.set(INDEX_PARKING, listParking);
            locationsList.set(INDEX_ZAP, listZap);

            try
            {
                Thread.sleep(30000);
            }
            catch (InterruptedException e)
            {
            }
        }
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


