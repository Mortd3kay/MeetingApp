package com.lazysecs.meetingapp.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //        private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static final String BASE_URL = "https://meetingappbackend.xyz:443/";
    private static String TOKEN;
    private static RetrofitClient instance;
    private static boolean needsHeader = true;
    private Retrofit retrofit;

    private RetrofitClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        chain -> {
                            Request original = chain.request();

                            Request.Builder requestBuilder = original.newBuilder()
                                    .method(original.method(), original.body());

                            if (needsHeader) {
                                requestBuilder.addHeader("Authorization", TOKEN);
                            }

                            Request request = requestBuilder.build();
                            okhttp3.Response response = chain.proceed(request);

                            switch (response.code()) {
                                case 401:
//                                    ((MainActivity) requireActivity()).logout();
                                    break;
                                case 500:
                                    break;
                                default:

                                    break;
                            }

                            return response;
                        }
                )
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static void setToken(String token) {
        TOKEN = "Token " + token;
    }

    public static void needsHeader(boolean needs) {
        needsHeader = needs;
    }

    public static synchronized RetrofitClient getInstance(String token) {
        if (instance == null) {
            instance = new RetrofitClient();
        }

        if (token == null)
            needsHeader(false);
        else
            setToken(token);

        return instance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }

    private Context getContext() {
        return null;
    }
}