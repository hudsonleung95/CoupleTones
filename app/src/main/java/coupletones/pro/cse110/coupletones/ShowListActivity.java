package coupletones.pro.cse110.coupletones;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    RadioGroup radioGroup;

    //Custom vibration patterns, starts with delay (0) then alternates between vibrate and sleep
    //in milliseconds
    long [] vibe1 = {0, 500, 200, 500}; //two long vibrates
    long [] vibe2 = {0, 250, 100, 250}; //two short vibrates
    long [] vibe3 = {0, 350, 400, 350}; //two medium vibrates
    long [] vibe4 = {0, 300, 100, 300, 100, 300}; //three short vibrate
    long [] vibe5 = {0, 250, 250, 250}; //two short vibrates, medium pause between
    long [] vibe6 = {0, 250, 200, 500}; //one short vibrate, then long vibrate
    long [] vibe7 = {0, 500, 200, 250}; //one long vibrate, then short vibrate
    long [] vibe8 = {0, 250, 100, 250, 100, 500}; //two short vibrates, then long vibrate
    long [] vibe9 = {0, 500, 200, 250, 200, 250}; //one long vibrate, then two short vibrates
    long [] vibe10 = {0, 400, 200, 400, 250}; //two medium vibrates, one short vibrate
    long [][] vibeTones = {vibe1, vibe2, vibe3, vibe4, vibe5, vibe6, vibe7, vibe8, vibe9, vibe10};

    int [] audibleTones = {R.raw.coins, R.raw.spring, R.raw.gentle_alarm, R.raw.tweet, R.raw.ache,
            R.raw.chirp, R.raw.croak, R.raw.suppressed, R.raw.pedantic, R.raw.inquisitiveness};
    private boolean isArrivalTone;
    private ParseClient parseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_show_list);

        listview = (ListView) findViewById(R.id.listView);

        //Initialize variables
        dataStorage = new DataStorage(this);
        latLngs = new ArrayList<LatLng>();
        locationNames = new ArrayList<String>();
        parseClient = new ParseClient(this);
       // parseClient.pullPartnerFav();

        updateList();

        if (listview != null) {
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {

                    //Get location clicked by using the position as index in ArrayList
                    indexOf = position;
                    //DialogFragment dialog = new EditLocationDialog();
                    //dialog.show(getFragmentManager(), getText(R.string.edit_location).toString());
                    showOnMap();
                }
            });
        }

//        if (listview != null) {
//            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, final View view,
//                                        int position, long id) {
//                    indexOf = position;
//                    LayoutInflater inflater = getLayoutInflater();
//                    View dialoglayout = inflater.inflate(R.layout.layout_dialog_partner_location_settings,
//                            null);
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(ShowListActivity.this);
//                    builder.setCancelable(true);
//                    builder.setView(dialoglayout);
//                    builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//
//                    builder.show();
//
//                        //Get location clicked by using the position as index in ArrayList
////                        indexOf = position;
////                        DialogFragment dialog = new EditLocationDialog();
////                        dialog.show(getFragmentManager(), getText(R.string.edit_location).toString());
//
//                    }
//                });
//            }
    }

    public void showOnMap(){
        Intent mapsIntent = new Intent(ShowListActivity.this, MapsActivity.class);
        mapsIntent.putExtra("LOC_CLICKED", locationNames.get(indexOf));
        mapsIntent.putExtra("SHOW_My_LOCS", true);
        startActivity(mapsIntent);
    }

    public void chooseVibeNotification(View view){
        LayoutInflater inflater = getLayoutInflater();
        final View vibeToneLayout = inflater.inflate(R.layout.vibrate_notifications_list,
                null);
        radioGroup = (RadioGroup) vibeToneLayout.findViewById(R.id.vibe_list_radio_group);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowListActivity.this);
        builder.setView(vibeToneLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Save Vibe tone
                int vibePatternIndex = radioGroup.indexOfChild(vibeToneLayout.findViewById(radioGroup.getCheckedRadioButtonId()));

                //Add code to save chosen index

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(RadioGroup group, int checkedId) {
                  // checkedId is the RadioButton selected
                  Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                  int vibeIndex = radioGroup.indexOfChild(vibeToneLayout.findViewById(radioGroup.getCheckedRadioButtonId()));
                  v.vibrate(vibeTones[vibeIndex], -1);
              }
        });
        builder.show();
    }

    public void chooseAudibleNotification(View view){
        LayoutInflater inflater = getLayoutInflater();
        final View audibleToneLayout = inflater.inflate(R.layout.audible_notifications_list,
                null);
        radioGroup = (RadioGroup) audibleToneLayout.findViewById(R.id.audible_list_radio_group);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowListActivity.this);
        builder.setView(audibleToneLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Save Audible tone
                int audiblePatternIndex = radioGroup.indexOfChild(audibleToneLayout.findViewById
                        (radioGroup.getCheckedRadioButtonId()));

                //Add code to save chosen index

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int audibleIndex = radioGroup.indexOfChild(audibleToneLayout.findViewById(radioGroup.getCheckedRadioButtonId()));
                String audioPath = "android.resource://coupletones.pro.cse110.coupletones/" +
                        audibleTones[audibleIndex];
                RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(audioPath)).play();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_TONE)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                chosenTone = uri.toString();
                Log.d("TONE: ", this.chosenTone);

            }
            else
            {
                Uri defaultTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                chosenTone = defaultTone.toString();
            }
            parseClient.pushTone(chosenTone, latLngs.get(indexOf), isArrivalTone);
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
