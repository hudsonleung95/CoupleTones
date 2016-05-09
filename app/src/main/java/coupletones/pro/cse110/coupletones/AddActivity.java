package coupletones.pro.cse110.coupletones;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddActivity extends AppCompatActivity
{
    private static final String TAG = AddActivity.class.getSimpleName();
    private TextView tv_ur_email;
    private EditText et_self_id;
    private EditText et_input_id;
    private DataStorage dataStorage;
    private ParseClient parseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add);
        init();

        if(dataStorage.getFirstTime()){
            startActivity(new Intent(AddActivity.this, WelcomeActivity.class));
            dataStorage.setFirstTime(false);
        }
        else
        {
            startActivity(new Intent(AddActivity.this, HistoryActivity.class));
        }

    }

    private void init(){
        et_self_id = (EditText)findViewById(R.id.add_et_selfid);
        et_input_id = (EditText)findViewById(R.id.add_et_input);
        dataStorage = new DataStorage(this);
        parseClient = new ParseClient(this);
        et_self_id.setText(dataStorage.getSelfId());
        Log.d("PARSE ID : ", dataStorage.getSelfId());

        if(dataStorage.getPartnerId() != null)
            et_input_id.setText(dataStorage.getPartnerId());
    }

    /**
     * this method will be called when user press the "GO TO APP" button
     * first it checks if user has input any id in the edittext field
     *
     * then, it calls the checkId method to check if the id exist
     *
     * @param v
     *      view that call this method
     */
    public void addPartner(View v){
        String inputId = et_input_id.getText().toString();
        parseClient.checkId(inputId);
    }
}
