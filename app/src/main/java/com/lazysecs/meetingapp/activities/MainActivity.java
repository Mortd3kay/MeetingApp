package com.lazysecs.meetingapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.fragments.BottomSheetFragment;
import com.lazysecs.meetingapp.fragments.EventsFragment;
import com.lazysecs.meetingapp.fragments.HomeFragment;
import com.lazysecs.meetingapp.fragments.MessagesFragment;
import com.lazysecs.meetingapp.fragments.TicketsFragment;
import com.lazysecs.meetingapp.models.Chat;
import com.lazysecs.meetingapp.models.RequestGet;
import com.lazysecs.meetingapp.models.ShortChat;
import com.lazysecs.meetingapp.models.ShortRequest;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.services.NotificationBadgeManager;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.services.WebSocketListenerService;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomSheetFragment.ItemClickListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //    NotificationListener
    public static MainActivity instance;
    private static BottomNavigationView navigation;
    private static int notifications = 0;
    final Fragment homeFragment = new HomeFragment();
    final Fragment eventsFragment = new EventsFragment();
    final Fragment ticketsFragment = new TicketsFragment();
    final Fragment messagesFragment = new MessagesFragment();
    final FragmentManager fm = getSupportFragmentManager();
    private final int EVENT_ICON = R.drawable.ic_events;
    private final int TICKET_ICON = R.drawable.ic_tickets;
    private final String EVENTS = "СОБЫТИЯ";
    private final String TICKETS = "БИЛЕТЫ";
    Intent webSocketIntent;
    private int notSeenNotifications = 0;
    private Fragment active = eventsFragment;
    private String content;
    private boolean isInit = false;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fm.beginTransaction().hide(active).show(homeFragment).commit();
                active = homeFragment;
                return true;
            case R.id.navigation_content:
                if (content.equals(EVENTS)) {
                    fm.beginTransaction().hide(active).show(eventsFragment).commit();
                    active = eventsFragment;
                } else if (content.equals(TICKETS)) {
                    fm.beginTransaction().hide(active).show(ticketsFragment).commit();
                    active = ticketsFragment;
                }
                return true;
            case R.id.navigation_messages:
                fm.beginTransaction().hide(active).show(messagesFragment).commit();
                active = messagesFragment;

                return true;
        }
        return false;
    };
    private Location currentLocation;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        setupLocation();
//        checkLocationPermission();

        instance = this;
        setLocale();

        navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        MenuItem item = navigation.getMenu().findItem(R.id.navigation_content);
        item.setChecked(true);

        content = PreferenceUtils.getContentType(this);
        if (content.equals(EVENTS)) {
            navigation.getMenu().findItem(R.id.navigation_content).setIcon(EVENT_ICON);
            fm.beginTransaction().add(R.id.main_container, eventsFragment, "3").commit();
        } else if (content.equals(TICKETS)) {
            navigation.getMenu().findItem(R.id.navigation_content).setIcon(TICKET_ICON);
            fm.beginTransaction().add(R.id.main_container, ticketsFragment, "3").hide(ticketsFragment).commit();
        }


        fm.beginTransaction().add(R.id.main_container, messagesFragment, "2").hide(messagesFragment).commit();
        fm.beginTransaction().add(R.id.main_container, homeFragment, "1").hide(homeFragment).commit();

        meProfile();
        startWebSocketListener();
    }

    @SuppressLint("MissingPermission")
    public void setupLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = location;
                    }
                });
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Использование Вашей геопозиции")
                        .setMessage("Чтобы находить события вблизи от Вас приложению потребутся доступ к Вашей геопозиции во время работы приложения.")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .setNegativeButton("НЕТ", (dialogInterface, i) -> {

                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            setupLocation();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        setupLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }


    public Location getLocation() {
        if (currentLocation != null)
            return currentLocation;
        else
            return null;
    }


    public void stopWebSocketListener() {
        if (isMyServiceRunning(WebSocketListenerService.class) && webSocketIntent != null) {
            stopService(webSocketIntent);
        }
    }

    public void startWebSocketListener() {
        stopWebSocketListener();

        if (!isMyServiceRunning(WebSocketListenerService.class)) {
            webSocketIntent = new Intent(this, WebSocketListenerService.class);
            webSocketIntent.putExtra("EXTRA_TOKEN", PreferenceUtils.getToken(this));
            webSocketIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

            startService(webSocketIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addNotificationBadge(int notificationsToAdd) {
        if (!isBadgeVisible())
            showNotificationBadge();

        notifications += notificationsToAdd;
        if (notifications < 1)
            hideNotificationBadge();
        else
            navigation.getOrCreateBadge(R.id.navigation_messages).setNumber(notifications);
    }

    public void subNotificationBadge(int notificationsToRemove) {
        if (!isBadgeVisible())
            showNotificationBadge();

        notifications -= notificationsToRemove;
        if (notifications < 1)
            hideNotificationBadge();
        else
            navigation.getOrCreateBadge(R.id.navigation_messages).setNumber(notifications);

    }

    public Chat convertShortChatToChat(ShortChat shortChat) {
        Chat chat = new Chat();
        chat.setLastSeenMessageId(shortChat.getLastSeenMessageId());
        chat.setLastMessageId(shortChat.getLastMessageId());
        chat.setContentId(shortChat.getContentId());

        return chat;
    }

    public RequestGet convertShortRequestToRequst(ShortRequest shortRequest) {
        RequestGet request = new RequestGet();
        request.setSeen(shortRequest.isSeen());
        request.setDecision(shortRequest.getDecision());
        request.setId(shortRequest.getId());

        UserProfile fromUser = new UserProfile();
        fromUser.setId(shortRequest.getFromUser());
        request.setFromUser(fromUser);

        UserProfile toUser = new UserProfile();
        toUser.setId(shortRequest.getToUser());
        request.setToUser(toUser);

        return request;
    }

    public void initNotificationBadge() {
        UserProfile userProfile = UserProfileManager.getInstance().getMyProfile();
        navigation.getOrCreateBadge(R.id.navigation_messages);

        if (navigation.getBadge(R.id.navigation_messages).getNumber() <= 0)
            hideNotificationBadge();

        for (ShortChat shortChat : userProfile.getShortChats()) {
            NotificationBadgeManager.getInstance().notifyChat(convertShortChatToChat(shortChat));
        }

        for (ShortRequest shortRequest : userProfile.getShortRequests()) {
            NotificationBadgeManager.getInstance().notifyRequest(convertShortRequestToRequst(shortRequest));
        }
    }

    public void hideNotificationBadge() {
        Objects.requireNonNull(navigation.getBadge(R.id.navigation_messages)).setVisible(false);
    }

    public boolean isBadgeVisible() {
        return Objects.requireNonNull(navigation.getBadge(R.id.navigation_messages)).isVisible();
    }

    public void showNotificationBadge() {
        Objects.requireNonNull(navigation.getBadge(R.id.navigation_messages)).setVisible(true);
    }

    public void logout() {
        PreferenceUtils.removeAll(this);

//        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
//            ((ActivityManager) Objects.requireNonNull(getSystemService(ACTIVITY_SERVICE)))
//                    .clearApplicationUserData();
//            return;
//        }

        Intent intent = new Intent(this, StartActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }

    private void meProfile() {
        Call<UserProfile> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .meProfile();

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.body() != null) {
                    UserProfile userProfile = response.body();
                    UserProfileManager.getInstance().initialize(userProfile);
                    PreferenceUtils.saveUserId(userProfile.getId(), getContext());
                }

                if (response.code() == 401)
                    logout();
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
            }
        });
    }

    private Context getContext() {
        return this;
    }

    public boolean isInit() {
        return this.isInit;
    }

    @Override
    public void onItemClick(String item) {
//        if (item.equals(EVENTS)) {
//            if (content.equals(TICKETS))
//                changeContent(ticketsFragment, eventsFragment, EVENTS, EVENT_ICON);
//        } else if (item.equals(TICKETS)) {
//            if (content.equals(EVENTS))
//                changeContent(eventsFragment, ticketsFragment, TICKETS, TICKET_ICON);
//        }
    }

    private void changeContent(Fragment removeFragment, Fragment addFragment, String contentType, int icon) {
        fm.beginTransaction().remove(removeFragment).add(R.id.main_container, addFragment, "3").show(addFragment).commit();
        navigation.getMenu().findItem(R.id.navigation_content).setIcon(icon);
        active = addFragment;
        content = contentType;
        PreferenceUtils.saveContentType(content, this);
    }

    private void setLocale() {
        Configuration config = getApplicationContext().getResources().getConfiguration();
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config,
                getApplicationContext().getResources().getDisplayMetrics());
    }
}
