package com.example.womensafety;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.womensafety.R.string.google_api_key;
import static com.example.womensafety.R.string.google_map_key;

public class locationMap extends FragmentActivity implements OnMapReadyCallback {

    Spinner spinner;
    Button find_btn;
    GoogleMap map;
    SupportMapFragment supportMapFragment;
    Location currentlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 101;
    double currentlat = 0, currentlong = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);

        spinner = findViewById(R.id.spinner);
        find_btn = findViewById(R.id.findbtn);


        String[] placetypeList = {"hospital", "police station"};
        String[] placeNamelist = {"Hospital", "Police Station"};

        spinner.setAdapter(new ArrayAdapter<>(locationMap.this,
                android.R.layout.simple_spinner_dropdown_item, placeNamelist));


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getcurrentLocation();


        find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = spinner.getSelectedItemPosition();
                String url = "https://maps.googleapis.com/maps/api/places/nearbysearch/json" + "?location=" + currentlocation.getLatitude() + "," + currentlocation.getLongitude() +
                        "&radius=5000" + "&type=" + placetypeList[i] +
                        "&sensor=true" + "&key=" + getResources().getString(google_map_key);

                //new PlaceTask().execute(url);
            }
        });
    }


    private void getcurrentLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

            return;
        }else {
           Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        currentlocation = location;

                        Toast.makeText(getApplicationContext(), "Latitude:" + currentlocation.getLatitude() + "  " + "Longitude:" + currentlocation.getLongitude(), Toast.LENGTH_LONG).show();


                        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                        supportMapFragment.getMapAsync(locationMap.this);
                    }
                }
            });
        }


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        LatLng latLng=new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("I am Here.");
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        map.addMarker(markerOptions);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getcurrentLocation();
                }
                break;
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {
            String data=null;
            try {
                 data=downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {

        URL url=new URL(string);
        HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
        httpURLConnection.connect();

        InputStream stream=httpURLConnection.getInputStream();
        BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder=new StringBuilder();
        String line= "";

        while((line=reader.readLine())!=null){

            builder.append(line);
        }
        String data=builder.toString();
        reader.close();
        return data;
    }

    private  class ParserTask extends AsyncTask<String,Integer,List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser=new JsonParser();

            List<HashMap<String,String>> mapList=null;
            JSONObject object=null;

            try {
                object=new JSONObject(strings[0]);
                mapList=jsonParser.parseResult(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            map.clear();
            for(int i=0;i<hashMaps.size();i++){
                HashMap<String ,String> hashmapList=hashMaps.get(i);

                double lat= Double.parseDouble(hashmapList.get("lat"));

                double lng= Double.parseDouble(hashmapList.get("lng"));
                String name=hashmapList.get("name");

                LatLng latLng=new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude());

                MarkerOptions markerOptions=new MarkerOptions();

                markerOptions.position(latLng);
                markerOptions.title(name);

                map.addMarker(markerOptions);

            }
        }
    }

}