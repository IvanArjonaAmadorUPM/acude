package com.example.acude;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

public class LocationActivity extends AppCompatActivity {


    int latitude, longitude, altitude, accuracy;

    boolean sw_locationUpdates, sw_gps;

    //Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;
    //Location request
    LocationRequest locationRequest;

    //interval time to update location in milliseconds
    public static final int DEFAULT_UPDATE_INTERVAL = 10 * 1000;
    public static final int FAST_UPDATE_INTERVAL = 5 * 1000;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //set all properties of Location
        locationRequest = LocationRequest.create()
                .setInterval(DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(FAST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }


}
