package coupletones.pro.cse110.coupletones;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class PushNotificationReceiver extends ParsePushBroadcastReceiver{
    private static int audIdx;
    private static int vibeIdx;
    private static Context context;
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

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //Send first notification with message and CoupleTones default sound
        super.onPushReceive(context, intent);
        this.context = context;

        //Send second notification with customized sound and vibration
        try {
            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            boolean isArrival = data.getBoolean("isArrival");
            LatLng latLng = new LatLng(data.getDouble("loc_lat"), data.getDouble("loc_lng"));

            new ParseClient(context).pullToneIndex(latLng, isArrival);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Notification notification = super.getNotification(context, intent);

        //Set default notification sound when received
        notification.sound = Uri.parse("android.resource://" + context.getPackageName() +
                "/notification_sound.mp3");

        return notification;
    }

    public static void saveTones(int[] tones){
        audIdx = 0;
        vibeIdx = 0;
        if(tones[0] != -1 && tones[1] != -1){
            audIdx = tones[0];
            vibeIdx = tones[1];
        }
        showNotification();
    }

    private static void showNotification(){
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification();

//            if(isArrival){

        //Change later to actual customized settings
        notification.vibrate = vibeTones[vibeIdx];
        notification.sound = Uri.parse("android.resource://coupletones.pro.cse110.coupletones/" +
                audibleTones[audIdx]);
//        notification.defaults |= Notification.DEFAULT_SOUND;

        //Send customized tones 2 seconds after first tone is played
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.notify(0, notification);
            }
        }, 2000);
    }
}
