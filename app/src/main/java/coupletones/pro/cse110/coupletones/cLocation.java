package coupletones.pro.cse110.coupletones;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

/**
 * Created by HUDSON on 5/4/2016.
 */
public class cLocation
{
    Context context;
    LatLng latLng;
    String name;

    public cLocation(LatLng latLng, Context context){
        this.latLng = latLng;
        this.context = context;
        this.name = getDefaultName();
    }

    public cLocation(Place place){
        this.latLng = place.getLatLng();
        this.name = place.getName().toString();
    }

    public LatLng getLatLng(){
        return latLng;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }


    /**
     * this method tries to get a name/address from the latLng
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
