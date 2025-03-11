package com.example.screenshot_app.model.api;

public class ApiToken {
    private int id;
    private String baseUrl;
    private String token;

    public ApiToken(int id, String baseUrl, String token) {
        this.id = id;
        this.baseUrl = baseUrl;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
