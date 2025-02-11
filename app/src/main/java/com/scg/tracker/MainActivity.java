package com.scg.tracker;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private int REQUEST_LOCATION = 99;
    private MapView mapView;
    private GoogleMap googleMap;
    private String mLatitude;
    private String mLongitude;
    private String mName;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker userMarker;
    private ActionBar actionBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dashboard");

        initNavigationMenu();
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            // should select the best provider but bypassing with NETWORK Provider for now
            // location = locationManager.getLastKnownLocation(provider);
            // location = locationManager.getLastKnownLocation(provider);\
            Intent serviceIntent = new Intent(this, LocationService.class);
            startForegroundService(serviceIntent);

        }

        // Start the LocationService


//        scheduleLocationWorker();
//
//        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        initToolbar();
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
//        Toast.makeText(MainActivity.this,"Test",
//                Toast.LENGTH_SHORT).show();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {

                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    updateLocationOnMap(location);
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));

                    // Optionally, add a marker at the current location
//                    googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("My Current Location"));



                }

            }
        };
//        throw new RuntimeException("Test Crash"); // Force a crash


    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000); // 10 seconds
            locationRequest.setFastestInterval(5000); // 5 seconds
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                //Toast.makeText(getApplicationContext(), item.getTitle() + " Selected", Toast.LENGTH_SHORT).show();
                if(item.getTitle().equals("Dashboard")){
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //finish();
                    //startActivity(getIntent());
                }else if(item.getTitle().equals("Trips")){
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }



                else if(item.getTitle().equals("Log out")) {
//                    SharedPreferences.Editor mEditor = mPrefs.edit();
//                    mEditor.putString("userId", "0").commit();
//                    EncryptedPrefsUtil.saveString("userId", "0");
//                    EncryptedPrefsUtil.saveString("authToken", "");
//                    EncryptedPrefsUtil.saveString("email", "");
//                    EncryptedPrefsUtil.saveString("password", "");

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

//                else if(item.getTitle().equals("Profile")){
//                    Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
//                    startActivity(intent);
//                }else if(item.getTitle().equals("POS Audits")){
//                    Intent intent = new Intent(MainActivity.this,POSAuditsListActivity.class);
//                    startActivity(intent);
//                }

                //actionBar.setTitle(item.getTitle());
                drawer.closeDrawers();
                return true;

            }
        });

        // open drawer at start
        //drawer.openDrawer(GravityCompat.START);
    }


    private void updateLocationOnMap(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//        Toast.makeText(MainActivity.this,
//                "Location found : Lat "+location.getLatitude()+
//                        " - Long: "+location.getLongitude(),
//                Toast.LENGTH_SHORT).show();
        if (userMarker != null) {
//              userMarker.setPosition(userLocation);
        } else {
//              userMarker = googleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
        }
         googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }


    public void scheduleLocationWorker() {
        WorkRequest locationWorkRequest = new PeriodicWorkRequest.Builder(LocationWorker.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueue(locationWorkRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // You can customize the map here if needed
        // For example, enable zoom controls:
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        Intent intent = getIntent();
        mLatitude = "-1.289";
        mLongitude = "36.822";
        mName = intent.getStringExtra("name");

        // Add a marker at a specific location and move the camera
        // Example: San Francisco
        LatLng sanFrancisco = new LatLng(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
//        googleMap.addMarker(new MarkerOptions().position(sanFrancisco).title(mName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sanFrancisco, 12)); // Zoom level 12



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            startLocationUpdates();
        }



    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && (grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, now you can access location
                Intent serviceIntent = new Intent(this, LocationService.class);
                startForegroundService(serviceIntent);


//                if (mapView != null) {
//                    Toast.makeText(MainActivity.this, "not null mapview",
//                            Toast.LENGTH_SHORT).show();
//                    mapView.getMapAsync(this);
//                } else {
//                    Toast.makeText(MainActivity.this, "mapview null",
//                            Toast.LENGTH_SHORT).show();
//                }
//                startLocationUpdates();
            } else {
                // Permission denied, handle accordingly
                Log.e("MainActivity", "Location permission denied");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}