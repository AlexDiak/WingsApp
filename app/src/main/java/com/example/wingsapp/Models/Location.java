package com.example.wingsapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("lat")
    @Expose
    private float lat;
    @SerializedName("lng")
    @Expose
    private float lng;

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("crossStreet")
    @Expose
    private String crossStreet;

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public String getAddress(){ return address;}
    public String getCrossStreet(){ return crossStreet;}
}
