package com.scg.tracker;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.scg.tracker.adapters.AdapterListCustomers;
import com.scg.tracker.adapters.AdapterListCustomers;
import com.scg.tracker.models.Customer;
import com.scg.tracker.models.Customer;
import com.scg.tracker.util.EncryptedPrefsUtil;
//import com.scg.tracker.util.Toasts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;

public class SelectCustomerActivity extends AppCompatActivity implements OnSuccessListener {

    private Button selectCustomerButton;
    private RecyclerView recyclerView;

    private ProgressDialog pd;
    private JSONArray customers = null;
    private AdapterListCustomers mAdapter;
    private View parent_view;

    private SharedPreferences mPrefs;
    private String mUserId;
    private String mAuthToken;
    private int REQUEST_LOCATION = 99;

    private LocationManager locationManager;
    private String provider;
    //private MyLocationListener mylistener;
    private Criteria criteria;
    private Location location;
    private FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    private String mCustomerName;

    private Location mLastLocation;
    private TextView emptyView;

    private String customerId;
    private String lat1;
    private String lon1;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private long lastIntentTime = 0;
    private static final long MIN_CLICK_INTERVAL = 1000; // 1000 milliseconds

    private List<Customer> items;

    private TextView mNoCustomersTextView;
    private String mCallingActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_customer);
        initToolbar();

        mPrefs = getSharedPreferences("label", 0);
//        mUserId = mPrefs.getString("userId", "0");
//        mAuthToken = mPrefs.getString("authToken", "");
        Intent i = getIntent();
        mCallingActivity = mPrefs.getString("callingActivityOffRouteCustomer", "");

        mUserId = EncryptedPrefsUtil.getString("userId", "0");
        mAuthToken = EncryptedPrefsUtil.getString("authToken", "");

        mNoCustomersTextView = (TextView) findViewById(R.id.noCustomersText);



//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // user defines the criteria
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   //default
        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        //provider = locationManager.getBestProvider(criteria, false);
//        provider = LocationManager.NETWORK_PROVIDER;



        emptyView = (TextView) findViewById(R.id.empty_view);

        //START OF LATEST LOCATION CALL UPDATE

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds'
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//
//                    mLastLocation = location;
//
//
//                    if(lat1!=null && mLastLocation!=null){
//                        Double distance = MyUtils.calculateDistance(Double.parseDouble(lat1),Double.parseDouble(lon1),location.getLatitude(),location.getLongitude());
//                        //toastIconError(distance+"");
//                        //+" - "+mLastLocation.getAltitude()
//                        toastIconError(mLastLocation.getLatitude()+" - "+mLastLocation.getLongitude()+" - distance: "+distance*1000);
//
//                    }else if(mLastLocation!=null){
//                        //+" - "+mLastLocation.getAltitude()
//                        toastIconError(mLastLocation.getLatitude()+" - "+mLastLocation.getLongitude());
//
//                    }
//
//                }
//            }
//        };
//
//        requestLocationUpdates();

        //END OF LATEST LOCATION CALL UPDATE







//        location = null;
//
//        if (ActivityCompat.checkSelfPermission(SelectCustomerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SelectCustomerActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(SelectCustomerActivity.this, new String[]
//                    {ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
//
//        } else {
//            //should select the best provider but bypassing with NETWORK Provider for now
//            //location = locationManager.getLastKnownLocation(provider);
//            location = locationManager.getLastKnownLocation(provider);
//        }



        //mylistener = new MyLocationListener();

        //selectCustomerButton=(Button) findViewById(R.id.selectCustomerButton);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

//        new JsonTask().execute(Constants.BASE_URL + "api/customers", "GET");

        NetworkUtils.
                fetchData("GET","customers",null
                        ,SelectCustomerActivity.this,SelectCustomerActivity.this);

//        selectCustomerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //Intent intent = new Intent(ExistingCustomerActivity.this, SelectActionActivity.class);
//                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                //startActivity(intent);
//                //Clocking in
//                long unixTime = System.currentTimeMillis() / 1000L;
//                new JsonTask().execute(Constants.BASE_URL + "api/checkin","POST","1",unixTime+"","32.45567","44.4594","1");
//
//            }
//        });

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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Existing Customer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Tools.setSystemBarColor(this);
    }

    @Override
    public void onSuccess(ResponseBody responseBodyReceived) {

        Log.d("API Response", responseBodyReceived.toString());
        System.out.println("result: "+responseBodyReceived);

        try {
            // Get the response body as a string
            String responseBody = responseBodyReceived.string();

            // Create a JSONObject from the response string
            JSONObject jsonObject = new JSONObject(responseBody);
            List<Customer> items = new ArrayList<>();

            customers = jsonObject.getJSONArray("customers");
            System.out.println("Accounts length: "+customers.length());

            for (int i = 0; i < customers.length(); i++) {
                Customer obj = new Customer();
                //obj.image = drw_arr.getResourceId(i, -1);
                obj.id = customers.getJSONObject(i).getString("id");
                obj.name = customers.getJSONObject(i).getString("name");
                obj.phone = customers.getJSONObject(i).getString("phone");

//                Toasts.toastIconError(getContext(),obj.status);

                String accountName = "";

//                if (
//                customers.getJSONObject(i).has("lead") &&
//                        customers.getJSONObject(i).get("lead") instanceof JSONObject
//                ) {
//                    accountName = customers.getJSONObject(i).
//                            getJSONObject("lead").getString("name");
//                }else if(
//                        customers.getJSONObject(i).has("account") &&
//                                customers.getJSONObject(i).get("account") instanceof JSONObject
//                ){

//                }
//                accountName = customers.getJSONObject(i).
//                        getJSONObject("account").optString("name");
//
//                obj.account_name = accountName;

                String createdAt = customers.getJSONObject(i).getString("created_at");
                ZonedDateTime dateTime = Instant.parse(createdAt).atZone(ZoneId.of("Africa/Nairobi"));
                String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy \nHH:mm"));
                obj.date = formatted;
                //obj.imageDrw = drw_arr.getDrawable(obj.image);
                items.add(obj);

            }

            if (items.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }


            //set data and list adapter
            mAdapter = new AdapterListCustomers(items);
            recyclerView.setAdapter(mAdapter);

            mAdapter.setOnItemClickListener(new AdapterListCustomers.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Customer obj, int position) {

                    Customer customer = items.get(position);

                    Intent intent = new Intent(SelectCustomerActivity.this,
                            AddTripActivity.class);
                    intent.putExtra("customerId",customer.id);
                    System.out.println("customerId: "+customer.id);
                    startActivity(intent);

                }
            });

//                        mAdapter.setOnItemClickListener(new AdapterListAccounts.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(View view, Account obj, int position) {
//
//                                Account account = items.get(position);
//
//                                PreventDoubleClickOnLists.
//                                        showActivityWithoutDoubleClick(
//                                                "account",AccountsActivity.this,
//                                                account, IndividualAccountActivity.class);
//
//
//
//                            }
//                        });


            // Extract values from the JSON object
//                        JSONArray accounts = jsonObject.getJSONArray("customers");
//                        String name = accounts.getJSONObject(0).getString("name");
//                        String message = jsonObject.getString("message");
//                        String name = jsonObject.getString("name");

            // Show a toast with the message
//                        Toasts.toastIconSuccess(AccountsActivity.this, name);

            // Log the values for debugging
//                        Log.d("API Response", "Message: " + name + ", Name: " + name);
        } catch (Exception e) {
//            Toasts.toastIconError(getContext(),"Error Loading");
            Toast.makeText(SelectCustomerActivity.this, "Error Loading",
                    Toast.LENGTH_SHORT).show();
            Log.e("API Response", "JSON Parsing error: " + e.getMessage());
        }

    }



    @Override
    public void onComplete() {

    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(SelectCustomerActivity.this);

            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();

        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {

                URL url = new URL(params[0]);
                String requestMethod = params[1];
                String stringUrl = params[0];

                System.out.println("String Url: " + stringUrl);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(requestMethod);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + mAuthToken);

                if (requestMethod.equals("POST")) {

                    Uri.Builder builder;

                    if (stringUrl.contains("api/logcheckin")) {
                        builder = new Uri.Builder()
                                .appendQueryParameter("customerLat", params[2])
                                .appendQueryParameter("customerLong", params[3])
                                .appendQueryParameter("checkinLat", params[4])
                                .appendQueryParameter("checkinLong", params[5]);

                    } else {

                        builder = new Uri.Builder()
                                .appendQueryParameter("userId", params[2])
                                .appendQueryParameter("time", params[3])
                                .appendQueryParameter("lat", params[4])
                                .appendQueryParameter("long", params[5])
                                .appendQueryParameter("customerId", params[6]);

                    }

                    String query = builder.build().getEncodedQuery();

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();

                    System.out.println("Params " + params[2] + " " + params[3] + " " + params[4] + " " + params[5]);
                }
                connection.connect();

                InputStream stream;

                if (connection.getResponseCode() == 200) {
                    stream = connection.getInputStream();

                } else {
                    /* error from server */
                    //_is = httpConn.getErrorStream();
                    stream = connection.getErrorStream();

                }

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(TAG, "Malf Message");
            } catch (IOException e) {
                Log.e("Message here: ", e.getMessage());
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

                       if(pd != null && pd.isShowing() && !isFinishing()){
                pd.dismiss();
            }

            try {

                System.out.println("Result: " + result);
                if (result != null && result.contains("customers")) {
                        JSONObject jsonObject = new JSONObject(result);
                        //txtJson.setText(jsonObject.toString());
                        items = new ArrayList<>();

                        if (jsonObject.has("customers") &&
                                jsonObject.get("customers") instanceof JSONArray) {

                        customers = jsonObject.getJSONArray("customers");
                        System.out.println("Customers length: " + customers.length());

                        for (int i = 0; i < customers.length(); i++) {
                            Customer obj = new Customer();
                            //obj.image = drw_arr.getResourceId(i, -1);
                            obj.name = customers.getJSONObject(i).getString("name");
//                            obj.contact_person_name = customers.getJSONObject(i).getString("contact_person_name");
                            obj.phone = customers.getJSONObject(i).getString("phone");
                            obj.id = customers.getJSONObject(i).getString("id");
//                            obj.lat = customers.getJSONObject(i).getString("lat");
//                            obj.lon = customers.getJSONObject(i).getString("long");
//                            obj.image = customers.getJSONObject(i).getString("image");
                            //obj.imageDrw = drw_arr.getDrawable(obj.image);
                            String createdAt = customers.getJSONObject(i).getString("created_at");
                            ZonedDateTime dateTime = Instant.parse(createdAt).atZone(ZoneId.of("Africa/Nairobi"));
                            String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy \nHH:mm"));
                            obj.date = formatted;
                            items.add(obj);
                        }

                        if (items.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }


                        //set data and list adapter
                        mAdapter = new AdapterListCustomers(items);
                        recyclerView.setAdapter(mAdapter);


                        // on item list clicked
                        mAdapter.setOnItemClickListener(new AdapterListCustomers.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, Customer obj, int position) {

                                Customer customer = items.get(position);

                                if(mCallingActivity.equals("OffRouteSaleActivity")){

                                    Intent intent = new Intent(SelectCustomerActivity.this,
                                            AddTripActivity.class);

//                                    intent.putExtra("visitId", "0");
                                    intent.putExtra("customerId", customer.id);
                                    intent.putExtra("customerName", customer.name);
//                                    intent.putExtra("callingActivity", "makeSale");

                                    startActivity(intent);

                                }else{

//                                    showIndividualActivityWithoutDoubleClick(customer);

                                    Intent intent = new Intent(SelectCustomerActivity.this, AddTripActivity.class);
                                    mCustomerName = customer.name;
                                    customerId = customer.id;
                                    //Toast.makeText(SelectCustomerActivity.this,"customerId"+customerId+" - customerName"+mCustomerName,Toast.LENGTH_SHORT).show();
                                    intent.putExtra("customerName",mCustomerName);
                                    intent.putExtra("customerId", customerId);
//                                    intent.putExtra("lat",customer.lat);
//                                    intent.putExtra("long",customer.lon);
                                    intent.putExtra("customer",customer);
//                                    intent.putExtra("customer", customer);
                                    startActivity(intent);

                                }





// Tried this but ended up with slightly inaccurate distances which is okay
// but not rapidly updating locations
//                                LocationRequest locationRequest = LocationRequest.create();
//
//                                if( ActivityCompat.checkSelfPermission(SelectCustomerActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SelectCustomerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                                    mFusedLocationClient.getCurrentLocation(locationRequest.PRIORITY_HIGH_ACCURACY, null)
//                                            .addOnSuccessListener(SelectCustomerActivity.this, new OnSuccessListener<Location>() {
//                                                @Override
//                                                public void onSuccess(Location location) {
//                                                    // Got last known location. In some rare situations this can be null.
//                                                    if (location != null) {
//                                                        //do your thing
//                                                        Double distance = MyUtils.calculateDistance(Double.parseDouble(lat1),Double.parseDouble(lon1),location.getLatitude(),location.getLongitude());
//                                                        toastIconError(distance+"");
//
//                                                    }
//                                                    Log.w(TAG, "No current location could be found");
//                                                }
//                                            });
//                                }
// Tried this but ended up with slightly inaccurate distances which is okay
// but not rapidly updating locations





                                //getLastLocation(obj.id,obj.lat,obj.lon);
                                //add permissions check if else to last location

                                //requestNewLocationData();

                                //con

                            }
                        });


                    }
//                    else if (result.contains("customerId") ){
//
//                        JSONObject jsonObject = new JSONObject(result);
//                        //txtJson.setText(jsonObject.toString());
//                        String visitId = jsonObject.getString("visitId");
//                        String customerId = jsonObject.getString("customerId");
//                        long unixTimestamp = Instant.now().getEpochSecond();
//
//
//
//                        Intent intent = new Intent(SelectCustomerActivity.this, SelectActionActivity.class);
//                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        //Toast.makeText(SelectCustomerActivity.this,"visitId"+visitId+" - customerId"+customerId+" - customerName"+mCustomerName,Toast.LENGTH_SHORT).show();
//                        intent.putExtra("visitId", visitId);
//                        intent.putExtra("customerName",mCustomerName);
//                        intent.putExtra("customerId", customerId);
//                        intent.putExtra("lat","");
//                        intent.putExtra("long","");
//                        startActivity(intent);
//                    }
                    else{
                        //this is logging the checkin
                        //toastIconError("logged");
                        FirebaseCrashlytics.getInstance().recordException(new Exception("SelectCustomer Error: "+result));
                        Intent intent = new Intent(SelectCustomerActivity.this, ErrorLoadingActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("callingActivity", "SelectCustomer");

                        //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ProductsListActivity.this).toBundle());
                        startActivity(intent);
                        finish();
                    }

                }
                else if(result != null && result.contains("Unauthenticated")){
                    EncryptedPrefsUtil.saveString("userId", "0");
                    EncryptedPrefsUtil.saveString("authToken", "");
                    EncryptedPrefsUtil.saveString("email", "");
                    EncryptedPrefsUtil.saveString("password","");
                    Intent intent = new Intent(SelectCustomerActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    FirebaseCrashlytics.getInstance().recordException(new Exception("SelectCustomer Error: "+result));
                    Intent intent = new Intent(SelectCustomerActivity.this, ErrorLoadingActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("callingActivity", "SelectCustomer");
                    startActivity(intent);
                    finish();
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }

    //START OF LATEST LOCATION CODE
//
//    private void requestLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//        }
//    }
//
//    @Override

//    protected void onStart() {
//        super.onStart();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mFusedLocationClient.removeLocationUpdates(locationCallback);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                requestLocationUpdates();
//            } else {
//                toastIconError("Location Permission Denied");
//                //Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    //END OF LATEST LOCATION CODE




    @SuppressLint("MissingPermission")
    private void getLastLocation(String customerId,String lat1, String lon1) {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        //location = task.getResult();
                        //we should constantly check the location of the user as they
                        //try to check in while selecting an existing customer
                        //that's why we've removed the if statement
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            String locationText=location.getLatitude() + " - "+location.getLongitude() + "";
                            // Toast.makeText(getBaseContext(),locationText,Toast.LENGTH_SHORT).show();
                            String lat2 = location.getLatitude()+"";
                            String lon2 = location.getLongitude()+"";
                            long unixTime = System.currentTimeMillis() / 1000L;

//                            String lat1 = "40.714268"; // New York
//                            String lon1 = "-74.005974";
//                            String lat2 = "34.0522"; // Los Angeles
//                            String lon2 = "-118.2437";
                            System.out.println("lat1: "+lat1+" - long1: "+lon1+" : lat2"+lat2+" - "+lon2);

//                            double distanceAccurate = MyUtils.calculateVincentyDistance(Double.parseDouble(lat1), Double.parseDouble(lon1),
//                                    Double.parseDouble(lat2), Double.parseDouble(lon2));
//                            double distanceEstimate = MyUtils.calculateDistance(Double.parseDouble(lat1), Double.parseDouble(lon1),
//                                    Double.parseDouble(lat2), Double.parseDouble(lon2));
//                            System.out.println("Distance: "+distanceEstimate);
                            //toastIconError("Vincenty: "+distanceAccurate);
                            new JsonTask().execute(Constants.BASE_URL + "api/logcheckin","POST",lat1+"",lon1+"",lat2+"",lon2+"");

//                            //toastIconError("Haversine Distance: "+distanceEstimate);
//                            if(distanceEstimate<0.04){
////                                new JsonTask().execute(Constants.BASE_URL + "api/checkin","POST",mUserId,unixTime+"",lat2+"",lon2+"",customerId);
////                                SharedPreferences.Editor mEditor = mPrefs.edit();
////                                mEditor.putString("customerLat", lat1).commit();
////                                mEditor.putString("customerLong", lon1).commit();
////                                toastIconError("Checking In");
//                            }else{
//                                Toasts.toastIconError(SelectCustomerActivity.this,
//                                        "Please move closer to the customer");
//                            }
                        }
                    }
                });
            } else {
//                Toasts.toastIconError(SelectCustomerActivity.this,
//                        "Please turn on" + " your location...");
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                //Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void getLastLocationWithoutSubmit() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            String locationText=location.getLatitude() + " - "+location.getLongitude() + "";
                           // Toast.makeText(getBaseContext(),locationText,Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else {
//                Toasts.toastIconError(SelectCustomerActivity.this,
//                        "Please turn on" + " your location...");

                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }




    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            mLastLocation = locationResult.getLastLocation();
            location=mLastLocation;

            //implement checkin action on mlastlocation later
            //toastIconError("mLastLocation:"+mLastLocation.getLatitude()+""+" - "+mLastLocation.getLongitude());

            String locationText=location.getLatitude() + " - "+location.getLongitude() + "";
            //toastIconError(locationText);
            //Toast.makeText(getBaseContext(),locationText,Toast.LENGTH_SHORT).show();
            String lat2 = location.getLatitude()+"";
            String lon2 = location.getLongitude()+"";
            long unixTime = System.currentTimeMillis() / 1000L;

//                            String lat1 = "40.714268"; // New York
//                            String lon1 = "-74.005974";
//                            String lat2 = "34.0522"; // Los Angeles
//                            String lon2 = "-118.2437";
            System.out.println("lat1: "+lat1+" - long1: "+lon1+" : lat2"+lat2+" - "+lon2);

//                            double distanceAccurate = MyUtils.calculateVincentyDistance(Double.parseDouble(lat1), Double.parseDouble(lon1),
//                                    Double.parseDouble(lat2), Double.parseDouble(lon2));
//            double distanceEstimate = MyUtils.calculateDistance(Double.parseDouble(lat1), Double.parseDouble(lon1),
//                    Double.parseDouble(lat2), Double.parseDouble(lon2));
//            System.out.println("Distance: "+distanceEstimate);
//            //toastIconError("Vincenty: "+distanceAccurate);
//            new JsonTask().execute(Constants.BASE_URL + "api/logcheckin","POST",lat1+"",lon1+"",lat2+"",lon2+"");


            //toastIconError("Haversine Distance: "+distanceEstimate);
//            if(distanceEstimate<0.04){
////                                new JsonTask().execute(Constants.BASE_URL + "api/checkin","POST",mUserId,unixTime+"",lat2+"",lon2+"",customerId);
////                                SharedPreferences.Editor mEditor = mPrefs.edit();
////                                mEditor.putString("customerLat", lat1).commit();
////                                mEditor.putString("customerLong", lon1).commit();
////                                toastIconError("Checking In");
//            }else{
////                Toasts.toastIconError(SelectCustomerActivity.this,
////                        "Please move closer to the customer");
//                Toast.makeText( SelectCustomerActivity.this,
//                        "Please move closer to the customer",
//                        Toast.LENGTH_LONG ).show();
//
//            }
            //latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            //longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };




    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
//    @Override
//    public void
//    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//                //getLastLocationWithoutSubmit();
//            }
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            //getLastLocationWithoutSubmit();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);



        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search by customer name...");
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        try {
            @SuppressLint("SoonBlockedPrivateApi") Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {

        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return false;
            }
        });

        return true;
    }

    //searchview
    private void populateAdapter(String query) {
//        if(query.equals("")){
//
//        }
//        else {

            //final ArrayList<Integer> counterGroup = new ArrayList<Integer>();
            List<Customer> newCustomersList =new ArrayList<>();

            for (int counter = 0; counter < items.size(); counter++) {
                if(query.contains(" ")){
                    String[] querySplit = query.toLowerCase().split("\\s+");
                    String product_name_searched=items.get(counter).name.toLowerCase();


                    if(containsAllWords(product_name_searched,querySplit)){
                       // counterGroup.add(counter);
                        newCustomersList.add(items.get(counter));
                    }

                }else {
                    if (items.get(counter).name.toLowerCase().contains(query.toLowerCase())) {
                        //mNoProductsTextView.setVisibility(View.GONE);
                        //counterGroup.add(counter);
                        newCustomersList.add(items.get(counter));

                    }

                }

            }

            if (newCustomersList.size()>0) {
                mNoCustomersTextView.setVisibility(View.GONE);
            }else
            {
                mNoCustomersTextView.setVisibility(View.VISIBLE);
            }

            if(recyclerView==null) {
                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            }
            mAdapter = new AdapterListCustomers(newCustomersList);
            recyclerView.setAdapter(mAdapter);
//            recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View v,
//                                        int position, long id) {
//
//                    // Sending image id to FullScreenActivity
//                    Intent i = new Intent(MainActivity.this, DisplayProductsActivity.class);
//                    // passing array index
//                    i.putExtra("productClicked",productsList.get(counterGroup.get(position)) );
//                    startActivity(i);
//
//                }
//            });
            mAdapter.setOnItemClickListener(new AdapterListCustomers.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Customer obj, int position) {

                    Customer customer = newCustomersList.get(position);

                    if(mCallingActivity.equals("OffRouteSaleActivity")){

                        Intent intent = new Intent(SelectCustomerActivity.this,
                                AddTripActivity.class);

                        intent.putExtra("visitId", "0");
                        intent.putExtra("customerId", customer.id);
                        intent.putExtra("customerName", customer.name);
                        intent.putExtra("callingActivity", "makeSale");

                        startActivity(intent);

                    }else{

                        showIndividualActivityWithoutDoubleClick(customer);

                    }



                }
            });
//        }
    }
    public static boolean containsAllWords(String word, String ...keywords) {
        for (String k : keywords)
            if (!word.contains(k)) return false;
        return true;
    }
    private void showIndividualActivityWithoutDoubleClick(Customer customer){
        long currentTime = System.currentTimeMillis();
        //Toast.makeText(SalesActivity.this, "Current"+currentTime+" - Last"+lastIntentTime+" - Diff "+(currentTime - lastIntentTime) , Toast.LENGTH_SHORT).show();

        if (currentTime - lastIntentTime > MIN_CLICK_INTERVAL) {

            lastIntentTime = currentTime;
            // Handle the item click, e.g., start a new activity


        } else {
            // Ignore the click or show a message
            // Toast.makeText(ProductsListActivity.this, "Please wait before clicking again", Toast.LENGTH_SHORT).show();
        }
    }




}