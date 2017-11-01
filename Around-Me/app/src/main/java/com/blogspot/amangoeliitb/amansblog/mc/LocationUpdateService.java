package com.blogspot.amangoeliitb.amansblog.mc;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.blogspot.amangoeliitb.amansblog.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author dilraj
 */

public class LocationUpdateService extends IntentService {

    private final String HOST = "ec2-52-15-178-137.us-east-2.compute.amazonaws.com";
    private final String TAG = "LocationUpdateService";

    public LocationUpdateService() {
        super("LocationUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").encodedAuthority(HOST);
        builder.appendPath("androidServer/UpdateLocation");
        String urlString = builder.build().toString();

        JSONObject requestBody = new JSONObject();
        while (true) {
            try {
                requestBody.put("token", "tokenValue");
                requestBody.put("latitude", gpsTracker.getLatitude());
                requestBody.put("longitude", gpsTracker.getLongitude());
            } catch (JSONException exception) {
                Log.v(TAG, exception.getStackTrace().toString());
                exception.printStackTrace();
            }

            Log.v(TAG, "URL: " + urlString);
            Log.v(TAG, "Request body: " + requestBody.toString());
            PostRequestHandler handler = new PostRequestHandler(getApplicationContext());
            handler.execute(urlString, requestBody.toString());
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Log.v(TAG, e.getStackTrace().toString());
            }
        }
    }

    public LocationUpdateService(String name) {
        super(name);
    }
}
