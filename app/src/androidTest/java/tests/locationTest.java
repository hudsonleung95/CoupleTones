package tests;


import android.os.Handler;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import coupletones.pro.cse110.coupletones.MapsActivity;
import coupletones.pro.cse110.coupletones.DataStorage;
import coupletones.pro.cse110.coupletones.cLocation;


/**
 * Created by Andy on 5/7/16.
 */
public class locationTest extends ActivityInstrumentationTestCase2<MapsActivity> {

    MapsActivity mapsActivity;
    GoogleMap gMap;
    List<Marker> favsList;
    String TAG;

    public locationTest() {
        super(MapsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        TAG = "setUp";
        super.setUp();
        mapsActivity = getActivity();
        gMap = mapsActivity.getMap();
        favsList = new ArrayList<>();
    }


    public void test_addDefaultNameFav() {
        TAG = "test_addDefaultNameFav";
        Log.d(TAG, "STARTING test_addDefaultNameFav!!!!!!!!!!!!!!!!!");
        final LatLng geiselLatLng= new LatLng(32.881117, -117.237278);
        final cLocation geiselLoc = new cLocation(geiselLatLng, mapsActivity);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Marker newFav = gMap.addMarker(new MarkerOptions().position(geiselLatLng).
                        title(geiselLoc.getName()));
                favsList.add(newFav);
                mapsActivity.addMarkerToPref(newFav);
            }

        });

        assertTrue("Geisel location is favorited", locationIsSaved(geiselLoc));
        Log.d(TAG, "ENDING test_addDefaultNameFav!!!!!!!!!!!!!!!!!");
    }

    public void test_addCustomNameFav() {
        TAG = "test_addCustomNameFav";
        Log.d(TAG, "STARTING test_addCustomNameFav!!!!!!!!!!!!!!!!!");
        final LatLng utcLatLng = new LatLng(32.871127, -117.210754);
        final cLocation utcLoc = new cLocation(utcLatLng, mapsActivity);
        final String customName = "My Favorite Mall!";
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Marker newFav = gMap.addMarker(new MarkerOptions().position(utcLatLng).
                        title(customName));
                favsList.add(newFav);
                mapsActivity.addMarkerToPref(newFav);
            }
        });
        utcLoc.setName(customName);
        assertTrue(locationIsSaved(utcLoc));
        Log.d(TAG, "ENDING test_addCustomNameFav!!!!!!!!!!!!!!!!!");
    }

    /*
    public void test_changeNameFav() {
        TAG = "test_changeNameFav";
        final LatLng shoresLatLng = new LatLng(32.858167, -117.256469);
        final cLocation shoresLoc = new cLocation(shoresLatLng, mapsActivity);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Marker newFav = gMap.addMarker(new MarkerOptions().position(shoresLatLng).
                        title(shoresLoc.getName()));
                favsList.add(newFav);
                mapsActivity.addMarkerToPref(newFav);
            }
        });
        assertTrue("La Jollas Shores location is favorited", locationIsSaved(shoresLoc));
        String newName = "My favorite beach!!!!!";
        shoresLoc.setName(newName);
        Marker markerToChange = null;

        //GET MARKER SOMEHOW

        if (markerToChange == null) {
            throw new NullPointerException("markerToChange is NULL");
        }

        mapsActivity.changeMarkerName(markerToChange, newName);
        assertTrue("La Jollas Shores location is renamed", locationIsSaved(shoresLoc));
    }
    */

    private boolean locationIsSaved(cLocation loc) {
        TAG = "locationIsSaved";
        DataStorage dataStorage = mapsActivity.getDS();
        String favLatLngFromJson = dataStorage.getLatLngList();
        String favNamesFromJson = dataStorage.getLocNameList();

        if (favLatLngFromJson == null) {
            throw new NullPointerException("favLatLngFromJson is NULL");
        }
        if (favNamesFromJson == null) {
            throw new NullPointerException("favNamesFromJson is NULL");
        }

        LatLng[] favLatLng = new Gson().fromJson(favLatLngFromJson, LatLng[].class);
        List<LatLng> latLngs = Arrays.asList(favLatLng);
        latLngs = new ArrayList<>(latLngs);
        String[] favNames = new Gson().fromJson(favNamesFromJson, String[].class);
        List<String> locNames = Arrays.asList(favNames);
        locNames = new ArrayList<>(locNames);

        boolean foundPos = false;
        boolean foundName = false;

        for (LatLng i : latLngs) {
            Log.d(TAG, "COORDINATES: " + i.toString());
            if (i.equals(loc.getLatLng())) {
                foundPos = true;
                break;
            }
        }

        for (String i : locNames) {
            Log.d(TAG, "NAME: " + i);
            if (i.equals(loc.getName())) {
                foundName = true;
                break;
            }

        }

        return (foundPos && foundName);
    }

    @Override
    public void tearDown() throws Exception {
        TAG = "tearDown";
        Log.d(TAG, "STARTING TEAR-DOWN");
        super.tearDown();
        /*Log.d("locationTest", Integer.toString(favsList.size()));
        for (Marker i : favsList) {
            Log.d(TAG, i.getTitle());
            mapsActivity.removeMarkerFromMap(i);
        }*/
    }
}
