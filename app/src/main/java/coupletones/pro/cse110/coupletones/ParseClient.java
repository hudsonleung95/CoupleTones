package coupletones.pro.cse110.coupletones;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * The ParseClient is used as an alternative to GoogleCloudMessaging in order to send push
 * notifications to the partner when the user visits a favorite location
 */
public class ParseClient
{
    private DataStorage data;
    private Context context;
    private ProgressDialog progressDialog;
    private Date threeAM;

    public ParseClient(Context context){
        this.context = context;

        //Get data from shared preferences
        data = new DataStorage(context);

        progressDialog = new ProgressDialog(context);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        threeAM = cal.getTime();
    }

    /**
     * Call this method when a user visit his/her fav
     *
     * @param msg
     *      msg to be sent,
     *      in our case, we send the visited location
     */
    public void sendNotification(String msg){
        //target to partner's installation id
        ParseQuery query = ParseInstallation.getQuery();
        query.whereEqualTo(context.getText(R.string.parse_key_userid).toString(),
                data.getPartnerId());
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(msg);
        push.sendInBackground();
    }

    /**
     * This method checks if the input id is a valid one
     *
     * @param id
     */
    public void checkId(final String id){
        //show a progress dialog when checking the id
        progressDialog.setMessage(context.getText(R.string.pc_checking).toString());
        progressDialog.show();
        ParseQuery<ParseObject> query =
                ParseQuery.getQuery(context.getText(R.string.parse_key_table_user).toString());
//        query.whereEqualTo(context.getText(R.string.parse_key_installid).toString(), id);
        query.whereEqualTo(context.getText(R.string.parse_key_userid).toString(), id);
//        query.whereEqualTo("userId", id);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resultList, ParseException e) {
                //close the progress dialog when done
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                //if exist
                if (resultList != null){
                    Log.d("SIZE :", ""+resultList.size());
                    if (resultList.size() > 0){
                        data.setPartnerId(id); //save partner id
                        context.startActivity(new Intent(context, HistoryActivity.class));
                    }else{
                        //Display a toast if invalid id
                        Toast.makeText(context,
                                context.getText(R.string.add_warn_id_notfound).toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * this method push a history whenever a user
     * visited his/her favorite to the parse DB
     *
     * @param name
     * @param location
     */
    public void pushHistory(String name, Location location){
        ParseObject parseObject
                = new ParseObject(context.getText(R.string.parse_key_table_history).toString());

        parseObject.put(context.getText(R.string.parse_key_locName).toString(), name);
        parseObject.put(context.getText(R.string.parse_key_latlng).toString(),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
        parseObject.put(context.getText(R.string.parse_key_userid).toString(),
                data.getSelfId());
        parseObject.saveInBackground();
    }

    public void pushFavLoc(Marker marker){
        ParseObject parseObject
                = new ParseObject(context.getText(R.string.parse_key_table_fav).toString());

        parseObject.put(context.getText(R.string.parse_key_locName).toString(),
                                marker.getTitle());
        parseObject.put(context.getText(R.string.parse_key_latlng).toString(),
                new ParseGeoPoint(marker.getPosition().latitude, marker.getPosition().longitude));
        parseObject.put(context.getText(R.string.parse_key_userid).toString(),
                data.getSelfId());
        parseObject.saveInBackground();
    }

    public void pushTone(final String tone, LatLng latLng, final boolean isArrival){
        ParseQuery query
                = new ParseQuery(context.getText(R.string.parse_key_table_fav).toString());

        query.whereEqualTo(context.getText(R.string.parse_key_userid).toString(),
                data.getPartnerId());
        query.whereEqualTo(context.getText(R.string.parse_key_latlng).toString(),
                new ParseGeoPoint(latLng.latitude, latLng.longitude));

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> matches, ParseException e) {

                //if have visited any of the fav
                if (matches != null) {
                    if (matches.size() == 1) {
                        ParseObject result = matches.get(0);
                        if(isArrival)
                            result.put(context.getText(R.string.parse_key_tone_arv).toString(),
                                    tone);
                        else
                            result.put(context.getText(R.string.parse_key_tone_dpt).toString(),
                                    tone);
                        result.saveInBackground();
                    }else{
                        Toast.makeText(context,
                                context.getText(R.string.pc_warn_updateTone).toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * this method pull partner's log from the parse DB
     *
     * then if the partner has visited one or more of his/her fav
     * and if the caller is HistoryActivity, update the log list
     *
     */
    public void pullPartnerHistory(){
        final ArrayList<HashMap<String, String>> history = new ArrayList<>();

        progressDialog.setMessage(context.getText(R.string.pc_download).toString());
        progressDialog.show();

        ParseQuery<ParseObject> query =
                ParseQuery.getQuery(context.getText(R.string.parse_key_table_history).toString());

        query.whereEqualTo(context.getText(R.string.parse_key_userid).toString(),
                data.getPartnerId());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> historyList, ParseException e) {
                //close the progress dialog when done
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                //if have visited any of the fav
                if (historyList != null){
                    if (historyList.size() > 0){
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        for(ParseObject temp: historyList){
                            HashMap<String, String> newHist = new HashMap<String, String>();

                            String time = timeFormat.format(temp.getCreatedAt());

                            newHist.put(context.getText(R.string.parse_key_locName).toString()
                                    , (String)temp.get(context.getText(R.string.parse_key_locName).toString()));

                            newHist.put(context.getText(R.string.parse_key_date).toString(), time);

                            history.add(newHist);
                        }

                        if (context instanceof HistoryActivity){
                            ((HistoryActivity) context).updateList(history);
                        }

                    }else{
                        //Display a toast if invalid id
                        Toast.makeText(context,
                                context.getText(R.string.hist_empty).toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void pullPartnerFav(){
        final ArrayList<HashMap<String, String>> favs = new ArrayList<>();
        if(context instanceof ShowListActivity){
            progressDialog.setMessage(context.getText(R.string.pc_download_fav).toString());
            progressDialog.show();
        }

        ParseQuery<ParseObject> query =
                ParseQuery.getQuery(context.getText(R.string.parse_key_table_fav).toString());

        query.whereEqualTo(context.getText(R.string.parse_key_userid).toString(),
                data.getPartnerId());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> favList, ParseException e) {
                //close the progress dialog when done
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                //if have visited any of the fav
                if (favList != null){
                    if (favList.size() > 0){
                        for(ParseObject temp: favList){
                            HashMap<String, String> tempFav = new HashMap<String, String>();

                            tempFav.put(context.getText(R.string.parse_key_locName).toString()
                                    , (String)temp.get(context.getText(R.string.parse_key_locName).toString()));


                            favs.add(tempFav);
                        }
                        Log.d("FAV : ", "fav added : " + favs.size());

                        if (context instanceof ShowListActivity){

                        }

                    }else{
                        //Display a toast if invalid id
                        Toast.makeText(context,
                                context.getText(R.string.pc_empty_fav).toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void saveUserId(final String username){
        ParseQuery<ParseObject> query =
                ParseQuery.getQuery(context.getText(R.string.parse_key_table_install).toString());
        query.whereEqualTo(context.getText(R.string.parse_key_installid).toString(), data.getSelfInstallId());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resultList, ParseException e) {

                //if exist
                if (resultList != null){
                    if (resultList.size() == 1){
                        ParseObject result = resultList.get(0);
                        result.put(context.getText(R.string.parse_key_userid).toString(),
                                        username);
                        result.saveInBackground();
                    }else{
                        //Display a toast if invalid id
                        Toast.makeText(context,
                                context.getText(R.string.add_warn_id_notfound).toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
