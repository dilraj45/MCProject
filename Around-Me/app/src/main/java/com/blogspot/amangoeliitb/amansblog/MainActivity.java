package com.blogspot.amangoeliitb.amansblog;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            MainActivityFragment fragment = new MainActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putString("TYPE", getIntent().getExtras().getString("TYPE"));
            Log.v("MainType", getIntent().getExtras().getString("TYPE"));
            fragment.setArguments(bundle);
            Log.v("Bundle", bundle.toString());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }
}