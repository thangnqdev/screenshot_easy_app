package com.example.screenshot_app.controller.services.interfaces;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadServiceInterface {

    @Multipart
    @POST("files")
    Call<ResponseBody> uploadFile(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part file
    );
}