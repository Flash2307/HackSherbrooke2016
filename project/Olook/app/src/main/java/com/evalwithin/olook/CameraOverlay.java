package com.evalwithin.olook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.view.View;

import com.evalwithin.olook.Data.AreaOfInterest;

import java.util.ArrayList;

/**
 * Created by Frederik on 4/24/2016.
 */
public class CameraOverlay extends View {
    private int w, h;

    private boolean detection = false;
    private ArrayList<AreaOfInterest> areas;

    private Location myLocation;

    public CameraOverlay(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (detection) {
            Paint p = new Paint();

            for (AreaOfInterest area : areas) {
                Bitmap wifi = BitmapFactory.decodeResource(getResources(), R.drawable.wifi);
                double angle = getAreaAngle(area, myLocation);

                int areaX = (int)((float)w/2 + (float)angle/3.14*w/2);
                String descr = area.getLocationName();
                canvas.drawBitmap(wifi, (int)((float)w/2 + (float)angle/3.14*w/2), h/2, p);
                p.setTextSize(25);
                canvas.drawText(descr, areaX - 100, h/2 + 150, p);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    public void showDetection(ArrayList<AreaOfInterest> areas, Location myLoc) {
        detection = true;
        this.areas = areas;
        this.myLocation = myLoc;
        invalidate();
    }

    public void hideDetection() {
        detection = false;
        invalidate();
    }

    private double getAreaAngle(AreaOfInterest area, Location myLoc) {
        double distY = area.getLocY() - myLoc.getLatitude();
        double distX = area.getLocX() - myLoc.getLongitude();
        return Math.atan2(distY, distX);
    }
}
