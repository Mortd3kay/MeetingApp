package com.lazysecs.meetingapp.adapters;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.lazysecs.meetingapp.activities.UserProfileStepperActivity;
import com.lazysecs.meetingapp.fragments.profile_stepper.UserAdditionalInformationStepperFragment;
import com.lazysecs.meetingapp.fragments.profile_stepper.UserCategoriesStepperFragment;
import com.lazysecs.meetingapp.fragments.profile_stepper.UserBasicInformationStepperFragmentFragment;
import com.lazysecs.meetingapp.fragments.profile_stepper.UserPublishStepperFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class UserStepperAdapter extends AbstractFragmentStepAdapter {

    private static final String CURRENT_STEP_POSITION_KEY = "messageResourceId";

    public UserStepperAdapter(FragmentManager fm, UserProfileStepperActivity context) {
        super(fm, context);
    }

    public UserStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return UserBasicInformationStepperFragmentFragment.newInstance();
            case 1:
                return UserCategoriesStepperFragment.newInstance();
            case 2:
                return UserAdditionalInformationStepperFragment.newInstance();
            case 3:
                return UserPublishStepperFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        switch (position) {
            case 0:
                return new StepViewModel.Builder(context)
                        .setTitle("Базовая информация")
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle("Ваши интересы")
                        .create();
            case 2:
                return new StepViewModel.Builder(context)
                        .setTitle("Дополнительная информация")
                        .create();

            case 3:
                return new StepViewModel.Builder(context)
                        .setTitle("Готово!")
                        .create();
        }
        return null;
    }
}