package com.example.wingsapp.Retrofit;

import com.example.wingsapp.Models.FoursquareJSON;
import com.example.wingsapp.Models.FoursquareJSONVenueInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IMyService {

    @GET("venues/search")
    Call<FoursquareJSON> getVenues(@Query("client_id") String clientId,
                                   @Query("client_secret") String clientSecret,
                                   @Query("v") String date,
                                   @Query("ll") String longitudeLatitude,
                                   @Query("categoryId") String categoryId,
                                   @Query("radius") int radius);

    @GET("venues/{venue_id}?")
    Call<FoursquareJSONVenueInfo> getVenueInfo(@Path("venue_id") String venue_id,
                                               @Query("client_id") String clientId,
                                               @Query("client_secret") String clientSecret,
                                               @Query("v") String date);
}
