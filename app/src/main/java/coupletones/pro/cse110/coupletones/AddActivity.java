package coupletones.pro.cse110.coupletones;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add);
    }

    //this method will be called when user press the add button
    public void addPartner(View v){
        //TODO check id pairing status

        //go to the hist
        startActivity(new Intent(this, HistoryActivity.class));
    }
}
