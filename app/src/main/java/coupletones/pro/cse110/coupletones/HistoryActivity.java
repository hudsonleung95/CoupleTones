package coupletones.pro.cse110.coupletones;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The class that shows the history locations of the user visited.
 * Also the class provides the sidebar options for users
 */
public class HistoryActivity extends AppCompatActivity
{
    private DataStorage dataStorage;
    private ParseClient parseClient;
    ListView list;
    private DrawerLayout settingsDrawer;
    private ListView settingsList;
    private ArrayAdapter<String> settingsAdapter;
    private ActionBarDrawerToggle settingsToggle;
    private String activityTitle;
    private ArrayList<HashMap<String, String>> history;

    /**
     * When the activity started, shows the user's partner visited favorite locations.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_history);
        dataStorage = new DataStorage(this);
        parseClient = new ParseClient(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        list = (ListView)findViewById(R.id.hist_list);
        list.setEmptyView(findViewById(R.id.hist_tv_empty));

        settingsDrawer = (DrawerLayout) findViewById(R.id.drawer_settings_layout);
        settingsList = (ListView) findViewById(R.id.settings_list);

        // creates the sidebar and shows the options
        String[] settingsArr = { "Partner Settings", "My Fav Location List",
                                    "Partner's Fav Loc List", "Help", "Log Out" };
        settingsAdapter = new ArrayAdapter<String>(this, R.layout.layout_list_item, settingsArr);
        settingsList.setAdapter(settingsAdapter);

        //switch through the sidebar options
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        Intent addIntent = new Intent(HistoryActivity.this, AddActivity.class);
                        addIntent.putExtra("CHANGE_PARTNER", true);
                        startActivity(addIntent);
                        break;
                    case 1:
                        Intent showListIntent = new Intent(HistoryActivity.this, ShowListActivity.class);
                        startActivity(showListIntent);
                        break;
                    case 2:
                        Intent partnerListIntent = new Intent(HistoryActivity.this,
                                                                PartnerListActivity.class);
                        startActivity(partnerListIntent);
                        break;
                    case 3:
                        Intent introIntent = new Intent(HistoryActivity.this, WelcomeActivity.class);
                        startActivity(introIntent);
                        break;
                    case 4:
                        Intent logOutIntent = new Intent(HistoryActivity.this, Logout.class);
                        startActivity(logOutIntent);
                        break;
                }
            }
        });
        activityTitle = "CoupleTones";

        settingsToggle = new ActionBarDrawerToggle(this, settingsDrawer,R.string.drawer_open, R.string.drawer_close){

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(activityTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                getSupportActionBar().setTitle("Settings");
                invalidateOptionsMenu();
            }
        };
        settingsToggle.setDrawerIndicatorEnabled(true);
        settingsDrawer.addDrawerListener(settingsToggle);

        parseClient.pullPartnerHistory();

        //Keep the current location running through the background to make sure to receive the
        //notifications
        startService(new Intent(HistoryActivity.this, CurrentLocationTracker.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_hist, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (settingsToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //when press the map button, show map
        if(item.getItemId() == R.id.hist_tomap)
            startActivity(new Intent(this, MapsActivity.class));
        else if(item.getItemId() == R.id.hist_refresh)
            parseClient.pullPartnerHistory();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync toggle state after onRestoreInstanceState has occurred.
        settingsToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        settingsToggle.onConfigurationChanged(newConfig);
    }

    /**
     * this method will update the history list
     * call in parseClient after downloading all related history
     *
     * @param history
     *      data of the history, including Name and time
     */
    public void updateList(ArrayList<HashMap<String, String>> history){
        this.history = history;

        if(history.size() > 1){
            SimpleAdapter adapter = new SimpleAdapter(this, history,
                    android.R.layout.simple_list_item_2,
                    new String[] {getText(R.string.parse_key_locName).toString()
                            , getText(R.string.parse_key_date).toString()},
                    new int[] {android.R.id.text1, android.R.id.text2});

            list.setAdapter(adapter);
        }
    }


}
