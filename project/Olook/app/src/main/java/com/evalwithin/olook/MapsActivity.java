package com.evalwithin.olook;

import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.evalwithin.olook.Data.Attrait;
import com.evalwithin.olook.Data.Parking;
import com.evalwithin.olook.Data.Zap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GPSListener {

    private GPSTracker gpsTracker;
    private OrientationTracker orientationTracker;
    private PopupMenu popupMenu;

    private static String TAG = MapsActivity.class.getSimpleName();

    ListView m_drawerList;
    RelativeLayout m_drawerPane;

    private ActionBarDrawerToggle m_drawerToggle;
    private DrawerLayout m_drawerLayout;
    private GoogleMap m_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new DataFetcher().execute(Parking.URL_PARKING, Parking.class.toString());
        new DataFetcher().execute(Attrait.URL_ATTRAIT, Attrait.class.toString());
        new DataFetcher().execute(Zap.URL_ZAP, Zap.class.toString());
        MapUtils.init(getResources());

        gpsTracker = new GPSTracker(getApplicationContext());
        orientationTracker = new OrientationTracker(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(menu.size() == 0)
        {
            for(int i = 0; i < 5; i++)
            {
                menu.add(0, i, Menu.NONE, "item " + i).setCheckable(true);
            }
        }

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        menuItem.setChecked(!menuItem.isChecked());
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        m_map = googleMap;

        //m_map.getUiSettings().setScrollGesturesEnabled(false);
        //m_map.getUiSettings().setCompassEnabled(false);

        Location loc = gpsTracker.getLocation();
        LatLng ll = new LatLng(loc.getLatitude(), loc.getLongitude());

        centerMap(loc, 15);

        MapUtils.setMyLocation(m_map, ll);
        MapUtils.addInterestPoint(googleMap, new LatLng(-30, 140), "Nice area!");

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition pos) {
                float maxLevel = 18f;
                float minLevel = 15f;

                if (pos.zoom > maxLevel) {
                    // TODO : Mettre le mode camera
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(maxLevel));
                } else if (pos.zoom < minLevel) {
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(minLevel));
                }
                centerMap(gpsTracker.getLocation());
            }
        });

        // rotate 90 degrees
        CameraPosition oldPos = googleMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(245.0F).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onGPSLocationChanged(Location newLocation) {
        centerMap(newLocation);
    }

    private void centerMap(Location location) {
        if(location == null)
            return;

        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        m_map.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
    }

    private void centerMap(Location location, float zoom) {
        if(location == null)
            return;

        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, zoom));
    }
}
