package com.scg.tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

//import com.example.crm.utils.EncryptedPrefsUtil;

import androidx.annotation.Nullable;

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

public class NetworkUtils {

//    public static void fetchDataGet(String endpoint, String token, OnSuccessListener listener,
//                                    Context context) {
//
//        System.out.println("endpoint: " + endpoint);
//
//        EncryptedPrefsUtil.init(context);
//        token = EncryptedPrefsUtil.getString("authToken", "");
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setView(R.layout.progress_dialog_layout); // Use a custom layout or a simple ProgressBar
//        builder.setCancelable(false); // Prevent closing the dialog while loading
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        ApiService apiService = ApiClient.getInstance().getApiService();
//        Call<ResponseBody> call = apiService.getData(endpoint, "Bearer " + token,
//                "application/json");
//        // Get the URL
//        String url = call.request().url().toString();
//        System.out.println("Request URL: " + url);
//
//
//        String finalToken = token;
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                dialog.dismiss();
//                if (response.isSuccessful() && response.body() != null) {
//                    listener.onSuccess(response.body());
//                } else {
//                    Log.e("API Error", "Response code: " + response.code());
//                    if (response.code() == 401) {
//                        Toasts.toastIconError(context, finalToken + "Session Expired. Please login again");
////                        return;
//                        EncryptedPrefsUtil.saveString("userId", "0");
//                        EncryptedPrefsUtil.saveString("authToken", "");
//                        EncryptedPrefsUtil.saveString("email", "");
//                        EncryptedPrefsUtil.saveString("password", "");
//                        Intent intent = new Intent(context, LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                    Toasts.toastIconError(context, "Error Loading. Please try again");
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                dialog.dismiss();
//                handleFailure(t);
//                Toasts.toastIconError(context, "Error Loading. Please try again");
//            }
//        });
//    }

    public static void fetchDataPost(String endpoint, String token, JSONObject requestBody,
                                     OnSuccessListener listener, Context context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.progress_dialog_layout); // Use a custom layout or a simple ProgressBar
        builder.setCancelable(false); // Prevent closing the dialog while loading
        AlertDialog dialog = builder.create();
        dialog.show();

        token = EncryptedPrefsUtil.getString("authToken", "");

        ApiService apiService = ApiClient.getInstance().getApiService();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),
                requestBody.toString());
        Call<ResponseBody> call = apiService.postData(endpoint, "Bearer " + token,
                "application/json", body);

        String finalToken = token;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                listener.onComplete();
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    Log.e("API Error", "Response code: " + response.code());
                    if (response.code() == 401) {
                        Toast.makeText(context, "401", Toast.LENGTH_SHORT).show();
//                        Toasts.toastIconError(context, finalToken + "Session Expired. Please login again");
////                        return;
//                        EncryptedPrefsUtil.saveString("userId", "0");
//                        EncryptedPrefsUtil.saveString("authToken", "");
//                        EncryptedPrefsUtil.saveString("email", "");
//                        EncryptedPrefsUtil.saveString("password", "");
//                        Intent intent = new Intent(context, LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Error Loading. Please try again", Toast.LENGTH_SHORT).show();

                    }

                    try {
                        if (response.errorBody() != null) {
                            String errorMessage = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorMessage);
                            Log.e("API Error Body", errorMessage);
                            Toast.makeText(context, errorJson.getString("message"), Toast.LENGTH_SHORT).show();


                        } else {
                            Log.e("API Error", "No error body returned from the server");
                            Toast.makeText(context, "Error Loading. Please try again", Toast.LENGTH_SHORT).show();

                        }
                    } catch (IOException e) {
                        Log.e("API Error", "Failed to read error body", e);
                        Toast.makeText(context, "Error Loading. Please try again", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        Log.e("API Error", "Failed to convert error to json", e);
                        Toast.makeText(context, "Error Loading. Please try again", Toast.LENGTH_SHORT).show();

                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(context, "Error Loading. Please try again", Toast.LENGTH_SHORT).show();

                handleFailure(t);
            }
        });
    }

    public static void fetchData(String method, String endpoint, @Nullable JSONObject requestBody,
                                 OnSuccessListener listener, Context context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.progress_dialog_layout); // Use a custom layout or a simple ProgressBar
        builder.setCancelable(false); // Prevent closing the dialog while loading
        AlertDialog dialog = builder.create();
        dialog.show();

        String token = EncryptedPrefsUtil.getString("authToken", "");
//        String token = "fsdfsdfsdf";

        ApiService apiService = ApiClient.getInstance().getApiService();
        RequestBody body = null;
        if (requestBody != null) {
            body = RequestBody.create(MediaType.parse("application/json"),
                    requestBody.toString());
        }

        Call<ResponseBody> call = null;
        if (method.equals("POST")) {
            call = apiService.postData(endpoint, "Bearer " + token,
                    "application/json", body);
        } else if (method.equals("PUT")) {
            call = apiService.putData(endpoint, "Bearer " + token,
                    "application/json", body);
        } else {
            call = apiService.getData(endpoint, "Bearer " + token,
                    "application/json");

        }

        String finalToken = token;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                listener.onComplete();
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    Log.e("API Error", "Response code: " + response.code());
                    if (response.code() == 401) {

                        Toast.makeText(context, finalToken + "Session Expired. Please login again", Toast.LENGTH_SHORT).show();

//                        return;
//                        EncryptedPrefsUtil.saveString("userId", "0");
//                        EncryptedPrefsUtil.saveString("authToken", "");
//                        EncryptedPrefsUtil.saveString("email", "");
//                        EncryptedPrefsUtil.saveString("password", "");
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {

                        Toast.makeText(context, "Error Loading. Please try again", Toast.LENGTH_SHORT).show();

                    }

                    try {
                        if (response.errorBody() != null) {
                            String errorMessage = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorMessage);
                            Log.e("API Error Body", errorMessage);
                            Toast.makeText(context, errorJson.getString("message"), Toast.LENGTH_SHORT).show();

                        } else {
                            Log.e("API Error", "No error body returned from the server");

                            Toast.makeText(context,  "Error Loading. Please try again", Toast.LENGTH_SHORT).show();


                        }
                    } catch (IOException e) {
                        Log.e("API Error", "Failed to read error body", e);
                        Toast.makeText(context, "Error Loading. Please try again", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        Log.e("API Error", "Failed to convert error to json", e);
                        Toast.makeText(context, "Error Loading. Please try again", Toast.LENGTH_SHORT).show();

                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(context, "Error Loading. Please try again", Toast.LENGTH_SHORT).show();

                handleFailure(t);
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
