package coupletones.pro.cse110.coupletones;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The class to receive the notification and build the connection within server
 */
public class PushNotificationReceiver extends ParsePushBroadcastReceiver{
    private static int audIdx;
    private static int vibeIdx;
    private static Context context;
    private static boolean isArrival;
    static long [] vibe1 = {0, 500, 200, 500}; //two long vibrates
    static long [] vibe2 = {0, 250, 100, 250}; //two short vibrates
    static long [] vibe3 = {0, 350, 400, 350}; //two medium vibrates
    static long [] vibe4 = {0, 300, 100, 300, 100, 300}; //three short vibrate
    static long [] vibe5 = {0, 250, 250, 250}; //two short vibrates, medium pause between
    static long [] vibe6 = {0, 250, 200, 500}; //one short vibrate, then long vibrate
    static long [] vibe7 = {0, 500, 200, 250}; //one long vibrate, then short vibrate
    static long [] vibe8 = {0, 250, 100, 250, 100, 500}; //two short vibrates, then long vibrate
    static long [] vibe9 = {0, 500, 200, 250, 200, 250}; //one long vibrate, then two short vibrates
    static long [] vibe10 = {0, 400, 200, 400, 250}; //two medium vibrates, one short vibrate
    static long [][] vibeTones = {vibe1, vibe2, vibe3, vibe4, vibe5, vibe6, vibe7, vibe8, vibe9, vibe10};
    static int [] audibleTones = {R.raw.coins, R.raw.spring, R.raw.gentle_alarm, R.raw.tweet, R.raw.ache,
            R.raw.chirp, R.raw.croak, R.raw.suppressed, R.raw.pedantic, R.raw.inquisitiveness};

    /**
     * Set the push recerive
     * @param context
     * @param intent
     */
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //Send first notification with message and CoupleTones default sound
        super.onPushReceive(context, intent);
        this.context = context;

        //Send second notification with customized sound and vibration
        try {
            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            isArrival = data.getBoolean("isArrival");
            LatLng latLng = new LatLng(data.getDouble("loc_lat"), data.getDouble("loc_lng"));

            new ParseClient(context).pullToneIndex(latLng, isArrival);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the notification
     * @param context
     * @param intent
     * @return
     */
    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Notification notification = super.getNotification(context, intent);
//        notification.defaults &= ~(1 << Notification.DEFAULT_SOUND);
//        notification.defaults &= ~(1 << Notification.DEFAULT_VIBRATE);
        notification.defaults = 0;
//        notification.icon = R.drawable.ic_people_outline_black_24dp;
        //Set default notification sound when received
        if(isArrival)
            notification.sound = Uri.parse("android.resource://coupletones.pro.cse110.coupletones/" +
                R.raw.notification_sound);
        else //departure
            notification.sound = Uri.parse("android.resource://coupletones.pro.cse110.coupletones/" +
                    R.raw.furrow);

        return notification;
    }

    /**
     * Save the tones
     * @param tones
     */
    public static void saveTones(int[] tones){
        audIdx = 0;
        vibeIdx = 0;
        if(tones[0] != -1 && tones[1] != -1){
            audIdx = tones[0];
            vibeIdx = tones[1];
        }
        showNotification();
    }

    /**
     * Show the notification to the user
     * return : void
     */
    private static void showNotification(){
        MediaPlayer mp = MediaPlayer.create(context, audibleTones[audIdx]);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mp.start();
        // Vibrate for 500 milliseconds
        v.vibrate(vibeTones[vibeIdx], -1);
    }
}
