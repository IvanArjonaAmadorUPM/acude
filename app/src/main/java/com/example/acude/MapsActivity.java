package com.example.acude;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.acude.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView searchView;

    private static final String TAG = MapsActivity.class.getSimpleName();

    private ActivityMapsBinding binding;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private LatLng actualPosition;
    private LatLng destinationCoords;
    private String routeTime;


    public static final int MIN_TIME = 1000; //1 SECOND
    public static final int MIN_DISTANCE = 5; //5 METERS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*get permissions*/
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        setMapStyle(mMap);

        Button trafficButton = findViewById(R.id.trafficButton);
        trafficButton.setBackgroundColor(getResources().getColor(R.color.red));
        trafficButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setTrafficEnabled(!mMap.isTrafficEnabled());
                if(mMap.isTrafficEnabled()){
                    trafficButton.setBackgroundColor(getResources().getColor(R.color.green));
                }else {
                    trafficButton.setBackgroundColor(getResources().getColor(R.color.red));
                }


            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDQzqtfnABh1HPxjOM0T_LbB9LJzztH7J0");
        }
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_search_fragment);

        //assert autocompleteFragment != null;
        //autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(40.217127,-3.131993),
                new LatLng(40.657572,-4.120929)));
        autocompleteFragment.setCountries("ES");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                String destinationAddress = place.getName();
                destinationCoords = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(destinationCoords).title(destinationAddress));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationCoords, 14));
                setRoute(actualPosition, destinationCoords, mMap);
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                actualPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(actualPosition).title("my position"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(actualPosition, 12));
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
    public void setMapStyle(GoogleMap mMap) {
        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.map_style_json)));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }
    }

    private void setRoute(LatLng actualPosition, LatLng destinationCoords, GoogleMap mMap) {

        Float actualLat = (float) actualPosition.latitude;
        Float actualLng = (float) actualPosition.longitude;
        String actualString = actualLat.toString() + "," + actualLng.toString();

        Float destinationLat = (float) destinationCoords.latitude;
        Float destinationLng = (float) destinationCoords.longitude;
        String destinationString = destinationLat.toString() + "," + destinationLng.toString();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", destinationString)
                .appendQueryParameter("origin", actualString)
                .appendQueryParameter("mode", "driving")
                .appendQueryParameter("key", "AIzaSyDQzqtfnABh1HPxjOM0T_LbB9LJzztH7J0")
                .appendQueryParameter("traffic_model","best_guess") //best_guess
                .appendQueryParameter("departure_time","now")
                .toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");

                    if (status.equals("OK")) {

                        JSONArray routes = response.getJSONArray("routes");
                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        getTime(routes);

                        for (int i=0;i<routes.length();i++){
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            for (int j=0;j<legs.length();j++){
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");

                                for (int k=0;k<steps.length();k++){
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");List<LatLng> list = decodePoly(polyline);

                                    for (int l=0;l<list.size();l++){
                                        LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                    }
                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(40);
                            polylineOptions.color(ContextCompat.getColor(MapsActivity.this, R.color.lightRed));
                            polylineOptions.geodesic(true);
                        }
                        mMap.addPolyline(polylineOptions);
                        Point point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    private void getTime(JSONArray routes) throws JSONException {
        for (int i=0;i<routes.length();i++) {
            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
            for(int j=0;j<legs.length();j++){
                String time = legs.getJSONObject(j).getJSONObject("duration").getString("text");
                routeTime = time.substring(0,2);
                TextView totalRouteTime = findViewById(R.id.totalTimeRoute);
                totalRouteTime.setText(routeTime + "minutos");
            }
        }

    }

    private List<LatLng> decodePoly(String encoded){

        List<LatLng> poly = new ArrayList<LatLng>();

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }

        return poly;

        }
    }
