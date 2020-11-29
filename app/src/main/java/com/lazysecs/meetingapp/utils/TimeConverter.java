package com.lazysecs.meetingapp.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import com.lazysecs.meetingapp.R;

import java.util.Calendar;

public class TimeConverter {
    public static String getTime(TimePicker timePicker, int hour, int minute) {
        timePicker.setIs24HourView(true);

        String strMinute = String.valueOf(minute);
        if (minute < 10) {
            strMinute = "0" + minute;
        }

        String strHour = String.valueOf(hour);
        if (hour < 10) {
            strHour = "0" + hour;
        }

        return strHour + ":" + strMinute;
    }

    public static void showTimePickerDialog(Context context, TimePickerDialog.OnTimeSetListener listener) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                R.style.DialogTheme,
                listener,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }
}
