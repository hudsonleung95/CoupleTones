package coupletones.pro.cse110.coupletones;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

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

    public ParseClient(Context context){
        this.context = context;

        //Get data from shared preferences
        data = new DataStorage(context);

        progressDialog = new ProgressDialog(context);
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
        query.whereEqualTo(context.getText(R.string.parse_key_installid).toString(),
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
                ParseQuery.getQuery(context.getText(R.string.parse_key_table_install).toString());
        query.whereEqualTo(context.getText(R.string.parse_key_installid).toString(), id);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> commentList, ParseException e) {
                //close the progress dialog when done
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                //if exist
                if (commentList != null){
                    if (commentList.size() > 0){
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
}
