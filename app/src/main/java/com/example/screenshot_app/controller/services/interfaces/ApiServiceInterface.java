package com.example.screenshot_app.controller.services.interfaces;

import com.example.screenshot_app.controller.response.GraphQLResponse;
import com.example.screenshot_app.model.query.GraphQLQuery;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiServiceInterface {
    @Headers("Content-Type: application/json")
    @POST("graphql")
    Call<GraphQLResponse> fetchProjects(@Body GraphQLQuery query);

}
