package com.lazysecs.meetingapp.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lazysecs.meetingapp.customviews.CustomCallback;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.EmailConfirmation;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.goodiebag.pinview.Pinview;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class ConfirmCodeActivity extends AppCompatActivity {

    @BindView(R.id.pin_view_confirm)
    Pinview pinview;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_code);
        ButterKnife.bind(this);

        generateCode();

        pinview.setActivated(true);
        pinview.setPressed(true);
        pinview.setTextColor(Color.BLACK);
        pinview.setPinViewEventListener((pinView, fromUser) -> {
            hideKeyboard();
            confirmProfile(Long.parseLong(pinView.getValue()));
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void confirmProfile(long code) {
        Call<Void> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .checkCode(new EmailConfirmation(email, code));

        call.enqueue(new CustomCallback<Void>(getContext()) {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                super.onResponse(call, response);
                if (response.code() == 406) {
                    Toast.makeText(ConfirmCodeActivity.this, "Неправильный код :(", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 202) {
                    Toast.makeText(ConfirmCodeActivity.this, "Аккаунт подтвержден!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private void generateCode() {
        email = UserProfileManager.getInstance().getMyProfile().getEmail();

        Call<Void> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .generateCode(new EmailConfirmation(email));

        call.enqueue(new CustomCallback<Void>(getContext()) {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                super.onResponse(call, response);
                Toast.makeText(ConfirmCodeActivity.this, "Код был отправлен на почту", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                super.onFailure(call, t);
                Toast.makeText(ConfirmCodeActivity.this, "Ошибка при генерации кода", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Context getContext() {
        return this;
    }
}
