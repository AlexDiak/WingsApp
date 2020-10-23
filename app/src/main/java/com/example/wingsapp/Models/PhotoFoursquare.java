package com.example.wingsapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoFoursquare{

    @SerializedName("prefix")
    @Expose
    private String prefix;

    @SerializedName("suffix")
    @Expose
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}