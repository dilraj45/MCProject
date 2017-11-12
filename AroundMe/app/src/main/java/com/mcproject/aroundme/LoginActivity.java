package com.mcproject.aroundme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author dilraj
 */
// todo: code for checking if a particular user exists in database or not
public class LoginActivity extends AppCompatActivity implements Constants {

    private final String TAG = "LoginActivity";
    private Button login_button;
    private TextView userNameTextView, passwordTextView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(LoginActivity.this);
        userNameTextView = findViewById(R.id.input_username_login);
        passwordTextView = findViewById(R.id.input_password_login);
        Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        this.startService(serviceIntent);
    }

    private boolean validateCredentials() {
        boolean valid = true;

        String username = userNameTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        if (username.isEmpty()) {
            userNameTextView.setError("Enter a valid username");
            valid = false;
        } else {
            userNameTextView.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordTextView.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordTextView.setError(null);
        }
        return valid;
    }

    private void authenticate_credentials(String username, String password) {
        Log.v(TAG, "Authenticating credential for username " + username);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_BUILD_SCHEME).encodedAuthority(HOST);
        builder.appendPath(LOG_IN_URI);
        String urlString = builder.build().toString();

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put(USERNAME, username);
            requestBody.put(PASSWORD, password);
            PostRequestHandler handler = new PostRequestHandler(getApplicationContext()) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    try {
                        JSONObject responseJSON = new JSONObject(this.response);
                        String authToken = responseJSON.getString(AUTH_TOKEN);

                        Log.v(TAG, "Saving authentication token to Shared preferences!!");
                        SharedPreferences pref = this.appContext.getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
                        pref.edit().putString(AUTH_TOKEN, authToken).apply();
                        progressDialog.dismiss();

                        Intent intent = new Intent(this.appContext, ViewSOSRequest.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException exception) {
                        Log.d(TAG, exception.getMessage());
                        Toast.makeText(this.appContext, "Invalid Credentials!!", Toast.LENGTH_LONG).show();
                    } finally {
                        progressDialog.dismiss();
                        login_button.setEnabled(true);
                    }
                }
            };
            Log.v(TAG, String.format("URL: %s", urlString));
            Log.v(TAG, String.format("Request Body: %s", requestBody.toString()));
            handler.execute(urlString, requestBody.toString());
        } catch (JSONException exception) {
            Log.d(TAG, exception.getStackTrace().toString());
            (findViewById(R.id.btn_login)).setEnabled(true);
            return;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        login_button.setEnabled(true);
    }

    public void login(View view) {
        if (!validateCredentials()) {
            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
            return;
        }
        login_button.setEnabled(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = userNameTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        // authenticating credentials
        authenticate_credentials(username, password);
    }

    public void signUpActivity(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
