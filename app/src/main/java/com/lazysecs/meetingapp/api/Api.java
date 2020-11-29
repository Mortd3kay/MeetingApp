package com.lazysecs.meetingapp.api;

import com.lazysecs.meetingapp.models.Chat;
import com.lazysecs.meetingapp.models.CommonMessage;
import com.lazysecs.meetingapp.models.Complaint;
import com.lazysecs.meetingapp.models.EmailConfirmation;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.models.Feedback;
import com.lazysecs.meetingapp.models.ForgetPassword;
import com.lazysecs.meetingapp.models.LastSeenMessage;
import com.lazysecs.meetingapp.models.LoginData;
import com.lazysecs.meetingapp.models.MegaCategory;
import com.lazysecs.meetingapp.models.Message;
import com.lazysecs.meetingapp.models.Password;
import com.lazysecs.meetingapp.models.PrivateMessage;
import com.lazysecs.meetingapp.models.ProfilePhoto;
import com.lazysecs.meetingapp.models.RegisterData;
import com.lazysecs.meetingapp.models.RequestGet;
import com.lazysecs.meetingapp.models.RequestSend;
import com.lazysecs.meetingapp.models.Ticket;
import com.lazysecs.meetingapp.models.Token;
import com.lazysecs.meetingapp.models.UserProfile;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    @Headers("Content-Type: application/json")
    @DELETE("api/events/{pk}/members/{uid}/")
    Call<Void> removeMember(@Path("pk") String pk, @Path("uid") String uid);

    @Headers("Content-Type: application/json")
    @DELETE("api/requests/{pk}/")
    Call<Void> removeRequest(@Path("pk") String pk);

    @Headers("Content-Type: application/json")
    @DELETE("api/events/{pk}/")
    Call<Void> removeEvent(@Path("pk") String pk);

    @Headers("Content-Type: application/json")
    @DELETE("token_auth/profile/me/")
    Call<Void> removeProfile();

    // Authentication
    @Headers("Content-Type: application/json")
    @POST("token_auth/auth/")
    Call<Token> login(@Body LoginData loginData);

    @Headers("Content-Type: application/json")
    @POST("token_auth/users/")
    Call<UserProfile> users(@Body RegisterData registerData);

    @Headers("Content-Type: application/json")
    @POST("token_auth/profile/me/")
    Call<Token> changePassword(@Body Password password);

    @Headers("Content-Type: application/json")
    @POST("token_auth/forget_password/")
    Call<ForgetPassword> restorePassword(@Body ForgetPassword forgetPassword);

    // User Profile
    @Headers("Content-Type: application/json")
    @PUT("token_auth/profile/me/")
    Call<UserProfile> updateProfile(@Body UserProfile userProfile);

    @Headers("Content-Type: application/json")
    @GET("token_auth/profile/me/")
    Call<UserProfile> meProfile();

    @Headers("Content-Type: application/json")
    @GET("token_auth/profile/{pk}/")
    Call<UserProfile> getUserProfile(@Path("pk") String pk);

    @Multipart
    @POST("token_auth/profile/upload/")
    Call<ProfilePhoto> uploadFile(@PartMap Map<String, RequestBody> params);

    // Events
    @Headers("Content-Type: application/json")
    @POST("api/events/")
    Call<Event> createEvent(@Body Event event);

    @Headers("Content-Type: application/json")
    @PUT("api/events/{pk}/")
    Call<Event> updateEvent(@Path("pk") String pk, @Body Event event);

    @Headers("Content-Type: application/json")
    @GET("api/events/{pk}/")
    Call<Event> getEvent(@Path("pk") String pk);

    @Headers("Content-Type: application/json")
    @GET("api/events/")
    Call<List<Event>> getEvents(@Query("category") List<String> categories,
                                @Query("sex") List<String> sex,
                                @Query("from_age") String fromAge,
                                @Query("to_age") String toAge,
                                @Query("latitude") String latitude,
                                @Query("longitude") String longitude,
                                @Query("distance") String distance,
                                @Query("text") String text,
                                @Query("me") String me,
                                @Query("requested") String requested,
                                @Query("ended") String ended,
                                @Query("offset") int offset,
                                @Query("user_id") String userId);

    // Tickets
    @Headers("Content-Type: application/json")
    @GET("api/tickets/")
    Call<List<Ticket>> getTickets(@Query("category") List<String> categories,
                                  @Query("me") List<String> roles);

    @Headers("Content-Type: application/json")
    @POST("api/tickets/")
    Call<Ticket> createTicket(@Body Ticket ticket);

    @Headers("Content-Type: application/json")
    @GET("api/tickets/{pk}/")
    Call<Ticket> getTicket(@Path("pk") String pk);

    @Headers("Content-Type: application/json")
    @PUT("api/tickets/{pk}/")
    Call<Ticket> updateTicket(@Path("pk") String pk, @Body Ticket ticket);

    // Requests
    @Headers("Content-Type: application/json")
    @POST("api/requests/")
    Call<RequestGet> sendRequest(@Body RequestSend eventRequest);

    @Headers("Content-Type: application/json")
    @PUT("api/requests/{pk}/")
    Call<RequestGet> answerRequest(@Path("pk") String pk, @Body RequestSend decision);

    @Headers("Content-Type: application/json")
    @GET("api/requests/")
    Call<List<RequestGet>> getRequests();

    // Moderator
    @Headers("Content-Type: application/json")
    @GET("api/complaints/")
    Call<List<Complaint>> getComplaints();

    @Headers("Content-Type: application/json")
    @GET("api/complaints/{pk}/")
    Call<Complaint> getComplaint(@Path("pk") String pk);

    @Headers("Content-Type: application/json")
    @POST("api/complaints/")
    Call<Complaint> sendComplaint(@Body Complaint complaint);

    // Messages
    @Headers("Content-Type: application/json")
    @POST("api/messages/")
    Call<CommonMessage> sendMessage(@Body Message message);

    @Headers("Content-Type: application/json")
    @GET("api/messages/{event_id}/")
    Call<List<CommonMessage>> getEventMessages(@Path("event_id") String event_id, @Query("offset") String offset);

    @Headers("Content-Type: application/json")
    @POST("api/private-messages/")
    Call<PrivateMessage> sendPrivateMessage(@Body PrivateMessage privateMessage);

    @Headers("Content-Type: application/json")
    @POST("api/feedback/")
    Call<Feedback> sendFeedback(@Body Feedback feedback);

    @Headers("Content-Type: application/json")
    @POST("api/new_messages/")
    Call<LastSeenMessage> updateLastSeenMessageInChat(@Body LastSeenMessage lastSeenMessage);

    @Headers("Content-Type: application/json")
    @GET("api/private-messages/{user_id}/")
    Call<List<CommonMessage>> getPrivateMessage(@Path("user_id") String user_id);

    @Headers("Content-Type: application/json")
    @GET("api/chats/")
    Call<List<Chat>> getEventChats(@Query("chat_id") String chatId);

    // Categories
    @Headers("Content-Type: application/json")
    @GET("api/categories/")
    Call<List<MegaCategory>> getCategories();

    // Email Confirmation
    @Headers("Content-Type: application/json")
    @POST("api/generate_code/")
    Call<Void> generateCode(@Body EmailConfirmation email);

    @Headers("Content-Type: application/json")
    @POST("api/check_code/")
    Call<Void> checkCode(@Body EmailConfirmation emailConfirmation);

    @Headers("Content-Type: application/json")
    @PUT("token_auth/tokens/")
    Call<Void> updateFirebaseToken(@Body String firebaseToken);
}
