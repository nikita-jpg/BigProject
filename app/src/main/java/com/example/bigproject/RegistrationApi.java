package com.example.bigproject;

import java.security.PublicKey;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RegistrationApi {
    @POST( "reg/getPublicKeyForReg")
    Call<String> getPublicKeyForReg();

    @POST( "reg/regPerson")
    Call<String[]> regPerson(@Body String[] arr);

}
