package com.sest1601.lab7;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sest1601.lab7.database.HistoryEntity;
import com.sest1601.lab7.history.AsyncDbCall;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        LatLngBounds kov =  new LatLngBounds((new LatLng(55.001099, 11.10694)), new LatLng(69.063141, 24.16707));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(kov, 100));

        new AsyncDbCall(new AsyncDbCall.OnFetchFinishedListener() {
            @Override
            public void onFetchFinished(List<HistoryEntity> result) {
                // Result from the AsyncTask (List of history Entities)
                addMarkers(result);
            }
        }).execute();
    }

    // Add Markers to map for all Enteties that have Lat and Long coordinates
    private void addMarkers(List<HistoryEntity> callHistory) {
        for (HistoryEntity entity: callHistory) {
            if (!entity.getLat().equals("No Permission, no audio loaded!") && !entity.getLng().equals("No Permission, no audio loaded!")) {
                LatLng loc = new LatLng(Double.parseDouble(entity.getLat()), Double.parseDouble(entity.getLng()));
                mMap.addMarker(new MarkerOptions()
                   .position(loc)
                   .title(entity.getNumber())
                   .snippet(entity.getDate())
                );
            }
        }


    }
}
