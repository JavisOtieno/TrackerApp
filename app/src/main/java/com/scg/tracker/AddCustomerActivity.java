package com.scg.tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import android.content.SharedPreferences;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class AddCustomerActivity extends AppCompatActivity implements OnSuccessListener {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Button btSubmit;
    private Boolean isCustomerRunning = false;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private String startLocation;
    private String destinationLocation;
    private String description;
    private String amount;
    private SharedPreferences mPrefs;
    private String endpoint;

    private TextView locationDetectTextview;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Boolean isTaskRunning = false;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Mylocationdatabasehelper dbHelper;
    private String mCustomerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_customer);

        dbHelper = new Mylocationdatabasehelper(getApplicationContext());
        dbHelper.createTable();


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Add Customer");

        btSubmit = (Button) findViewById(R.id.submitCustomerButton);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);


//        Intent i = getIntent();
////        mCustomerId = i.getStringExtra("customerId");
//
//        if(i.getStringExtra("customerId") == null){
//            mCustomerId = null;
//        }else{
//            mCustomerId = i.getStringExtra("customerId");
//        }
//        Toast.makeText(getBaseContext(),mCustomerId+"-",Toast.LENGTH_SHORT).show();



        //START OF LATEST LOCATION CALL UPDATE
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds'

        detectLocation();

        requestLocationUpdates();
        //END OF LATEST LOCATION CALL UPDATE


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCustomerRunning) {
                    isTaskRunning = true;
//                    description = descriptionEditText.getText().toString();
//                    amount = amountEditText.getText().toString();
                    String name = nameEditText.getText().toString();
                    String phone = phoneEditText.getText().toString();

                    JSONObject requestBody = new JSONObject();
                    try {

                        requestBody.put("name", name);
                        requestBody.put("phone", phone);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    endpoint = "addcustomer";
                    NetworkUtils.
                            fetchDataPost(endpoint,
                                    "", requestBody,AddCustomerActivity.this,
                                    AddCustomerActivity.this);

                    isTaskRunning = false;



                }

            }
        });


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

    @Override
    public void onSuccess(ResponseBody responseBody) {

        String responseBodyString = null;
        try {
            String result = responseBody.string();
            System.out.println("Result: "+result);

            if (result != null && result.contains("Customer") &&
                    endpoint.contains( "addcustomer")){


                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("status") &&
                        jsonObject.get("status") instanceof String &&
                        jsonObject.get("status").equals("success")
                ) {

                    String customerId = jsonObject.getString("customerId");
                    mPrefs = getSharedPreferences("label", 0);
//                    SharedPreferences.Editor mEditor = mPrefs.edit();
//                    mEditor.putString("customerId", customerId).commit();



//                    dbHelper.insertLocation(mLastLocation.getLatitude()+"",
//                            mLastLocation.getLongitude()+"",
//                            mLastLocation.getAccuracy()+"",
//                            "0","unsynced",
//                            Integer.parseInt(customerId),"start",startLocation);

//                    Toasts.toastIconSuccess(AddCustomerActivity.this,"Customer Added Successfully");
                    Toast.makeText(getBaseContext(), "Customer Added Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddCustomerActivity.this, AddTripActivity.class);
                    intent.putExtra("customerId", customerId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                else if(result.contains("errors")){
                    String message = jsonObject.getString("message");
//                    Toasts.toastIconError(AddCustomerActivity.this,message);
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                }
                else{

//                    Toasts.toastIconError(AddCustomerActivity.this,"Error Adding Customer. Please Try Again");
                    Toast.makeText(getBaseContext(), "Error Adding Customer. Please Try Again", Toast.LENGTH_SHORT).show();
//                    FirebaseCrashlytics.getInstance().recordException(new Exception("Add Deal Error: "+result));

                }

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
        isCustomerRunning = false;
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