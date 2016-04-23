package com.evalwithin.olook.Data;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.evalwithin.olook.OLookApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Pascal on 23/04/2016.
 */
public class DataManager extends AppCompatActivity implements Runnable
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

    public void run()
    {
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);

        while(true)
        {
            //TODO: Verify if file exists --> Create/Update if not existing
            ArrayList<Location> fileZap = readFile(Zap.ZAP_FILENAME);
            if (fileZap != null)
            {

            }


            //TODO: Verify last modified date of file --> Update if too old
            ArrayList<Location> listZap = updateZAP();

            //TODO: Update to ram
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
            FileOutputStream fos = context.openFileOutput(fileName, MODE_PRIVATE);
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

    private ArrayList<Location> updateZAP()
    {
        String csvData = getDataString(Zap.URL_ZAP);
        ArrayList<Location> zapData = Zap.parseCSV(csvData);

        writeFile(zapData, Zap.ZAP_FILENAME);

        return zapData;
    }
}
