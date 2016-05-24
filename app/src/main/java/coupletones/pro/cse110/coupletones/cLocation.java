package coupletones.pro.cse110.coupletones;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

/**
 * The class that provides(holds) the data of the favorite location that user added.
 */
public class cLocation
{
    Context context;
    LatLng latLng;
    String name;

    /**
     * constructor
     * @param latLng
     * @param context
     */
    public cLocation(LatLng latLng, Context context){
        this.latLng = latLng;
        this.context = context;
        this.name = getDefaultName();
    }

    /**
     * constructor
     * @param place
     */
    public cLocation(Place place){
        this.latLng = place.getLatLng();
        this.name = place.getName().toString();
    }

    public cLocation(LatLng latLng, String name){
        this.latLng = latLng;
        this.name = name;
    }

    /**
     * Getters to get the location's latitude and longitude
     * @return LatLng
     */
    public LatLng getLatLng(){
        return latLng;
    }

    /**
     * Getter to get the name of the favorite location that user adds
     * @return String
     */
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }


    /**
     * this method tries to get a name/address from the locations
     *
     * @return
     *      if found, return given name
     *      else, return "Favorite cLocation"
     */
    private String getDefaultName(){
        try{
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0)
                return addresses.get(0).getAddressLine(0);
        }catch (Exception err){
            err.printStackTrace();

        }
        return context.getText(R.string.fav_loc).toString();
    }
}
