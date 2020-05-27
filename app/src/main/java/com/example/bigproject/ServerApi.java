package com.example.bigproject;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ServerApi {

    @POST( "/regPerson")
    Call<String> regPerson(@Query(value = "login") String login,@Query(value = "password") String password);

    @POST( "/autPerson")
    Call<String> autPerson(@Query(value = "login") String login,@Query(value = "password") String password);

    @POST("/downloadFile")
    Call<String[]> uploadText(
            @Query(value = "auth") String authString,
            @Query(value = "name") String name
            );

    @POST("/uploadImage")
    Call<Integer> uploadImage(@Query(value = "auth") String authString,
                              @Query(value = "fileName") String fileName,
                              @Body byte[] arr);
    @POST("/getFileNameArr")
    Call<String[]> getFileNameArr(@Query(value = "auth") String auth);
}
