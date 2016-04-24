package com.evalwithin.olook.Data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pascal on 23/04/2016.
 */
public class Parking extends Location
{
    public final static String PARKING_FILENAME = "ParkingData.dat";
    public final static String URL_PARKING = "http://donnees.ville.sherbrooke.qc.ca/storage/f/2015-04-15T12%3A24%3A53.719Z/stationnementpublic.json";

    public Parking(double locX, double locY, String parkingName)
    {
        super(locX, locY, parkingName);
    }

    public static ArrayList<Location> parseJSON(String jsonString) {
        ArrayList<Location> parkingList = new ArrayList<>();
        try
        {
            JSONObject jObj = new JSONObject(jsonString);
            JSONArray jArray = jObj.getJSONArray("features");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject obj = jArray.getJSONObject(i);
                JSONObject properties = obj.getJSONObject("properties");

                double locX = properties.optDouble("x", 0);
                double locY = properties.optDouble("y", 0);
                String name = properties.getString("NOM");

                parkingList.add(new Parking(locX, locY, name));
            }
            return parkingList;
        }
        catch (JSONException e)
        {
            Log.e("Parking JSONException", e.toString());
        }
        return null;
    }
}
