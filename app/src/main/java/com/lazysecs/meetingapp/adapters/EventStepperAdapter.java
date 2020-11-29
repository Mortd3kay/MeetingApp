package com.lazysecs.meetingapp.adapters;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.lazysecs.meetingapp.activities.EventStepperActivity;
import com.lazysecs.meetingapp.fragments.event_stepper.EventCategoriesStepperFragment;
import com.lazysecs.meetingapp.fragments.event_stepper.EventDateStepperFragment;
import com.lazysecs.meetingapp.fragments.event_stepper.EventDescriptionStepperFragment;
import com.lazysecs.meetingapp.fragments.event_stepper.EventGeoLocationStepperFragment;
import com.lazysecs.meetingapp.fragments.event_stepper.EventPublishStepperFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class EventStepperAdapter extends AbstractFragmentStepAdapter {

    private static final String CURRENT_STEP_POSITION_KEY = "messageResourceId";

    public EventStepperAdapter(FragmentManager fm, EventStepperActivity context) {
        super(fm, context);
    }

    public EventStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return EventDescriptionStepperFragment.newInstance();

            case 1:
                return EventDateStepperFragment.newInstance();

            case 2:
                return EventGeoLocationStepperFragment.newInstance();


            case 3:
                return EventCategoriesStepperFragment.newInstance();

            case 4:
                return EventPublishStepperFragment.newInstance();
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
                        .setTitle("Куда пойдем?")
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle("Тик-Так")
                        .create();
            case 2:
                return new StepViewModel.Builder(context)
                        .setTitle("А где это?")
                        .create();
            case 3:
                return new StepViewModel.Builder(context)
                        .setTitle("Лыжи? Кино? Бар?")
                        .create();
            case 4:
                return new StepViewModel.Builder(context)
                        .setTitle("Готово!")
                        .create();
        }
        return null;
    }
}