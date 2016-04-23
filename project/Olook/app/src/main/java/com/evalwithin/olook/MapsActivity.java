package com.evalwithin.olook;

import android.app.FragmentManager;
import android.content.Context;
import android.app.Fragment;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_map = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        m_map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        m_map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
