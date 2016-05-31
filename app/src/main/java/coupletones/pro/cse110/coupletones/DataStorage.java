package coupletones.pro.cse110.coupletones;

import android.content.Context;
import android.content.SharedPreferences;

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

    public String getLatLngList(){
        return locations.getString(context.getText(R.string.sp_key_list_latlng).toString(),
                null);
    }

    public String getLocNameList(){
        return locations.getString(context.getText(R.string.sp_key_list_locname).toString(),
                null);
    }

    public String getPartnerId(){
        return partner.getString(context.getText(R.string.sp_key_item_partnerid).toString(),
                null);
    }

    public void setPartnerId(String id){
        editor = partner.edit();
        editor.putString(context.getText(R.string.sp_key_item_partnerid).toString(), id);
        editor.commit();
    }

    public String getSelfInstallId(){
        return partner.getString(context.getText(R.string.sp_key_item_selfinstallid).toString(),
                null);
    }

    public void setSelfInstallId(String id){
        editor = partner.edit();
        editor.putString(context.getText(R.string.sp_key_item_selfinstallid).toString(), id);
        editor.commit();
    }

    public String getSelfId(){
        return partner.getString(context.getText(R.string.sp_key_item_selfuserid).toString(),
                null);
    }

    public void setSelfId(String id){
        editor = partner.edit();
        editor.putString(context.getText(R.string.sp_key_item_selfuserid).toString(), id);
        editor.commit();
    }

    public void setLatLngList(String list){
        editor = locations.edit();
        editor.putString(context.getText(R.string.sp_key_list_latlng).toString(), list);
        editor.commit();
    }

    public void setLocNameList(String list){
        editor = locations.edit();
        editor.putString(context.getText(R.string.sp_key_list_locname).toString(), list);
        editor.commit();
    }

    public boolean getFirstTime(){
        return setting.getBoolean(context.getText(R.string.sp_key_item_first_time).toString(),
                true);
    }

    public void setFirstTime(boolean firstTime){
        editor = setting.edit();
        editor.putBoolean(context.getText(R.string.sp_key_item_first_time).toString(),
                firstTime);
        editor.commit();
    }

}
