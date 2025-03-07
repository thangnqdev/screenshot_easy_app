package com.example.screenshot_app.controller.interfaces;

import com.example.screenshot_app.controller.constant.Constant;
import com.example.screenshot_app.controller.response.GraphQLResponse;
import com.example.screenshot_app.model.GraphQLQuery;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GraphQLServiceInterface {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer " + Constant.TOKEN
    })
    @POST(Constant.GET_DATA_URL)
    Call<GraphQLResponse> getProjects(@Body GraphQLQuery query);
}