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

/**
 * Created by lagac on 4/23/2016.
 */
public class GPSTracker extends Service implements LocationListener {

    private Context context;

    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;

    private Location location = null;
    protected LocationManager locationManager;

    public GPSTracker(Context context){
        this.context = context;
        initGPS();
    }

    private void initGPS(){
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(networkEnabled && context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60, 10, this);

            if(locationManager != null)
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(gpsEnabled && context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60, 10, this);

            //if(locationManager != null)
            //    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Location getLocation(){
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
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
