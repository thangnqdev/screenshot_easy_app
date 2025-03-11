package com.example.screenshot_app.view.repository;

import android.content.Context;
import android.util.Log;

import com.example.screenshot_app.controller.services.interfaces.ApiServiceInterface;
import com.example.screenshot_app.controller.response.GraphQLResponse;
import com.example.screenshot_app.model.query.GraphQLQuery;
import com.example.screenshot_app.model.project.Project;
import com.example.screenshot_app.controller.client.ApiConfigManager;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProjectRepository {

    private final ApiServiceInterface apiService;

    public interface ProjectCallback {
        void onSuccess(List<Project> projects);
        void onFailure(String errorMessage);
    }

    public ProjectRepository(Context context) {
        this.apiService = createApiService(context);
    }

    private ApiServiceInterface createApiService(Context context) {
        Map<String, String> apiConfig = ApiConfigManager.INSTANCE.getApiConfig(context);
        String baseUrl = apiConfig.get("baseUrl");
        String token = apiConfig.get("token");

        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://cms.bolttech.space";
        }

        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "https://" + baseUrl;
        }

        if (token == null || token.isEmpty()) {
            token = "KjzcGYUfUJQdLca7SEx2hpZkXe5STOwj";
        }

        String finalToken = token;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("Authorization", "Bearer " + finalToken)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiServiceInterface.class);
    }

    public void fetchProjects(ProjectCallback callback) {
        String query = "{ projects { id title } }";
        GraphQLQuery graphQLQuery = new GraphQLQuery(query);

        apiService.fetchProjects(graphQLQuery).enqueue(new Callback<GraphQLResponse>() {
            @Override
            public void onResponse(Call<GraphQLResponse> call, Response<GraphQLResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData().getProjects());
                } else {
                    callback.onFailure("Lỗi phản hồi từ API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GraphQLResponse> call, Throwable t) {
                Log.e("ProjectRepository", "Lỗi khi gọi API: " + t.getMessage());
                callback.onFailure("Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }
}
