package com.lazysecs.meetingapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.adapters.ForgetPasswordStepperAdapter;
import com.lazysecs.meetingapp.interfaces.ForgerPasswordManager;
import com.stepstone.stepper.StepperLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgetPasswordActivity extends AppCompatActivity implements ForgerPasswordManager {

    private static final String EMAIL = "email";
    @BindView(R.id.stepperLayout)
    StepperLayout stepperLayout;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_without_complete_button);
        ButterKnife.bind(this);

        setupAdapter();
    }

    public void setupAdapter() {
        ForgetPasswordStepperAdapter forgetPasswordStepperAdapter = new ForgetPasswordStepperAdapter(getSupportFragmentManager(), this);
        stepperLayout.setAdapter(forgetPasswordStepperAdapter);
        stepperLayout.setCompleteButtonEnabled(false);
        stepperLayout.setCompleteButtonColor(getResources().getColor(R.color.colorTransparent));

    }

    @Override
    public void saveEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EMAIL, email);
        super.onSaveInstanceState(outState);
    }
}