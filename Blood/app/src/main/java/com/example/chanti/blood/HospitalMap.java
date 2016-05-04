package com.example.chanti.blood;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by ${anudeep} on 4/30/2016.
 */
public class HospitalMap extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    Geocoder geocoder;
    Button m;
    Intent extra = getIntent();
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<String> latList = new ArrayList<>();
    private ArrayList<String> lonList = new ArrayList<>();
    private ArrayList<String> hospitalNameList = new ArrayList<>();
    private ArrayList<LatLng> latlngsList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_map);
        // m = (Button) findViewById(R.id.getLocation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_hospital);
        mapFragment.getMapAsync(this);

        if (getIntent().getExtras() != null) {
            latList = getIntent().getExtras().getStringArrayList("latitude");
            lonList = getIntent().getExtras().getStringArrayList("longitude");
            hospitalNameList = getIntent().getExtras().getStringArrayList("list");
        }

        for (int i = 0;i < latList.size();i++) {
            Double l =  Double.parseDouble(latList.get(i));
            Double m =  Double.parseDouble(lonList.get(i));
            latlngsList.add(new LatLng(l,m));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);
        int j = 0;
        for (LatLng point : latlngsList) {
            options.position(point);
            options.title(hospitalNameList.get(j));
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
            mMap.addMarker(options);
            j++;
        }
        System.out.print(j);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngsList.get(0), 8));
    }
}
