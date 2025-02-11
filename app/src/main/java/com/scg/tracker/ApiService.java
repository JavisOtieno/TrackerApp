package com.scg.tracker;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("{endpoint}")
//@GET("api/apitest")
//    Call<MyResponse=> getData(
    Call<ResponseBody> getData(
//            @Body RequestBody body,
//            @Query("param") String param
            @Path(value = "endpoint", encoded = true) String endpoint,  // Add endpoint as a parameter
            @Header("Authorization") String authToken,  // Set the Authorization header dynamically
            @Header("Accept") String acceptHeader
    );
    @Headers("Accept: application/json")
    @POST("{endpoint}")
    Call<ResponseBody> postData(
            @Path(value = "endpoint", encoded = true) String endpoint,
            @Header("Authorization") String authHeader,
            @Header("Content-Type") String contentType,
//            @Header("Accept") String acceptHeader,
            @Body RequestBody body);
    @Headers("Accept: application/json")
    @PUT("{endpoint}")
    Call<ResponseBody> putData(
            @Path(value = "endpoint", encoded = true) String endpoint,
            @Header("Authorization") String authHeader,
            @Header("Content-Type") String contentType,
//            @Header("Accept") String acceptHeader,
            @Body RequestBody body);
}
