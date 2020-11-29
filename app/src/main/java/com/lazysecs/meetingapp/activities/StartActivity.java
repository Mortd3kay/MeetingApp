package com.lazysecs.meetingapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.services.AuthService;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.text_email)
    EditText textEmail;
    @BindView(R.id.text_password)
    EditText textPassword;
    @BindView(R.id.layout_logo)
    LinearLayout layoutLogo;
    @BindView(R.id.layout_start)
    LinearLayout layoutStart;
    @BindView(R.id.layout_parent)
    LinearLayout layoutParent;
    @BindView(R.id.text_welcome)
    TextView textWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        loadApp();
    }

    @OnClick(R.id.forget_password)
    void forgetPassword() {
        Intent intent = new Intent(StartActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_login)
    public void login() {
        login(textEmail.getText().toString(), textPassword.getText().toString());
    }

    private void login(String email, String password) {
        RetrofitClient.needsHeader(false);

        AuthService authService = new AuthService(getContext());
        authService.authenticate(email, password);
//        authService.finishAuth();
    }

    @OnClick(R.id.button_register)
    void createAccount() {
        startActivity(new Intent(StartActivity.this, RegisterActivity.class));
    }

    @SuppressLint("ResourceAsColor")
    private void loadApp() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (PreferenceUtils.hasToken(this)) {
                if (PreferenceUtils.isFilled(this)) {
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(StartActivity.this, CreateUserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                layoutLogo.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutStart.setVisibility(View.VISIBLE);
                layoutParent.setBackgroundColor(getResources().getColor(R.color.ms_white));
                textWelcome.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        }, 1000);
    }

    private Context getContext() {
        return this;
    }
}
