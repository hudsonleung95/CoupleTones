package coupletones.pro.cse110.coupletones;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The ShowListActivity allows the user to see a list of the current saved locations when
 * the "Location Settings" is pressed
 */

public class ShowListActivity extends AppCompatActivity
        implements EditLocationDialog.LocationDialogListener {

    private DataStorage dataStorage;
    private List<LatLng> latLngs;
    private List<String> locationNames;
    private ArrayAdapter<String> adapter;
    private ListView listview;
    private int indexOf;
    private String chosenTone;
    private static final int REQUEST_PICK_TONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_show_list);

        listview = (ListView) findViewById(R.id.listView);

        //Initialize variables
        dataStorage = new DataStorage(this);
        latLngs = new ArrayList<LatLng>();
        locationNames = new ArrayList<String>();

        updateList();

            if (listview != null) {
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        indexOf = position;
                        LayoutInflater inflater = getLayoutInflater();
                        View dialoglayout = inflater.inflate(R.layout.layout_dialog_partner_location_settings,
                                null);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowListActivity.this);
                        builder.setCancelable(true);
                        builder.setView(dialoglayout);
                        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();

                        //Get location clicked by using the position as index in ArrayList
//                        indexOf = position;
//                        DialogFragment dialog = new EditLocationDialog();
//                        dialog.show(getFragmentManager(), getText(R.string.edit_location).toString());

                    }
                });
            }
    }

    public void showOnMap(View view){

    }

    public void chooseNotification(View view){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        startActivityForResult(intent, REQUEST_PICK_TONE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_TONE)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                this.chosenTone = uri.toString();
            }
            else
            {
                Uri defaultTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                this.chosenTone = defaultTone.toString();
            }
        }
    }

    /**
     * Save the edited name i shared preferences when "Save" is pressed
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String loc_name = ((EditLocationDialog)dialog).getNameFromUser();

        //Save updated name in shared preferences
        if ( loc_name.length() != 0){
            locationNames.set(indexOf, loc_name);
            String namesList = new Gson().toJson(locationNames);
            dataStorage.setLocNameList(namesList);
            updateList();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Dont do anything when cancel is pressed
    }

    /**
     * Get the current saved locations from shared preferences and display on the list
     */
    private void updateList(){

        //Get the current saved locations from Shared Preferences
        String favLatLngFromJson = dataStorage.getLatLngList();
        String favNamesFromJson = dataStorage.getLocNameList();

        if(favNamesFromJson != null && favLatLngFromJson != null) {
            String[] favNames = new Gson().fromJson(favNamesFromJson, String[].class);
            LatLng[] favLatLng = new Gson().fromJson(favLatLngFromJson, LatLng[].class);

            //Convert from json to the actual ArrayLists
            latLngs = Arrays.asList(favLatLng);
            latLngs = new ArrayList<LatLng>(latLngs);
            locationNames = Arrays.asList(favNames);
            locationNames = new ArrayList<String>(locationNames);

            adapter = new ArrayAdapter(this,
                    R.layout.list_item_mylocation,
                    R.id.list_item_mylocation_textview, locationNames);

            if (listview != null) {
                listview.setAdapter(adapter);
            }
        }
    }
}
