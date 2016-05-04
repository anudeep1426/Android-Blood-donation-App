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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${anudeep} on 3/30/2016.
 */
public class ProfileEditActivity extends AppCompatActivity {

    String userName, password, firstName, lastName, bloodGroup, mobile;
    EditText profile_textview_first_name, profile_textview_last_name, profile_textview_email, profile_textview_password, profile_textview_mobile;
    Spinner profile_textview_blood_group;
    String bloodGroupArray[] = {"A-","A+","AB-","AB+","B-","B+","O-","O+"};
    Button profileUpdateBtn, profileCancelBtn;
    Map<String, Object> updatedInfo = new HashMap<String, Object>();

    Firebase updateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolBar);
        Firebase.setAndroidContext(this);

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

                profile_textview_first_name = (EditText) findViewById(R.id.profile_view_first_name);
                profile_textview_last_name = (EditText) findViewById(R.id.profile_view_last_name);
                profile_textview_email = (EditText) findViewById(R.id.profile_view_email);
                profile_textview_password = (EditText) findViewById(R.id.profile_view_password);
                profile_textview_mobile = (EditText) findViewById(R.id.profile_view_mobile);
                profile_textview_blood_group = (Spinner) findViewById(R.id.profile_view_blood_group);

                profile_textview_first_name.setText(firstName);
                profile_textview_last_name.setText(lastName);
                profile_textview_email.setText(userName);
                profile_textview_password.setText(password);
                profile_textview_mobile.setText(mobile);
                profile_textview_blood_group.setSelection(Arrays.asList(bloodGroupArray).indexOf(bloodGroup));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }
    public void onClickCancel(View v) {
        profileCancelBtn = (Button) findViewById(R.id.cancel);

        if (v.getId() == R.id.cancel) {
            Intent c = new Intent(ProfileEditActivity.this, ProfileViewActivity.class);
            startActivity(c);
        }
    }

    public void onClickUpdate(View v) {

        profileUpdateBtn = (Button) findViewById(R.id.update);

        if(v.getId() == R.id.update) {
            updateRef = new Firebase("https://bloodmanagement.firebaseio.com/Users/" + userName);

            updateRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    profile_textview_first_name = (EditText) findViewById(R.id.profile_view_first_name);
                    profile_textview_last_name = (EditText) findViewById(R.id.profile_view_last_name);
                    profile_textview_email = (EditText) findViewById(R.id.profile_view_email);
                    profile_textview_password = (EditText) findViewById(R.id.profile_view_password);
                    profile_textview_mobile = (EditText) findViewById(R.id.profile_view_mobile);
                    profile_textview_blood_group = (Spinner) findViewById(R.id.profile_view_blood_group);


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            updatedInfo.put("first_name", profile_textview_first_name.getText().toString());
            updatedInfo.put("last_name", profile_textview_last_name.getText().toString());
            updatedInfo.put("user_name", profile_textview_email.getText().toString());
            updatedInfo.put("password", profile_textview_password.getText().toString());
            updatedInfo.put("mobile", profile_textview_mobile.getText().toString());
            //updatedInfo.put("blood_group", profile_textview_blood_group.getSelectedItem());

            updateRef.updateChildren(updatedInfo);

            Intent p = new Intent(ProfileEditActivity.this, ProfileViewActivity.class);
            startActivity(p);
        }
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

        if (item.getItemId() == R.id.action_logout) {
            Intent s = new Intent(ProfileEditActivity.this, LoginActivity.class);
            startActivity(s);
        }

        return super.onOptionsItemSelected(item);
    }
}
