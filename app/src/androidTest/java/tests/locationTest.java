/**
 * Test file for adding, customizing, and deleting locations
 */

package tests;

import android.os.Handler;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
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
    String TAG;
    //flag to wait for thread to finish
    boolean changedFlag = false;

    public locationTest() {
        super(MapsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        TAG = "setUp";
        Log.d(TAG, "STARTING setUp!!!!!!!!!!!!!!!!!");
        super.setUp();
        mapsActivity = getActivity();
        gMap = mapsActivity.getMap();
        changedFlag = false;
    }


    /* Test for scenario of user adding a new favorite location without naming it
     * Given the user is trying to add Geisel Library as a favorite location
     * When the user taps on and confirms they want to add Geisel Library
     * Then Geisel Library should be saved as a marker on the map
     */
    /*
    public void test_addDefaultNameFav() {
        TAG = "test_addDefaultNameFav";
        Log.d(TAG, "STARTING test_addDefaultNameFav!!!!!!!!!!!!!!!!!");

        final LatLng geiselLatLng= new LatLng(32.881117, -117.237278);
        final cLocation geiselLoc = new cLocation(geiselLatLng, mapsActivity);

        //handler to add marker in google map
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Marker newFav = gMap.addMarker(new MarkerOptions().position(geiselLatLng).
                        title(geiselLoc.getName()));
                mapsActivity.addMarkerToPref(newFav);
                //flag to indicate new thread running
                changedFlag = true;
            }

        });

        while (!changedFlag) {
            //wait for thread to end so sharedPreferences is updated
        }

        assertTrue("Geisel location is favorited", checkName(geiselLoc));
        assertTrue("Geisel location is favorited", checkPosition(geiselLoc));
        Log.d(TAG, "ENDING test_addDefaultNameFav!!!!!!!!!!!!!!!!!");
    }
    */

    /* Test for scenario of user adding a new favorite location and renaming it
     * Given the user is trying to add UTC Mall as a favorite location with a custom name
     * When the user taps on and confirms they want to add UTC Mall AND changes the name of
     * to My Favorite Mall!
     * Then UTC Mall should be saved as a marker on the map as My Favorite Mall!
     */

    /*
    public void test_addCustomNameFav() {
        TAG = "test_addCustomNameFav";
        Log.d(TAG, "STARTING test_addCustomNameFav!!!!!!!!!!!!!!!!!");
        final LatLng utcLatLng = new LatLng(32.871127, -117.210754);
        final cLocation utcLoc = new cLocation(utcLatLng, mapsActivity);
        final String customName = "My Favorite Mall!";

        //handler to add marker in google map
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Marker newFav = gMap.addMarker(new MarkerOptions().position(utcLatLng).
                        title(customName));
                if (mapsActivity == null) {
                    Log.d(TAG, "mapsActivity is NULL");
                }
                mapsActivity.addMarkerToPref(newFav);
                //flag to indicate new thread running
                changedFlag = true;
            }
        });

        while (!changedFlag) {
            //wait for thread to end so sharedPreferences is updated
        }

        utcLoc.setName(customName);
        assertTrue("My Favorite Mall! is favorited", checkName(utcLoc));
        assertTrue("My Favorite Mall! is favorited", checkPosition(utcLoc));
        Log.d(TAG, "ENDING test_addCustomNameFav!!!!!!!!!!!!!!!!!");
    }
    */

    /* Test for scenario of user changing a location's name
     * Given the user has already favorited La Jolla shores and wants to change its name
     * When the user taps on and changes the name of La Jolla Shores to My Favorite Beach!!!!!!!
     * Then La Jolla Shores should be updated as a marker on the map with the new name.
     */

    /*
    public void test_changeNameFav() {
        TAG = "test_changeNameFav";
        Log.d(TAG, "STARTING test_changeNameFav!!!!!!!!!!!!!!!!!");
        final LatLng shoresLatLng = new LatLng(32.858167, -117.256469);
        final cLocation shoresLoc = new cLocation(shoresLatLng, mapsActivity);
        final String newName = "My favorite beach!!!!!!";

        //handler to add marker in google map
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Marker newFav = gMap.addMarker(new MarkerOptions().position(shoresLatLng).
                        title(shoresLoc.getName()));
                mapsActivity.addMarkerToPref(newFav);
                mapsActivity.changeMarkerName(newFav, newName);
                //flag to indicate new thread running
                changedFlag = true;
            }
        });

        while (!changedFlag) {
            //wait for thread to end so sharedPreferences is updated
        }

        shoresLoc.setName(newName);
        assertTrue("La Jollas Shores location is renamed", checkName(shoresLoc));
        assertTrue("La Jollas Shores location is renamed", checkPosition(shoresLoc));
        Log.d(TAG, "ENDING test_changeNameFav!!!!!!!!!!!!!!!!!");
    }
    */

    /* Test for scenario of user removing a favorite location
     * Given the user has already favorited Belmont Park and wants to remove it
     * When the user taps on and chooses to remove Belmont Park as a favorite
     * Then Belmont Park should not show up / be saved as a favorite.
     */

    /*
    public void test_removeFavorite() {
        TAG = "test_removeFavorite";
        final LatLng belmontParkLatLng = new LatLng(32.770668, -117.251554);
        final cLocation belmontParkLoc = new cLocation(belmontParkLatLng, mapsActivity);

        //handler to add marker to google map
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Marker newFav = gMap.addMarker(new MarkerOptions().position(belmontParkLatLng).
                        title(belmontParkLoc.getName()));
                mapsActivity.addMarkerToPref(newFav);
                mapsActivity.removeMarkerFromMap(newFav);
                //flag to indicate new thread running
                changedFlag = true;
            }
        });

        while (!changedFlag) {
            //wait for thread to end so sharedPreferences is updated
        }

        assertFalse("Belmont Park location is deleted", checkName(belmontParkLoc));
        assertFalse("Belmont Park locatoin is deleted", checkPosition(belmontParkLoc));
    }
    */

    /* Private helper method to check if a test location's position is saved in SharedPreferences */
    /*
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
    */

    /* Private method to check if a test location's name is saved in SharedPrefeerences */
    /*
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
    */

    @Override
    public void tearDown() throws Exception {
        TAG = "tearDown";
        Log.d(TAG, "STARTING TEAR-DOWN");
        super.tearDown();
    }
}
