package com.example.wingsapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Locale;

public class VenueInfo {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("categories")
    @Expose
    private List<Category> categories;

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("bestPhoto")
    @Expose
    private PhotoFoursquare bestphoto;


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Location getLocation() {
        return location;
    }

    public PhotoFoursquare getBestPhoto() {
        return bestphoto;
    }
}
