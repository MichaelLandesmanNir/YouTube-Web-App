package com.example.newyoutube;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
public interface ApiService {
    @Multipart
    @POST("/videos")
    Call<ResponseBody> uploadVideo(
            @Part MultipartBody.Part video,
            @Part MultipartBody.Part image,
            @Part("title") RequestBody title,
            @Part("channel") RequestBody channel,
            @Part("date") RequestBody date
    );
}

