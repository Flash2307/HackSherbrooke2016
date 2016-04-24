package com.evalwithin.olook.Data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pascal on 23/04/2016.
 */
public class Parcometre extends AreaOfInterest
{
    public final static String PARCOMETRE_FILENAME = "ParcometreData.dat";
    public final static String URL_PARCOMETRE = " http://donnees.ville.sherbrooke.qc.ca/storage/f/2015-04-15T12%3A18%3A31.523Z/horodateur.json";

    public Parcometre(double locX, double locY, String name)
    {
        super(locX, locY, name, "Horodateur.");
    }

    public static ArrayList<AreaOfInterest> parseString(String dataString)
    {
        ArrayList<AreaOfInterest> parkingList = new ArrayList<>();
        try
        {
            JSONObject jObj = new JSONObject(dataString);
            JSONArray jArray = jObj.getJSONArray("features");
            for (int i = 0; i < jArray.length(); i++)
            {
                JSONObject obj = jArray.getJSONObject(i);
                JSONObject properties = obj.getJSONObject("properties");

                double locX = properties.optDouble("x", 0);
                double locY = properties.optDouble("y", 0);
                String name = properties.optString("TYPE_resolved", "");

                parkingList.add(new Parcometre(locX, locY, name));
            }
            return parkingList;
        }
        catch (JSONException e)
        {
            Log.e("Parkmeter JSONException", e.toString());
        }
        return null;
    }
}
