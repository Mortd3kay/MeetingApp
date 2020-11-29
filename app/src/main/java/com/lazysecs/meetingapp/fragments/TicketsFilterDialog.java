package com.lazysecs.meetingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.UserProfile;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TicketsFilterDialog extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.chip_group)
    ChipGroup chipGroup;

    private Callback callback;
    private List<String> categories = null;

    static TicketsFilterDialog newInstance() {
        return new TicketsFilterDialog();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.EventsFilterDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets_filter_dialog, container, false);
        ButterKnife.bind(this, view);

        categories = new ArrayList<>();

        ImageButton close = view.findViewById(R.id.fullscreen_dialog_close);
        close.setOnClickListener(this);

        Button apply = view.findViewById(R.id.fullscreen_dialog_action_confirm);
        apply.setOnClickListener(this);

        UserProfile userProfile = UserProfileManager.getInstance().getMyProfile();
        if (userProfile != null) {
            for (Category category : userProfile.getCategories()) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.category_item, chipGroup, false);
                chip.setText(category.getName());
                chip.setChecked(true);
                categories.add(String.valueOf(chip.getText()));

                chip.setOnCheckedChangeListener((compoundButton, checked) -> {
                    if (checked)
                        categories.add(String.valueOf(chip.getText()));
                    else
                        categories.remove(String.valueOf(chip.getText()));

                });

                chipGroup.addView(chip);
            }
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.fullscreen_dialog_close:
                dismiss();
                break;

            case R.id.fullscreen_dialog_action_confirm:
                callback.onActionClick(categories);

                dismiss();
                break;

        }

    }

    public interface Callback {

        void onActionClick(List<String> categories);

    }

}