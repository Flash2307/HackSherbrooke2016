package com.evalwithin.olook;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Frederik on 4/30/2016.
 */
public class VRGestureTracker implements OrientationListener {
    final double ERROR_MULTIPLICATOR = 10;
    final double MAX_CUMULATIVE_ERROR_FOR_AIM_DETECTION = 18;

    private OrientationTracker orientationTracker;

    final int ORIENTATION_SAMPLE_SIZE = 50;
    private float[] sampleOrientationX = new float[ORIENTATION_SAMPLE_SIZE];
    private float[] sampleOrientationY = new float[ORIENTATION_SAMPLE_SIZE];
    private float[] sampleOrientationZ = new float[ORIENTATION_SAMPLE_SIZE];
    private int sampleOrientationCount = 0;

    ArrayList<VRGestureListener> listeners = new ArrayList<>();

    public VRGestureTracker(Context context) {
        orientationTracker = new OrientationTracker(context);
        orientationTracker.addListener(this);
    }

    @Override
    public void onOrientationChanged(float x, float y, float z) {
        // wait for sample to complete
        if (sampleOrientationCount < ORIENTATION_SAMPLE_SIZE) {
            sampleOrientationX[sampleOrientationCount] = x;
            sampleOrientationY[sampleOrientationCount] = y;
            sampleOrientationZ[sampleOrientationCount] = z;
            sampleOrientationCount++;
        } else {
            // average
            float avgX = sampleOrientationX[0];
            float avgY = sampleOrientationY[0];
            float avgZ = sampleOrientationZ[0];

            // calculate cumulative error
            float errorX, errorY, errorZ;
            errorX = errorY = errorZ = 0;

            for (int i=1; i<sampleOrientationCount-1; i++) {
                avgX += sampleOrientationX[i];
                avgY += sampleOrientationY[i];
                avgZ += sampleOrientationZ[i];

                errorX += ERROR_MULTIPLICATOR * Math.abs(sampleOrientationX[i + 1] - sampleOrientationX[i]);
                errorY += ERROR_MULTIPLICATOR * Math.abs(sampleOrientationY[i + 1] - sampleOrientationY[i]);
                errorZ += ERROR_MULTIPLICATOR * Math.abs(sampleOrientationZ[i + 1] - sampleOrientationZ[i]);
            }

            avgX /= ORIENTATION_SAMPLE_SIZE;
            avgY /= ORIENTATION_SAMPLE_SIZE;
            avgZ /= ORIENTATION_SAMPLE_SIZE;

            float cumulativeError = errorX + errorY + errorY;
            if (cumulativeError < MAX_CUMULATIVE_ERROR_FOR_AIM_DETECTION) {
                onAimToDirection(avgX, avgY, avgZ);
            } else {
                onMoveAway();
            }

            sampleOrientationCount = 0;
        }
    }

    @Override
    public void onAccuracyChanged(int accuracy) {

    }

    public void addListener(VRGestureListener listener) {
        listeners.add(listener);
    }

    public void onAimToDirection(float x, float y, float z) {
        for (VRGestureListener listener : listeners) {
            listener.onAimToDirection(x, y, z);
        }
    }

    public void onMoveAway() {
        for (VRGestureListener listener : listeners) {
            listener.onMoveAway();
        }
    }
}
