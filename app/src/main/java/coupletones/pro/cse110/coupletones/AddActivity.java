package coupletones.pro.cse110.coupletones;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
        tv_ur_email = (TextView)findViewById(R.id.add_tv_id);
        et_self_id = (EditText)findViewById(R.id.add_et_selfid);
        et_input_id = (EditText)findViewById(R.id.add_et_input);
        dataStorage = new DataStorage(this);
        parseClient = new ParseClient(this);
        setEmail();
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
        if(inputId.length() == 0){
            Toast.makeText(this,
                    getText(R.string.add_warn_empty_input).toString(),
                    Toast.LENGTH_SHORT).show();

            return;
        }
        parseClient.checkId(inputId);
    }

    /**
     * this method set the email TextView to user's first gmail
     *
     * if user has no account, show no email string in the view
     */
    private void setEmail(){
        String email = getEmail();

        if(email == null)
            tv_ur_email.setText(getText(R.string.add_no_email));
        else
            tv_ur_email.setText(email);
    }

    /**
     * this method get the user's FIRST gmail account on device
     *
     * @return
     *      null - if user has no account on their device
     *      otherwise - first gmail account on device
     */
    private String getEmail(){
        String email = null;
        Account[] accounts = AccountManager.get(this)
                .getAccountsByType(getText(R.string.add_key_google).toString());
        if(accounts.length > 0)
            email = accounts[0].name;

        return email;
    }
}
