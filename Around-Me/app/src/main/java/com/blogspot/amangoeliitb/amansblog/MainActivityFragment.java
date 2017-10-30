package com.blogspot.amangoeliitb.amansblog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivityFragment extends Fragment {
    private SwipeRefreshLayout swipeContainer;
    String type;

    private ArrayAdapter <String> listViewAdapter;

    public ArrayList <Message> messages;

    ArrayList<String> message_names;

    private static final String TAG = "MyActivity";
    DBHelper mydb ;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        Bundle extras = getArguments();
        if(extras == null) {
            Log.v("Arguments", "is null");
        }
        type = extras.getString("TYPE") ;
        Log.v("Type", type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        messages = new ArrayList<Message>();

        mydb = new DBHelper(getActivity()) ;

        if((type.equalsIgnoreCase("sos") && mydb.getSOScount() > 0) || (type.equalsIgnoreCase("general") && mydb.getGeneralCount() > 0)){
            Log.v(TAG, "Database already exists") ;
            Log.v("type", type);
            messages = mydb.getAllMessages(type);
            Log.v("Size:", messages.size() + "");
            for(int i = 0 ; i < messages.size() ; i++)
                Log.v("Message:", messages.get(i).message);
        }
        else {
            //Log.v("Size of database:", rows + "");
            Log.v(TAG, "Creating the database") ;
            FetchFromServer();
        }

        UpdateAdapter();

        listViewAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        message_names);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String message = messages.get(position).message;
                String latitude = messages.get(position).latitude;
                String longitude = messages.get(position).longitude;
                String date = messages.get(position).date;

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("MESSAGE", message);
                extras.putString("LATITUDE", latitude);
                extras.putString("LONGITUDE", longitude);
                extras.putString("DATE", date);
                intent.putExtras(extras);
                Log.v("Something was", "clicked");
                startActivity(intent);
            }
        });

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG, "Swiped!") ;
                mydb.clearDatabase();
                FetchFromServer();
                swipeContainer.setRefreshing(false);
            }
        });

        return rootView;
    }

    public void UpdateAdapter() {
        Log.v("Updating", "Adapter");
        message_names = new ArrayList<String>();

        Log.v("Size of messages:", messages.size() + "");

        for(int i = 0 ; i < messages.size() ; i++)
            message_names.add(messages.get(i).message);
    }

    public void FetchFromServer() {
        double latitude = 0.0, longitude = 0.0;
        GPSTracker gps = new GPSTracker(getActivity().getApplicationContext());
        if (!gps.canGetLocation())
            gps.showSettingsAlert();
        else {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }
        //http://localhost:9091/androidServer/postMessage?latitude=1&longitude=1
        Uri.Builder builder = new Uri.Builder();
        String host = Message.host;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String distance = sharedPreferences.getString("example_list", "1");
        String time = sharedPreferences.getString("example_list_1", "90");

        builder.scheme("http");
        builder.encodedAuthority(host);
        builder.appendPath("androidServer")
                .appendEncodedPath("postMessage")
                .appendQueryParameter("latitude", Double.toString(latitude))
                .appendQueryParameter("longitude", Double.toString(longitude))
                .appendQueryParameter("distance", distance)
                .appendQueryParameter("time", time)
                .appendQueryParameter("type", type);

        String myUrl = builder.build().toString();
        FetchDataFromServer fetchDataFromServer = new FetchDataFromServer();
        Log.v("Ended", "I am here as well!");
        fetchDataFromServer.execute(myUrl);
        Log.v("Ended", "Fetch from server call");
    }

    public class FetchDataFromServer extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

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

                String[] unparsed_messages = updates.split("\\|\\|");
                Log.v("Size : " , Integer.toString(unparsed_messages.length));

                messages = new ArrayList<>();
                //mydb.clearDatabase();
                for(int i = 0 ; i < unparsed_messages.length ; i++) {
                    String[] temp = unparsed_messages[i].split("\\|");
                    Message message = new Message();
                    message.message = temp[0];
                    message.latitude = temp[1];
                    message.longitude = temp[2];
                    message.date = temp[3];
                    mydb.insertMessage(temp[0], temp[1], temp[2], temp[3], type);
                    Log.v(temp[0] + " " + temp[1], temp[2] + " " + temp[3]);
                    messages.add(message);
                }
                message_names = new ArrayList<String>();
                for(int i = 0 ; i < messages.size() ; i++) {
                    message_names.add(messages.get(i).message);
                    Log.v("Aatma", message_names.get(i));
                }
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

            Log.v("Ended", "Asynctask");
            String[] tmp = new String[message_names.size()];
            for(int i = 0 ; i < message_names.size() ; i++)
                tmp[i] = message_names.get(i);
            return tmp;
        }

        @Override
        protected void onPostExecute(String[] result) {
            for(int i = 0 ; i < message_names.size() ; i++) {
                Log.v("Aatma", message_names.get(i));
            }
            listViewAdapter.clear();
            Log.v("Size of result", result.length + "");
            for(String msg : result)
                listViewAdapter.add(msg);

            Log.v("Ended", "On Post Execute");
        }
    }
}
