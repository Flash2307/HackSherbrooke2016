package com.evalwithin.olook;

/**
 * Created by Frederik on 4/23/2016.
 */
public interface OrientationListener {
    void onOrientationChanged(float azimut, float pitch, float roll);
    void onAccuracyChanged(int accuracy);
}
