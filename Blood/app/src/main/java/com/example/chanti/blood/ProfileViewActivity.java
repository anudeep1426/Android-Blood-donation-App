package com.example.chanti.blood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by ${anudeep} on 3/30/2016.
 */
public class ProfileViewActivity extends AppCompatActivity {

    String userName, password, firstName, lastName, bloodGroup, mobile;
    TextView profile_textview_first_name, profile_textview_last_name, profile_textview_email, profile_textview_password, profile_textview_blood_group, profile_textview_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Firebase.setAndroidContext(this);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final SharedPreferences profile = this.getSharedPreferences("Login", 0);
        userName = profile.getString("mobile", null);
        Log.d("dummy", userName);

        Firebase ref = new Firebase("https://bloodmanagement.firebaseio.com/Users/" + userName);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firstName = dataSnapshot.child("first_name").getValue().toString();
                lastName = dataSnapshot.child("last_name").getValue().toString();
                password = dataSnapshot.child("password").getValue().toString();
                mobile = dataSnapshot.child("mobile").getValue().toString();
                bloodGroup = dataSnapshot.child("blood_group").getValue().toString();

                profile_textview_first_name = (TextView)findViewById(R.id.profile_view_first_name);
                profile_textview_last_name = (TextView) findViewById(R.id.profile_view_last_name);
                profile_textview_email = (TextView) findViewById(R.id.profile_view_email);
                profile_textview_password = (TextView) findViewById(R.id.profile_view_password);
                profile_textview_mobile = (TextView) findViewById(R.id.profile_view_mobile);
                profile_textview_blood_group = (TextView) findViewById(R.id.profile_view_blood_group);

                profile_textview_first_name.setText(firstName + " " + lastName);
//                profile_textview_last_name.setText(lastName);
                profile_textview_email.setText(userName);
                profile_textview_password.setText(password);
                profile_textview_mobile.setText(mobile);
                profile_textview_blood_group.setText(bloodGroup);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_view, menu);
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

        if (item.getItemId() == R.id.action_logout) {
            Intent s = new Intent(ProfileViewActivity.this, LoginActivity.class);
            startActivity(s);
        }

        if(item.getItemId() == R.id.profile_edit) {
            Intent e = new Intent(ProfileViewActivity.this,  ProfileEditActivity.class);
            startActivity(e);
        }

        return super.onOptionsItemSelected(item);
    }
}
