package com.example.myloc;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    static SQLiteDatabase database;

    LocationManager locationManager;
    LocationListener locationListener;

    Integer artId;

    Integer a;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = MapsActivity.this.openOrCreateDatabase("Locs", MODE_PRIVATE, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


         locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences("com.atilsamancioglu.mynewtravelbook",MODE_PRIVATE);
                boolean firstTimeCheck = sharedPreferences.getBoolean("notFirstTime",false);


                if (!firstTimeCheck) {
                    LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                    sharedPreferences.edit().putBoolean("notFirstTime",true).apply();


                }

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

         Intent intent = getIntent();
         if(intent.getStringExtra("info").matches("new")) {
             if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
             } else {
                 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                 Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                 if (lastLocation != null) {
                     LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                 }
             }



        a=3;
         }

        else{
            mMap.clear();
            artId = intent.getIntExtra("locId", 1);
            try {


                Cursor cursor = database.rawQuery("SELECT * FROM locs WHERE id = ?", new String[]{String.valueOf(artId)});


                int locNameIx = cursor.getColumnIndex("locname");

                int locLatIx = cursor.getColumnIndex("loclat");
                int locLongIx = cursor.getColumnIndex("loclong");
                while (cursor.moveToNext()) {
                    String locName = cursor.getString(locNameIx);
                    Double locLat1 = cursor.getDouble(locLatIx);
                    Double locLong1 = cursor.getDouble(locLongIx);
                    LatLng latLng1 = new LatLng(locLat1, locLong1);

                    mMap.addMarker(new MarkerOptions().title(locName).position(latLng1));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));
                    a=2;

                }

                cursor.close();

            }
            catch (Exception e)
            {

            }
        }





        mMap.setOnMapLongClickListener(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Intent intent = getIntent();
        if (intent.getStringExtra("info").matches("new")) {
            if (grantResults.length > 0) {
                if (requestCode == 1) {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                        if (lastLocation != null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                        }


                    }
                }
            }

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        else{
            mMap.clear();
            artId = intent.getIntExtra("locId", 1);
            try {


                Cursor cursor = database.rawQuery("SELECT * FROM locs WHERE id = ?", new String[]{String.valueOf(artId)});


                int locNameIx = cursor.getColumnIndex("locname");

                int locLatIx = cursor.getColumnIndex("loclat");
                int locLongIx = cursor.getColumnIndex("loclong");
                while (cursor.moveToNext()) {
                    String locName = cursor.getString(locNameIx);
                    Double locLat1 = cursor.getDouble(locLatIx);
                    Double locLong1 = cursor.getDouble(locLongIx);
                    LatLng latLng1 = new LatLng(locLat1, locLong1);

                    mMap.addMarker(new MarkerOptions().title(locName).position(latLng1));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));
                    a=2;

                }

                cursor.close();

            }
            catch (Exception e)
            {

            }
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare();

                    if (addressList.get(0).getSubThoroughfare() != null) {
                        address += addressList.get(0).getSubThoroughfare();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address.matches("")) {
            address = "No Address";
        }



        String locName = address;
        Double locLat = latLng.latitude;
        Double locLong = latLng.longitude;



            try {


                database = this.openOrCreateDatabase("Locs", MODE_PRIVATE, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS locs (id INTEGER PRIMARY KEY , locname VARCHAR, loclat REAL, loclong REAL)");

                String sqlString = "INSERT INTO locs (locname, loclat, loclong) VALUES (?, ?, ?)";
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1, locName);
                sqLiteStatement.bindDouble(2, locLat);
                sqLiteStatement.bindDouble(3, locLong);

                sqLiteStatement.execute();

                Toast toast = new Toast(this);
                toast.makeText(this,"Konum Kaydedildi.",Toast.LENGTH_SHORT).show();
                 a = 1;



            }

            catch (Exception e){

            }




        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

    }

    @Override
    public void onBackPressed() {
        if(a==1 || a==2 || a==3) {
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            a=0;
        }
        super.onBackPressed();

    }
}