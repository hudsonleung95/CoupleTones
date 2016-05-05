package coupletones.pro.cse110.coupletones;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public static final String MARKERS = "LOCATION_DATA";
    private int numLoc;
    private cLocation location; //for user selected location
    private EditText et_drawer_name; //edit name field in drawer
    private List<LatLng> latLngs;
    private List<String> locationNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = getSharedPreferences(MARKERS, Context.MODE_PRIVATE);
        latLngs = new ArrayList<LatLng>();
        locationNames = new ArrayList<String>();
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
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        init(); //initialize components
        loadMarkers();
    }

    @Override
    public void onMapLongClick(LatLng point){
        location = new cLocation(point, this);
        DialogFragment dialog = new AddLocationDialog();
        dialog.show(getFragmentManager(), getText(R.string.map_ask_add).toString());
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        String loc_name = ((AddLocationDialog)dialog).getNameFromUser();
        //if user enters nothing
        if ( loc_name.length() != 0){
            location.setName(loc_name);
        }

        //put a marker if a user add to favorite
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                location.getLatLng().latitude,
                location.getLatLng().longitude))
                .title(location.getName()).draggable(true));

        numLoc++;
        marker.showInfoWindow();
        addMarkerToPref(marker);

    }

    private void saveMarkerPrefs(){
        //String markersList = new Gson().toJson(markers);
        String latLngsList = new Gson().toJson(latLngs);
        String namesList = new Gson().toJson(locationNames);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LatLngs", latLngsList);
        editor.putString("LocNames", namesList);
        editor.commit();

    }

    private void addMarkerToPref(Marker marker){
        latLngs.add(marker.getPosition());
        locationNames.add(marker.getTitle());
        saveMarkerPrefs();
    }

    private void removeMarkerFromPref(Marker marker){
        latLngs.remove(marker.getPosition());
        locationNames.remove(marker.getTitle());
        numLoc--;
        saveMarkerPrefs();
    }

    private void loadMarkers(){
        String favFromJson = sharedPreferences.getString("LatLngs", null);
        LatLng[] favLatLng = new Gson().fromJson(favFromJson, LatLng[].class);
        String favNamesFromJson = sharedPreferences.getString("LocNames", null);
        String[] favNames = new Gson().fromJson(favNamesFromJson, String[].class);

        latLngs = Arrays.asList(favLatLng);
        latLngs = new ArrayList<LatLng>(latLngs);
        locationNames = Arrays.asList(favNames);
        locationNames = new ArrayList<String>(locationNames);

//        SharedPreferences.Editor editor = sharedPreferences.edit();

        for(int i = 0; i < latLngs.size(); i++){
            LatLng point = latLngs.get(i);
            String name = locationNames.get(i);
            mMap.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude))
                    .draggable(true).title(name));
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}

    @Override
    public void onMarkerDragEnd(Marker marker){
//        int numMarker = markers.indexOf(marker);
//        LatLng pos = marker.getPosition();
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putFloat("lat" + numMarker, (float) pos.latitude);
//        editor.putFloat("lng" + numMarker, (float) pos.longitude);
//        editor.apply();
        saveMarkerPrefs();
    }

    @Override
    public void onMarkerDragStart(Marker marker){}

    @Override
    public void onMarkerDrag(Marker marker){}


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

                //save location coordinates in sharedPreferences
                numLoc++;

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
        }
    }

    /**
     * this method will be called when user click the add button in the info layer
     *
     * @param v
     *      view that call this method
     */
    public void addToFav(View v){
        //TODO store the location to favorite

        //close the info layer
        layout_drawer.closeDrawer(Gravity.RIGHT);
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
        showPlaceInfo(marker);
        return true;
    }


}
