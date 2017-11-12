package com.mcproject.aroundme;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author dilraj
 */

public class LocationUpdateService extends IntentService implements Constants {

    private final String TAG = "LocationUpdateService";

    public LocationUpdateService() {
        super("LocationUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_BUILD_SCHEME).encodedAuthority(HOST);
        builder.appendPath(UPDATE_LOCATION_URI);
        String urlString = builder.build().toString();

        JSONObject requestBody = new JSONObject();
        while (true) {
            try {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
                requestBody.put(AUTH_TOKEN, pref.getString(AUTH_TOKEN, null));
                requestBody.put(LATITUDE, gpsTracker.getLatitude());
                requestBody.put(LONGITUDE, gpsTracker.getLongitude());
            } catch (JSONException exception) {
                Log.v(TAG, exception.getStackTrace().toString());
                exception.printStackTrace();
            }

            Log.v(TAG, "URL: " + urlString);
            Log.v(TAG, "Request body: " + requestBody.toString());
            PostRequestHandler handler = new PostRequestHandler(getApplicationContext()) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    return;
                }
            };
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
