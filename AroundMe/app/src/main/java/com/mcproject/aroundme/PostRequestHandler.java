package com.mcproject.aroundme;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author dilraj
 */
@TargetApi(19)
public class PostRequestHandler extends AsyncTask<String, Void, Void> {

    private final String TAG = "PostRequestHandler";
    protected Context appContext;
    protected String response = null;
    public PostRequestHandler(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String urlString = strings[0];
        String jsonString = null;

        if (strings.length >= 1)
            jsonString = strings[1];

        Log.v(TAG, String.format("Post request body %s", jsonString));

        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString + "/");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"))) {
                Log.v(TAG, "Writing jsonString to OutputStream");
                writer.write(jsonString);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                this.response = buffer.toString();
                Log.v(TAG, "Post request response body" + this.response);
            }
        } catch(Exception e) {
            Log.v(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (this.response == null || this.response.isEmpty()) {
            Toast.makeText(this.appContext, "Something went wrong!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(appContext, this.response, Toast.LENGTH_SHORT).show();
    }
}

