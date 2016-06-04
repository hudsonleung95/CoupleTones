package coupletones.pro.cse110.coupletones;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * this is the centralized data storage with SharedPreferences
 * Contains all the getters and setters to access and change the data
 */
public class DataStorage
{
    Context context;
    SharedPreferences partner;
    SharedPreferences locations;
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    /**
     * create sharedPreferences to store the data
     * @param context
     */
    public DataStorage(Context context){
        this.context = context;
        partner = context.getSharedPreferences(context.getText(R.string.sp_key_parse_ids).toString(),
                Context.MODE_PRIVATE);
        locations = context.getSharedPreferences(context.getText(R.string.sp_key_location).toString(),
                Context.MODE_PRIVATE);
        setting = context.getSharedPreferences(context.getText(R.string.sp_key_setting).toString(),
                Context.MODE_PRIVATE);
    }

    //Getter to get the location's latLng
    //Return String
    public String getLatLngList(){
        return locations.getString(context.getText(R.string.sp_key_list_latlng).toString(),
                null);
    }

    //Getter to get the user's location's names
    public String getLocNameList(){
        return locations.getString(context.getText(R.string.sp_key_list_locname).toString(),
                null);
    }

    //Getter to get the user's partner's ID
    public String getPartnerId(){
        return partner.getString(context.getText(R.string.sp_key_item_partnerid).toString(),
                null);
    }

    //Setter to set the partner's ID
    public void setPartnerId(String id){
        editor = partner.edit();
        editor.putString(context.getText(R.string.sp_key_item_partnerid).toString(), id);
        editor.commit();
    }

    //Getter to get the user's ID
    //Return: String
    public String getSelfInstallId(){
        return partner.getString(context.getText(R.string.sp_key_item_selfinstallid).toString(),
                null);
    }

    /**
     * Set the self Id
     * @param id
     */
    public void setSelfInstallId(String id){
        editor = partner.edit();
        editor.putString(context.getText(R.string.sp_key_item_selfinstallid).toString(), id);
        editor.commit();
    }

    /**
     * get the person's self ID
     * @return String
     */
    public String getSelfId(){
        return partner.getString(context.getText(R.string.sp_key_item_selfuserid).toString(),
                null);
    }

    /**
     * Set the ID
     * @param id
     */
    public void setSelfId(String id){
        editor = partner.edit();
        editor.putString(context.getText(R.string.sp_key_item_selfuserid).toString(), id);
        editor.commit();
    }

    /**
     * Set the Locations' LatLng information
     * @param list
     */
    public void setLatLngList(String list){
        editor = locations.edit();
        editor.putString(context.getText(R.string.sp_key_list_latlng).toString(), list);
        editor.commit();
    }

    /**
     * Set the Location names to the list
     * @param list
     */
    public void setLocNameList(String list){
        editor = locations.edit();
        editor.putString(context.getText(R.string.sp_key_list_locname).toString(), list);
        editor.commit();
    }

    /**
     * Get the first time arriving the location
     * @return
     */
    public boolean getFirstTime(){
        return setting.getBoolean(context.getText(R.string.sp_key_item_first_time).toString(),
                true);
    }

    /**
     * Set the first time arriving the location
     * @param firstTime
     */
    public void setFirstTime(boolean firstTime){
        editor = setting.edit();
        editor.putBoolean(context.getText(R.string.sp_key_item_first_time).toString(),
                firstTime);
        editor.commit();
    }

    /**
     * Set Partner's information of favorite locations
     * @param latLngs
     */
    public void setPartnerLatLngList(ArrayList<LatLng> latLngs){

        editor = locations.edit();
        editor.putString(context.getText(R.string.sp_key_list_partner_latlng).toString()
                , new Gson().toJson(latLngs));
        editor.commit();
    }

    /**
     * Set the all the names of the partner's favorite locations
     * @param locNames
     */
    public void setPartnerLocNameList(ArrayList<String> locNames){
        editor = locations.edit();
        editor.putString(context.getText(R.string.sp_key_list_partner_locname).toString()
                , new Gson().toJson(locNames));
        editor.commit();
    }

    /**
     * Get the partner's list of locations
     * @return ArrayList
     */
    public ArrayList<LatLng> getPartnerLatLngList(){
        String favLatLngFromJson = locations.getString(
                context.getText(R.string.sp_key_list_partner_latlng).toString(),
                null);
        if(favLatLngFromJson != null){
            LatLng[] favLatLng = new Gson().fromJson(favLatLngFromJson, LatLng[].class);
            return new ArrayList<LatLng>(Arrays.asList(favLatLng));
        }

        return null;
    }

    /**
     * Get the partner's locations' names
     * @return ArrayList
     */
    public ArrayList<String> getPartnerLocNameList(){
        String favLatLngFromJson = locations.getString(
                context.getText(R.string.sp_key_list_partner_locname).toString(),
                null);
        if(favLatLngFromJson != null){
            String[] favLocNames = new Gson().fromJson(favLatLngFromJson, String[].class);
            Log.d("test_viewPartnerHistory", "IngetPartnerLocNameList");
            return new ArrayList<String>(Arrays.asList(favLocNames));
        }

        return null;
    }

    public ArrayList<LatLng> getSelfLatLngList(){
        String favLatLngFromJson = locations.getString(
                context.getText(R.string.sp_key_list_latlng).toString(),
                null);
        if(favLatLngFromJson != null){
            LatLng[] favLatLng = new Gson().fromJson(favLatLngFromJson, LatLng[].class);
            return new ArrayList<LatLng>(Arrays.asList(favLatLng));
        }

        return null;
    }

    public ArrayList<String> getSelfLocNameList(){
        String favLatLngFromJson = locations.getString(
                context.getText(R.string.sp_key_list_locname).toString(),
                null);
        if(favLatLngFromJson != null){
            String[] favLocNames = new Gson().fromJson(favLatLngFromJson, String[].class);
            return new ArrayList<String>(Arrays.asList(favLocNames));
        }

        return null;
    }

}
