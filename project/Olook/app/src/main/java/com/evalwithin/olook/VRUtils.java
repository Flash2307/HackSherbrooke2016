package com.evalwithin.olook;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;

import com.evalwithin.olook.Data.AreaOfInterest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Frederik on 4/30/2016.
 */
public class VRUtils {
    private static final double AZIMUT_DETECTION_DELTA = 0.3;

    public static ArrayList<AreaOfInterest> searchNearestDirection(Location myLocation, double azimut, Map<String, ArrayList<AreaOfInterest>> localData) {
        // search nearest at direction
        ArrayList<AreaOfInterest> potentialAreas = new ArrayList<>();
        final Location myLoc = myLocation;

        Set<String> keys = localData.keySet();
        for (String key : keys) {
            for (AreaOfInterest area : localData.get(key)) {
                if (VRUtils.isAreaInAzimut(area, myLoc, azimut)) {
                    potentialAreas.add(area);
                }
            }
        }

        if (potentialAreas.size() > 0) {
            Collections.sort(potentialAreas, new Comparator<AreaOfInterest>() {
                @Override
                public int compare(AreaOfInterest area1, AreaOfInterest area2) {
                    Location locArea1 = new Location("Area 1");
                    locArea1.setLongitude(area1.getLocX());
                    locArea1.setLatitude(area1.getLocY());

                    Location locArea2 = new Location("Area 2");
                    locArea2.setLongitude(area2.getLocX());
                    locArea2.setLatitude(area2.getLocY());

                    return myLoc.distanceTo(locArea1) < myLoc.distanceTo(locArea2) ? -1 : 1;
                }
            });

            ArrayList<AreaOfInterest> areas = new ArrayList<>();
            int nAreas = 3;

            int n = nAreas;
            if (potentialAreas.size() < nAreas) {
                n = potentialAreas.size();
            }
            for (int i = 0; i < n; i++) {
                areas.add(potentialAreas.get(i));
            }

            return areas;
        }

        return new ArrayList<>();



        /*
        if (potentialAreas.size() > 0) {
            Location firstArea = new Location("First");
            firstArea.setLongitude(potentialAreas.get(0).getLocX());
            firstArea.setLatitude(potentialAreas.get(0).getLocY());
            double minDistance = myLoc.distanceTo(firstArea);
            AreaOfInterest minDistanceArea = potentialAreas.get(0);

            for (AreaOfInterest area : potentialAreas) {
                Location loc = new Location("Next loc");
                loc.setLongitude(area.getLocX());
                loc.setLatitude(area.getLocY());
                double distance = myLoc.distanceTo(loc);

                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceArea = area;
                }
            }
            */
    }

    private static boolean isAreaInAzimut(AreaOfInterest area, Location myLocation, double azimut) {
        double distY = area.getLocY() - myLocation.getLatitude();
        double distX = area.getLocX() - myLocation.getLongitude();
        double angleArea = Math.atan2(distY, distX);

        return angleArea >= (azimut - AZIMUT_DETECTION_DELTA) && angleArea <= (azimut + AZIMUT_DETECTION_DELTA);
    }

    public static void drawDetection(Canvas canvas, Resources res, ArrayList<AreaOfInterest> areas, Location myLocation) {
        Paint p = new Paint();

        for (AreaOfInterest area : areas) {
            Bitmap wifi = BitmapFactory.decodeResource(res, R.drawable.wifi);
            double angle = getAreaAngle(area, myLocation);

            int w = canvas.getWidth();
            int h = canvas.getHeight();
            canvas.drawBitmap(wifi, (int)((float)w/2 + (float)angle/3.14*w/2), h/2, p);
        }
    }

    public static double getAreaAngle(AreaOfInterest area, Location myLoc) {
        double distY = area.getLocY() - myLoc.getLatitude();
        double distX = area.getLocX() - myLoc.getLongitude();
        return Math.atan2(distY, distX);
    }
}
