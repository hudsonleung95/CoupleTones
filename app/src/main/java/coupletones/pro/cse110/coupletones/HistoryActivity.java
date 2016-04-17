package coupletones.pro.cse110.coupletones;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class HistoryActivity extends AppCompatActivity
{
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_history);

        list = (ListView)findViewById(R.id.hist_list);

        list.setEmptyView(findViewById(R.id.hist_tv_empty));
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
        //when press the map button, show map
        if(item.getItemId() == R.id.hist_tomap)
            startActivity(new Intent(this, MapsActivity.class));

        return super.onOptionsItemSelected(item);
    }


}
