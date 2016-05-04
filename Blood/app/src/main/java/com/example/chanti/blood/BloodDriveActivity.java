package com.example.chanti.blood;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ${anudeep} on 4/30/2016.
 */
public class BloodDriveActivity extends AppCompatActivity {
    String zipTxt, response;
    ImageButton searchBtn;
    Double lat,lon;
    Geocoder geoCoder;
    ListView lView;
    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> bloodDriveList = new ArrayList<String>();
    ArrayList<String> latList = new ArrayList<String>();
    ArrayList<String> lonList = new ArrayList<String>();
    //String[] bdList = new String[100];
  //  final ArrayList<HashMap<String, String>> hospitalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_drive);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

        lView = (ListView) findViewById(R.id.hosList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_blood_drive_text, bloodDriveList);

        lView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchHospitals(View v)
    {
        zipTxt = ((EditText) findViewById(R.id.zip)).getText().toString();

        if(v.getId() == R.id.search) {

            String latlon = getLatLon(zipTxt);
            String API_KEY = "AIzaSyB2Zc4MBAAcqktEb9k9SORLCekFqkhpgFM";
            final String urlTxt = "https://maps.googleapis.com/maps/api/place/search/json?location=" + latlon + "&radius=5000&types=blooddrive&sensor=false&key=" + API_KEY;

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(urlTxt);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        StringBuilder stringBuilder = new StringBuilder();
                        InputStream is;
                        is = urlConnection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String line;
                        while ((line = br.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        response = stringBuilder.toString();
                        System.out.println(response);
                        is.close();
                        urlConnection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject jobject = new JSONObject(response);
                        JSONArray jsonArray = jobject.getJSONArray("results");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject j = jsonArray.getJSONObject(i);
                            HashMap<String, String> m = new HashMap<>();
                            if (j.has("opening_hours") && !j.isNull("opening_hours")) {
                                m.put("Open", j.getJSONObject("opening_hours").getString("open_now"));
                            }
                            m.put("Address", j.getString("vicinity"));
                            m.put("Name", j.getString("name"));
                            String s = m.toString();
                            bloodDriveList.add(s.substring(1, s.length() - 1));
                            name.add(j.getString("name"));
                            // hospitalList.add(m);
                            if (!j.isNull("geometry")) {
                                JSONObject tmp = j.getJSONObject("geometry").getJSONObject("location");
                                latList.add(tmp.getString("lat"));
                                lonList.add(tmp.getString("lng"));
                            }
                            // hospitalList.add(m);
                        }
                        System.out.println(name);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public void onClickMap(View v)
    {
        Intent m = new Intent(BloodDriveActivity.this, BloodDriveMap.class);
        m.putStringArrayListExtra("list", name);
        m.putStringArrayListExtra("latitude", latList);
        m.putStringArrayListExtra("longitude", lonList);
        startActivityForResult(m,0);
    }

    public String getLatLon(String zip)
    {
        String latlon = "-1,-1";
        try {
            List<Address> addresses = geoCoder.getFromLocationName(zip, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Use the address as needed

                lat = address.getLatitude();
                lon = address.getLongitude();
                System.out.println(lat);
                System.out.println(lon);
                String message = String.format("Latitude: %f, Longitude: %f",
                        address.getLatitude(), address.getLongitude());
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                latlon = lat + "," + lon;
            } else {
                // Display appropriate message when Geocoder services are not available
                Toast.makeText(this, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // handle exception
        }
        return latlon;
    }
}
