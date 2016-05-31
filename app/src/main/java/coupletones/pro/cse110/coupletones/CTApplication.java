package coupletones.pro.cse110.coupletones;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * this class initialize Parse for the app
 */
public class CTApplication extends Application
{
    private static CTApplication sInstance;

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized CTApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this,
                getResources().getString(R.string.parse_app_id),
                getResources().getString(R.string.parse_client_key));


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.saveInBackground();

        new DataStorage(this).setSelfInstallId(installation.getInstallationId());

        // initialize the singleton
        sInstance = this;

    }
}
