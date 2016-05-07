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

import com.google.android.gms.common.api.GoogleApiClient;


public class AddActivity extends AppCompatActivity
{
    private static final String TAG = AddActivity.class.getSimpleName();
    private TextView tv_ur_email;
    private EditText et_self_id;
    private EditText et_input_id;
    private DataStorage dataStorage;
    private ParseClient parseClient;
    private static final int REQUEST_INVITE = 0;
    private GoogleApiClient mGoogleApiClient;
    private static final String USER_SETTINGS = "USER_SETTINGS";

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

//        // Create an auto-managed GoogleApiClient with acccess to App Invites.
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(AppInvite.API)
//                .enableAutoManage(this, this)
//                .build();
//
//        // Check for App Invite invitations and launch deep-link activity if possible.
//        // Requires that an Activity is registered in AndroidManifest.xml to handle
//        // deep-link URLs.
//        boolean autoLaunchDeepLink = true;
//        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
//                .setResultCallback(
//                        new ResultCallback<AppInviteInvitationResult>() {
//                            @Override
//                            public void onResult(AppInviteInvitationResult result) {
//                                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
//                                // Because autoLaunchDeepLink = true we don't have to do anything
//                                // here, but we could set that to false and manually choose
//                                // an Activity to launch to handle the deep link here.
//                            }
//                        });
//        Button goToApp = (Button) findViewById(R.id.enter_app_button);
//        assert goToApp != null;
//        goToApp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(AddActivity.this, HistoryActivity.class));
//            }
//        });
//
//        Button invite = (Button) findViewById(R.id.invite_button);
//        assert invite != null;
//        invite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onInviteClicked();
//            }
//        });
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
    }

//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed:" + connectionResult);
//        showMessage(getString(R.string.google_play_services_error));
//    }

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
      //  inputId = "31864be7-914f-4b71-bd33-f4e6cbe42704"; //TODO: del later, mac test id
      //  inputId = "8c35f813-024b-4660-9618-d6262ef3067e"; //TODO: del later, desktop test id
        parseClient.checkId(inputId);
    }

    /**
     * this method set the email TextView to user's first gmail
     *
     * if user has no account, show no email string in the view
     */
    private void setEmail(){
        String email = getEmail();
        TextView user_email = (TextView) findViewById(R.id.add_tv_id);

        if(email == null)
            user_email.setText(getText(R.string.add_no_email));
        else
            user_email.setText(email);
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

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.invite_button:
//                onInviteClicked();
//                break;
//            case R.id.enter_app_button:
//                break;
//        }
//
//    }

//    private void onInviteClicked() {
//        Intent intent = new AppInviteInvitation
//                .IntentBuilder(getString(R.string.invitation_title))
//                /*.setMessage(getString(R.string.invitation_message))
//                //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
//                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
//                //.setEmailSubject(getString(R.string.invitation_subject))
//                .setCallToActionText(getString(R.string.invitation_cta))
//                .build();*/
//                .setMessage(getString(R.string.invitation_message))
//                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
//                .setEmailHtmlContent("<html><body>" +
//                        "<h1>App Invites</h1>" +
//                        "<a href=\"%%APPINVITE_LINK_PLACEHOLDER%%\">Install Now!</a>" +
//                        "<body></html>")
//                .setEmailSubject(getString(R.string.invitation_subject))
//                .build();
//        startActivityForResult(intent, REQUEST_INVITE);
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
//
//        if (requestCode == REQUEST_INVITE) {
//            if (resultCode == RESULT_OK) {
//                // Check how many invitations were sent and log a message
//                // The ids array contains the unique invitation ids for each invitation sent
//                // (one for each contact select by the user). You can use these for analytics
//                // as the ID will be consistent on the sending and receiving devices.
//                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
//                Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
//            } else {
//                // Sending failed or it was canceled, show failure message to the user
//                showMessage(getString(R.string.send_failed));
//            }
//        }
//    }
//    private void showMessage(String msg) {
//        ViewGroup container = (ViewGroup) findViewById(R.id.snackbar_layout);
//        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
//    }

}
