package re.notifica.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import re.notifica.model.NotificareUser;
import re.notifica.model.NotificareUserPreference;
import re.notifica.model.NotificareUserPreferenceOption;
import re.notifica.model.NotificareUserSegment;
import re.notifica.ui.UserPreferencesActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private ProgressDialog dialog;

    protected static final String TAG = MainActivity.class.getSimpleName();

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

        if(Notificare.shared().isLoggedIn()) {
            inflater.inflate(R.menu.user, menu);
        }else{
            inflater.inflate(R.menu.main, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(Notificare.shared().isLoggedIn()) {
            menu.findItem(R.id.action_signout).setVisible(!drawerOpen);
        }else {
            menu.findItem(R.id.action_ibeacons).setVisible(!drawerOpen);
        }

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
        case R.id.action_signout:

            item.setVisible(false);

        	Notificare.shared().userLogout(new NotificareCallback<Boolean>() {

                @Override
                public void onSuccess(Boolean result) {

                    Fragment fragment = new WebViewFragment();
                    Bundle args = new Bundle();
                    args.putInt(WebViewFragment.ARG_NAVIGATION_NUMBER, 0);
                    fragment.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    mDrawerList.setItemChecked(0, true);
                    setTitle(navigationLabels[0]);

                }

                @Override
                public void onError(NotificareError error) {

                }
            });

            return true;
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
		        
			} else if (second.equals("Beacons")) {

                startActivity(new Intent(MainActivity.this, BeaconsActivity.class));

            } else if (second.equals("Settings")) {

				startActivity(new Intent(MainActivity.this, UserPreferencesActivity.class));
				
			} else {

				if(!Notificare.shared().isLoggedIn()){

                    Fragment fragment = new SignInFragment();
                    Bundle args = new Bundle();
                    args.putInt(SignInFragment.ARG_NAVIGATION_NUMBER, position);
                    fragment.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    setTitle(navigationLabels[position]);

				} else {
                    Fragment fragment = new UserProfileFragment();
                    Bundle args = new Bundle();
                    args.putInt(UserProfileFragment.ARG_NAVIGATION_NUMBER, position);
                    fragment.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    setTitle(navigationLabels[position]);
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
     * Fragment with a WebView
     */
    public static class SignInFragment extends Fragment {
        public static final String ARG_NAVIGATION_NUMBER = "navigation_pos";

        public SignInFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
            final int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
            String label = getResources().getStringArray(R.array.navigation_labels)[i];
            String url = getResources().getStringArray(R.array.navigation_urls)[i];

            rootView.findViewById(R.id.buttonSignin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //signIn();

                    EditText emailField = (EditText) rootView.findViewById(R.id.email);
                    EditText passwordField = (EditText) rootView.findViewById(R.id.pass);

                    String email = emailField.getText().toString();
                    String password = passwordField.getText().toString();

                    TextView info = (TextView) rootView.findViewById(R.id.infoText);


                    if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                        info.setText(R.string.error_sign_in);
                    } else if (password.length() < 6) {
                        info.setText(R.string.error_pass_too_short);
                    } else if (!email.contains("@")) {
                        info.setText(R.string.error_invalid_email);
                    } else {

                        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loader_signin), true);


                        Notificare.shared().userLogin(email, password, new NotificareCallback<Boolean>() {

                            @Override
                            public void onError(NotificareError arg0) {
                                dialog.dismiss();

                            }

                            @Override
                            public void onSuccess(Boolean result) {

                                Notificare.shared().fetchUserDetails(new NotificareCallback<NotificareUser>() {

                                    @Override
                                    public void onError(NotificareError arg0) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onSuccess(NotificareUser arg0) {
                                        Notificare.shared().setUserId(arg0.getUserId());
                                        Notificare.shared().registerDevice(Notificare.shared().getDeviceId(), arg0.getUserId(), arg0.getUserName(), new NotificareCallback<String>() {

                                            @Override
                                            public void onSuccess(String result) {

                                                Fragment fragment = new UserProfileFragment();
                                                Bundle args = new Bundle();
                                                args.putInt(UserProfileFragment.ARG_NAVIGATION_NUMBER, i);
                                                fragment.setArguments(args);
                                                FragmentManager fragmentManager = getFragmentManager();
                                                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onError(NotificareError error) {
                                                dialog.dismiss();
                                            }

                                        });
                                    }

                                });

                            }

                        });
                    }
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
            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loader), true);

            if(Notificare.shared().isLoggedIn())
                Notificare.shared().fetchUserDetails(new NotificareCallback<NotificareUser>() {

                    @Override
                    public void onSuccess(NotificareUser userResult) {

                        final NotificareUser result = userResult;
                        Notificare.shared().fetchUserPreferences(new NotificareCallback<List<NotificareUserPreference>>() {
                            @Override
                            public void onSuccess(List<NotificareUserPreference> notificareUserPreferences) {

                                final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                                final ArrayList<NotificareUserPreference> prefs = new ArrayList<NotificareUserPreference>();

                                HashMap<String, String> header1 = new HashMap<String, String>();
                                header1.put("label", getString(R.string.header_user_profile));
                                list.add(header1);

                                HashMap<String, String> userProfile = new HashMap<String, String>();
                                userProfile.put("name", result.getUserName());
                                userProfile.put("email", result.getUserId());
                                userProfile.put("token", result.getAccessToken() + "@pushmail.notifica.re");
                                userProfile.put("icon", result.getUserId());
                                userProfile.put("label", "user_profile");
                                userProfile.put("action", result.getUserId());
                                list.add(userProfile);

                                final HashMap<String, String> changePass = new HashMap<String, String>();
                                changePass.put("label", getString(R.string.title_change_pass));
                                list.add(changePass);

                                HashMap<String, String> generateToken = new HashMap<String, String>();
                                generateToken.put("label", getString(R.string.title_generate_token));
                                list.add(generateToken);

                                HashMap<String, String> header2 = new HashMap<String, String>();
                                header2.put("label", getString(R.string.header_user_preferences));
                                list.add(header2);

                                for (NotificareUserPreference preferenceObj : notificareUserPreferences) {
                                    prefs.add(preferenceObj);
                                    HashMap<String, String> pref = new HashMap<String, String>();
                                    pref.put("label", preferenceObj.getLabel());

                                    if(preferenceObj.getPreferenceType().equals("choice")){
                                        for (NotificareUserPreferenceOption segmentObj : preferenceObj.getPreferenceOptions()) {

                                            if(segmentObj.isSelected()){
                                                pref.put("name", segmentObj.getLabel());
                                            }

                                        }
                                    }
                                    list.add(pref);
                                }

                                ListAdapter adapter = new UserProfileAdapter(getActivity(), list, prefs);

                                userProfileList.setAdapter(adapter);

                                userProfileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> aView, View view, int position,
                                                            long arg) {

                                        if(list.get(position).get("label").equals(getString(R.string.title_change_pass))){
                                            onChangePassword();
                                        }

                                        if(list.get(position).get("label").equals(getString(R.string.title_generate_token))){
                                            onGenerateToken();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(NotificareError notificareError) {

                                dialog.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onError(NotificareError error) {
                        if (error.getCode() == NotificareError.FORBIDDEN || error.getCode() == NotificareError.UNAUTHORIZED) {
                            dialog.dismiss();
                            Toast.makeText(Notificare.shared().getApplicationContext(), "Unauthorized access", Toast.LENGTH_LONG).show();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(Notificare.shared().getApplicationContext(), "Error fetching user details", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            
            getActivity().setTitle(label);
            return rootView;
        }

        public void onChangePassword(){

                AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());

                LinearLayout layout = new LinearLayout(this.getActivity().getBaseContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                alert.setTitle(getString(R.string.app_name));
                alert.setMessage(getString(R.string.title_change_pass));

                final EditText pass1 = new EditText(this.getActivity());
                final EditText pass2 = new EditText(this.getActivity());
                pass1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                pass2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(pass1);
                layout.addView(pass2);
                alert.setView(layout);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                        if (!pass1.getText().equals(pass2.getText())) {
                            onChangePasswordError(getString(R.string.error_pass_not_match));
                        } else if (pass1.getText() == null && pass2.getText() == null) {
                            onChangePasswordError(getString(R.string.error_reset_pass));
                        } else if (pass1.getText().length() < 5) {
                            onChangePasswordError(getString(R.string.error_pass_too_short));
                        } else {
                            doChangePassword(pass1);
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();

        }

        public void doChangePassword(EditText pass){
            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loader_signin), true);
            final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());

            String password = pass.getText().toString();
            Notificare.shared().changePassword(password, new NotificareCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    dialog.dismiss();
                    alert.setTitle(getString(R.string.app_name));
                    alert.setMessage(getString(R.string.success_change_password));
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();

                }

                @Override
                public void onError(NotificareError notificareError) {
                    dialog.dismiss();
                    alert.setTitle(getString(R.string.app_name));
                    alert.setMessage(getString(R.string.error_change_password));
                    alert.show();

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                }
            });
        }

        public void onChangePasswordError(String error){
            final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
            alert.setTitle(getString(R.string.app_name));
            alert.setMessage(error);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            alert.show();
        }

        public void onGenerateToken(){

            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loader_signin), true);
            final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());


            Notificare.shared().generateAccessToken(new NotificareCallback<NotificareUser>() {
                @Override
                public void onSuccess(NotificareUser notificareUser) {
                    dialog.dismiss();
                    alert.setTitle(getString(R.string.app_name));
                    alert.setMessage(getString(R.string.success_generate_token));
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            int i = getArguments().getInt(ARG_NAVIGATION_NUMBER);
                            Fragment fragment = new UserProfileFragment();
                            Bundle args = new Bundle();
                            args.putInt(UserProfileFragment.ARG_NAVIGATION_NUMBER, i);
                            fragment.setArguments(args);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                        }
                    });
                    alert.show();
                }

                @Override
                public void onError(NotificareError notificareError) {
                    dialog.dismiss();
                    alert.setTitle(getString(R.string.app_name));
                    alert.setMessage(getString(R.string.success_generate_token));
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
            });
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



    public void goToSignup(View view) {

        Intent a = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(a);

    }

    public void goToLostPass(View view) {

        Intent a = new Intent(MainActivity.this, LostPassActivity.class);
        startActivity(a);

    }



}
