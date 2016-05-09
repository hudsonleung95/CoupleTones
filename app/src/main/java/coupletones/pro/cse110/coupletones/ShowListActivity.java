package coupletones.pro.cse110.coupletones;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowListActivity extends AppCompatActivity
        implements EditLocationDialog.LocationDialogListener {

    private DataStorage dataStorage;
    private List<LatLng> latLngs;
    private List<String> locationNames;
    private ArrayAdapter<String> adapter;
    private ListView listview;
    private int indexOf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_show_list);

        listview = (ListView) findViewById(R.id.listView);

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
                        DialogFragment dialog = new EditLocationDialog();
                        dialog.show(getFragmentManager(), getText(R.string.edit_location).toString());

                    }
                });
            }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String loc_name = ((EditLocationDialog)dialog).getNameFromUser();
        //if user enters nothing
        if ( loc_name.length() != 0){
            locationNames.set(indexOf, loc_name);
            String namesList = new Gson().toJson(locationNames);
            dataStorage.setLocNameList(namesList);
            updateList();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private void updateList(){
        String favLatLngFromJson = dataStorage.getLatLngList();
        String favNamesFromJson = dataStorage.getLocNameList();

        if(favNamesFromJson != null && favLatLngFromJson != null) {
            String[] favNames = new Gson().fromJson(favNamesFromJson, String[].class);
            LatLng[] favLatLng = new Gson().fromJson(favLatLngFromJson, LatLng[].class);

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
