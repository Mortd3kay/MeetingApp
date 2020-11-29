package com.lazysecs.meetingapp.fragments.forget_password_stepper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.customviews.CustomCallback;
import com.lazysecs.meetingapp.interfaces.ForgerPasswordManager;
import com.lazysecs.meetingapp.models.EmailConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;


public class ForgetPasswordEmailStepperFragment extends Fragment implements BlockingStep {

    @BindView(R.id.text_email)
    MaterialEditText textEmail;
    private ForgerPasswordManager forgerPasswordManager;

    public static ForgetPasswordEmailStepperFragment newInstance() {
        return new ForgetPasswordEmailStepperFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password_email_stepper, container, false);
        ButterKnife.bind(this, view);
        return view;
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

    @OnClick(R.id.button_send_code)
    void generateCode() {
        verifyStep();

        String email = String.valueOf(textEmail.getText());

        Call<Void> call = RetrofitClient
                .getInstance(null)
                .getApi()
                .generateCode(new EmailConfirmation(email));

        call.enqueue(new CustomCallback<Void>(getContext()) {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                super.onResponse(call, response);
                Toast.makeText(getActivity(), "Код был отправлен на почту", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                super.onFailure(call, t);
                Toast.makeText(getActivity(), "Ошибка при генерации кода", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (textEmail.getText().toString().matches("")) {
            textEmail.setError("Поле является обязательным для заполнения");
            return new VerificationError("Email, который Вы указывали при создании аккаунта");
        }

        return null;
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        verifyStep();
        forgerPasswordManager.saveEmail(String.valueOf(textEmail.getText()));

        callback.goToNextStep();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
