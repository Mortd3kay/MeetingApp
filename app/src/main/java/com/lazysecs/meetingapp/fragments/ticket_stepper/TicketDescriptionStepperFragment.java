package com.lazysecs.meetingapp.fragments.ticket_stepper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.interfaces.TicketManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketDescriptionStepperFragment extends Fragment implements BlockingStep {

    @BindView(R.id.text_ticket_name)
    MaterialEditText textName;

    @BindView(R.id.text_ticket_price)
    MaterialEditText textPrice;

    private TicketManager ticketManager;

    public static TicketDescriptionStepperFragment newInstance() {
        return new TicketDescriptionStepperFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_basic_data_stepper, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TicketManager) {
            ticketManager = (TicketManager) context;
        } else {
            throw new IllegalStateException("Activity must implement TicketManager interface!");
        }
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        ticketManager.saveName(Objects.requireNonNull(textName.getText()).toString());
        ticketManager.savePrice(Integer.parseInt(Objects.requireNonNull(textPrice.getText()).toString()));

        callback.goToNextStep();
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
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
