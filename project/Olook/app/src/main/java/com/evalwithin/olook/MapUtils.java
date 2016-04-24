package com.evalwithin.olook;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapUtils {
    public enum IconIndex {USER(0), EVENT(1), PARKING(2), PARKMETER(3), WIFI(4), INTEREST(5), RESTAURANT(6), BUS(7); int value; IconIndex(int val){value = val;}}

    static final int[] iconRessources = { R.drawable.user, R.drawable.event, R.drawable.parking, R.drawable.parking_payant,
            R.drawable.wifi, R.drawable.park, R.drawable.restaurant, R.drawable.bus };

    static final BitmapDescriptor[] iconImages = new BitmapDescriptor[iconRessources.length];

    static final HashMap<String, IconIndex> datasetIconIndexes = new HashMap<String, IconIndex>();

    //static final int ICON_MY_LOCATION = R.drawable.user;
    //static final int ICON_INTEREST_AREA = R.drawable.wifi;
    //static BitmapDescriptor mIconMyLocation, mIconInterestArea;
    static Marker marker;


    static void init(Resources res) {
        /*Bitmap iconMyLocation = BitmapFactory.decodeResource(res, ICON_MY_LOCATION);
        mIconMyLocation = BitmapDescriptorFactory.fromBitmap(iconMyLocation);

        Bitmap iconInterestArea = BitmapFactory.decodeResource(res, ICON_INTEREST_AREA);
        mIconInterestArea = BitmapDescriptorFactory.fromBitmap(iconInterestArea);*/

        for(int i = 0; i < iconRessources.length; ++i)
            iconImages[i] = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(res, iconRessources[i]));

        datasetIconIndexes.put(res.getString(R.string.filter_name_attrait), IconIndex.INTEREST);
        datasetIconIndexes.put(res.getString(R.string.filter_name_parking), IconIndex.PARKING);
        datasetIconIndexes.put(res.getString(R.string.filter_name_zap), IconIndex.WIFI);
        datasetIconIndexes.put(res.getString(R.string.filter_name_parcometre), IconIndex.PARKMETER);
    }

    static void setMyLocation(GoogleMap map, LatLng coord) {
        if(marker != null)
            marker.remove();
        
        MarkerOptions markerOptions = new MarkerOptions().position(coord).title("");
        markerOptions.icon(iconImages[IconIndex.USER.value]);
        marker = map.addMarker(markerOptions);
    }

    static void addInterestPoint(GoogleMap map, LatLng coord, IconIndex icon, String description) {
        MarkerOptions marker = new MarkerOptions().position(coord).title(description);
        marker.icon(iconImages[icon.value]);
        map.addMarker(marker);
    }

    static IconIndex getIconIndex(String dataset){
        IconIndex idx = datasetIconIndexes.get(dataset);
        return idx;
    }
}
