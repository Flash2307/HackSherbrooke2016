package com.evalwithin.olook;

/**
 * Created by Frederik on 4/23/2016.
 */
public interface OrientationListener {
    void onOrientationChanged(float x, float y, float z);
    void onAccuracyChanged(int accuracy);
}
