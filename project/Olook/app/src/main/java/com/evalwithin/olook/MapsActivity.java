package com.evalwithin.olook;

import android.app.FragmentManager;
import android.content.Context;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GPSTracker tracker;
    private OrientationTracker orientationTracker;

    private static String TAG = MapsActivity.class.getSimpleName();

    ListView m_drawerList;
    RelativeLayout m_drawerPane;

    private ActionBarDrawerToggle m_drawerToggle;
    private DrawerLayout m_drawerLayout;
    private GoogleMap m_map;

    ArrayList<NavigationItem> m_navigationItems = new ArrayList<NavigationItem>();

    class NavigationItem {
        String m_title;
        String m_subTitle;
        int m_Icon;

        public NavigationItem(String title, String subtitle, int icon) {
            m_title = title;
            m_subTitle = subtitle;
            m_Icon = icon;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context m_context;
        ArrayList<NavigationItem> m_navigationItems;

        public DrawerListAdapter(Context context, ArrayList<NavigationItem> navItems) {
            m_context = context;
            m_navigationItems = navItems;
        }

        @Override
        public int getCount() {
            return m_navigationItems.size();
        }

        @Override
        public Object getItem(int position) {
            return m_navigationItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( m_navigationItems.get(position).m_title );
            subtitleView.setText( m_navigationItems.get(position).m_subTitle );
            iconView.setImageResource(m_navigationItems.get(position).m_Icon);

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Add options in sliding menu
        m_navigationItems.add(new NavigationItem("Home", "Meetup destination", R.drawable.ic_action_home));

        // Populate the Drawer with options
        m_drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        m_drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        m_drawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, m_navigationItems);
        m_drawerList.setAdapter(adapter);

        // Click listeners for drawer
        m_drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MapUtils.init(getResources());

        tracker = new GPSTracker(getApplicationContext());
        orientationTracker = new OrientationTracker(getApplicationContext());
    }

    public void onLocationUpdated() {

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapUtils.addInterestPoint(googleMap, new LatLng(-30, 140), "Nice area!");

        Location myLocation;
        while (tracker.getLocation() == null);
        myLocation = tracker.getLocation();
        LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        MapUtils.setMyLocation(googleMap, myLatLng);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15f));

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            float maxLevel = 18f;
            float minLevel = 15f;

            @Override
            public void onCameraChange(CameraPosition pos) {
                if (pos.zoom > maxLevel) {
                    // TODO : Mettre le mode camera
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(maxLevel));
                } else if (pos.zoom < minLevel) {
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(minLevel));
                }
            }
        });

        // rotate 90 degrees
        CameraPosition oldPos = googleMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(245.0F)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    /*Called when item selected in drawer*/
    private void selectItemFromDrawer(int position) {
        Fragment fragment = new PreferencesFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();

        m_drawerList.setItemChecked(position, true);
        setTitle(m_navigationItems.get(position).m_title);

        // Close the drawer
        m_drawerLayout.closeDrawer(m_drawerPane);
    }
}
