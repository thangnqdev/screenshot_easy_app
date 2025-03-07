package com.example.screenshot_app.model;

import com.google.gson.annotations.SerializedName;

public class Project {
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
