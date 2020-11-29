package com.lazysecs.meetingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.Feedback;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersSuggestionsActivity extends AppCompatActivity {

    @BindView(R.id.text)
    EditText textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_suggestions);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_send)
    void sendFeedback() {
        int userId = UserProfileManager.getInstance().getMyProfile().getId();

        String text = textView.getText().toString();
        if (text.matches("")) {
            Toast.makeText(getContext(), "Не получится отправить пустой отзыв :)", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Feedback> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .sendFeedback(new Feedback(text, userId));

        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(@NonNull Call<Feedback> call, @NonNull Response<Feedback> response) {
                Toast.makeText(getContext(), "Отзыв был успешно отправлен! Спасибо! :)", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Feedback> call, @NonNull Throwable t) {
                Log.d("failure", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private Context getContext() {
        return this;
    }
}
