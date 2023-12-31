package com.example.womensafety;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.womensafety.databinding.ActivityGoogleMapBinding;

public class googleMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityGoogleMapBinding binding;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private final long MIN_TIME=1000;
    private  final long MIN_DIST=5;
    private LatLng latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGoogleMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try{
                    LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("I am Here.");
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14F));


                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        };

        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DIST,locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DIST,locationListener);
        }catch (SecurityException e){
            e.printStackTrace();
        }
        

    }
}