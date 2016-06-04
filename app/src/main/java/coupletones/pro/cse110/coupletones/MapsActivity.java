package coupletones.pro.cse110.coupletones;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The MapsActivity is responsible for handling user interactions when the user clicks on the map
 * button. The activity will handle adding, setting, and removing favorite locations
 */

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, OnConnectionFailedListener,OnMapLongClickListener,
        AddLocationDialog.LocationDialogListener, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener{
    private GoogleMap mMap;
    private LocationManager locMan;
    private String locProv;

    private static final float ZOOMLV = 13.5f;
    private DrawerLayout layout_drawer;
    private GoogleApiClient googleApi;
    private SharedPreferences sharedPreferences;
    private cLocation location; //for user selected location
    private EditText et_drawer_name; //edit name field in drawer
    private List<LatLng> latLngs;
    private List<String> locationNames;
    private final Context context = this;
    private DataStorage dataStorage;
    private Location currMarkerLoc;
    private ParseClient parseClient;
    private boolean partnerLocs;
    private boolean showMyLoc;
    private String locClicked;
    private Marker markerClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
//        TextView title = (TextView) findViewById(R.id.maps_title);

        partnerLocs = false;
        showMyLoc = false;
        locClicked = "";
        if(extras != null){
            Log.d("GOT EXTRA :", "!!!!");
            if(extras.getBoolean("SHOW_My_LOCS")){
                setContentView(R.layout.layout_maps);
                showMyLoc = true;
//                title.setText("Your Favorite Locations");

            }
            else {
                setContentView(R.layout.layout_partner_maps);
                partnerLocs = extras.getBoolean("SHOW_PARTNER_LOCS");
//                title.setText("Your Partner's Favorite Locations");
            }
            locClicked = extras.getString("LOC_CLICKED");
        }
        else {
            setContentView(R.layout.layout_maps);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Obtain markers saved in shared preferences
        dataStorage = new DataStorage(this);

        //Initialize list of saved markers separating by latatude/longitute and names
        latLngs = new ArrayList<LatLng>();
        locationNames = new ArrayList<String>();
        parseClient = new ParseClient(this);

        //initialize google api client
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApi = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .addApi(AppIndex.API).build();
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
    public void onMapReady(GoogleMap googleMap)
    {
        //Enable interactions with the google map
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if(!partnerLocs) {
            Log.d("GO TO INTI :", "!!!!");
            init(); //initialize components
            mMap.setOnMapLongClickListener(this);
            mMap.setOnMarkerDragListener(this);

        }
        mMap.setOnMarkerClickListener(this);

        if(showMyLoc || partnerLocs){
            loadMarkers();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerClicked.getPosition(), ZOOMLV));
            markerClicked.showInfoWindow();
        }
        else{
            loadMarkers();         //change to loadPartnerMarkers() later
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerClicked.getPosition(), ZOOMLV));
//            markerClicked.showInfoWindow();
        }
    }

    /**
     * Allow the user to add a location by long clicking on any location on the map
     * @param point
     */
    @Override
    public void onMapLongClick(LatLng point){
        location = new cLocation(point, this);

        //Open a dialog for the user to allow the user to set the name for the saved location
        DialogFragment dialog = new AddLocationDialog();
        dialog.show(getFragmentManager(), getText(R.string.map_ask_add).toString());
    }

    /**
     * If the user clicks on the "Add" button, the location will be saved
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        String loc_name = ((AddLocationDialog)dialog).getNameFromUser();
        //if user enters nothing
        if ( loc_name.length() != 0){
            location.setName(loc_name);
        }

        //put a marker if a user adds to favorite
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                location.getLatLng().latitude,
                location.getLatLng().longitude))
                .title(location.getName()));

        marker.showInfoWindow();

        //Save the marker in shared preferences
        addMarkerToPref(marker);
    }

    /**
     * The method will save the current items in the LatLngs and LocaitonNames ArrayLists in
     * shared preferences
     */
    private void saveMarkerPrefs(){
        String latLngsList = new Gson().toJson(latLngs);
        String namesList = new Gson().toJson(locationNames);
        dataStorage.setLatLngList(latLngsList);
        dataStorage.setLocNameList(namesList);
    }

    /**
     * The helper method will add a marker into the ArrayLists and save it in shared preferences
     * @param marker
     */
    public void addMarkerToPref(Marker marker){
        parseClient.pushFavLoc(marker);
        latLngs.add(marker.getPosition());
        locationNames.add(marker.getTitle());
        saveMarkerPrefs();
    }

    /**
     * The helper method will remove a marker from the ArrayLists and save the changes in shared
     * preferences
     * @param marker
     */
    private void removeMarkerFromPref(Marker marker){
        latLngs.remove(marker.getPosition());
        locationNames.remove(marker.getTitle());
        saveMarkerPrefs();
    }

    /**
     * The method will remove a marker from the displayed map and save changes in shared preferences
     * @param marker
     */
    public void removeMarkerFromMap(Marker marker){
        marker.remove();
        removeMarkerFromPref(marker);
    }

    /**
     * The method will save the given name in shared preferences
     * @param marker
     * @param newName Name entered in the EditText field
     */
    public void changeMarkerName(Marker marker, String newName){
        int indexOf = locationNames.indexOf(marker.getTitle());
        locationNames.set(indexOf, newName);
        marker.setTitle(newName);
        saveMarkerPrefs();
    }

    /**
     * The method will load all the markers from shared preferences and display them on the map
     */
    private void loadMarkers(){
        if (partnerLocs){
            latLngs = dataStorage.getPartnerLatLngList();
            locationNames = dataStorage.getPartnerLocNameList();
        }else{
            String favLatLngFromJson = dataStorage.getLatLngList();
            String favNamesFromJson = dataStorage.getLocNameList();

            if(favNamesFromJson != null && favLatLngFromJson != null){
                String[] favNames = new Gson().fromJson(favNamesFromJson, String[].class);
                LatLng[] favLatLng = new Gson().fromJson(favLatLngFromJson, LatLng[].class);

                //Convert json to the actual ArrayLists
                latLngs = Arrays.asList(favLatLng);
                latLngs = new ArrayList<LatLng>(latLngs);
                locationNames = Arrays.asList(favNames);
                locationNames = new ArrayList<String>(locationNames);
            }
        }




        //Add the markers back onto the map
        for (int i = 0; i < latLngs.size(); i++) {
            LatLng point = latLngs.get(i);
            String name = locationNames.get(i);
            Marker addedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng
                    (point.latitude, point.longitude)).title(name));
            if(partnerLocs){
                addedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            if(name.equals(locClicked)){
                markerClicked = addedMarker;
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Do nothing when "Cancel" is pressed
    }

    /**
     * When the marker is dragged the latitude and longitude coordinates are saved in shared
     * preferences
     * @param marker
     */
    @Override
    public void onMarkerDragEnd(Marker marker){
        int indexOf = locationNames.indexOf(marker.getTitle());
        latLngs.set(indexOf, marker.getPosition());
        saveMarkerPrefs();
    }

    @Override
    public void onMarkerDragStart(Marker marker){
        //Fixed bug where marker jumps when long pressed
        int indexOf = locationNames.indexOf(marker.getTitle());
        marker.setPosition(latLngs.get(indexOf));
    }

    @Override
    public void onMarkerDrag(Marker marker){ }


    /**
     * this method initialize the components used in the map
     */
    private void init(){
        //drawer layers, used to show the info of location
        layout_drawer = (DrawerLayout)findViewById(R.id.map_drawer);
        //lock the drawer first, so empty drawer wont be dragged out
        layout_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //views in the info layer
        et_drawer_name = (EditText)findViewById(R.id.drawer_et_name);

        //used to get current location
        locMan = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locProv = LocationManager.GPS_PROVIDER;

        //check user permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            //if do not have getLocation permission, request
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }else if(mMap != null) {
            //get current location
            Location current = locMan.getLastKnownLocation(locProv);
            mMap.setMyLocationEnabled(true); //my location button

            if(current != null){
                //go to current location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(current.getLatitude(), current.getLongitude()),
                        ZOOMLV));
            }

        }

        //initialize search bar
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.map_search);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                location = new cLocation(place);

                //move camera to searched place and zoom in
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), ZOOMLV));

                //show the dialog for user to add
                openAddDialog();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
    }

    /**
     * this method shows the info layer
     *
     * @param marker
     *      marker that user selected
     */
    private void showPlaceInfo(Marker marker){
        if (marker != null){
            //unlock the drawer
            layout_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            et_drawer_name.setText(marker.getTitle());

            //open the drawer layer from the right
            layout_drawer.openDrawer(Gravity.RIGHT);

            final Marker markerCopy = marker;

            Button remove = (Button) findViewById(R.id.drawer_btn_remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_drawer.closeDrawer(Gravity.RIGHT);
                    layout_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Are you sure you want to remove location?")
                            .setCancelable(false)
                            .setPositiveButton("Remove",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    removeMarkerFromMap(markerCopy);
                                    Toast.makeText(getApplicationContext(), "Location Removed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            //Close the drawer when cancel is pressed
            Button cancel = (Button) findViewById(R.id.drawer_btn_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_drawer.closeDrawer(Gravity.RIGHT);
                    layout_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            });

            //Save changes in the name and close drawer when save is pressed
            Button save = (Button) findViewById(R.id.drawer_btn_save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editName = (EditText) findViewById(R.id.drawer_et_name);
                    String editedName = editName.getText().toString();
                    changeMarkerName(markerCopy, editedName);
                    Toast.makeText(getApplicationContext(), "Name Saved", Toast.LENGTH_SHORT).show();
                    layout_drawer.closeDrawer(Gravity.RIGHT);
                    layout_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            });
        }
    }

    /**
     * this method opens a dialog for the user to add location
     * called after search/hold a location
     */
    private void openAddDialog(){
        DialogFragment dialog = new AddLocationDialog();
        dialog.show(getFragmentManager(), getText(R.string.map_ask_add).toString());
    }

    /**
     * this method handles the error when failing to connect the google api client
     *
     * @param result
     *      result of connection
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
    }

    /**
     * this method will be called after onCreate()
     */
    @Override
    protected void onStart() {
        super.onStart();

        //connect google api client
        googleApi.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://coupletones.pro.cse110.coupletones/http/host/path")
        );
        AppIndex.AppIndexApi.start(googleApi, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://coupletones.pro.cse110.coupletones/http/host/path")
        );
        AppIndex.AppIndexApi.end(googleApi, viewAction);
        googleApi.disconnect();
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        if(!partnerLocs) {
            showPlaceInfo(marker);
        }
        else{
            marker.showInfoWindow();
        }
        return true;
    }

    public GoogleMap getMap() {
        return this.mMap;
    }

    public DataStorage getDS() {
        return this.dataStorage;
    }
}
