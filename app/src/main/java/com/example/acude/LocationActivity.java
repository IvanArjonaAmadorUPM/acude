package com.example.acude;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationActivity extends AppCompatActivity {


    private static final int PERMISSIONS_FINE_LOCATION = 99;
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

        //updateGPS()

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch ( requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else {
                    Toast.makeText(this,"Esta aplicación necesita permisos de ubicación",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    public void updateGPS(){
        //get permision from user to track GPS
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //the user have provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Update values of Location after having permission

                    System.out.println("latitude" + location.getLatitude());

                    System.out.println("longitude" + location.getLongitude());

                    System.out.println("altitude" + location.getAltitude());

                }
            });
        }
        else {
            // request permission to the user

            //check if the user have the correct version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);

            }
        }
        //get current location

    }


}
