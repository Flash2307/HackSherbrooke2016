package com.evalwithin.olook;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by lagac on 4/23/2016.
 */
public class GPSTracker extends Service implements LocationListener {

    private Context context;

    private final static int MIN_DIST_BEFORE_UPDATE = 10;
    private final static int TIME_BETWEEN_UPDATES = 60;

    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;

    private Location location = null;
    protected LocationManager locationManager;

    ArrayList<GPSListener> listeners = new ArrayList<GPSListener>();

    public GPSTracker(Context context){
        this.context = context;

        //Hardcoded location for emulators (will be override if Network or GPS is present)
        location = new Location("HARDCODED !");
        location.setLongitude(45.410538);
        location.setLatitude(-71.887056);

        initGPS();
    }

    private void initGPS(){
        try {
            Location tmp = null;

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(networkEnabled && context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_BETWEEN_UPDATES, MIN_DIST_BEFORE_UPDATE, this);

            if(locationManager != null) {
                tmp = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if(tmp != null)
                    location = tmp;
            }
            if(gpsEnabled && context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BETWEEN_UPDATES, MIN_DIST_BEFORE_UPDATE, this);

            if(locationManager != null) {
                tmp = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(tmp != null)
                    location = tmp;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Location getLocation(){
        return location;
    }

    public void addListener(GPSListener listener){
        listeners.add(listener);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null)
            return;

        this.location = location;

        for (int i = 0; i < listeners.size(); ++i)
            listeners.get(i).onGPSLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
