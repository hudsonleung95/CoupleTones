package coupletones.pro.cse110.coupletones;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
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

import java.util.Map;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, OnConnectionFailedListener
{

    private GoogleMap mMap;
    private LocationManager locMan;
    private String locProv;
    private Marker curMark;
    private Place curPlace;
    private static final float ZOOMLV = 13.5f;
    private DrawerLayout layout_drawer;
    private LinearLayout layout_info;
    private TextView tv_info_title;
    private TextView tv_info_address;
    private ImageView img_info_img;
    private Button btn_addFav;
    private GoogleApiClient googleApi;
    private int img_width, img_height;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialize google api client
        googleApi = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
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
        init(); //initialize components
    }


    /**
     * this method initialize the components used in the map
     */
    private void init(){
        //drawer layers, used to show the info of location
        layout_drawer = (DrawerLayout)findViewById(R.id.map_drawer);
        layout_info = (LinearLayout)findViewById(R.id.map_layout_info);

        //views in the info layer
        tv_info_title = (TextView)findViewById(R.id.map_info_tv_title);
        tv_info_address = (TextView)findViewById(R.id.map_info_tv_address);
        img_info_img = (ImageView)findViewById(R.id.map_info_img);
        btn_addFav = (Button)findViewById(R.id.map_info_btn_add);

        img_width = img_info_img.getWidth();
        img_height = img_info_img.getHeight();

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

            //go to current location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(current.getLatitude(), current.getLongitude()),
                    ZOOMLV));
        }

        //initialize search bar
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.map_search);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                curPlace = place; //save current searched place

                //clear all previous marker
                mMap.clear();

                //put new marker in searched place
                curMark = mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                       .title(place.getName().toString()));

                //move camera to searched place and zoom in
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), ZOOMLV));

                //show location info after selecting a place from search bar
                showPlaceInfo(place);
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
     * @param place
     *      place that user selected
     */
    private void showPlaceInfo(Place place){
        if (place != null){
            //clear the previous image
            img_info_img.setImageResource(android.R.color.transparent);

            //set information
            tv_info_title.setText(place.getName());
            tv_info_address.setText(place.getAddress());

            //get and display the image in background
            new getImageInBG().execute();

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
    protected void onStart(){
        super.onStart();

        //connect google api client
        googleApi.connect();
    }

    /**
     * inner class getImageInBG()
     *
     * this will be called to download the image of the location in the backgroud
     * TODO: fix later, not working for frequent download
     *
     */
    private class getImageInBG extends AsyncTask<Void, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(Void... params) {
            PlacePhotoMetadataBuffer photoBuffer;

            // Get a PlacePhotoMetadataResult containing metadata for the first 10 photos.
            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(googleApi, curPlace.getId()).await();
            // Get a PhotoMetadataBuffer instance containing a list of photos (PhotoMetadata).
            if (result != null && result.getStatus().isSuccess()) {
                photoBuffer = result.getPhotoMetadata();

                //if the place have photo
                if (photoBuffer.getCount() > 0) {
                    // get the first bitmap in an ImageView in the size of the view
                    Bitmap img = photoBuffer.get(0)
                            .getScaledPhoto(googleApi, img_width, img_height).await().getBitmap();

                    //have to release the buffer to prevent memory leak
                    photoBuffer.release();

                    return img;
                }

                //release buffer although no photo
                photoBuffer.release();
            }

            //if no result or fail
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap img) {
            if(img != null) //got image
                img_info_img.setImageBitmap(img);
            else //else, just set to gallery icon
                img_info_img.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

}
