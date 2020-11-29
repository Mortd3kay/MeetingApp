package com.lazysecs.meetingapp.fragments.profile_stepper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lazysecs.meetingapp.interfaces.IUserProfileManager;
import com.lazysecs.meetingapp.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdditionalInformationStepperFragment extends Fragment implements BlockingStep {

    @BindView(R.id.text_city)
    MaterialEditText textCity;

    @BindView(R.id.text_education)
    MaterialEditText textEducation;

    @BindView(R.id.text_job)
    MaterialEditText textJob;

    @BindView(R.id.header_h4)
    TextView headerH4;

    private IUserProfileManager iUserProfileManager;

    public static UserAdditionalInformationStepperFragment newInstance() {
        return new UserAdditionalInformationStepperFragment();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_additional_information, container, false);
        ButterKnife.bind(this, view);

        int unicode = 0x1F60E;
        headerH4.setText("Информация необязательна, но поможет лучше узнать вас "
                + new String(Character.toChars(unicode)));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof IUserProfileManager) {
            iUserProfileManager = (IUserProfileManager) context;
        } else {
            throw new IllegalStateException("Activity must implement IUserProfileManager interface!");
        }
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        iUserProfileManager.saveEducation(Objects.requireNonNull(textEducation.getText()).toString());
        iUserProfileManager.saveCity(Objects.requireNonNull(textCity.getText()).toString());
        iUserProfileManager.saveJob(Objects.requireNonNull(textJob.getText()).toString());

        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
