package com.example.chanti.blood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${anudeep} on 4/29/2016.
 */
public class DonorMap extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    Geocoder geocoder;
    Button m;
    Intent extra = getIntent();
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ArrayList<String> latList = new ArrayList<String>();
    private ArrayList<String> lonList = new ArrayList<String>();
    private ArrayList<String> nameList = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_map);
       // m = (Button) findViewById(R.id.getLocation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_donor);
        mapFragment.getMapAsync(this);

        if( getIntent().getExtras() != null) {
            latList = getIntent().getExtras().getStringArrayList("latitudeList");
            lonList = getIntent().getExtras().getStringArrayList("longitudeList");
            nameList = getIntent().getExtras().getStringArrayList("name");
        }

        for (int i = 0;i < lonList.size();i++) {
            Double l =  Double.parseDouble(latList.get(i));
            Double m = Double.parseDouble(lonList.get(i));
            latlngs.add(new LatLng(l,m));
        }
    }


/*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mmap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
    */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        int i = 0;
        for (LatLng point : latlngs) {
            options.position(point);
            options.title(nameList.get(i));
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
            mMap.addMarker(options);
            i++;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngs.get(0), 5));
    }

}
