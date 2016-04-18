package coupletones.pro.cse110.coupletones;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class AddActivity extends AppCompatActivity
{
    private TextView tv_ur_email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add);
        tv_ur_email = (TextView)findViewById(R.id.add_tv_id);
        setEmail();
    }

    //this method will be called when user press the add button
    public void addPartner(View v){
        //TODO check id pairing status

        //go to the hist
        startActivity(new Intent(this, HistoryActivity.class));
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
        Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
        if(accounts.length > 0)
            email = accounts[0].name;

        return email;
    }
}
