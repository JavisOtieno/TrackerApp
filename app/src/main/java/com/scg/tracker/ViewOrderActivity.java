package com.scg.tracker;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.ResponseBody;

public class ViewOrderActivity extends AppCompatActivity implements OnSuccessListener {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Button startTripButton;
    private Boolean isTripRunning = false;
    private EditText startLocationEditText;
    private EditText destinationLocationEditText;
    private EditText descriptionEditText;
    private EditText stopoverEditText;
    private EditText amountEditText;
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
    private Button addStopoverButton;
    private Mylocationdatabasehelper dbHelper;
    private String mTripId;
    private String endLocationName;
    private Button startDirectionsButton;
    private Button contactCustomerButton;
    private EditText customerNameEditText;
    private EditText customerPhoneEditText;
    private Button tripRouteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_order);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("View Order");

        dbHelper = new Mylocationdatabasehelper(getApplicationContext());
        dbHelper.createTable();



        startTripButton = (Button) findViewById(R.id.startTripButton);
        startLocationEditText = (EditText) findViewById(R.id.startLocationEditText);
        destinationLocationEditText = (EditText) findViewById(R.id.destinationLocationName);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
//        stopoverEditText = (EditText) findViewById(R.id.stopoverEditText);
        locationDetectTextview = (TextView) findViewById(R.id.locationDetectTextview);
        startDirectionsButton = (Button)  findViewById(R.id.startingDirectionsButton);
        contactCustomerButton = (Button) findViewById(R.id.contactCustomerButton);
        customerNameEditText = (EditText) findViewById(R.id.customerNameEditText);
        customerPhoneEditText = (EditText) findViewById(R.id.customerPhoneEditText);
        tripRouteButton = (Button) findViewById(R.id.tripRouteButton);
//        addStopoverButton = (Button) findViewById(R.id.addStopoverButton);

        Intent intent = getIntent();
        tripId = intent.getStringExtra("tripId");
        endpoint = "trip/"+tripId;


        NetworkUtils.
                fetchData("GET",endpoint,
                        null, ViewOrderActivity.this,
                        ViewOrderActivity.this);

        //START OF LATEST LOCATION CALL UPDATE

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds'

        detectLocation();

        requestLocationUpdates();
        //END OF LATEST LOCATION CALL UPDATE





        startTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isTaskRunning) {
                    isTaskRunning = true;
                    if (mLastLocation != null) {
                        String locationText = mLastLocation.getLatitude() + " - " + mLastLocation.getLongitude() + "";
                        //       Toast.makeText(getBaseContext(),locationText,Toast.LENGTH_SHORT).show();
                        String lat = mLastLocation.getLatitude() + "";
                        String lon = mLastLocation.getLongitude() + "";
                        long unixTime = System.currentTimeMillis() / 1000L;

                        endpoint = "starttrip/"+tripId;

                        JSONObject requestBody = new JSONObject();
                        try {

                            requestBody.put("lat", lat);
                            requestBody.put("long",lon);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        NetworkUtils.
                                fetchData("PUT",endpoint,
                                        requestBody, ViewOrderActivity.this,
                                        ViewOrderActivity.this);

                    } else {
//                        Toasts.toastIconError(ClockinActivity.this,"Detecting Location ...");
                        Toast.makeText(ViewOrderActivity.this,
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

            if (result != null && result.contains("trip") &&
                    endpoint.equals("trip/"+tripId)){

                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("trip") &&
                        jsonObject.get("trip") instanceof JSONObject
                ) {

                    JSONObject trip = jsonObject.getJSONObject("trip");

                    startLocation = trip.getString("start_location");
                    startLocationEditText.setText(startLocation);
                    endLocationName = trip.
                            getString("end_location");
                    destinationLocationEditText.setText(trip.
                            getString("end_location"));
                    descriptionEditText.setText(trip.
                            getString("description"));
                    amountEditText.setText(trip.
                            getString("amount"));
                    JSONArray stopovers = trip.getJSONArray("locations");
                    String stopoverNames = "";
                    String customerPhone = trip.getJSONObject("customer").getString("phone");

                    customerNameEditText.setText(trip.getJSONObject("customer").getString("name"));
                    customerPhoneEditText.setText(trip.getJSONObject("customer").getString("phone"));


                    for (int i = 0; i < stopovers.length(); i++) {
                        String name = stopovers.getJSONObject(i).getString("name");
                        String createdAt = stopovers.getJSONObject(i).getString("created_at");
                        ZonedDateTime dateTime = Instant.parse(createdAt).atZone(ZoneId.of("Africa/Nairobi"));
                        String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));

                        int currentPosition = i+1;
                        stopoverNames +=currentPosition+". "+name+" "+formatted+"\n";
                        //obj.imageDrw = drw_arr.getDrawable(obj.image);
//                        items.add(obj);

                    }

                    if(stopovers.length()==0){
                        stopoverNames = "No stopovers yet";
                    }

                    contactCustomerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+customerPhone));
                            startActivity(intent);
                        }
                    });

                    tripRouteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
//                        public void onClick(View view) {
//                            String strUri = null;
//                            try {
//                                String startLat = trip.getString("start_lat");
//                                String startLong = trip.getString("start_long");
//                                String endLat = trip.getString("end_lat");
//                                String endLong = trip.getString("end_long");
//
//                                strUri = "http://maps.google.com/maps?saddr=" + startLat + "," + startLong +
//                                        "&daddr=" + endLat + "," + endLong;
//
//                            } catch (JSONException e) {
//                                throw new RuntimeException(e);
//                            }
//
//                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
//
//                            if (intent.resolveActivity(getPackageManager()) != null) {
//                                try {
//                                    startActivity(intent);
//                                } catch (ActivityNotFoundException e) {
//                                    Toast.makeText(ViewOrderActivity.this, "No application available to open maps", Toast.LENGTH_SHORT).show();
//                                    FirebaseCrashlytics.getInstance().recordException(e);
//                                }
//                            } else {
//                                Toast.makeText(ViewOrderActivity.this, "No application available to open maps", Toast.LENGTH_SHORT).show();
//                                FirebaseCrashlytics.getInstance().recordException(new Exception("ViewOrderActivity: Attempt to open google maps failed: intent.resolveActivity returned null"));
//                            }
//                        }
                        public void onClick(View view) {
                            String strUri = null;
                            try {
                                String startLat = trip.getString("start_lat");
                                String startLong = trip.getString("start_long");
                                String endLat = trip.getString("end_lat");
                                String endLong = trip.getString("end_long");
                                String startLabel = trip.getString("start_location");
                                String endLabel = trip.getString("end_location");

                                strUri = "http://maps.google.com/maps?" +
                                        "saddr=" + startLat + "," + startLong + " (" + startLabel + ")" +
                                        "&daddr=" + endLat + "," + endLong + " (" + endLabel + ")";

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));

                            if (intent.resolveActivity(getPackageManager()) != null) {
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(ViewOrderActivity.this, "No application available to open maps", Toast.LENGTH_SHORT).show();
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                }
                            } else {
                                Toast.makeText(ViewOrderActivity.this, "No application available to open maps", Toast.LENGTH_SHORT).show();
                                FirebaseCrashlytics.getInstance().recordException(
                                        new Exception("ViewOrderActivity: Attempt to open Google Maps failed: intent.resolveActivity returned null")
                                );
                            }
                        }


                    });

                    startDirectionsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String strUri = null;
                            try {
                                strUri = "http://maps.google.com/maps?q=loc:" +
                                        trip.getString("start_lat") + "," + trip.getString("start_long")
                                        + " ("+trip.getString("start_location") +")";
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));

// Check if there's an app that can handle the intent
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    // Handle the case where no app can handle the intent
//                                        Toasts.toastIconError(IndividualOutletActivity.this,"No application available to open maps");
                                    Toast.makeText(ViewOrderActivity.this,"No application available to open maps",Toast.LENGTH_SHORT).show();
                                    FirebaseCrashlytics.getInstance().recordException(e);

                                }
                            } else {
                                // Handle the case where no app can handle the intent
//                                    Toasts.toastIconError(IndividualOutletActivity.this,"No application available to open maps");
                                Toast.makeText(ViewOrderActivity.this,"No application available to open maps",Toast.LENGTH_SHORT).show();
                                FirebaseCrashlytics.getInstance().recordException(new Exception("IndividualOutletActivity: Attempt to open google maps failed: " +
                                        "ThisLine: intent.resolveActivity(getPackageManager()) != null"));

                            }

                        }
                    });

//                    stopoverEditText.setText(stopoverNames);


//                    if (trip.getString("status").equals("order")) {
//
////                        locationDetectTextview.setVisibility(View.VISIBLE);
//                        startTripButton.setVisibility(View.VISIBLE);
//                        startDirectionsButton.setVisibility(View.VISIBLE);
//                        contactCustomerButton.setVisibility(View.VISIBLE);
////                        addStopoverButton.setVisibility(View.VISIBLE);
//
//                    }



                    String createdAt = trip.getString("created_at");
                    ZonedDateTime dateTime = Instant.parse(createdAt).atZone(ZoneId.of("Africa/Nairobi"));
                    String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
                    String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
//                    dateTextView.setText(formattedDate);
//                    timeTextView.setText(formattedTime);


                }
                else if(result.contains("errors")){
                    String message = jsonObject.getString("message");
//                    Toasts.toastIconError(IndividualDealActivity.this,message);
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                }
                else{
//                    Toasts.toastIconError(IndividualDealActivity.this,"Error Loading Account. Please Try Again");
                    Toast.makeText(getBaseContext(), "Error Loading Trip. Please Try Again", Toast.LENGTH_SHORT).show();
//                    FirebaseCrashlytics.getInstance().recordException(new Exception("Add Account Error: "+result));

                }

            }
            else if (result != null
//                    && result.contains("success")
                    && endpoint.equals("starttrip/"+tripId))
            {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("status") &&
                        jsonObject.get("status") instanceof String &&
                        jsonObject.get("status").equals("success")
                ) {
                    String message = jsonObject.getString("message");

                    mPrefs = getSharedPreferences("label", 0);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
//                    mTripId = mPrefs.getString("tripId", null);

                    mEditor.putString("tripId", tripId).commit();

                    dbHelper.insertLocation(mLastLocation.getLatitude()+"",
                            mLastLocation.getLongitude()+"",
                            mLastLocation.getAccuracy()+"",
                            "0","unsynced",
                            Integer.parseInt(tripId),"start",startLocation);

//                    mEditor.putString("tripId", null).commit();

//                    Toasts.toastIconSuccess(EditMeetingActivity.this,message);
                    Toast.makeText(ViewOrderActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ViewOrderActivity.this,
                            ViewTripActivity.class);
                    intent.putExtra("tripId", tripId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                else if(result.contains("errors")){
                    String message = jsonObject.getString("message");
//                    Toasts.toastIconError(EditMeetingActivity.this,message);
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    isTaskRunning = false;
                }
                else{

//                    Toasts.toastIconError(EditMeetingActivity.this,
//                            "Error Editing Meeting. Please Try Again");

                    Toast.makeText(ViewOrderActivity.this,
                            "Error Starting Trip. Please Try Again",Toast.LENGTH_SHORT).show();
                    FirebaseCrashlytics.getInstance().recordException(new Exception("Error Starting Trip Error: "+result));
                    isTaskRunning = false;
                }

            }

            else{
                isTaskRunning = false;
//                Toasts.toastIconError(IndividualDealActivity.this,"Loading Account Failed. Please Try Again");
                Toast.makeText(getBaseContext(), "Something went wrong. Please Try Again", Toast.LENGTH_SHORT).show();
                FirebaseCrashlytics.getInstance().recordException(new Exception("View Order Error: "+result));

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
                Intent intent = new Intent(ViewOrderActivity.this, PendingOrdersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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