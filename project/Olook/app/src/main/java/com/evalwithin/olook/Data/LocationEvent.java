package com.evalwithin.olook.Data;

import android.location.Geocoder;
import android.util.Log;

import com.evalwithin.olook.OLookApp;
import android.location.Address;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pascal on 24/04/2016.
 */
public class LocationEvent extends AreaOfInterest
{
    public final static String EVENT_FILENAME = "EventData.dat";
    public final static String URL_EVENT = "http://api.destinationsherbrooke.com/json/evenements?SecureKey=SmSE4gaX77120Cj6wlSGwE2t1Sk1om9z2H1X1teBx3rpmddancW51frlB91YNuF9";

    private String siteWeb;
    private String phoneNumber;

    public LocationEvent(double locX, double locY, String locName, String lieu, String siteWeb, String phoneNumber)
    {
        super(0, 0, locName);

        this.phoneNumber = phoneNumber;
        this.siteWeb = siteWeb;

        String address = "";
        if (lieu.toLowerCase().contains("|"))
        {
            address = lieu.substring(lieu.indexOf('|')+1).trim();
        }
        else if (!lieu.isEmpty())
        {
            address = lieu.substring(lieu.indexOf('(')+1, lieu.length()-1).trim();
        }

        if (!address.isEmpty()) {
            if (!address.toLowerCase().contains("sherbrooke")) {
                address += ", Sherbrooke";
            }

            Geocoder geocoder = new Geocoder(OLookApp.getAppContext());
            try {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (!addresses.isEmpty()) {
                    Address addr = addresses.get(0);
                    this.locX = addr.getLongitude();
                    this.locY = addr.getLatitude();
                }
            } catch (IOException e) {
            }
        }
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
                String lieu = obj.getString("Lieu");
                String siteWeb = obj.optString("SiteWeb", "");
                String phoneNbr = obj.optString("NumeroTelephone", "");

                listAttrait.add(new LocationEvent(locX, locY, name, lieu, siteWeb, phoneNbr));
            }
            return listAttrait;
        }
        catch (JSONException e)
        {
            Log.e("Event JSONException", e.toString());
        }

        return null;
    }
}
