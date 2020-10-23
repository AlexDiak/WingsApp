package com.example.wingsapp.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    public static Retrofit getInstance() {

        if(instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl("https://api.foursquare.com/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return instance;
    }
}