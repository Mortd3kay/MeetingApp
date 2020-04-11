package com.example.meetingapp.api;

import com.example.meetingapp.models.Event;
import com.example.meetingapp.models.Login;
import com.example.meetingapp.models.Test3;
import com.example.meetingapp.models.User;
import com.example.meetingapp.models.Test;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DjangoClient {

    @Headers("Content-Type: application/json")
    @POST("token_auth/auth")
    Call<User> login(@Body Login login);

    @Headers("Content-Type: application/json")
    @GET("api/events")
    Call<List<Event>> getEvents(@Header("Authorization") String authToken);

    @Headers("Content-Type: application/json")
    @GET("api/events/{pk}")
    Call<Event> getEvent(@Path("pk") String pk, @Header("Authorization") String authToken);

    @Headers("Content-Type: application/json")
    @POST("api/events")
    Call<Event> createEvent(@Body Event event, @Header("Authorization") String authToken);

    @Headers("Content-Type: application/json")
    @POST("api/requests")
    Call<String> sendRequest(@Body Test3 test3, @Header("Authorization") String authToken);
}