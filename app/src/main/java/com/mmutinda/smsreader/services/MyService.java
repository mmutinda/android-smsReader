package com.mmutinda.smsreader.services;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.mmutinda.smsreader.Config;
import com.mmutinda.smsreader.R;
import com.mmutinda.smsreader.Repository;
import com.mmutinda.smsreader.entities.SmsEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyService extends Service {

    private Repository repository;
    private static final String TAG = "MyService";
    NotificationCompat.Builder builder;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final int NOTIFICATION_ID = 200;



    @Override
    public void onCreate() {
        super.onCreate();
        repository = new Repository(getApplication());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getAllSms();
        startForeground(NOTIFICATION_ID, buildNotification());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }



    private Notification buildNotification() {

        // Create the persistent notification
        builder = new NotificationCompat.Builder(this, Config.TRACKER_CHANNEL);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(R.string.processing));
        builder.setOngoing(true);
        builder.setColor(getResources().getColor(R.color.colorPrimary));
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setUsesChronometer(true);
        builder.setSmallIcon(R.drawable.ic_notification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        return builder.build();
    }

    public void getAllSms() {
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(message, null, null, null, null);
//        startManagingCursor(c);
        int totalSMS = c.getCount();

        SmsEntity smsEntity;
        List<SmsEntity> smsEntities = new ArrayList<>();
        int counter = 0;
        if (c.moveToFirst()) {

            for (int i = 0; i < totalSMS; i++) { // hardcoded
                counter += 1;
                Log.d(TAG, "getAllSms: reading " + counter + " of " + totalSMS);
                String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                Date dateFormat= new Date(Long.valueOf(smsDate));

                String formatedDate = df.format(dateFormat);
                String type = "";
                switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                    case Telephony.Sms.MESSAGE_TYPE_INBOX:
                        type = "inbox";
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_SENT:
                        type = "sent";
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                        type = "outbox";
                        break;
                    default:
                        break;
                }

//                if (
//                        number.equalsIgnoreCase("Safaricom")
//                                || number.equalsIgnoreCase("CoopBank")
//                                || number.equalsIgnoreCase("MPESA")
//                ) {
//                    c.moveToNext();
//                    continue;
//                }

                smsEntity = new SmsEntity();
                smsEntity.setType(type);
                smsEntity.setTimestamp(formatedDate);
                smsEntity.set_id(c.getString(c.getColumnIndexOrThrow("_id")));
                smsEntity.setAddress(number);
                smsEntity.setBody(body);
//                smsEntity.setContactName(getContactName(
//                        getApplicationContext(),
//                        c.getString(c
//                                .getColumnIndexOrThrow("address"))));

                smsEntities.add(smsEntity);
                if (counter % 200 == 0) {
                    repository.insertManySms(smsEntities);
                    smsEntities = new ArrayList<>();
                }

                c.moveToNext();
            }
        }
        c.close();
        Log.d(TAG, "getAllSms: Complete");
        Log.d(TAG, "Welcome");
        stopSelf();
    }

    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }
}