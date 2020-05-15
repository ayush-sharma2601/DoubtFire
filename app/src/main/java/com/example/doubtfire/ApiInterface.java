package com.example.doubtfire;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("v4/")
    Call<String> getResult(@Query("expr") String expr);
}
