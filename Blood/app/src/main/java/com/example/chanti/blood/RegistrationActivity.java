package com.example.chanti.blood;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by chanti on 27-Feb-16.
 **/
public class RegistrationActivity extends Activity implements View.OnClickListener,LocationListener{

    String firstNameTxt, lastNameTxt, passwordTxt, phoneTxt, userNameTxt, addressTxt;
    EditText userName, firstName, lastName, password, phone;
    String city, state, zip, address1;
    String l;
    String m;
    boolean isValid = false;
    String bloodGroupTxt;
    LatLng latLng;
    double latitute, longitude;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Firebase.setAndroidContext(this);
        findViewById(R.id.getAddressButton).setOnClickListener(this);
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
    }

    public void register(View v) {
        userName = ((EditText) findViewById(R.id.editUserName));
        firstName = ((EditText) findViewById(R.id.editFirstName));
        lastName = ((EditText) findViewById(R.id.editLastName));
        password = ((EditText) findViewById(R.id.editPassword));
        phone = ((EditText) findViewById(R.id.editPhoneNumber));

        if(v.getId() == R.id.btnRegister) {
            firstNameTxt = ((EditText) findViewById(R.id.editFirstName)).getText().toString();
            lastNameTxt = ((EditText) findViewById(R.id.editLastName)).getText().toString();
            passwordTxt = ((EditText) findViewById(R.id.editPassword)).getText().toString();
            phoneTxt = ((EditText) findViewById(R.id.editPhoneNumber)).getText().toString();
            userNameTxt = ((EditText) findViewById(R.id.editUserName)).getText().toString();
            userNameTxt = userNameTxt.substring(0,userName.length() - 4);
            addressTxt = ((TextView) findViewById(R.id.address)).getText().toString();
            bloodGroupTxt = ((Spinner) findViewById(R.id.spinnerBloodGroup)).getSelectedItem().toString();

            if (!addressTxt.isEmpty()) {
                address1 = addressTxt.substring(0, addressTxt.indexOf('\t'));
                city = addressTxt.substring(addressTxt.indexOf('\t') + 1, addressTxt.indexOf(','));
                state = addressTxt.substring(addressTxt.indexOf(',') + 2, addressTxt.indexOf(',') + 4);
                zip = addressTxt.substring(addressTxt.indexOf(',') + 5, addressTxt.indexOf(',') + 10);
            }

            isValid = checkValidations();

            if(isValid) {
                userName.setError(null);

                final Firebase userListRef = new Firebase("https://bloodmanagement.firebaseio.com/Users");

                userListRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userNameTxt)) {
                            userName.setError("User Exist. Please sign in.");
                        } else {
                            Firebase ref = new Firebase("https://bloodmanagement.firebaseio.com/");
                            Firebase userRef = ref.child("Users").child(phoneTxt);
                            userRef.child("user_name").setValue(userNameTxt);
                            userRef.child("first_name").setValue(firstNameTxt);
                            userRef.child("last_name").setValue(lastNameTxt);
                            userRef.child("password").setValue(passwordTxt);
                            userRef.child("mobile").setValue(phoneTxt);
                            userRef.child("address").setValue(addressTxt);
                            userRef.child("address1").setValue(address1);
                            userRef.child("city").setValue(city.toLowerCase().trim());
                            userRef.child("state").setValue(state);
                            userRef.child("zip").setValue(zip);
                            userRef.child("blood_group").setValue(bloodGroupTxt);

                            userRef.child("latitude").setValue(latitute);
                            userRef.child("longitude").setValue(longitude);
                            SharedPreferences preferences = getSharedPreferences("AUTH", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("mobile", phoneTxt);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            //launch MainActivity
                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }
    }

    private boolean checkValidations() {
        boolean flag = true;
        if(userNameTxt.isEmpty()) {
            userName.setError("Field is required.");
            flag = false;
        }
        else {
            if (userNameTxt.indexOf('@') == -1) {
                userName.setError("Email is not valid");
                flag = false;
            }
        }

        if(firstNameTxt.isEmpty()) {
            firstName.setError("Field is required.");
            flag = false;
        }

        if(lastNameTxt.isEmpty()) {
            lastName.setError("Field is required");
            flag = false;
        }

        if(phoneTxt.isEmpty()) {
            phone.setError("Field is required");
            flag = false;
        }

        if(passwordTxt.isEmpty()) {
            password.setError("Field is required");
            flag = false;
        }

        return flag;
    }

    public void setLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //show message or ask permissions from the user.
            return;
        }
        //Getting the current location of the user.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0,this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null){
            Toast.makeText(this,"Unable to fetch Location.Please enter",Toast.LENGTH_SHORT).show();
            return;
        }
        latitute = location.getLatitude();
        longitude = location.getLongitude();
        latLng = new LatLng(latitute,longitude);
        new AddressTask().execute(latLng);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getAddressButton:
                setLocation();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Snackbar.make(findViewById(android.R.id.content), "Unable to fetch Location.Please enter", Snackbar.LENGTH_SHORT).show();
            return;
        }
        latitute = location.getLatitude();
        longitude = location.getLongitude();
        latLng = new LatLng(latitute,longitude);
        new AddressTask().execute(latLng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class AddressTask extends AsyncTask<LatLng,Void,List<Address>>{

        Geocoder geocoder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            geocoder = new Geocoder(RegistrationActivity.this, Locale.ENGLISH);
        }

        @Override
        protected List<Address> doInBackground(LatLng... params) {
            LatLng latLng = params[0];
            if(latLng == null) return null;
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1);
                return addresses;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);
            if(addresses != null){
                Address address = addresses.get(0);
                StringBuilder userAddress =  new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    userAddress.append(address.getAddressLine(i)).append("\t");
                }
                userAddress.append(address.getCountryName()).append("\t");
                TextView location = (TextView) findViewById(R.id.address);
                location.setText(userAddress);
            }
        }
    }

}
