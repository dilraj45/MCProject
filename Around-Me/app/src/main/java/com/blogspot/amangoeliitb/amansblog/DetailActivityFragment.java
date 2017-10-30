package com.blogspot.amangoeliitb.amansblog;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DetailActivityFragment extends Fragment {
    public static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        Bundle extras = intent.getExtras();
        String message = extras.getString("MESSAGE") ;
        String latitude = extras.getString("LATITUDE") ;
        String longitude = extras.getString("LONGITUDE") ;
        String date = extras.getString("DATE") ;
        Log.v(message, date);

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

        Log.v("I am ", "here");
        Log.v(latitude + "", longitude + "");

        try {
            Log.v("HERE, here", "here");
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
            Log.v("address", addresses.size() + "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);
        String knownName = addresses.get(0).getFeatureName();

        ((TextView) rootView.findViewById(R.id.location)).setText(address + "(" + knownName + ")");
        ((TextView) rootView.findViewById(R.id.message)).setText(message);
        ((TextView) rootView.findViewById(R.id.date)).setText(date);

        return rootView;
    }
}