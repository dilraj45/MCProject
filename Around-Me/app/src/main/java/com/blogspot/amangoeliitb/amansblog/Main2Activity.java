package com.blogspot.amangoeliitb.amansblog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class Main2Activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
    }

    public void openSOS(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("TYPE", "sos");
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.post_location:
                intent = new Intent(this, PostLocation.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    public void openGeneral(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("TYPE", "general");
        intent.putExtras(extras);
        startActivity(intent);
    }
}
