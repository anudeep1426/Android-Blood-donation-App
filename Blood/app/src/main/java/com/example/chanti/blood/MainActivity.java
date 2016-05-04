package com.example.chanti.blood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{
    String cityTxt;
    String bloodGroupTxt;
    ImageButton searchBtn;
    FloatingActionButton mapBtn;
    ArrayList<String> latList = new ArrayList<String>();
    ArrayList<String> lonList = new ArrayList<String>();
    ArrayList<String> mobileNumberList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    final ArrayList<HashMap<String, String>> donorsList = new ArrayList<>();

    ShareDialog shareDialog;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
//        SimpleAdapter adaptor = new SimpleAdapter(this, donorsList, R.layout.activity_listview, new String[]{"userName", "address", "blood_group"}, new int[]{R.id.donorName, R.id.donorAddress, R.id.donorBloodGroup});
        ListView listView = (ListView) findViewById(R.id.bloodDonorsList);
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        mapBtn = (FloatingActionButton) findViewById(R.id.mapIcon);

        //Left navigation bar
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        shareDialog = new ShareDialog(this);
    }

    private void addDrawerItems() {
        SharedPreferences p = this.getSharedPreferences("Login",0);
        String[] osArray = { p.getString("mobile",null), "Menu", "Hospitals", "Blood Banks", "Blood Drive", "Share on FB"};
        mAdapter = new ArrayAdapter<String>(this, R.layout.activity_nav_text, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                System.out.println(id);

                if (position == 2) {
                    Intent h = new Intent(MainActivity.this, HospitalActivity.class);
                    startActivity(h);
                }

                if (position == 3) {
                    Intent h = new Intent(MainActivity.this, BloodBankActivity.class);
                    startActivity(h);
                }

                if (position == 4) {
                    Intent h = new Intent(MainActivity.this, BloodDriveActivity.class);
                    startActivity(h);
                }

                if (position == 5) {

                   if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("Blood Requirement")
                                .setContentDescription(
                                        "Need Blood Immediately")
                                .build();

                        shareDialog.show(linkContent);  // Show facebook ShareDialog
                    }

                }
            }
        });

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (item.getItemId() == R.id.action_logout) {
            Intent s = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(s);
        }

        if(item.getItemId() == R.id.action_profile_view) {
            Intent p = new Intent(MainActivity.this, ProfileViewActivity.class);
            startActivity(p);
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void searchDonors(View v) {
        cityTxt = ((EditText) findViewById(R.id.city)).getText().toString();
        bloodGroupTxt = ((Spinner) findViewById(R.id.spinnerBloodGroup)).getSelectedItem().toString();
        searchBtn = (ImageButton) findViewById(R.id.search);
        donorsList.clear();
        if (v.getId() == R.id.search) {
            final Firebase ref = new Firebase("https://bloodmanagement.firebaseio.com/Users");
            final Query donorQuery = ref.orderByChild("city").equalTo(cityTxt.toLowerCase().trim());
            //Query donor = donorList.orderByChild("blood_group").equalTo(bloodGroupTxt);
            donorQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot blood : dataSnapshot.getChildren()) {
                        String name;
                        HashMap<String, String> m = new HashMap<>();
                        m.clear();
                        if(bloodGroupTxt.equals("O-" ) || bloodGroupTxt.equals("O+")) {
                            if (blood.child("blood_group").getValue().equals("O+") || blood.child("blood_group").getValue().equals("O-")) {
                                name = blood.child("first_name").getValue().toString() + " " + blood.child("last_name").getValue().toString();
                                m.put("userName", name);
                                m.put("address", blood.child("address").getValue().toString());
                                m.put("blood_group", blood.child("blood_group").getValue().toString());
                                latList.add(blood.child("latitude").getValue().toString());
                                lonList.add(blood.child("longitude").getValue().toString());
                                mobileNumberList.add(blood.child("mobile").getValue().toString());
                                nameList.add(name);
                                donorsList.add(m);
                            }
                        }
                        else if (bloodGroupTxt.equals("A-" ) || bloodGroupTxt.equals("A+")) {
                            if (blood.child("blood_group").getValue().equals("O+") || blood.child("blood_group").getValue().equals("O-") || blood.child("blood_group").getValue().equals("A+") || blood.child("blood_group").getValue().equals("A-")) {

                                name = blood.child("first_name").getValue().toString() + " " + blood.child("last_name").getValue().toString();
                                m.put("userName", name);
                                m.put("address", blood.child("address").getValue().toString());
                                m.put("blood_group", blood.child("blood_group").getValue().toString());
                                latList.add(blood.child("latitude").getValue().toString());
                                lonList.add(blood.child("longitude").getValue().toString());
                                mobileNumberList.add(blood.child("mobile").getValue().toString());
                                nameList.add(name);
                                donorsList.add(m);
                            }
                        }

                        else if (bloodGroupTxt.equals("B-" ) || bloodGroupTxt.equals("B+")) {
                            if (blood.child("blood_group").getValue().equals("O+") || blood.child("blood_group").getValue().equals("O-") || blood.child("blood_group").getValue().equals("B+") || blood.child("blood_group").getValue().equals("B-")) {

                                name = blood.child("first_name").getValue().toString() + " " + blood.child("last_name").getValue().toString();
                                m.put("userName", name);
                                m.put("address", blood.child("address").getValue().toString());
                                m.put("blood_group", blood.child("blood_group").getValue().toString());
                                latList.add(blood.child("latitude").getValue().toString());
                                lonList.add(blood.child("longitude").getValue().toString());
                                mobileNumberList.add(blood.child("mobile").getValue().toString());
                                nameList.add(name);
                                donorsList.add(m);
                            }
                        }
                        else {
                            if (blood.child("blood_group").getValue().equals("O+") || blood.child("blood_group").getValue().equals("O-") || blood.child("blood_group").getValue().equals("B+") || blood.child("blood_group").getValue().equals("B-") || blood.child("blood_group").getValue().equals("A+") || blood.child("blood_group").getValue().equals("A-") || blood.child("blood_group").getValue().equals("AB+") || blood.child("blood_group").getValue().equals("AB-")) {

                                name = blood.child("first_name").getValue().toString() + " " + blood.child("last_name").getValue().toString();
                                m.put("userName", name);
                                m.put("address", blood.child("address").getValue().toString());
                                m.put("blood_group", blood.child("blood_group").getValue().toString());
                                latList.add(blood.child("latitude").getValue().toString());
                                lonList.add(blood.child("longitude").getValue().toString());
                                mobileNumberList.add(blood.child("mobile").getValue().toString());
                                nameList.add(name);
                                donorsList.add(m);
                            }
                        }
                    }
                    Log.d("data", donorsList.toString());

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    public class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return donorsList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_listview, parent,false);
                holder = new ViewHolder();
                holder.donorName = (TextView) convertView.findViewById(R.id.donorName);
                holder.donorAddress = (TextView) convertView.findViewById(R.id.donorAddress);
                holder.donorBloodGroup = (TextView) convertView.findViewById(R.id.donorBloodGroup);
                holder.callButton = (ImageButton) convertView.findViewById(R.id.call);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            HashMap<String,String> map = donorsList.get(position);
            holder.donorName.setText(map.get("userName"));
            holder.donorAddress.setText(map.get("address"));
            holder.donorBloodGroup.setText(map.get("blood_group"));
            holder.callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** call function goes here
                     * use map to get the current position data
                     **/
                    Intent callDonor = new Intent(Intent.ACTION_DIAL);
                    callDonor.setData(Uri.parse("tel:" + mobileNumberList.get(position)));
                    startActivity(callDonor);
                }
            });
            return convertView;
        }

        class ViewHolder{
            TextView donorName,donorAddress,donorBloodGroup;
            ImageButton callButton;
        }

    }

    public void onClickMap(View v) {
        if(v.getId() == R.id.mapIcon) {
            Intent m = new Intent(getApplicationContext(), DonorMap.class);
            m.putExtra("blood", bloodGroupTxt);
            m.putExtra("city", cityTxt);
            m.putStringArrayListExtra("latitudeList", (ArrayList<String>) latList);
            m.putStringArrayListExtra("longitudeList", (ArrayList<String>) lonList);
            m.putStringArrayListExtra("name", (ArrayList<String>) nameList);
           // m.putExtra("latitudeList", latList);
            //m.putExtra("longitudeList", lonList);
            startActivityForResult(m, 0);
        }
    }
}
