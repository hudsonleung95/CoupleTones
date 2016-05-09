package coupletones.pro.cse110.coupletones;

import android.app.ListActivity;
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

public class HistoryActivity extends AppCompatActivity
{
    private DataStorage dataStorage;
    ListView list;
    private DrawerLayout settingsDrawer;
    private ListView settingsList;
    private ArrayAdapter<String> settingsAdapter;
    private ActionBarDrawerToggle settingsToggle;
    private String activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_history);
        dataStorage = new DataStorage(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        list = (ListView)findViewById(R.id.hist_list);
        list.setEmptyView(findViewById(R.id.hist_tv_empty));

        settingsDrawer = (DrawerLayout) findViewById(R.id.drawer_settings_layout);
        settingsList = (ListView) findViewById(R.id.settings_list);

        String[] settingsArr = { "Partner Settings", "Location List Settings", "Help" };
        settingsAdapter = new ArrayAdapter<String>(this, R.layout.layout_list_item, settingsArr);
        settingsList.setAdapter(settingsAdapter);
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        dataStorage.setFirstTime(true);
                        Intent addIntent = new Intent(HistoryActivity.this, AddActivity.class);
                        startActivity(addIntent);
                        break;
                    case 1:
                        Intent showListIntent = new Intent(HistoryActivity.this, ShowListActivity.class);
                        startActivity(showListIntent);
                        break;
                    case 2:
                        Intent introIntent = new Intent(HistoryActivity.this, WelcomeActivity.class);
                        startActivity(introIntent);
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

}
