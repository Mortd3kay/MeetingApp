package com.lazysecs.meetingapp.adapters;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.lazysecs.meetingapp.activities.EventStepperActivity;
import com.lazysecs.meetingapp.fragments.forget_password_stepper.ForgetPasswordEmailStepperFragment;
import com.lazysecs.meetingapp.fragments.forget_password_stepper.ForgetPasswordStepperFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class ForgetPasswordStepperAdapter extends AbstractFragmentStepAdapter {

    private static final String CURRENT_STEP_POSITION_KEY = "messageResourceId";

    public ForgetPasswordStepperAdapter(FragmentManager fm, EventStepperActivity context) {
        super(fm, context);
    }

    public ForgetPasswordStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return ForgetPasswordEmailStepperFragment.newInstance();

            case 1:
                return ForgetPasswordStepperFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {

        switch (position) {
            case 0:
                return new StepViewModel.Builder(context)
                        .setTitle("Mr. Печкин")
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle("Больше не забывай! ;)")
                        .create();
        }
        return null;
    }
}