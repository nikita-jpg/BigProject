package com.example.bigproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi
{
    @GET("auth/testAuth")
    Call<String> test(@Query("key") String key);
}
