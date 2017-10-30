package com.blogspot.amangoeliitb.amansblog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostLocation extends ActionBarActivity {

    GPSTracker gps;

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_location);
    }

    public void go_back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void post_location(View view) throws IOException {
        gps = new GPSTracker(PostLocation.this);

        // check if GPS enabled

        if (!gps.canGetLocation())
            gps.showSettingsAlert();
        else {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            EditText editText = (EditText) findViewById(R.id.message_text);
            String message = editText.getText().toString();

            Uri.Builder builder = new Uri.Builder();
            String host = Message.host;

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String distance = sharedPreferences.getString("example_list", "1");

            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
            int checkedId = radioGroup.getCheckedRadioButtonId();
            String type = "sos";
            if(checkedId == R.id.general)
                type = "general";

            builder.scheme("http");
            builder.encodedAuthority(host);
            builder.appendPath("androidServer")
                    .appendEncodedPath("postMessage")
                    .appendQueryParameter("latitude", Double.toString(latitude))
                    .appendQueryParameter("longitude", Double.toString(longitude))
                    .appendQueryParameter("message", message)
                    .appendQueryParameter("distance", distance)
                    .appendQueryParameter("type", type);

            String myUrl = builder.build().toString();
            Log.v("URL", myUrl);

            FetchDataFromServer fetchDataFromServer = new FetchDataFromServer();
            fetchDataFromServer.execute(myUrl);
            Log.v("I am ", "done");
            finish();
        }
    }

    public class FetchDataFromServer extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String updates = null;

            try {
                URL url = new URL(params[0]);

                Log.v("Built URI " , params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                updates = buffer.toString();
                Log.v("Fetched string: ", updates);
            }
            catch (IOException e) {
                Log.e("Error ", e.toString());
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Error closing stream", e.toString());
                    }
                }
            }
            return null;
        }
    }
}
