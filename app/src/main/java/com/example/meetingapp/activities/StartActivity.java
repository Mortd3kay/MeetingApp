package com.example.meetingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meetingapp.AuthService;
import com.example.meetingapp.R;
import com.example.meetingapp.UserProfileManager;
import com.example.meetingapp.api.RetrofitClient;
import com.example.meetingapp.models.UserProfile;
import com.example.meetingapp.utils.PreferenceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.text_email)
    public EditText textEmail;
    @BindView(R.id.text_password)
    public EditText textPassword;
    UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        userProfile = null;
        meProfile();
    }

    @OnClick(R.id.button_login)
    public void login() {
        login(textEmail.getText().toString(), textPassword.getText().toString());
    }

    private void login(String email, String password) {
        RetrofitClient.needsHeader(false);

        AuthService authService = new AuthService(getContext());
        authService.authenticate(email, password);
        authService.finishAuth();
    }

    @OnClick(R.id.button_register)
    void createAccount() {
        startActivity(new Intent(StartActivity.this, RegisterActivity.class));
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
                    userProfile = response.body();
                    UserProfileManager.getInstance().initialize(userProfile);
                    checkPrerequisites();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {

            }
        });
    }

    private void checkPrerequisites() {
        if (userProfile.getFilled() && PreferenceUtils.hasToken(this)) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (!userProfile.getFilled()) {
            Intent intent = new Intent(StartActivity.this, CreateUserProfileActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private Context getContext() {
        return this;
    }
}
