package com.evalwithin.olook;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by Frederik on 4/23/2016.
 */
public class OrientationTracker implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor, mAccelerometer, mMagnetometer;

    private boolean hasGravity = false;
    private boolean hasGeomag = false;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];

    ArrayList<OrientationListener> listeners = new ArrayList<>();

    public OrientationTracker(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        initSensors();
    }

    public void initSensors() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        mSensorManager.unregisterListener(this, mSensor);
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            for (int i=0; i<3; i++) {
                gravity[i] = event.values[i];
            }
            hasGravity = true;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            for (int i=0; i<3; i++) {
                geomagnetic[i] = event.values[i];
            }
            hasGeomag = true;
        }
        if (hasGravity && hasGeomag) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float orientation[] = new float[5];
                try {
                    SensorManager.getOrientation(R, orientation);
                    float x = orientation[0]; // orientation contains: azimut, pitch and roll
                    float y = orientation[1];
                    float z = orientation[2];

                    for (OrientationListener listener : listeners) {
                        listener.onOrientationChanged(x, y, z);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        for (OrientationListener listener : listeners) {
            listener.onAccuracyChanged(accuracy);
        }
    }

    public void addListener(OrientationListener listener) {
        listeners.add(listener);
    }
}
