package coupletones.pro.cse110.coupletones;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

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
    private boolean changePartner;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_add);
        init();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume(){
        super.onResume();
        checkLoginStatus();

        if (dataStorage.getSelfId() == null && ParseUser.getCurrentUser() != null) {
            parseClient.saveUserId(ParseUser.getCurrentUser().getUsername());
            dataStorage.setSelfId(ParseUser.getCurrentUser().getUsername());
        }




        Bundle extras = getIntent().getExtras();
        currentUser = ParseUser.getCurrentUser();


        if(extras != null && extras.getBoolean("CHANGE_PARTNER")) {
            TextView title = (TextView) findViewById(R.id.add_tv_logo);
            title.setText("Change Your Partner");
            changePartner = true;
        }

        else{
            if (dataStorage.getFirstTime()) {
                dataStorage.setFirstTime(false);
            }
        }

        if (dataStorage.getSelfId() != null)
            et_self_id.setText(dataStorage.getSelfId());

        //already login AND already added a partner AND not wanting to change partner
        if(dataStorage.getSelfId() != null && dataStorage.getPartnerId() != null && !changePartner){
            startActivity(new Intent(this, HistoryActivity.class));
        }
    }


    /**
     * this method will be called when first open the app.
     * Initialize the data
     */
    private void init(){
        changePartner = false;
        et_self_id = (EditText)findViewById(R.id.add_et_selfid);
        et_input_id = (EditText)findViewById(R.id.add_et_input);
        dataStorage = new DataStorage(this);
        parseClient = new ParseClient(this);
//        et_self_id.setText(dataStorage.getSelfId());

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

    public void setSelfId(String selfId) {
        et_self_id.setText(selfId);
    }

    public void setPartnerId(String partnerId) {
        et_input_id.setText(partnerId);
    }

    private void checkLoginStatus(){
//        Log.d("MY ID :", ParseUser.getCurrentUser().toString());
        if (dataStorage.getSelfId() == null && ParseUser.getCurrentUser()== null) {
            ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                    AddActivity.this);
            startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
        }
    }
}
