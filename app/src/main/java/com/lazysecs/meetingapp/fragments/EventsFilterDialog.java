package com.lazysecs.meetingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.appyvet.materialrangebar.RangeBar;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.MainActivity;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EventsFilterDialog extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.chip_group)
    ChipGroup chipGroup;

    @BindView(R.id.seek_bar_distance)
    RangeBar seekBarDistance;

    @BindView(R.id.range_bar_age)
    RangeBar rangeBarAge;

    @BindView(R.id.checkbox_male)
    MaterialCheckBox checkBoxMale;

    @BindView(R.id.checkbox_female)
    MaterialCheckBox checkBoxFemale;

    @BindView(R.id.checkbox_any)
    MaterialCheckBox checkBoxAny;

    private Callback callback;
    private List<String> categories = null;
    private List<String> sex = null;

    private String fromAge = null;
    private String toAge = null;
    private String distance = null;

    static EventsFilterDialog newInstance() {
        return new EventsFilterDialog();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.EventsFilterDialogTheme);
        checkLocationAvailable();
    }

    @OnClick(R.id.clear_filters)
    public void clearFilters() {
        chipGroup.clearCheck();
        categories = new ArrayList<>();

        sex = new ArrayList<>();
        sex.add("MALE");
        sex.add("FEMALE");
        sex.add("UNSURE");

        seekBarDistance.setSeekPinByValue(99);
        distance = "99";
        fromAge = "16";
        toAge = "35";
        rangeBarAge.setRangePinsByValue(16, 35);
        checkBoxMale.setChecked(true);
        checkBoxFemale.setChecked(true);
        checkBoxAny.setChecked(true);
    }

    void checkLocationAvailable() {
        if (MainActivity.instance.getLocation() == null) {
            Toast.makeText(requireActivity(), "Включите геолокацию в настройках смартфона", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_filter_dialog, container, false);
        ButterKnife.bind(this, view);

        clearFilters();

        seekBarDistance.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                distance = rightPinValue;
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }
        });

        rangeBarAge.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                fromAge = leftPinValue;
                toAge = rightPinValue;
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }
        });

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
                chip.setChecked(false);

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

    @OnClick({R.id.checkbox_male, R.id.checkbox_female, R.id.checkbox_any})
    public void onGenderCheckBoxClicked(CheckBox checkBox) {
        switch (checkBox.getId()) {
            case R.id.checkbox_male:
                handleCheckbox("MALE");

                break;
            case R.id.checkbox_female:
                handleCheckbox("FEMALE");

                break;
            case R.id.checkbox_any:
                handleCheckbox("UNSURE");

                break;
        }
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
                callback.onActionClick(categories, sex, fromAge, toAge, null, null, distance, null, true);

                dismiss();
                break;
        }
    }

    private void handleCheckbox(String sexString) {
        if (!sex.contains(sexString))
            sex.add(sexString);
        else
            sex.remove(sexString);
    }

    public interface Callback {
        void onActionClick(List<String> categories, List<String> sex, String fromAge, String toAge, String latitude, String longitude, String distance, String text, boolean renew);
    }
}