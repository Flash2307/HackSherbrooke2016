package com.evalwithin.olook.Data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pascal on 23/04/2016.
 */
public class Attrait extends AreaOfInterest
{
    public final static String ATTRAIT_FILENAME = "AttraitData.dat";
    public final static String URL_ATTRAIT = "http://api.destinationsherbrooke.com/json/attraits?SecureKey=SmSE4gaX77120Cj6wlSGwE2t1Sk1om9z2H1X1teBx3rpmddancW51frlB91YNuF9";

    private String siteWeb;
    private String phoneNumber;

    public Attrait(double locX, double locY, String locName, String siteWeb, String phoneNumber)
    {
        super(locX, locY, locName, "<a href='" + siteWeb + "'>" + siteWeb + "</a><br /><b><a href='tel:" + phoneNumber.toString() + "'>" + phoneNumber.toString() + "</a></b>");
        this.siteWeb = siteWeb;
        this.phoneNumber = phoneNumber;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public static ArrayList<AreaOfInterest> parseString(String dataString)
    {
        ArrayList<AreaOfInterest> listAttrait = new ArrayList<>();
        try
        {
            JSONObject jObj = new JSONObject(dataString);
            JSONArray jArray = jObj.getJSONArray("response");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject obj = jArray.getJSONObject(i);

                double locX = obj.optDouble("Longitude", 0);
                double locY = obj.optDouble("Latitude", 0);
                String name = obj.getString("Nom");
                String siteWeb = obj.getString("SiteWeb");
                String phoneNbr = obj.getString("NumeroTelephone");

                listAttrait.add(new Attrait(locX, locY, name, siteWeb, phoneNbr));
            }
            return listAttrait;
        }
        catch (JSONException e)
        {
            Log.e("Attrait JSONException", e.toString());
        }

        return null;
    }
}
