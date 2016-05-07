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

public class ParseClient
{
    private DataStorage data;
    private Context context;
    private ProgressDialog progressDialog;

    public ParseClient(Context context){
        this.context = context;
        data = new DataStorage(context);
        progressDialog = new ProgressDialog(context);
    }

    /**
     * call this method when a user visit his/her fav
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
     * this method check if the input id is a valid one
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
                    data.setPartnerId(id); //save partner id
                    context.startActivity(new Intent(context, HistoryActivity.class));
                }else
                    Toast.makeText(context,
                            context.getText(R.string.add_warn_id_notfound).toString(),
                            Toast.LENGTH_SHORT).show();
            }
        });
    }
}
