package com.scg.tracker;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.scg.tracker.R;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.scg.tracker.util.EncryptedPrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "LOCATION_SERVICE_CHANNEL";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Handler handler;
    private String mAuthToken;
    public static final Integer EARTH_RADIUS = 6371;
    private SharedPreferences mPrefs;
    private String mTripId;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        handler = new Handler(Looper.getMainLooper()); // Get main thread handler
        mPrefs = getSharedPreferences("label", 0);

        SharedPreferences.Editor mEditor = mPrefs.edit();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d(TAG, "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
//                        handler.post(() -> Toast.makeText(getApplicationContext(),
//                                "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude(),
//                                Toast.LENGTH_SHORT).show());
                        Double currentLat = location.getLatitude();
                        Double currentLong = location.getLongitude();
                        Float accuracy = location.getAccuracy();

                        String lat1 = mPrefs.getString("previousLat", "-17.8580257");
                        String lon1 = mPrefs.getString("previousLong",  "177.5741977");
                        mTripId = mPrefs.getString("tripId", null);

                        double distanceEstimate = calculateDistance(
                                Double.parseDouble(lat1), Double.parseDouble(lon1),
                                currentLat,
                                currentLong);

                        handler.post(() -> Toast.makeText(getApplicationContext(),
                                "Accuracy: " + accuracy+
                                "Distance: " + distanceEstimate+" Trip"+mTripId,
                                Toast.LENGTH_SHORT).show());


                        if (distanceEstimate > 0.1) {

                            //update location
                            JSONObject requestBody = new JSONObject();
                            try {

                                requestBody.put("lat", location.getLatitude());
                                requestBody.put("long",location.getLongitude());
                                requestBody.put("trip_id",mTripId);
                                requestBody.put("accuracy",location.getAccuracy());
                                requestBody.put("distance",distanceEstimate);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }





                                AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                                builder.setView(R.layout.progress_dialog_layout); // Use a custom layout or a simple ProgressBar
                                builder.setCancelable(false); // Prevent closing the dialog while loading
                                AlertDialog dialog = builder.create();
//                                dialog.show();
                                
                                



                                ApiService apiService = ApiClient.getInstance().getApiService();
                                RequestBody body = RequestBody.create(MediaType.parse("application/json"),
                                        requestBody.toString());
                                mAuthToken = EncryptedPrefsUtil.getString("authToken", "");
                                Call<ResponseBody> call = apiService.postData("addlocation", "Bearer " + mAuthToken,
                                        "application/json", body);

                                String finalToken = "token";
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        dialog.dismiss();
//                                        listener.onComplete();
                                        if (response.isSuccessful() && response.body() != null) {
//                                            listener.onSuccess(response.body());
                                                ResponseBody responseBody = response.body();

                                                String responseBodyString = null;
                                                try {
                                                    String result = responseBody.string();
                                                    System.out.println("Result: "+result);

                                                    if (result != null && result.contains("Location") ){


                                                        JSONObject jsonObject = new JSONObject(result);
                                                        if (jsonObject.has("status") &&
                                                                jsonObject.get("status") instanceof String &&
                                                                jsonObject.get("status").equals("success")
                                                        ) {

//                                                            handler.post(() -> Toast.makeText(getBaseContext(),
//                                                                    "Location Updated Successfully", Toast.LENGTH_SHORT).show());

//                                                            Toasts.toastIconSuccess(AddTaskActivity.this,"Task Added Successfully");
//                                                            Intent intent = new Intent(AddTaskActivity.this, TasksActivity.class);
//                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                            startActivity(intent);

                                                        }
                                                        else if(result.contains("errors")){
                                                            String message = jsonObject.getString("message");
//                                                            Toasts.toastIconError(AddTaskActivity.this,message);
//                                                            handler.post(() -> Toast.makeText(getBaseContext(), message,
//                                                                    Toast.LENGTH_SHORT).show());

                                                            //handler.post(() -> Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show());
                                                            FirebaseCrashlytics.getInstance().recordException(new Exception("Error Updating Location. : "+message));

                                                        }
                                                        else{
//                                                            handler.post(() -> Toast.makeText(getBaseContext(), "Error Updating Location. Please Try Again",
//                                                                    Toast.LENGTH_SHORT).show());

//                                                            Toasts.toastIconError(AddTaskActivity.this,"Error Adding Task. Please Try Again");
                    FirebaseCrashlytics.getInstance().recordException(new Exception("Error Updating Location. Please Try Again: "+result));

                                                        }

                                                    }
                                                    else{
//                                                        handler.post(() -> Toast.makeText(getBaseContext(), "Location Update Failed. Please Try Again",
//                                                                Toast.LENGTH_SHORT).show());
                                                        FirebaseCrashlytics.getInstance().recordException(new Exception("Location Update Failed. Please Try Again : "+result));

                                                    }

//            Log.d("API Success", "Data: " + jsonObject.toString());


                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }





                                        } else {
                                            Log.e("API Error", "Response code: " + response.code());
                                            if (response.code() == 401) {
//                                                handler.post(() -> Toast.makeText(getBaseContext(), "401", Toast.LENGTH_SHORT).show());

                                            } else {
//                                                handler.post(() -> Toast.makeText(getBaseContext(), "Error Loading. Please try again", Toast.LENGTH_SHORT).show());

                                            }

                                            try {
                                                if (response.errorBody() != null) {
                                                    String errorMessage = response.errorBody().string();
                                                    JSONObject errorJson = new JSONObject(errorMessage);
                                                    Log.e("API Error Body", errorMessage);
//                                                    handler.post(() -> Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_SHORT).show());


                                                } else {
                                                    Log.e("API Error", "No error body returned from the server");
//                                                    handler.post(() -> Toast.makeText(getBaseContext(), "Error Loading. Please try again", Toast.LENGTH_SHORT).show());

                                                }
                                            } catch (IOException e) {
                                                Log.e("API Error", "Failed to read error body", e);
//                                                handler.post(() -> Toast.makeText(getBaseContext(), "Error Loading. Please try again", Toast.LENGTH_SHORT).show());

                                            } catch (JSONException e) {
                                                Log.e("API Error", "Failed to convert error to json", e);
//                                                handler.post(() -> Toast.makeText(getBaseContext(), "Error Loading. Please try again", Toast.LENGTH_SHORT).show());

                                            }


                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        dialog.dismiss();
//                                        handler.post(() -> Toast.makeText(getBaseContext(), "Error Loading. Please try again", Toast.LENGTH_SHORT).show());

                                        handleFailure(t);
                                    }
                                });

                            mEditor.putString("previousLong", currentLong+"").commit();
                            mEditor.putString("previousLat", currentLat+"").commit();
//                            mEditor.putString("previousLong", "36.9894711").commit();
//                            mEditor.putString("previousLat", "-1.1770744").commit();

                            }


                        //check if lat was not present initially then commit current lat and long
                        //otherwise only compare to the last time distance was greater than 0.1 within the loop
                        else if(lat1.equals("-17.8580257")){
                            mEditor.putString("previousLong", currentLong+"").commit();
                            mEditor.putString("previousLat", currentLat+"").commit();

//                            mEditor.putString("previousLong", "36.9894711").commit();
//                            mEditor.putString("previousLat", "-1.1770744").commit();
                        }






                    }
                }

            }
        };

        startLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = createNotification();
        startForeground(1, notification);




        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Tracks location in the background");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Tracking your location in the background")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with a valid icon in your drawable folder
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // 10 seconds
                .setFastestInterval(5000); // 5 seconds



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        );

    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public static double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public static double calculateDistance(double startLat, double startLong, double endLat, double endLong) {

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    private static void handleFailure(Throwable t) {
        Log.e("API Failure", "Error: " + t.getMessage());

        if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            int responseCode = httpException.code();
            Log.e("API Failure", "Response code: " + responseCode);

            try {
                Response<?> response = httpException.response();
                if (response != null && response.errorBody() != null) {
                    String errorBody = response.errorBody().string();
                    Log.e("API Failure", "Error body: " + errorBody);
                }
            } catch (IOException e) {
                Log.e("API Failure", "Error reading error body: " + e.getMessage());
            }
        } else {
            Log.e("API Failure", "Unknown error", t);
        }
    }

}
