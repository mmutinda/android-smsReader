package com.mmutinda.smsreader;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * Created by mmutinda on 13-Dec-16.
 */

public class AppController extends Application {
    public static final String TAG = "AppController";
    public static final String TRACKER_CHANNEL = Config.TRACKER_CHANNEL;
    private static AppController mInstance;
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        createNotificationChannel();

    }


    private void createNotificationChannel() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = getString(R.string.channel_name_gps);
            String description = getString(R.string.channel_description_gps);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(TRACKER_CHANNEL, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(R.color.colorPrimary);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}

