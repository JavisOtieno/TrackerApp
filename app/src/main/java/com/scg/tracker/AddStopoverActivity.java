package com.scg.tracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.ResponseBody;

public class AddStopoverActivity extends AppCompatActivity implements OnSuccessListener {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Button addStopoverButton;
    private Boolean isTripRunning = false;
    private EditText stopoverEditText;

    private String startLocation;
    private String destinationLocation;
    private String description;
    private String amount;
    private String endpoint;
    private String tripId;
    private SharedPreferences mPrefs;

    private TextView locationDetectTextview;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Boolean isTaskRunning = false;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private String stopoverName;
    private Mylocationdatabasehelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_stopover);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Add Stop Over");

        dbHelper = new Mylocationdatabasehelper(getApplicationContext());
        dbHelper.createTable();

        addStopoverButton = (Button) findViewById(R.id.addStopoverButton);
        stopoverEditText = (EditText) findViewById(R.id.stopoverEditText);
        locationDetectTextview = (TextView) findViewById(R.id.locationDetectTextview);

        Intent intent = getIntent();
        tripId = intent.getStringExtra("tripId");
        endpoint = "trip/"+tripId;

        //START OF LATEST LOCATION CALL UPDATE

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds'

        detectLocation();

        requestLocationUpdates();
        //END OF LATEST LOCATION CALL UPDATE



        addStopoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isTaskRunning) {
                    isTaskRunning = true;
                    stopoverName = stopoverEditText.getText().toString();
                    if (mLastLocation != null) {
                        String locationText = mLastLocation.getLatitude() + " - " + mLastLocation.getLongitude() + "";
                        //       Toast.makeText(getBaseContext(),locationText,Toast.LENGTH_SHORT).show();
                        String lat = mLastLocation.getLatitude() + "";
                        String lon = mLastLocation.getLongitude() + "";
                        long unixTime = System.currentTimeMillis() / 1000L;

                        endpoint = "addstopover";

                        JSONObject requestBody = new JSONObject();
                        try {
                            requestBody.put("name", stopoverName);
                            requestBody.put("lat", lat);
                            requestBody.put("long",lon);
                            requestBody.put("trip_id",tripId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        NetworkUtils.
//                                fetchData("POST",endpoint,
//                                        requestBody, AddStopoverActivity.this,
//                                        AddStopoverActivity.this);

                        dbHelper.insertLocation(mLastLocation.getLatitude()+"",
                                mLastLocation.getLongitude()+"",
                                mLastLocation.getAccuracy()+"",
                                "0","unsynced",
                                Integer.parseInt(tripId),"stopover",stopoverName);

                        Toast.makeText(AddStopoverActivity.this, "Stopover added successfully",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddStopoverActivity.this,
                                ViewTripActivity.class);
                        intent.putExtra("tripId", tripId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
//                        Toasts.toastIconError(ClockinActivity.this,"Detecting Location ...");
                        Toast.makeText(AddStopoverActivity.this,
                                "Detecting Location ...",Toast.LENGTH_SHORT).show();
                        isTaskRunning = false;
                    }
                }



            }
        });

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

    }

    @Override
    public void onSuccess(ResponseBody responseBody) {
        String responseBodyString = null;
        try {
            String result = responseBody.string();
            System.out.println("Result: "+result);

            if (result != null && result.contains("success") &&
                    endpoint.equals("addstopover"))
            {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("status") &&
                        jsonObject.get("status") instanceof String &&
                        jsonObject.get("status").equals("success")
                ) {
                    String message = jsonObject.getString("message");

//                    mPrefs = getSharedPreferences("label", 0);
//                    SharedPreferences.Editor mEditor = mPrefs.edit();
//                    mEditor.putString("tripId", null).commit();

//                    Toasts.toastIconSuccess(EditMeetingActivity.this,message);
                    Toast.makeText(AddStopoverActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddStopoverActivity.this,
                            ViewTripActivity.class);
                    intent.putExtra("tripId", tripId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                else if(result.contains("errors")){
                    String message = jsonObject.getString("message");
//                    Toasts.toastIconError(EditMeetingActivity.this,message);
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                }
                else{

//                    Toasts.toastIconError(EditMeetingActivity.this,
//                            "Error Editing Meeting. Please Try Again");
                    Toast.makeText(AddStopoverActivity.this,
                            "Error Ending Trip. Please Try Again",Toast.LENGTH_SHORT).show();
                    FirebaseCrashlytics.getInstance().recordException(new Exception("Error Ending Trip Error: "+result));

                }

            }
            else{
//                Toasts.toastIconError(IndividualDealActivity.this,"Loading Account Failed. Please Try Again");
                Toast.makeText(getBaseContext(), "Something went wrong. Please Try Again", Toast.LENGTH_SHORT).show();
                FirebaseCrashlytics.getInstance().recordException(new Exception("Add Account Error: "+result));

            }

//            Log.d("API Success", "Data: " + jsonObject.toString());


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onComplete() {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //START OF LATEST LOCATION CODE

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
//                Toasts.toastIconError(ClockinActivity.this,"Location Permission Denied");
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void detectLocation(){
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    mLastLocation = location;


                    if(mLastLocation!=null){
                        //Double distance = MyUtils.calculateDistance(Double.parseDouble(lat1),Double.parseDouble(lon1),location.getLatitude(),location.getLongitude());
                        //toastIconError(distance+"");
                        //+" - "+mLastLocation.getAltitude()
                        //toastIconError(mLastLocation.getLatitude()+" - "+mLastLocation.getLongitude());

                    }else if(mLastLocation!=null){
                        //+" - "+mLastLocation.getAltitude()
                        //toastIconError(mLastLocation.getLatitude()+" - "+mLastLocation.getLongitude());

                    }

                }
            }
        };
    }

    //END OF LATEST LOCATION CODE

    //PICKED FROM OLD CODE BUT MIGHT BE VALID
    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            detectLocation();
        }
    }
    //PICKED FROM OLD CODE BUT MIGHT BE VALID
    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


}