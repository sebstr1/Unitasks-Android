package com.sest1601.bathingsites;

import android.Manifest;
import android.app.Activity;
import android.arch.persistence.room.util.StringUtil;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sest1601.bathingsites.database.AsyncDbQuery;
import com.sest1601.bathingsites.database.BathsiteEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private HashMap<BathsiteEntity, Pair<Marker, BathsiteEntity>> markerMap;
    private int maxDistance;
    private LocationManager locationManager;
    private LocationListener locationlistener;
    private GoogleMap mMap;
    private ArrayList<BathsiteEntity> allmarkers;
    private Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        int meters  = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_GPSradius), "50000"));
        maxDistance = meters*1000;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        markerMap = new HashMap<>();

        // Collects all bathsites from DB
        new AsyncDbQuery(this, new AsyncDbQuery.onQueryFinishedListener() {
            @Override
            public void onQueryFinished(List<BathsiteEntity> result) {
                allmarkers = new ArrayList<>(result);
                initiateMylocation();
            }
        }).execute();


        // Location listener implementation
        locationlistener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                myLocation = location;
                handleMyLocation();
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
        };
    }

    // Get current location
    private void initiateMylocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Enable button to find users position if clicked
            mMap.setMyLocationEnabled(true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locationlistener);

            // Get current location
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location.
                            if (location != null) {
                                myLocation = location;
                                handleMyLocation();
                            }
                        }
                    });
        } else {
            finish();
        }
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

        // If marker clicked, show relevant info and dont show null info.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Loop through markermap to find the clicked marker
                for (Map.Entry<BathsiteEntity, Pair<Marker, BathsiteEntity>> entry : markerMap.entrySet()) {

                    // If we found the correct marker, set the values and display it.
                    if (marker.equals(entry.getValue().first)) {
                        BathsiteEntity bathsite = entry.getValue().second;
                        String name = "";
                        String desc = "";
                        String address = "";
                        String rating = "";
                        String water = "";
                        String date = "";

                        // Check so they are not null (empty)
                        if (!(TextUtils.isEmpty(bathsite.getName()))) name = "Name: " + bathsite.getName() + "\n";
                        if (!(TextUtils.isEmpty(bathsite.getDescription()))) desc = "Desc: " + bathsite.getDescription() + "\n";
                        if (!(TextUtils.isEmpty(bathsite.getAddress()))) address = "Address: " + bathsite.getAddress() + "\n";
                        if ((bathsite.getRating() != 0.0)) rating = "Rating: " + bathsite.getRating() + "\n";
                        if (!(TextUtils.isEmpty(bathsite.getWatertemp()))) water = "Water temp: " + bathsite.getWatertemp() + "\n";
                        if (!(TextUtils.isEmpty(bathsite.getWatertempdate()))) date = "Date for temp: " + bathsite.getWatertempdate() + "\n";


                        // Create dialog and show values
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapActivity.this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Bathsite info");
                        alertBuilder.setMessage(name + desc + address + rating + water + date);
                        alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                }
                return false;
            }
        });
    }


    // Add circle that shows active radius, add new markers & zoom to location
    private void handleMyLocation() {
        mMap.clear();
        LatLng center = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());

        mMap.addCircle(new CircleOptions()
                .center(center)
                .radius(maxDistance)
                .clickable(false)
        );

        addMarkers(allmarkers);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center,9));

    }



    // Add Markers to map for all Enteties that have Lat and Long coordinates
    private void addMarkers(List<BathsiteEntity> sites) {
        for (BathsiteEntity entity: sites) {

            if (!TextUtils.isEmpty(entity.getLat()) && !TextUtils.isEmpty(entity.getLng())) {

                LatLng bathsiteLoc = new LatLng(Double.parseDouble(entity.getLat()), Double.parseDouble(entity.getLng()));
                Location bathsite = new Location(LocationManager.GPS_PROVIDER);
                bathsite.setLatitude(Double.parseDouble(entity.getLat()));
                bathsite.setLongitude(Double.parseDouble(entity.getLng()));

                if (!(myLocation.distanceTo(bathsite) > maxDistance)) {

                    Marker m = mMap.addMarker(new MarkerOptions()
                            .position(bathsiteLoc)
                            .title(entity.getName())

                    );
                    markerMap.put(entity, Pair.create(m, entity));
                }
            }
        }
    }
}

