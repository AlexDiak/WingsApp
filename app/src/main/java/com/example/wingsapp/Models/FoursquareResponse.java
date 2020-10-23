package com.example.wingsapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoursquareResponse {

    @SerializedName("venues")
    @Expose
    private List<Venue> venues;

    FoursquareResponse(){

    }

    public List<Venue> getVenues() {
        return venues;
    }
}

