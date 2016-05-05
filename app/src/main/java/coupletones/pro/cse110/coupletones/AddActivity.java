package coupletones.pro.cse110.coupletones;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import android.support.design.widget.Snackbar;
import com.google.android.gms.common.api.ResultCallback;


public class AddActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener
{
    private static final String TAG = AddActivity.class.getSimpleName();
    private TextView tv_ur_email;
    private static final int REQUEST_INVITE = 0;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add);
        tv_ur_email = (TextView)findViewById(R.id.add_tv_id);
        setEmail();
        findViewById(R.id.invite_button).setOnClickListener(this);



        // Create an auto-managed GoogleApiClient with acccess to App Invites.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                                // Because autoLaunchDeepLink = true we don't have to do anything
                                // here, but we could set that to false and manually choose
                                // an Activity to launch to handle the deep link here.
                            }
                        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        showMessage(getString(R.string.google_play_services_error));
    }

    /**
     * this method will be called when user press the add button
     * this method should be called automatically when the user
     * already has a partner added
     *
     * TODO: get information from SharedPreference and do the check
     * run the check when opening this app, and performClick if
     * already have a partner
     *
     * @param v
     *      view that call this method
     */
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invite_button:
                onInviteClicked();
                break;
            case R.id.enter_App:
                break;
        }

    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation
                .IntentBuilder(getString(R.string.invitation_title))
                /*.setMessage(getString(R.string.invitation_message))
                //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                //.setEmailSubject(getString(R.string.invitation_subject))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();*/
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setEmailHtmlContent("<html><body>" +
                        "<h1>App Invites</h1>" +
                        "<a href=\"%%APPINVITE_LINK_PLACEHOLDER%%\">Install Now!</a>" +
                        "<body></html>")
                .setEmailSubject(getString(R.string.invitation_subject))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
            } else {
                // Sending failed or it was canceled, show failure message to the user
                showMessage(getString(R.string.send_failed));
            }
        }
    }
    private void showMessage(String msg) {
        ViewGroup container = (ViewGroup) findViewById(R.id.snackbar_layout);
        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
    }

}
