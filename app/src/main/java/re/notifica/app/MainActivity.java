package re.notifica.app;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import re.notifica.Notificare;
import re.notifica.NotificareCallback;
import re.notifica.NotificareError;
import re.notifica.beacon.BeaconRangingListener;
import re.notifica.billing.BillingManager;
import re.notifica.billing.BillingResult;
import re.notifica.billing.Purchase;
import re.notifica.model.NotificareApplicationInfo;
import re.notifica.model.NotificareAsset;
import re.notifica.model.NotificareBeacon;
import re.notifica.model.NotificarePass;
import re.notifica.model.NotificarePassRedemption;
import re.notifica.model.NotificareProduct;
import re.notifica.model.NotificareRegion;
import re.notifica.model.NotificareUserData;
import re.notifica.support.v7.app.ActionBarBaseActivity;
import re.notifica.ui.UserPreferencesActivity;


public class MainActivity extends ActionBarBaseActivity implements Notificare.OnNotificareReadyListener, BeaconRangingListener, Notificare.OnBillingReadyListener, BillingManager.OnRefreshFinishedListener, BillingManager.OnPurchaseFinishedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public GoogleMap map;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Boolean inProgress = false;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navigationLabels;
    private ProgressDialog dialog;
    public Typeface myTypeface;
    public Typeface lightTypeface;
    public Typeface hairlineTypeface;
    protected ArrayAdapter<NotificareProduct> productListAdapter;
    public List<Circle> circlesList;
    private Menu mOptionsMenu;
    protected static final String TAG = MainActivity.class.getSimpleName();
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Log.i(TAG, "Created with intent: " + getIntent().toString());
//        if (getIntent() != null && getIntent().hasExtra(Notificare.INTENT_EXTRA_NOTIFICATION)) {
//            NotificareAction action = getIntent().getParcelableExtra(Notificare.INTENT_EXTRA_ACTION);
//            if (action != null) {
//                Log.i(TAG, "Action: " + action.getLabel());
//            }
//            handleNotificationOpenedIntent(getIntent());
//        }
        setContentView(R.layout.activity_main);

        builder = new AlertDialog.Builder(this);

        circlesList = new ArrayList<Circle>();
        productListAdapter = new ProductListAdapter(this, R.layout.product_list_cell);

        hairlineTypeface = Typeface.createFromAsset(getAssets(), "fonts/Lato-Hairline.ttf");
        myTypeface = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
        lightTypeface = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        Notificare.shared().addNotificareReadyListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "New intent: " + getIntent().toString());

//        if (intent != null && intent.hasExtra(Notificare.INTENT_EXTRA_NOTIFICATION)) {
//            handleNotificationOpenedIntent(intent);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Notificare.shared().removeNotificareReadyListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        mOptionsMenu = menu;

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
        switch (item.getItemId()) {
            case R.id.action_signout:

                item.setVisible(false);

                Notificare.shared().userLogout(new NotificareCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {

                        SignInFragment fragment = new SignInFragment();
                        Bundle args = new Bundle();
                        args.putInt(SignInFragment.ARG_NAVIGATION_NUMBER, 4);
                        fragment.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        mDrawerList.setItemChecked(4, true);
                        setTitle(navigationLabels[4]);

                    }

                    @Override
                    public void onError(NotificareError error) {

                    }
                });

                return true;
            case R.id.action_ibeacons:
                BeaconsFragment beaconsFragment = new BeaconsFragment();
                Bundle args = new Bundle();
                args.putInt(SignInFragment.ARG_NAVIGATION_NUMBER, 3);
                beaconsFragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, beaconsFragment).commit();

                // update selected item and title, then close the drawer
                mDrawerList.setItemChecked(3, true);
                setTitle(navigationLabels[3]);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRangingBeacons(final List<NotificareBeacon> notificareBeacons) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Log.d(TAG, "Ranging beacons");

                if (notificareBeacons.size() > 0) {
                    mOptionsMenu.getItem(0).setVisible(true);
                } else {
                    mOptionsMenu.getItem(0).setVisible(false);
                }

                switch (mOptionsMenu.getItem(0).getItemId()) {
                    case R.id.action_ibeacons:
                        if (notificareBeacons.size() > 0) {
                            mOptionsMenu.getItem(0).setVisible(true);
                        } else {
                            mOptionsMenu.getItem(0).setVisible(false);
                        }
                        break;
                }
            }

        });
    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void selectItem(int position) {

        String values[] = getResources().getStringArray(R.array.navigation_urls);
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

        } else {
            switch (second) {
                case "Map":

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

                    mMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;

                            if (Notificare.shared().hasLocationPermissionGranted()) {
                                map.setMyLocationEnabled(true);
                            }
                            map.animateCamera(CameraUpdateFactory.zoomTo(map.getMaxZoomLevel() - 6.0f));
                            map.setOnMapLoadedCallback(new OnMapLoadedCallback() {
                                @Override
                                public void onMapLoaded() {

                                    for (Circle region : circlesList) {
                                        region.remove();
                                    }

                                    circlesList = new ArrayList<Circle>();

                                    for (NotificareRegion region : Notificare.shared().getRegions()) {
                                        Circle circle;
                                        circle = map.addCircle(new CircleOptions()
                                                .center(new LatLng(region.getGeometry().getLatitude(), region.getGeometry().getLongitude()))
                                                .radius(region.getDistance())
                                                .fillColor(R.color.wildsand)
                                                .strokeColor(0)
                                                .strokeWidth(0));

                                        circlesList.add(circle);
                                    }

                                }
                            });


                            map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

                                @Override
                                public void onMyLocationChange(Location location) {

                                    for (Circle region : circlesList) {
                                        region.remove();
                                    }

                                    circlesList = new ArrayList<Circle>();

                                    for (NotificareRegion region : Notificare.shared().getRegions()) {
                                        Circle circle;
                                        circle = map.addCircle(new CircleOptions()
                                                .center(new LatLng(region.getGeometry().getLatitude(), region.getGeometry().getLongitude()))
                                                .radius(region.getDistance())
                                                .fillColor(R.color.wildsand)
                                                .strokeColor(0)
                                                .strokeWidth(0));

                                        circlesList.add(circle);
                                    }


                                    float zoom = map.getCameraPosition().zoom;

                                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoom));
                                }
                            });
                        }
                    });

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    setTitle(navigationLabels[position]);

                    break;
                case "Beacons": {

                    BeaconsFragment beaconsFragment = new BeaconsFragment();
                    Bundle args = new Bundle();
                    args.putInt(SignInFragment.ARG_NAVIGATION_NUMBER, position);
                    beaconsFragment.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, beaconsFragment).commit();

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    setTitle(navigationLabels[position]);

                    //startActivity(new Intent(MainActivity.this, BeaconsActivity.class));

                    break;
                }
                case "Settings":

                    startActivity(new Intent(MainActivity.this, UserPreferencesActivity.class));

                    break;
                case "Inbox":

                    startActivity(new Intent(MainActivity.this, InboxActivity.class));

                    break;
                case "Products": {

                    ProductsFragment fragment = new ProductsFragment();
                    fragment.productListAdapter = productListAdapter;
                    Bundle args = new Bundle();
                    args.putInt(SignInFragment.ARG_NAVIGATION_NUMBER, position);
                    fragment.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    setTitle(navigationLabels[position]);

                    builder.setMessage(R.string.alert_inapp_purchase_demo)
                            .setTitle(R.string.app_name)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog dialogInfo = builder.create();
                    dialogInfo.show();

                    break;
                }
                case "Assets": {
                    if (!Notificare.shared().hasServiceEnabled(NotificareApplicationInfo.SERVICE_KEY_STORAGE)) {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.app_name)
                                .setMessage(R.string.alert_storage_disabled)
                                .setCancelable(false)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                    AssetsFragment assetsFragment = new AssetsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, assetsFragment).commit();
                    setTitle(navigationLabels[position]);
                    break;
                }
                case "Main": {

                    Fragment fragment = new MainFragment();
                    Bundle args = new Bundle();
                    args.putInt(SignInFragment.ARG_NAVIGATION_NUMBER, position);
                    fragment.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    setTitle(navigationLabels[position]);

                    break;
                }
                default:

                    if (!Notificare.shared().isLoggedIn()) {

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
                    break;
            }
        }

        invalidateOptionsMenu();
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }
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


    public void goToSignup(View view) {

        Intent a = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(a);

    }

    public void goToLostPass(View view) {

        Intent a = new Intent(MainActivity.this, LostPassActivity.class);
        startActivity(a);

    }

    public void refreshFragment() {
        Fragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putInt(UserProfileFragment.ARG_NAVIGATION_NUMBER, 4);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Notificare.shared().getBillingManager() != null && Notificare.shared().getBillingManager().handleActivityResult(requestCode, resultCode, data)) {
            // Billingmanager handled the result
            inProgress = true; // wait for purchase to finish before doing other calls
        } else {
            // Something else came back to us
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Notificare.shared().addBillingReadyListener(this);
        //Notificare.shared().setForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Notificare.shared().removeBillingReadyListener(this);
        //Notificare.shared().setForeground(false);
    }

    @Override
    public void onNotificareReady(final NotificareApplicationInfo info) {
        if (!Notificare.shared().hasLocationPermissionGranted()) {
            Log.i(TAG, "permission not granted");
            if (Notificare.shared().didRequestLocationPermission()) {
                if (Notificare.shared().shouldShowRequestPermissionRationale(this)) {
                    // Here we should show a dialog explaining location updates
                    builder.setMessage(R.string.alert_location_permission_rationale)
                            .setTitle(R.string.app_name)
                            .setCancelable(true)
                            .setPositiveButton(R.string.button_location_permission_rationale_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Notificare.shared().requestLocationPermission(MainActivity.this, LOCATION_PERMISSION_REQUEST_CODE);
                                }
                            })
                            .create()
                            .show();
                }
            } else {
                Notificare.shared().requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

        if (BuildConfig.ENABLE_TEST_CALLS) {
            Notificare.shared().fetchAssets("test", new NotificareCallback<List<NotificareAsset>>() {
                @Override
                public void onSuccess(List<NotificareAsset> notificareAssets) {
                    for (NotificareAsset asset : notificareAssets) {
                        Log.d(TAG, asset.getTitle());
                        Log.d(TAG, asset.getOriginalFileName());
                    }
                }

                @Override
                public void onError(NotificareError notificareError) {
                    Log.e(TAG, "Error: " + notificareError.getMessage());
                }
            });

            Notificare.shared().fetchPass("505971ff-a0ee-4977-8dfa-a579d4795b44", new NotificareCallback<NotificarePass>() {
                @Override
                public void onSuccess(NotificarePass notificarePass) {
                    for (NotificarePassRedemption redemption : notificarePass.getRedeemHistory()) {
                        Log.d(TAG, "Redemption at " + redemption.getDate() + ": " + redemption.getComments());
                    }
                }

                @Override
                public void onError(NotificareError notificareError) {
                    Log.e(TAG, "Error: " + notificareError.getMessage());
                }
            });

            Notificare.shared().fetchUserData(new NotificareCallback<NotificareUserData>() {
                @Override
                public void onSuccess(NotificareUserData notificareUserData) {
                    try {
                        Log.d(TAG, "Fetch user data: " + notificareUserData.toJSONObject().toString());
                    } catch (JSONException e) {
                        Log.w(TAG, "Error parsing user data");
                    }
                    NotificareUserData newUserData = new NotificareUserData();
                    newUserData.setValue("firstName", "Massimo");
                    newUserData.setValue("lastName", "Gondoliere");
                    Notificare.shared().updateUserData(newUserData, new NotificareCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            Log.d(TAG, "Successfully updated userdata");
                        }

                        @Override
                        public void onError(NotificareError notificareError) {
                            Log.w(TAG, "Error updating user data");
                        }
                    });
                }

                @Override
                public void onError(NotificareError notificareError) {
                    Log.w(TAG, "Error fetching user data");
                }
            });

            Map<String,String> params = new HashMap<>();
            params.put("test", "yes");
            JSONObject data = new JSONObject();
            try {
                data.put("deviceID", Notificare.shared().getDeviceId());
                data.put("type", "re.notifica.event.custom.Test");
            } catch (JSONException e) {

            }
            Notificare.shared().doCloudRequest("POST", "/api/event", params, data, new NotificareCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    if (jsonObject != null) {
                        Log.i(TAG, jsonObject.toString());
                    }
                }

                @Override
                public void onError(NotificareError notificareError) {
                    Log.e(TAG, notificareError.getMessage());
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (Notificare.shared().checkRequestLocationPermissionResult(permissions, grantResults)) {
                    Log.i(TAG, "permission granted");
                    Notificare.shared().enableLocationUpdates();
                    if (BuildConfig.ENABLE_BEACONS) {
                        Notificare.shared().enableBeacons(60000);
                    }
                }
                break;
        }
    }

    @Override
    public void onBillingReady() {
        if (!inProgress) {
            productListAdapter.clear();
            Notificare.shared().getBillingManager().refresh(this);
        }
    }

    @Override
    public void onPurchaseFinished(BillingResult billingResult, Purchase purchase) {
        inProgress = false;
        productListAdapter.clear();
        Notificare.shared().getBillingManager().refresh(this);
    }

    @Override
    public void onRefreshFinished() {
        productListAdapter.addAll(Notificare.shared().getBillingManager().getProducts());
    }

    @Override
    public void onRefreshFailed(NotificareError notificareError) {
        Toast.makeText(this, "billing refresh failed: " + notificareError.getMessage(), Toast.LENGTH_LONG).show();
    }

}
