package com.lazysecs.meetingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.customviews.CustomCallback;
import com.lazysecs.meetingapp.models.Password;
import com.lazysecs.meetingapp.models.Token;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.text_current_password)
    EditText textCurrentPassword;

    @BindView(R.id.text_new_password)
    EditText textNewPassword;

    @BindView(R.id.text_email)
    TextView textEmail;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.text)
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        textEmail.setText(String.valueOf(UserProfileManager.getInstance().getMyProfile().getEmail()));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        text.setText("Изменить пароль");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.button_change_password)
    void changePassword() {
        String currentPassword = textCurrentPassword.getText().toString();
        String newPassword = textNewPassword.getText().toString();

        Call<Token> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(getContext()))
                .getApi()
                .changePassword(new Password(currentPassword, newPassword));

        call.enqueue(new CustomCallback<Token>(getContext()) {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                super.onResponse(call, response);
                if (response.isSuccessful() && response.body() != null) {
                    MainActivity instance = MainActivity.instance;
                    instance.startWebSocketListener();

                    PreferenceUtils.saveToken(response.body().getToken(), getContext());
                    RetrofitClient.setToken(response.body().getToken());

                    Toast.makeText(ChangePasswordActivity.this, "Пароль был успешно изменен!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                int a = 5;

            }
        });

    }

    private Context getContext() {
        return this;
    }
}
