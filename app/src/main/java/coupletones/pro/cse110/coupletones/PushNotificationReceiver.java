package coupletones.pro.cse110.coupletones;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class PushNotificationReceiver extends ParsePushBroadcastReceiver{

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //Send first notification with message and CoupleTones default sound
        super.onPushReceive(context, intent);

        //Send second notification with customized sound and vibration
        try {
            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            boolean isArrival = data.getBoolean("isArrival");

            final NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            final Notification notification = new Notification();

//            if(isArrival){

                //Change later to actual customized settings
                notification.vibrate = new long[]{100, 200, 100, 500};
                notification.defaults |= Notification.DEFAULT_SOUND;

                //Send customized tones 2 seconds after first tone is played
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notificationManager.notify(0, notification);
                    }
                }, 2000);

//            }
//            else{
//
//            }

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
}
