package com.evalwithin.olook;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Pascal on 23/04/2016.
 */
public class JSONFetcher extends AsyncTask<String, Void, String>
{
    private String jsonString;

    private ArrayList<Parking> parkingList;

    public JSONFetcher()
    {
        jsonString = "";
        parkingList = new ArrayList<>();
    }

    protected String doInBackground(String... params)
    {
        String urlStr = params[0];
        String usedClass = params[1];

        HttpURLConnection c = null;
        try
        {
            URL url = new URL(urlStr);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(20000);
            c.setReadTimeout(20000);
            c.connect();

            int status = c.getResponseCode();

            if (status == 200 || status == 201)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream(), "utf-8"), 8);
                StringBuilder strBuilder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null)
                {
                    strBuilder.append(line + "\n");
                }
                reader.close();
                jsonString = strBuilder.toString();
            }
        }
        catch (MalformedURLException e)
        {
            Log.e("MalformedURLException", e.toString());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.e("IOException", e.toString());
            e.printStackTrace();
        }
        finally
        {
            if (c != null)
            {
                try
                {
                    c.disconnect();
                }
                catch (Exception e)
                {
                    Log.e("Could not disconnect", e.toString());
                    e.printStackTrace();
                }
            }
        }

        return usedClass;
    }

    protected void onPostExecute(String s)
    {

        try
        {
            JSONArray jArray = new JSONArray(jsonString);

            if (s.equals("Parking"))
            {
                
            }
        }
        catch (JSONException e)
        {
            Log.e("JSONException", e.toString());
        }
    }
}
