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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 23/04/16.
 */
public class CameraActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    GPSListener, OrientationListener {
    private CameraPreview mPreview;
    private RelativeLayout mLayout;

    private GPSTracker gpsTracker;
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
        double radius = 500;
        localData = DataManager.getInstance().getAreaOfInterestValues(loc.getLongitude(), loc.getLatitude(), radius);
    }

    @Override
    public void onOrientationChanged(float azimut, float pitch, float roll) {
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
            // calculate cumulative error
            double errorX = sampleOrientationX[0];
            double errorY = sampleOrientationY[0];
            double errorZ = sampleOrientationZ[0];

            for (int i=1; i<sampleOrientationCount-1; i++) {
                errorX += 10*(sampleOrientationX[i + 1] - sampleOrientationX[i]);
                errorY += 10*(sampleOrientationY[i + 1] - sampleOrientationY[i]);
                errorZ += 10*(sampleOrientationZ[i + 1] - sampleOrientationZ[i]);
            }

            sampleOrientationCount = 0;

            double cumulativeError = errorX + errorY + errorY;
            if (cumulativeError < 4) {
                txAxisX.setText("x: " + String.format("%.3f", x));
                txAxisY.setText("y: " + String.format("%.3f", y));
                txAxisZ.setText("z: " + String.format("%.3f", z));
            }

            /*
            txAxisX.setText("Error X: " + String.format("%.3f", errorX));
            txAxisY.setText("Error Y: " + String.format("%.3f", errorY));
            txAxisZ.setText("Error Z: " + String.format("%.3f", errorZ));
            */
        }
    }

    @Override
    public void onAccuracyChanged(int accuracy) {

    }
}
