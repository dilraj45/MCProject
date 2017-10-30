package com.blogspot.amangoeliitb.amansblog.mc;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import com.blogspot.amangoeliitb.amansblog.R;

/**
 * @author dilraj
 */
public class PostSOSRequest extends AppCompatActivity {

    private String HOST = "172.31.68.121:8000";
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
        builder.scheme("http").encodedAuthority(HOST);
        builder.appendPath("androidServer/PostSOSRequest");
        String urlString = builder.build().toString();

        String message = ((EditText)findViewById(R.id.message)).getText().toString();
        String token = getAuthenticationToken();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", token);
            requestBody.put("message", message);
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
        return "tokenValue";
    }
}
