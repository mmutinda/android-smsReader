package com.mmutinda.testapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import com.mmutinda.testapp.services.MyService;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 10;
    private static final String TAG = "MainActivity";
    private Button btnFetch;
    private ProgressBar progressBar;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("stop")) {
                Toast.makeText(context, "Something went wrong on this device!! Cant start", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        btnFetch = findViewById(R.id.fetchSMS);
        progressBar = findViewById(R.id.progressBar);

        registerReceiver(receiver, new IntentFilter("stop"));

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( checkPermission1() && checkPermission2()) {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: start reading sms");
                    startTrackerService();
                } else {
                    Toast.makeText(MainActivity.this, "Enable requested permissions to continue", Toast.LENGTH_LONG).show();
                }


            }
        });

        setSupportActionBar(toolbar);
        requestRuntimePermission();
        Log.d(TAG, "onCreate: " + DebugDB.getAddressLog());

    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermission1() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
    }
    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermission2() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private void startTrackerService() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(), MyService.class));
        }else{
            startService(new Intent(getApplicationContext(), MyService.class));
        }
    }

    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
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
