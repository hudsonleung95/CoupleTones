package coupletones.pro.cse110.coupletones;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The AddActivity allows user to add a partner's RegID to pair.
 */
public class AddActivity extends AppCompatActivity
{
    private static final int LOGIN_REQUEST = 0;

    private static final String TAG = AddActivity.class.getSimpleName();
    private TextView tv_ur_email;
    private EditText et_self_id;
    private EditText et_input_id;
    private DataStorage dataStorage;
    private ParseClient parseClient;
    private ParseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*ParseLoginBuilder builder = new ParseLoginBuilder(AddActivity.this);
        startActivityForResult(builder.build(), 0);*/

        setContentView(R.layout.layout_add);
        init();
        Bundle extras = getIntent().getExtras();

        if (dataStorage.getSelfId() == null) {
            ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                    AddActivity.this);
            startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
//            startActivity(new Intent(AddActivity.this, HistoryActivity.class));
        }


        if(extras != null && extras.getBoolean("CHANGE_PARTNER")) {
            TextView title = (TextView) findViewById(R.id.add_tv_logo);
            title.setText("Change Your Partner");
        }
        else{
            if (dataStorage.getFirstTime()) {
                startActivity(new Intent(AddActivity.this, WelcomeActivity.class));
                dataStorage.setFirstTime(false);
            } else {
                startActivity(new Intent(AddActivity.this, HistoryActivity.class));
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        currentUser = ParseUser.getCurrentUser();

        if (dataStorage.getSelfId() == null && currentUser != null) {
            parseClient.saveUserId(currentUser.getUsername());
            dataStorage.setSelfId(currentUser.getUsername());
        }
    }


    /**
     * this method will be called when first open the app.
     * Initialize the data
     */
    private void init(){
        et_self_id = (EditText)findViewById(R.id.add_et_selfid);
        et_input_id = (EditText)findViewById(R.id.add_et_input);
        dataStorage = new DataStorage(this);
        parseClient = new ParseClient(this);
//        et_self_id.setText(dataStorage.getSelfId());
        if (dataStorage.getSelfId() != null)
            et_self_id.setText(dataStorage.getSelfId());
//        Log.d("PARSE ID : ", dataStorage.getSelfId());

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
        Log.d("ENTERED USERID :", inputId);

//        inputId = "a468a6e2-1def-4f69-9876-fe9a535adad4"; //TEST: MAC
        //if empty input, still able to use the app
        if (inputId.length() == 0){
            dataStorage.setPartnerId("");
            startActivity(new Intent(this, HistoryActivity.class));
        }else
            parseClient.checkId(inputId);
    }

    public DataStorage getDS() {
        return this.dataStorage;
    }
}
