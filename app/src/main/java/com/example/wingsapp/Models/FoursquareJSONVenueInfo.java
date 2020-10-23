package com.example.wingsapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FoursquareJSONVenueInfo {

    @SerializedName("response")
    @Expose
    private FoursquareResponseVenueInfo response;

    FoursquareJSONVenueInfo(){

    }

    public FoursquareResponseVenueInfo getResponse() {
        return response;
    }

}
