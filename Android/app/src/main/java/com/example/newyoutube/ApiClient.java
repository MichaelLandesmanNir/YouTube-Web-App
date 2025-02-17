package com.example.newyoutube.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

import com.example.newyoutube.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

public class ApiClient {
    private static final String BASE_URL = Strings.BASE_URL;
    private static OkHttpClient client;

    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    public static Response get(String url, String token) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + url)
                .get();

        if (token != null && !token.isEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }

        Request request = requestBuilder.build();
        return client.newCall(request).execute();
    }
    public static Response post(String url, JsonObject json) throws IOException {
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(body)
                .build();
        return client.newCall(request).execute();
    }
    public static Response put(String endpoint, JsonObject body, String token) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body.toString());
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .addHeader("Authorization", "Bearer " + token)
                .put(requestBody)
                .build();
        return client.newCall(request).execute();
    }

    public static Response delete(String url) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .delete()
                .build();
        return client.newCall(request).execute();
    }
}
