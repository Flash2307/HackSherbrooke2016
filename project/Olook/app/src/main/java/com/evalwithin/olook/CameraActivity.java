package com.evalwithin.olook;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evalwithin.olook.Data.AreaOfInterest;
import com.evalwithin.olook.Data.DataManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 23/04/16.
 */
public class CameraActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    GPSListener, OrientationListener {
    final double ERROR_MULTIPLICATOR = 10;
    final double MAX_CUMULATIVE_ERROR_FOR_DETECTION = 18;
    final double MIN_Y_ANGLE = 0.5;
    final double CAMERA_DETECTION_RANGE = 500;
    final double AZIMUT_DETECTION_DELTA = 0.3;

    private CameraPreview mPreview;
    private RelativeLayout mLayout;

    private GPSTracker gpsTracker;
    private boolean hasLocation = false;

    private OrientationTracker orientationTracker;
    Map<String, ArrayList<AreaOfInterest>> localData;

    TextView txAxisX, txAxisY, txAxisZ;

    final int ORIENTATION_SAMPLE_SIZE = 50;
    private float[] sampleOrientationX = new float[ORIENTATION_SAMPLE_SIZE];
    private float[] sampleOrientationY = new float[ORIENTATION_SAMPLE_SIZE];
    private float[] sampleOrientationZ = new float[ORIENTATION_SAMPLE_SIZE];
    private int sampleOrientationCount = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mLayout = (RelativeLayout) findViewById(R.id.cameraLayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem cameraItem = menu.findItem(R.id.nav_camera);
        cameraItem.setIcon(R.drawable.ic_place_black);
        cameraItem.setTitle(R.string.map);

        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getState() == Thread.State.NEW)
            dataManager.start();

        gpsTracker = new GPSTracker(getApplicationContext());
        gpsTracker.addListener(this);

        orientationTracker = new OrientationTracker(getApplicationContext());
        orientationTracker.addListener(this);

        txAxisX = (TextView) findViewById(R.id.axisX);
        txAxisY = (TextView) findViewById(R.id.axisY);
        txAxisZ = (TextView) findViewById(R.id.axisZ);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set the second argument by your choice.
        // Usually, 0 for back-facing camera, 1 for front-facing camera.
        // If the OS is pre-gingerbreak, this does not have any effect.
        mPreview = new CameraPreview(this, 0, CameraPreview.LayoutMode.FitToParent);
        RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // Un-comment below lines to specify the size.
        //previewLayoutParams.height = 500;
        //previewLayoutParams.width = 500;

        // Un-comment below line to specify the position.
        //mPreview.setCenterPosition(270, 130);

        mLayout.addView(mPreview, 0, previewLayoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
        mLayout.removeView(mPreview); // This is necessary.
        mPreview = null;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent myIntent = new Intent(this, MapsActivity.class);
            this.startActivity(myIntent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onGPSLocationChanged(Location newLocation) {
        Location loc = gpsTracker.getLocation();
        double radius = CAMERA_DETECTION_RANGE;
        localData = DataManager.getInstance().getAreaOfInterestValues(loc.getLongitude(), loc.getLatitude(), radius);
        hasLocation = true;
    }

    @Override
    public void onOrientationChanged(float azimut, float pitch, float roll) {
        if (!hasLocation) {
            return;
        }

        float x = azimut;
        float y = pitch;
        float z = roll;

        // wait for sample to complete
        if (sampleOrientationCount < ORIENTATION_SAMPLE_SIZE) {
            sampleOrientationX[sampleOrientationCount] = x;
            sampleOrientationY[sampleOrientationCount] = y;
            sampleOrientationZ[sampleOrientationCount] = z;
            sampleOrientationCount++;
        } else {
            // average
            double avgX = sampleOrientationX[0];
            double avgY = sampleOrientationY[0];
            double avgZ = sampleOrientationZ[0];

            // calculate cumulative error
            double errorX, errorY, errorZ;
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

            double cumulativeError = errorX + errorY + errorY;
            if (cumulativeError < MAX_CUMULATIVE_ERROR_FOR_DETECTION) {
                if (Math.abs(avgY) > MIN_Y_ANGLE) {
                    searchNearestDirection(avgX);
                }
            }

            sampleOrientationCount = 0;
        }
    }

    @Override
    public void onAccuracyChanged(int accuracy) {

    }

    private void searchNearestDirection(double azimut) {
        // search nearest at direction
        Location myLoc = gpsTracker.getLocation();
        ArrayList<AreaOfInterest> potentialAreas = new ArrayList<AreaOfInterest>();

        Set<String> keys = localData.keySet();
        for (String key: keys) {
            MapUtils.IconIndex idx = MapUtils.getIconIndex(key);
            for (AreaOfInterest area : localData.get(key)) {
                if (isAreaInAzimut(area, myLoc, azimut)) {
                    potentialAreas.add(area);
                }
            }
        }

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

            txAxisX.setText(minDistanceArea.getLocationName());
        }
    }

    private boolean isAreaInAzimut(AreaOfInterest area, Location myLocation, double azimut) {
        double distY = area.getLocY() - myLocation.getLatitude();
        double distX = area.getLocX() - myLocation.getLongitude();
        double angleArea = Math.atan2(distY, distX);

        return angleArea >= (azimut - AZIMUT_DETECTION_DELTA) && angleArea <= (azimut + AZIMUT_DETECTION_DELTA);
    }
}
