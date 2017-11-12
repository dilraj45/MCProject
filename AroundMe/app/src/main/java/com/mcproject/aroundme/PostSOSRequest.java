package com.mcproject.aroundme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author dilraj
 */
public class PostSOSRequest extends AppCompatActivity implements Constants {

    private final String TAG = "PostSOSRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_sosrequest);

        // todo: move this to landing activity after login
        Log.v(TAG, "Triggering background service");
        Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        this.startService(serviceIntent);
    }

    public void postSOSRequest(View view) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_BUILD_SCHEME).encodedAuthority(HOST);
        builder.appendPath(POST_SOS_REQUEST);
        String urlString = builder.build().toString();

        String message = ((EditText) findViewById(R.id.message)).getText().toString();
        String token = getAuthenticationToken();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put(AUTH_TOKEN, token);
            requestBody.put(MESSAGE, message);
        } catch (JSONException exception) {
            exception.printStackTrace();
            return;
        }

        PostRequestHandler handler = new PostRequestHandler(getApplicationContext());
        Log.v(TAG, String.format("URL: %s", urlString));
        Log.v(TAG, String.format("Request Body: %s", requestBody.toString()));
        handler.execute(urlString, requestBody.toString());
    }

    private String getAuthenticationToken() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
        return pref.getString(AUTH_TOKEN, null);
    }
}
