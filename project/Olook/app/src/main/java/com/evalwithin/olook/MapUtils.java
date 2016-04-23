package com.evalwithin.olook;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapUtils {
    static final int ICON_MY_LOCATION = R.mipmap.ic_launcher;
    static final int ICON_INTEREST_AREA = R.drawable.ic_setting_light;
    static BitmapDescriptor mIconMyLocation, mIconInterestArea;

    static void init(Resources res) {
        Bitmap iconMyLocation = BitmapFactory.decodeResource(res, ICON_MY_LOCATION);
        mIconMyLocation = BitmapDescriptorFactory.fromBitmap(iconMyLocation);

        Bitmap iconInterestArea = BitmapFactory.decodeResource(res, ICON_INTEREST_AREA);
        mIconInterestArea = BitmapDescriptorFactory.fromBitmap(iconInterestArea);
    }

    static void setMyLocation(GoogleMap map, LatLng coord) {
        MarkerOptions marker = new MarkerOptions().position(coord).title("My location");
        marker.icon(MapUtils.mIconMyLocation);
        map.addMarker(marker);
    }

    static void addInterestPoint(GoogleMap map, LatLng coord, String description) {
        MarkerOptions marker = new MarkerOptions().position(coord).title(description);
        marker.icon(MapUtils.mIconInterestArea);
        map.addMarker(marker);
    }
}
