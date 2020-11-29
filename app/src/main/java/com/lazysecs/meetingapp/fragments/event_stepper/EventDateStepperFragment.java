package com.lazysecs.meetingapp.fragments.event_stepper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lazysecs.meetingapp.interfaces.EventManager;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.utils.DateConverter;
import com.lazysecs.meetingapp.utils.TimeConverter;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EventDateStepperFragment extends Fragment implements BlockingStep, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.button_today)
    RadioButton buttonToday;

    @BindView(R.id.button_tomorrow)
    RadioButton buttonTomorrow;

    @BindView(R.id.text_date)
    MaterialEditText textDate;

    @BindView(R.id.text_time)
    MaterialEditText textTime;

    private EventManager eventManager;
    private Date date;

    public static EventDateStepperFragment newInstance() {
        return new EventDateStepperFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_date_stepper, container, false);
        ButterKnife.bind(this, view);

        date = new Date();
        if (eventManager.getAction().equals("edit"))
            date = DateConverter.getDateFromString(eventManager.getDate());

        defaultDate(date);

        return view;
    }

    @OnClick(R.id.text_date)
    void showDatePickerDialog() {
        DateConverter.showDatePickerDialog(date, getContext(), this);
    }

    @OnClick(R.id.text_time)
    void showTimePickerDialog() {
        TimeConverter.showTimePickerDialog(getContext(), this);
    }

    @OnClick({R.id.button_today, R.id.button_tomorrow})
    void onRadioButtonClicked(RadioButton radioButton) {
        boolean checked = radioButton.isChecked();

        switch (radioButton.getId()) {
            case R.id.button_today:
                if (checked) {
                    buttonToday.setTextColor(getResources().getColor(R.color.colorPrimary));
                    buttonTomorrow.setTextColor(Color.BLACK);

                    String today = DateConverter.getTodayDateString();
                    textDate.setText(today);
                    date = DateConverter.getDateFromString(today);
                }
                break;
            case R.id.button_tomorrow:
                if (checked) {
                    buttonToday.setTextColor(Color.BLACK);
                    buttonTomorrow.setTextColor(getResources().getColor(R.color.colorPrimary));

                    String tomorrow = DateConverter.getTomorrowDateString();

                    textDate.setText(tomorrow);
                    date = DateConverter.getDateFromString(tomorrow);
                }
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void defaultDate(Date date) {
        buttonToday.setTextColor(getResources().getColor(R.color.colorPrimary));
        textDate.setText(DateConverter.getStringDateFromDate(date));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String dateString = DateConverter.getDate(year, month, day);

        textDate.setText(dateString);
        date = DateConverter.getDateFromString(dateString);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        textTime.setText(TimeConverter.getTime(timePicker, hour, minute));
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
        if (Objects.requireNonNull(textDate.getText()).toString().matches("")) {
            textDate.setError("Поле является обязательным для заполнения");
            return new VerificationError("Поставьте дату события");
        }

        if (Objects.requireNonNull(textTime.getText()).toString().matches("")) {
            textTime.setError("Поле является обязательным для заполнения");
            return new VerificationError("Поставьте время события");
        }
        return null;
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        String stringDate = DateConverter.getStringDateFromDateForServer(date);
        eventManager.saveDate(stringDate);

        String time = Objects.requireNonNull(textTime.getText()).toString();
        eventManager.saveTime(time);

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
