package coupletones.pro.cse110.coupletones;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * The class build the current location tracker that tracks the current location of
 * users through the Google map
 */
public class CurrentLocationTracker extends Service implements LocationListener{

    private DataStorage dataStorage;
    private ParseClient parseClient;

    // flag for GPS status
    boolean isGPSOn = false;

    // flag for network status
    boolean isNetworkOn = false;

    // flag for GPS status
    boolean canGetLoc = false;

    Location lastVisited;
    Location currLoc;
    double latitude, longitude;
    private List<LatLng> latLngs;
    private List<String> locationNames;
    private HashMap<String, Long> timesLastVisited;
    private HashMap<String, Long> timeLastDepart;
    private String locLastVisited;
    private String locToSendNotification;

    // The minimum distance to change Updates in meters
    private static final long DIST_BTWN_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long TIME_BTWN_UPDATES = 60; // 1 minute

    private static final int LOC_RADIUS = 161; // 161 meters (0.1 miles)

    private static final int TIME_BTWN_SAME_LOC = 1800000; //30 minutes

    // Declaring a Location Manager
    protected LocationManager locMan;

    @Override
    public void onCreate() {
        super.onCreate();
        getLocation();
        Location temp = new Location("");
        temp.setLatitude(0);
        temp.setLongitude(0);
        currLoc = temp;
        locLastVisited = "";
        locToSendNotification = "";
        dataStorage = new DataStorage(this);
        latLngs = new ArrayList<LatLng>();
        locationNames = new ArrayList<String>();
        timesLastVisited = new HashMap<String, Long>();
        timeLastDepart = new HashMap<String, Long>();
        parseClient = new ParseClient(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLatitude();
        getLongitude();
        if (isAtLoc()) {
            sendNotification();
        }
        return Service.START_STICKY;
    }

    /**
     * Get the current location of the user
     * @return Location object
     */
    public Location getLocation() {
        try {
            locMan = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSOn = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkOn = locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSOn && !isNetworkOn) {
                this.canGetLoc = false;
            } else {
                this.canGetLoc = true;
                // First get location from Network Provider
                if (isNetworkOn) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }
                    locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_BTWN_UPDATES,
                            DIST_BTWN_UPDATES, this);
                    if (locMan != null) {
                        currLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (currLoc != null) {
                            latitude = currLoc.getLatitude();
                            longitude = currLoc.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSOn) {
                    if (currLoc == null) {
                        locMan.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, TIME_BTWN_UPDATES, DIST_BTWN_UPDATES, this);
                        if (locMan != null) {
                            currLoc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (currLoc != null) {
                                latitude = currLoc.getLatitude();
                                longitude = currLoc.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return currLoc;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Keep track of the changing location
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currLoc = location;
        getLatitude();
        getLongitude();
        if (isAtLoc()) {
            sendNotification();
        }

        //has visit some fav before
        if (lastVisited != null){
            //if not within last fav = departed
            if(!isWithinFavLoc(lastVisited)){
                sendDepartNoti();
            }
        }
    }

    /**
     * check if the user's current location is within the favorite location
     */
    private boolean isWithinFavLoc(Location favLoc) {
        if(favLoc != null) {
            return currLoc.distanceTo(favLoc) < LOC_RADIUS;
        }
        return false;
    }

    /*
     * Get the location within the radius area
     */
    private String getLocWithinRadius() {
        getFavoriteLocations();
        for (int i = 0; i < latLngs.size(); ++i) {
            LatLng point = latLngs.get(i);
            Location temp = new Location("");
            temp.setLatitude(point.latitude);
            temp.setLongitude(point.longitude);

            if (isWithinFavLoc(temp)) {
                lastVisited = temp;
                return locationNames.get(i);
            }
        }
        return null;
    }

    private boolean isAtLoc() {
        locToSendNotification = getLocWithinRadius();
        return locToSendNotification != null;
    }

    /**
     * When the user enter the partner's favorite location, the app will send notifications
     */
    public void sendNotification() {
        if (isAtLoc() && (!locLastVisited.equals(locToSendNotification) ||
                pastTimeLimit(locToSendNotification, true))){

            //Get timestamp
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            String currTime = dateFormat.format(date);

            timesLastVisited.put(locToSendNotification, System.currentTimeMillis());

            //push to history
            parseClient.pushHistory(locToSendNotification, currLoc);

            //Send message
            parseClient.sendNotification("Your partner visited " + locToSendNotification + " at "
                    + currTime, true, locToSendNotification);
            locLastVisited = locToSendNotification;
        }
    }

    private void sendDepartNoti(){
        if(pastTimeLimit(locLastVisited, false)){
            //Get timestamp
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            String currTime = dateFormat.format(date);

            timeLastDepart.put(locLastVisited, System.currentTimeMillis());

            //Send message
            parseClient.sendNotification("Your partner departed " + locLastVisited + " at "
                    + currTime);
        }
    }

    /**
     * Get the favorite location of users
     */
    private void getFavoriteLocations() {
        String favLatLngFromJson = dataStorage.getLatLngList();
        String favNamesFromJson = dataStorage.getLocNameList();

        if (favLatLngFromJson != null && favNamesFromJson != null) {
            LatLng[] favLatLng = new Gson().fromJson(favLatLngFromJson, LatLng[].class);
            String[] favNames = new Gson().fromJson(favNamesFromJson, String[].class);
            latLngs = Arrays.asList(favLatLng);
            latLngs = new ArrayList<LatLng>(latLngs);
            locationNames = Arrays.asList(favNames);
            locationNames = new ArrayList<String>(locationNames);
        }
    }


    /**
     * @param locName name of location to send notification
     * @return whether the location was visited in the last 30 minutes or not
     */
    private boolean pastTimeLimit(String locName, boolean isArrival){
        if(isArrival) {
            if (timesLastVisited.get(locName) == null) return true;
            return (System.currentTimeMillis() - timesLastVisited.get(locName)) >
                    TIME_BTWN_SAME_LOC;
        }else{ //depart
            if (timeLastDepart.get(locName) == null) return true;
            return (System.currentTimeMillis() - timeLastDepart.get(locName)) >
                    TIME_BTWN_SAME_LOC;
        }
    }

    /**
     * getter to get the latitude
     * @return double
     */
    public double getLatitude() {
        if (currLoc != null) {
            latitude = currLoc.getLatitude();
        }
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (currLoc != null) {
            longitude = currLoc.getLongitude();
        }
        return longitude;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
