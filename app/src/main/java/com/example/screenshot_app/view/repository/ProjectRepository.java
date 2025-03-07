package com.example.screenshot_app.view.repository;
import androidx.annotation.NonNull;

import com.example.screenshot_app.controller.constant.Constant;
import com.example.screenshot_app.controller.interfaces.GraphQLServiceInterface;
import com.example.screenshot_app.controller.response.GraphQLResponse;
import com.example.screenshot_app.model.GraphQLQuery;
import com.example.screenshot_app.model.Project;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProjectRepository {
    private final GraphQLServiceInterface service;

    public interface ProjectCallback {
        void onSuccess(List<Project> projects);
        void onFailure(String errorMessage);
    }

    public ProjectRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.GET_DATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(GraphQLServiceInterface.class);
    }

    public void fetchProjects(ProjectCallback callback) {
        String query = "{ projects { id title } }";
        Call<GraphQLResponse> call = service.getProjects(new GraphQLQuery(query));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GraphQLResponse> call, @NonNull Response<GraphQLResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData().getProjects());
                } else {
                    callback.onFailure("Lỗi phản hồi từ API");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GraphQLResponse> call, @NonNull Throwable t) {
                callback.onFailure("Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }
}
