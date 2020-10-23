package com.example.wingsapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FoursquareJSON {

    @SerializedName("response")
    @Expose
    private FoursquareResponse response;

    FoursquareJSON(){

    }

    public FoursquareResponse getResponse() {
        return response;
    }

}
