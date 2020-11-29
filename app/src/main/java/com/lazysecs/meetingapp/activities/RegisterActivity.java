package com.lazysecs.meetingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.customviews.CustomCallback;
import com.lazysecs.meetingapp.models.RegisterData;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.services.AuthService;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.text_email)
    EditText textEmail;
    @BindView(R.id.text_first_name)
    EditText textFirstName;
    @BindView(R.id.text_last_name)
    EditText textLastName;
    @BindView(R.id.text_password)
    EditText textPassword;

    @BindView(R.id.text_agreement)
    TextView textAgreement;

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        customTextView();
    }


    private void customTextView() {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder("Нажимая на \"Создать аккаунт\", Вы принимаете ");
        spanTxt.append("пользовательское соглашение");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/walkapp/terms-conditions"));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - "пользовательское соглашение".length(), spanTxt.length(), 0);

        spanTxt.append(" и");
        spanTxt.setSpan(null, 32, spanTxt.length(), 0);
        spanTxt.append(" политику конфиденциальности сервиса");

        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/walkapp/privacy-policy"));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - "политику конфиденциальности сервиса".length(), spanTxt.length(), 0);


        textAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        textAgreement.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    @OnClick(R.id.registerButton)
    public void register() {
        String email = Objects.requireNonNull(textEmail.getText()).toString();
        String firstName = Objects.requireNonNull(textFirstName.getText()).toString();
        String lastName = Objects.requireNonNull(textLastName.getText()).toString();
        String password = Objects.requireNonNull(textPassword.getText()).toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)
                || TextUtils.isEmpty(password))
            Toast.makeText(RegisterActivity.this, "Заполни все поля!", Toast.LENGTH_SHORT).show();
        else if (password.length() < 6)
            Toast.makeText(RegisterActivity.this, "Слишком короткий пароль", Toast.LENGTH_SHORT).show();
        else
            register(email, firstName, lastName, password);
    }

    private void register(String email, String firstName, String lastName, String password) {
        RetrofitClient.needsHeader(false);

        Call<UserProfile> call = RetrofitClient
                .getInstance("")
                .getApi()
                .users(new RegisterData(email, firstName, lastName, password));

        call.enqueue(new CustomCallback<UserProfile>(getContext()) {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                super.onResponse(call, response);

                if (response.isSuccessful() && response.body() != null) {
                    AuthService authService = new AuthService(getContext());
                    authService.authenticate(email, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "Аккаунт с таким email уже существует", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                super.onFailure(call, t);
                Toast.makeText(RegisterActivity.this, "Ошибка подключения к интернету", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public Context getContext() {
        return mContext;
    }
}
