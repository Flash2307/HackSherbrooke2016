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
    private boolean detection = false;
    private ArrayList<AreaOfInterest> areas;

    private Location myLocation;

    public CameraOverlay(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (detection) {
            VRUtils.drawDetection(canvas, getResources(), areas, myLocation);
        }
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
}
