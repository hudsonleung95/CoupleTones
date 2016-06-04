package coupletones.pro.cse110.coupletones;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.parse.ParseUser;

//The class to allow user to logout the app
public class Logout extends AppCompatActivity {

    private TextView title;
    private TextView email;
    private TextView name;
    private Button logOut;

    private ParseUser currentUser;
    //create several instance variables


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set up
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.profile_title);
        email = (TextView) findViewById(R.id.profile_email);
        name = (TextView) findViewById(R.id.profile_name);
        logOut = (Button) findViewById(R.id.logout_button);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        //If user click on the logout button
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ParseUser.getCurrentUser() != null) {
                    ParseUser.logOut();
                    currentUser = null;
                    new DataStorage(getApplicationContext()).setSelfId(null);
                    new ParseClient(getApplicationContext()).saveUserId("");
            //update the datastorage and the parseclient to null

                    startActivity(new Intent(Logout.this, AddActivity.class));
                    //start the logout activity
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = ParseUser.getCurrentUser();
        email.setText(currentUser.getEmail());
        String fullName = currentUser.getString("name");
        //After getting the email and user name, performing log out
        if(fullName != null) {
            name.setText(fullName);
        }
        logOut.setText(R.string.profile_logout_button_label);

    }

}
