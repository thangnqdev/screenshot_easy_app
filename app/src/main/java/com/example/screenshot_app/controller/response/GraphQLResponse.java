package com.example.screenshot_app.controller.response;
import com.example.screenshot_app.model.project.Project;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GraphQLResponse {
    @SerializedName("data")
    public Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("projects")
        public List<Project> projects;

        public List<Project> getProjects() {
            return projects;
        }
    }
}

