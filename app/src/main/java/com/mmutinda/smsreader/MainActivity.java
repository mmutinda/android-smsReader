package com.mmutinda.smsreader;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.amitshekhar.DebugDB;
import com.mmutinda.smsreader.entities.SmsEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 10;
    private static final String TAG = "MainActivity";
    private Button btnFetch;
    private ProgressBar progressBar;

    private  Repository  repository;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        btnFetch = findViewById(R.id.fetchSMS);
        progressBar = findViewById(R.id.progressBar);

        repository = new Repository(getApplication());

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "onClick: start reading sms");

                getAllSms();
                progressBar.setVisibility(View.GONE);

            }
        });
        setSupportActionBar(toolbar);

        requestRuntimePermission();

        Log.d(TAG, "onCreate: "+ DebugDB.getAddressLog());

    }

    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_SMS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    public void getAllSms() {
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(message, null, null, null, null);
        startManagingCursor(c);
        int totalSMS = c.getCount();

        SmsEntity smsEntity;
        int counter = 0;
        if (c.moveToFirst()) {

            for (int i = 0; i < 1000; i++) { // hardcoded
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

                if (
                        number.equalsIgnoreCase("Safaricom")
                        || number.equalsIgnoreCase("CoopBank")
                        || number.equalsIgnoreCase("MPESA")
                ) {
                    c.moveToNext();
                    continue;
                }

                smsEntity = new SmsEntity();
                smsEntity.setType(type);
                smsEntity.setTimestamp(formatedDate);
                smsEntity.set_id(c.getString(c.getColumnIndexOrThrow("_id")));
                smsEntity.setAddress(number);
                smsEntity.setBody(body);
                smsEntity.setContactName(getContactName(
                        getApplicationContext(),
                        c.getString(c
                                .getColumnIndexOrThrow("address"))));

                repository.insertSingleSms(smsEntity);
                c.moveToNext();
            }
        }
        c.close();
        Log.d(TAG, "getAllSms: Complete");
        Log.d(TAG, "Welcome");
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();


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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
            } else {
                // Permission denied.

                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
