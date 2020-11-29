package com.lazysecs.meetingapp.fragments.event_stepper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lazysecs.meetingapp.interfaces.EventManager;
import com.lazysecs.meetingapp.R;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EventDescriptionStepperFragment extends Fragment implements BlockingStep {

    @BindView(R.id.text_description)
    EditText textDescription;
    @BindView(R.id.header_h4)
    TextView headerH4;
    private EventManager eventManager;

    public static EventDescriptionStepperFragment newInstance() {
        return new EventDescriptionStepperFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_name_stepper, container, false);
        ButterKnife.bind(this, view);

        setHeader();
        if (eventManager.getAction().equals("edit"))
            loadDescription();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EventManager) {
            eventManager = (EventManager) context;
        } else {
            throw new IllegalStateException("Activity must implement EventManager interface!");
        }
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (Objects.requireNonNull(textDescription.getText()).toString().matches("")) {
            textDescription.setError("Поле является обязательным для заполнения");
            return new VerificationError("Не все обязательные поля заполнены");
        }
        return null;
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        String name = Objects.requireNonNull(textDescription.getText()).toString();
        eventManager.saveDescription(name);

        callback.goToNextStep();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onError(@NonNull VerificationError error) {
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    private void loadDescription() {
        textDescription.setText(eventManager.getDescription());
    }

    @SuppressLint("SetTextI18n")
    private void setHeader() {
        int unicode = 0x1F60A;
        headerH4.setText("Опишите событие максимально подробно " + new String(Character.toChars(unicode)));
    }
}
