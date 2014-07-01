package re.notifica.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import re.notifica.model.NotificareUser;
import re.notifica.ui.UserPreferencesActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends Activity implements OnMapLoadedCallback, OnMyLocationChangeListener {

	public GoogleMap map;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navigationLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        navigationLabels = getResources().getStringArray(R.array.navigation_labels);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, navigationLabels));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        Notificare.shared().fetchUserDetails(new NotificareCallback<NotificareUser>() {

			@Override
			public void onError(NotificareError arg0) {

			}

			@Override
			public void onSuccess(NotificareUser arg0) {
				Notificare.shared().setUserId(arg0.getUserId());
				Notificare.shared().registerDevice(Notificare.shared().getDeviceId(), arg0.getUserId(), arg0.getUserName(), new NotificareCallback<String>() {

					@Override
					public void onSuccess(String result) {

					}

					@Override
					public void onError(NotificareError error) {

					}
				});
			}
        	
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_ibeacons).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_ibeacons:
        	Intent a = new Intent(MainActivity.this, BeaconsActivity.class);
			startActivity(a);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

    	String values [] =  getResources().getStringArray(R.array.navigation_urls);
    	StringTokenizer tokens = new StringTokenizer(values[position], ":");
    	String first = tokens.nextToken();
    	String second = tokens.nextToken();

		if (first.equals("http") || second.equals("https")) {

            Fragment fragment = new WebViewFragment();
            Bundle args = new Bundle();
            args.putInt(WebViewFragment.ARG_NAVIGATION_NUMBER, position);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(navigationLabels[position]);
            
		}else {
			if(second.equals("Map")){
				
				GoogleMapOptions options = new GoogleMapOptions();
				options.mapType(GoogleMap.MAP_TYPE_NORMAL)
				.compassEnabled(true)
				.rotateGesturesEnabled(true)
				.scrollGesturesEnabled(true)
				.tiltGesturesEnabled(true)
				.zoomGesturesEnabled(true)
				.zoomControlsEnabled(true);

				MapFragment mMapFragment = MapFragment.newInstance(options);
				android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				fragmentTransaction.add(R.id.content_frame, mMapFragment);
				fragmentTransaction.commit();
				getFragmentManager().executePendingTransactions();
				
//				//@TODO: Add markers based on fences being monitored 
				map = mMapFragment.getMap();
				map.setMyLocationEnabled(true);
				map.setOnMapLoadedCallback(this);
				map.setOnMyLocationChangeListener(this);

		        // update selected item and title, then close the drawer
		        mDrawerList.setItemChecked(position, true);
		        setTitle(navigationLabels[position]);
		        
			} else if (second.equals("Settings")) {

				startActivity(new Intent(MainActivity.this, UserPreferencesActivity.class));
				
			} else {
				
				if(Notificare.shared().isLoggedIn()){
					Fragment fragment = new UserProfileFragment();
					Bundle args = new Bundle();
					args.putInt(UserProfileFragment.ARG_NAVIGATION_NUMBER, position);
					fragment.setArguments(args);
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
					
//					Fragment fragment2 = new AccessTokenFragment();
//					Bundle args2 = new Bundle();
//					args.putInt(AccessTokenFragment.ARG_NAVIGATION_NUMBER, position);
//					fragment.setArguments(args2);
//					FragmentManager fragmentManager2 = getFragmentManager();
//					fragmentManager2.beginTransaction().replace(R.id.content_frame, fragment2).commit();
//					
//					fragmentManager.beginTransaction().add(R.id.content_frame, fragment2).commit();
					// update selected item and title, then close the drawer
			        mDrawerList.setItemChecked(position, true);
			        setTitle(navigationLabels[position]);
				} else {
					Intent a = new Intent(MainActivity.this, SignInActivity.class);
					startActivity(a);
				}
			}
		}



        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

 
    /**
     * Fragment with a WebView
     */
    public static class WebViewFragment extends Fragment {
        public static final String ARG_NAVIGATION_NUMBER = "navigation_pos";

        public WebViewFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
            int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
            String label = getResources().getStringArray(R.array.navigation_labels)[i];
            String url = getResources().getStringArray(R.array.navigation_urls)[i];

            WebView webView =  (WebView) rootView.findViewById(R.id.webView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            getActivity().setTitle(label);
            return rootView;
        }
    }
    
    
    /**
     * Fragment with a User profile
     */
    public static class UserProfileFragment extends Fragment {
        public static final String ARG_NAVIGATION_NUMBER = "navigation_pos";

        public UserProfileFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
            int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
            String label = getResources().getStringArray(R.array.navigation_labels)[i];
            //String url = getResources().getStringArray(R.array.navigation_urls)[i];
            final ListView userProfileList =  (ListView) rootView.findViewById(R.id.userProfileList);
            
            Notificare.shared().fetchUserDetails(new NotificareCallback<NotificareUser>() {
				
				@Override
				public void onSuccess(NotificareUser result) {
					
		            final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		           
		            HashMap<String, String> email = new HashMap<String, String>();
		            email.put("label", "email");
		            email.put("value", result.getUserId());
		    		list.add(email);
		    		
		    		HashMap<String, String> name = new HashMap<String, String>();
		    		name.put("label", "name");
		    		name.put("value", result.getUserName());
		    		list.add(name);
		    		
		    		HashMap<String, String> access_token = new HashMap<String, String>();
		    		access_token.put("label", "email token");
		    		access_token.put("value", result.getAccessToken());
		    		list.add(access_token);
		    		
		            ListAdapter adapter = new UserProfileAdapter(getActivity(), list);
		            userProfileList.setAdapter(adapter);
				}
				
				@Override
				public void onError(NotificareError error) {
					if (error.getCode() == NotificareError.FORBIDDEN || error.getCode() == NotificareError.UNAUTHORIZED) {
						Intent intent = new Intent(Notificare.shared().getApplicationContext(), SignInActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(Notificare.shared().getApplicationContext(), "Error fetching user details", Toast.LENGTH_LONG).show();
					}
				}
			});
            
            getActivity().setTitle(label);
            return rootView;
        }
    }

	@Override
	public void onMapLoaded() {
		// TODO Add markers based on 
		
	}

	@Override
	public void onMyLocationChange(Location arg0) {
		LatLng userLocation = new LatLng(arg0.getLatitude(), arg0.getLongitude());
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
//		map.addMarker(new MarkerOptions()
//		.title("Your current location")
//		.position(userLocation));
	}

}
