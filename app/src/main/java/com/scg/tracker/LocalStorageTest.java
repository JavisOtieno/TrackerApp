package com.scg.tracker;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.scg.tracker.models.Locationmodel;
import com.scg.tracker.util.EncryptedPrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class LocalStorageTest extends AppCompatActivity {

    Mydatabasehelper dbHelper;
    Mylocationdatabasehelper dbHelperLocation;
    EditText nameInput, emailInput;
    TextView displayUsers;
    Button saveButton, loadButton, loadUnsycedButton;
    Button markSyncedButton;
    Button markUnsyncedButton;
    Button deleteAllButton;
    private String mAuthToken;
    private Button SyncLocationsToServerButton;

    private final Handler handler = new Handler(Looper.getMainLooper()); // Declare this once


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_storage_test);

        dbHelper = new Mydatabasehelper(this);
        dbHelperLocation = new Mylocationdatabasehelper(this);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        displayUsers = findViewById(R.id.displayLocations);
        saveButton = findViewById(R.id.saveButton);
        loadButton = findViewById(R.id.loadButton);
        loadUnsycedButton = findViewById(R.id.loadUnsyncedButton);
        deleteAllButton = findViewById(R.id.deleteLocationsButton);
        markSyncedButton = findViewById(R.id.markLocationsSyncedButton);
        markUnsyncedButton = findViewById(R.id.markLocationsUnsyncedButton);
        SyncLocationsToServerButton = findViewById(R.id.syncButton);

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            dbHelper.insertUser(name, email);
            //dbHelper.dropDB();
            dbHelperLocation.addColumnTest();

            dbHelperLocation.createTable();
            dbHelperLocation.insertLocation("-1.289", "36.822", "20",
                    "0.12345", "unsynced",1,"movement","TestEndName");
            dbHelperLocation.insertLocation("-1.273", "36.811", "20",
                    "0.46345", "unsynced",1,"movement","TestEndName");

//            dbHelperLocation.insertLocation("-1.000","36.000","21","0.12345","unsynced");

        });
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelperLocation.deleteAll();

            }
        });
        markSyncedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelperLocation.markAllSynced();
            }
        });
        markUnsyncedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelperLocation.markAllUnsynced();
            }
        });

        loadButton.setOnClickListener(v -> {

//            dbHelperLocation.dropDB();


            List<String> users = dbHelper.getAllUsers();

            displayUsers.setText(String.join("\n", users));
            List<String> locations = dbHelperLocation.getAllLocations();
            displayUsers.setText(String.join("\n", locations));
        });

        loadUnsycedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> locations = dbHelperLocation.getUnsyncedLocationsForDisplay();
                displayUsers.setText(String.join("\n", locations));

            }
        });

        SyncLocationsToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                syncUnsyncedLocations(LocalStorageTest.this);
                syncNextLocation();
            }
        });

    }

    public void syncUnsyncedLocations(Context context) {
//        Mylocationdatabasehelper db = new Mylocationdatabasehelper(context);
        List<Locationmodel> unsynced = dbHelperLocation.getUnsyncedLocations();

        for (Locationmodel location : unsynced) {
            // Make API request (e.g., using Retrofit or Volley)

            //update location
            JSONObject requestBody = new JSONObject();
            try {


//                handler.post(() -> Toast.makeText(getApplicationContext(),
//                        "Trip id: " + mTripId,
//                        Toast.LENGTH_SHORT).show());

                requestBody.put("lat", location.lat);
                requestBody.put("long", location.lon);
                requestBody.put("trip_id", null);
                requestBody.put("accuracy", location.accuracy);
                requestBody.put("distance", location.distance);

            } catch (JSONException e) {
                e.printStackTrace();
            }

//                                AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
//                                builder.setView(R.layout.progress_dialog_layout); // Use a custom layout or a simple ProgressBar
//                                builder.setCancelable(false); // Prevent closing the dialog while loading
//                                AlertDialog dialog = builder.create();
//                                dialog.show();

            ApiService apiService = ApiClient.getInstance().getApiService();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"),
                    requestBody.toString());
            mAuthToken = EncryptedPrefsUtil.getString("authToken", "");

            Log.d("LocationService", "Internet is available syncing" + location.lat + location.lon);

            Call<ResponseBody> call = apiService.postData("addlocation", "Bearer " + mAuthToken,
                    "application/json", body);

            String finalToken = "token";
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                            dialog.dismiss();
//                                        listener.onComplete();
                    if (response.isSuccessful() && response.body() != null) {
//                                            listener.onSuccess(response.body());
                        ResponseBody responseBody = response.body();

                        String responseBodyString = null;
                        try {
                            String result = responseBody.string();
                            System.out.println("Result: " + result);

                            if (result != null && result.contains("Location")) {


                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.has("status") &&
                                        jsonObject.get("status") instanceof String &&
                                        jsonObject.get("status").equals("success")
                                ) {
                                    dbHelperLocation.markLocationAsSynced(location.id);

//                                                            handler.post(() -> Toast.makeText(getBaseContext(),
//                                                                    "Location Updated Successfully", Toast.LENGTH_SHORT).show());

//                                                            Toasts.toastIconSuccess(AddTaskActivity.this,"Task Added Successfully");

//                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                            startActivity(intent);

                                } else if (result.contains("errors")) {
                                    String message = jsonObject.getString("message");
//                                                            Toasts.toastIconError(AddTaskActivity.this,message);
//                                                            handler.post(() -> Toast.makeText(getBaseContext(), message,
//                                                                    Toast.LENGTH_SHORT).show());

                                    //handler.post(() -> Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show());
                                    FirebaseCrashlytics.getInstance().recordException(new Exception("Error Updating Location. : " + message));

                                } else {
//                                                            handler.post(() -> Toast.makeText(getBaseContext(), "Error Updating Location. Please Try Again",
//                                                                    Toast.LENGTH_SHORT).show());

//                                                            Toasts.toastIconError(AddTaskActivity.this,"Error Adding Task. Please Try Again");
                                    FirebaseCrashlytics.getInstance().recordException(new Exception("Error Updating Location. Please Try Again: " + result));

                                }

                            } else {
//                                                        handler.post(() -> Toast.makeText(getBaseContext(), "Location Update Failed. Please Try Again",
//                                                                Toast.LENGTH_SHORT).show());
                                FirebaseCrashlytics.getInstance().recordException(new Exception("Location Update Failed. Please Try Again : " + result));

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
//                                            dialog.dismiss();
//                                        handler.post(() -> Toast.makeText(getBaseContext(), "Error Loading. Please try again", Toast.LENGTH_SHORT).show());

                    handleFailure(t);
                }
            });

            // proceed with network tasks


//            uploadLocationToServer(location, success -> {
////                if (success) {
//
////                }
//            });

        }
    }

    private void syncNextLocation() {
        Locationmodel location = dbHelperLocation.getFirstUnsyncedLocation(); // query single item from DB

        if (location == null) {
            Log.d("Sync", "All locations synced.");
            return;
        }

        Log.d("Syncing location with id: ", location.id+"");

        if(location.tripId == null){
            Log.d("SyncTripId", " Trip id: null");

        }else{
            Log.d("SyncTripId", " Trip id: "+location.tripId);
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("lat", location.lat);
            requestBody.put("long", location.lon);
            if(location.tripId == null){
                requestBody.put("trip_id", null);
            }else{
                requestBody.put("trip_id", location.tripId);
            }
            requestBody.put("accuracy", location.accuracy);
            requestBody.put("distance", location.distance);
            requestBody.put("type", location.type);
            requestBody.put("name", location.name);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Stop sync on invalid JSON
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                requestBody.toString()
        );

        String mAuthToken = EncryptedPrefsUtil.getString("authToken", "");
        ApiService apiService = ApiClient.getInstance().getApiService();

        Call<ResponseBody> call = apiService.postData("addlocation", "Bearer " + mAuthToken,
                "application/json", body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") &&
                                "success".equals(jsonObject.getString("status"))) {

                            dbHelperLocation.markLocationAsSynced(location.id);
                            // Recursively sync the next unsynced location
//                            syncNextLocation();
                            handler.postDelayed(() -> syncNextLocation(), 500);

                        } else {
                            FirebaseCrashlytics.getInstance().recordException(
                                    new Exception("Sync error: " + result));
                        }

                    } catch (IOException | JSONException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                } else {
                    FirebaseCrashlytics.getInstance().recordException(
                            new Exception("Failed: HTTP " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
            }
        });
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
