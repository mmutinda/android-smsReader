package com.mmutinda.smsreader;

import android.Manifest;
import android.content.Intent;
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
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.amitshekhar.DebugDB;
import com.mmutinda.smsreader.services.MyService;
import com.mmutinda.smsreader.workers.SampleWorker;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 10;
    private static final String TAG = "MainActivity";
    private Button btnFetch, btnUpload;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        btnFetch = findViewById(R.id.fetchSMS);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "onClick: start reading sms");
                startTrackerService();
                progressBar.setVisibility(View.GONE);

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "onClick: start uploading sms...");
                uploadLocalData();
                progressBar.setVisibility(View.GONE);

            }
        });

        setSupportActionBar(toolbar);
        requestRuntimePermission();
        Log.d(TAG, "onCreate: " + DebugDB.getAddressLog());

    }


    private void uploadLocalData() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest respondRequest =
                new OneTimeWorkRequest.Builder(SampleWorker.class)
//                        .setInputData(createInputData(idnumber, String.valueOf(tracker_id), type, activity_string))
                        .setConstraints(constraints)
                        .build();
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.beginUniqueWork(
                "tracker_id",
                ExistingWorkPolicy.KEEP,
                respondRequest).enqueue();

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
                new String[]{Manifest.permission.READ_SMS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
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
