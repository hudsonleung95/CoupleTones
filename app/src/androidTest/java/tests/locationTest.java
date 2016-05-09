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

        assertTrue("Geisel location is favorited", checkName(geiselLoc));
        assertTrue("Geisel location is favorited", checkPosition(geiselLoc));
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
        assertTrue("My Favorite Mall! is favorited", checkName(utcLoc));
        assertTrue("My Favorite Mall! is favorited", checkPosition(utcLoc));
        Log.d(TAG, "ENDING test_addCustomNameFav!!!!!!!!!!!!!!!!!");
    }


    public void test_changeNameFav() {
        TAG = "test_changeNameFav";
        Log.d(TAG, "STARTING test_changeNameFav!!!!!!!!!!!!!!!!!");
        final LatLng shoresLatLng = new LatLng(32.858167, -117.256469);
        final cLocation shoresLoc = new cLocation(shoresLatLng, mapsActivity);
        final String newName = "My favorite beach!!!!!!";
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Marker newFav = gMap.addMarker(new MarkerOptions().position(shoresLatLng).
                        title(shoresLoc.getName()));
                favsList.add(newFav);
                mapsActivity.addMarkerToPref(newFav);
                mapsActivity.changeMarkerName(newFav, newName);
            }
        });

        shoresLoc.setName(newName);
        assertTrue("La Jollas Shores location is renamed", checkName(shoresLoc));
        assertTrue("La Jollas Shores location is renamed", checkPosition(shoresLoc));
        Log.d(TAG, "ENDING test_changeNameFav!!!!!!!!!!!!!!!!!");
    }


    public void test_removeFavorite() {
        TAG = "test_removeFavorite";
        final LatLng belmontParkLatLng = new LatLng(32.770668, -117.251554);
        final cLocation belmontParkLoc = new cLocation(belmontParkLatLng, mapsActivity);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Marker newFav = gMap.addMarker(new MarkerOptions().position(belmontParkLatLng).
                        title(belmontParkLoc.getName()));
                favsList.add(newFav);
                mapsActivity.addMarkerToPref(newFav);
                mapsActivity.removeMarkerFromMap(newFav);
            }
        });

        assertFalse("Belmont Park location is deleted", checkName(belmontParkLoc));
        assertFalse("Belmont Park locatoin is deleted", checkPosition(belmontParkLoc));
    }


    private boolean checkPosition(cLocation location) {
        TAG = "checkPosition";
        DataStorage dataStorage = mapsActivity.getDS();
        String favLatLngFromJson = dataStorage.getLatLngList();
        if (favLatLngFromJson == null) {
            throw new NullPointerException("favLatLngFromJson is NULL");
        }

        LatLng[] favLatLng = new Gson().fromJson(favLatLngFromJson, LatLng[].class);
        List<LatLng> latLngs = Arrays.asList(favLatLng);
        latLngs = new ArrayList<>(latLngs);

        for (LatLng i : latLngs) {
            Log.d(TAG, "COORDINATES: " + i.toString());
            if (i.equals(location.getLatLng())) {
                return true;
            }
        }

        return false;
    }

    private boolean checkName(cLocation location) {
        TAG = "checkName";
        DataStorage dataStorage = mapsActivity.getDS();
        String favNamesFromJson = dataStorage.getLocNameList();
        if (favNamesFromJson == null) {
            throw new NullPointerException("favNamesFromJson is NULL");
        }

        String[] favNames = new Gson().fromJson(favNamesFromJson, String[].class);
        List<String> locNames = Arrays.asList(favNames);
        locNames = new ArrayList<>(locNames);

        for (String i : locNames) {
            Log.d(TAG, "NAME: " + i);
            if (i.equals(location.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void tearDown() throws Exception {
        TAG = "tearDown";
        Log.d(TAG, "STARTING TEAR-DOWN");
        super.tearDown();
    }
}
