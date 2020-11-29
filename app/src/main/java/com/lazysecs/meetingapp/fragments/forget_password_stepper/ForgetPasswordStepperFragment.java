package com.lazysecs.meetingapp.fragments.forget_password_stepper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.StartActivity;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.customviews.CustomCallback;
import com.lazysecs.meetingapp.interfaces.ForgerPasswordManager;
import com.lazysecs.meetingapp.models.ForgetPassword;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class ForgetPasswordStepperFragment extends Fragment implements BlockingStep {

    @BindView(R.id.text_confirmation_code)
    EditText textConfirmationCode;
    @BindView(R.id.text_new_password)
    EditText textNewPassword;
    @BindView(R.id.text_new_password_confirmation)
    EditText textNewPasswordConfirmation;
    private ForgerPasswordManager forgerPasswordManager;

    public static ForgetPasswordStepperFragment newInstance() {
        return new ForgetPasswordStepperFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @OnClick(R.id.button_change_password)
    void onClick() {
        verifyStep();
    }

    private void restorePassword() {
        String email = forgerPasswordManager.getEmail();
        String code = textConfirmationCode.getText().toString();
        String newPassword = textNewPassword.getText().toString();

        Call<ForgetPassword> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireContext()))
                .getApi()
                .restorePassword(new ForgetPassword(email, code, newPassword));

        call.enqueue(new CustomCallback<ForgetPassword>(getContext()) {
            @Override
            public void onResponse(@NonNull Call<ForgetPassword> call, @NonNull Response<ForgetPassword> response) {
                super.onResponse(call, response);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireActivity(), "Пароль был успешно изменен!", Toast.LENGTH_SHORT).show();
                    openStartActivity();
                } else {
                    Toast.makeText(requireActivity(), "Неверный код подтверждения!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForgetPassword> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ForgerPasswordManager) {
            forgerPasswordManager = (ForgerPasswordManager) context;
        } else {
            throw new IllegalStateException("Activity must implement ForgerPasswordManager interface!");
        }
    }

    void openStartActivity() {
        PreferenceUtils.removeAll(requireActivity());

        Intent intent = new Intent(requireActivity(), StartActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        requireActivity().finish();
    }


    @Nullable
    @Override
    public VerificationError verifyStep() {
        String newPassword = textNewPassword.getText().toString();
        String newPasswordConfirmation = textNewPasswordConfirmation.getText().toString();

        if (!newPassword.equals(newPasswordConfirmation) && !newPassword.equals("")) {
            textNewPassword.setError("Пароли не совпадают");
            textNewPasswordConfirmation.setError("Пароли не совпадают");
            return new VerificationError("Пароли не совпадают");
        } else {
            restorePassword();
        }

        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
