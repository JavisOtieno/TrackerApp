package com.scg.tracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.location.LocationRequest;

public class LocationWorker extends Worker {

    private static final String TAG = "LocationWorker";

    public LocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Initialize FusedLocationProviderClient
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getApplicationContext());

        // Create a location request
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // 10 seconds
                .setFastestInterval(5000); // 5 seconds

        // Request the last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Handle the location result
                    Log.d(TAG, "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
                    // Send to a server or save locally
                } else {
                    Log.d(TAG, "No location retrieved.");
                }
            }
        });

        // Return Result.success() to indicate the worker completed successfully
        return Result.success();
    }
}


