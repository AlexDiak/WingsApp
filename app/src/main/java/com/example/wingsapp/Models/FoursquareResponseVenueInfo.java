package com.example.wingsapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoursquareResponseVenueInfo {

    @SerializedName("venue")
    @Expose
    private VenueInfo venue;

    FoursquareResponseVenueInfo(){

    }

    public VenueInfo getVenueInfo() {
        return venue;
    }
}
