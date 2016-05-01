package com.evalwithin.olook;

import android.content.Intent;
import android.hardware.Camera;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 23/04/16.
 */
public class CameraActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    GPSListener, VRGestureListener {
    final double MIN_Y_ANGLE = 0.5;
    final double CAMERA_DETECTION_RANGE = 80000;

    private CameraPreview mPreview;
    private RelativeLayout mLayout;
    private CameraOverlay mOverlay;

    private GPSTracker gpsTracker;
    private VRGestureTracker gestureTracker;

    private boolean hasLocation = false;
    private Map<String, ArrayList<AreaOfInterest>> localData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mLayout = (RelativeLayout) findViewById(R.id.cameraLayout);

        mOverlay = new CameraOverlay(getApplicationContext());
        mLayout.addView(mOverlay);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);

            Menu menu = navigationView.getMenu();
            MenuItem cameraItem = menu.findItem(R.id.nav_camera);
            cameraItem.setIcon(R.drawable.ic_place_black);
            cameraItem.setTitle(R.string.map);
        }

        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getState() == Thread.State.NEW) {
            dataManager.start();
        }

        gpsTracker = new GPSTracker(getApplicationContext());
        gpsTracker.addListener(this);

        gestureTracker = new VRGestureTracker(getApplicationContext());
        gestureTracker.addListener(this);
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
    public void onAimToDirection(float x, float y, float z) {
        if (hasLocation && Math.abs(y) > MIN_Y_ANGLE) {
            Location myLocation = gpsTracker.getLocation();
            ArrayList<AreaOfInterest> aimedAreas = VRUtils.searchNearestDirection(myLocation, x, localData);
            if (aimedAreas.size() > 0) {
                mOverlay.showDetection(aimedAreas, myLocation);
            }
        }
    }

    @Override
    public void onMoveAway() {
        mOverlay.hideDetection();
    }
}
