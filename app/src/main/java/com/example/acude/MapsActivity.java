package com.example.acude;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.acude.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView searchView;


    private ActivityMapsBinding binding;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private LatLng actualPosition;
    private LatLng destinationCoords;


    public static final int MIN_TIME = 1000; //1 SECOND
    public static final int MIN_DISTANCE = 5; //5 METERS


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        /*get permissions*/
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        //LatLng firefighterAlcala = new LatLng(40.48855, -3.384250);
        //mMap.addMarker(new MarkerOptions().position(firefighterAlcala).title("firefighterAlcala in Alcala"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(firefighterAlcala));

        searchView = findViewById(R.id.idSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String destinationIntroduced = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (destinationIntroduced != null || destinationIntroduced.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(destinationIntroduced, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address destinationAddress = addressList.get(0);
                    destinationCoords = new LatLng(destinationAddress.getLatitude(), destinationAddress.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(destinationCoords).title(destinationIntroduced));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationCoords, 10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                actualPosition = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(actualPosition).title("my position"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(actualPosition,8));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LocationListener.super.onStatusChanged(provider, status, extras);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
            }
        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
}