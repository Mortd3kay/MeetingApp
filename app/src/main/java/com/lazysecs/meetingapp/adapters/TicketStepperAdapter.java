package com.lazysecs.meetingapp.adapters;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.lazysecs.meetingapp.activities.TicketStepperActivity;
import com.lazysecs.meetingapp.fragments.ticket_stepper.TicketDescriptionStepperFragment;
import com.lazysecs.meetingapp.fragments.ticket_stepper.TicketCategoriesStepperFragment;
import com.lazysecs.meetingapp.fragments.ticket_stepper.TicketDateStepperFragment;
import com.lazysecs.meetingapp.fragments.ticket_stepper.TicketLocationStepperFragment;
import com.lazysecs.meetingapp.fragments.ticket_stepper.TicketPublishStepperFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class TicketStepperAdapter extends AbstractFragmentStepAdapter {

    private static final String CURRENT_STEP_POSITION_KEY = "messageResourceId";

    public TicketStepperAdapter(FragmentManager fm, TicketStepperActivity context) {
        super(fm, context);
    }

    public TicketStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return TicketDescriptionStepperFragment.newInstance();

            case 1:
                return TicketDateStepperFragment.newInstance();

            case 2:
                return TicketCategoriesStepperFragment.newInstance();

            case 3:
                return TicketLocationStepperFragment.newInstance();

            case 4:
                return TicketPublishStepperFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {

        switch (position) {
            case 0:
                return new StepViewModel.Builder(context)
                        .setTitle("Основные детали")
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle("Тик-Так")
                        .create();

            case 2:
                return new StepViewModel.Builder(context)
                        .setTitle("Выберите категории")
                        .create();

            case 3:
                return new StepViewModel.Builder(context)
                        .setTitle("Куда идем?")
                        .create();
            case 4:
                return new StepViewModel.Builder(context)
                        .setTitle("Готово!")
                        .create();
        }
        return null;
    }
}