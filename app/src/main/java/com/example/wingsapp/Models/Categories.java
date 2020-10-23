package com.example.wingsapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Categories {

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }
}

