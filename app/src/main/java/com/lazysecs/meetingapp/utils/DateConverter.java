package com.lazysecs.meetingapp.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;

import com.lazysecs.meetingapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class DateConverter {
    private final static String pattern = "dd MMMM";
    private final static String fullPattern = "yyyy/MM/dd HH:mm:ss";
    private final static String serverPattern = "YYYY-MM-dd";

    public static String getDate(int year, int month, int day) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", new Locale("RU"));
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month, day);
        return sdf.format(calendar.getTime());
    }

    public static Date getDateFromString(String dateString) {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat(pattern);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void showDatePickerDialog(Date date, Context context, DatePickerDialog.OnDateSetListener listener) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                R.style.DialogTheme,
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    public static String getStringDateFromDate(Date date) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static String getStringDateFromDateForServer(Date date) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(serverPattern);
        return dateFormat.format(date);
    }


    public static String getTodayDateString() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date());
    }

    public static String getTomorrowDateString() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);

        return dateFormat.format(calendar.getTime());
    }

    public static String getCalendarDateFormat(int year, int month, int day) {
        String strMonth = String.valueOf(month + 1);
        if (month < 10) {
            strMonth = "0" + (month + 1);
        }

        String strDay = String.valueOf(day);
        if (day < 10) {
            strDay = "0" + day;
        }

        return year + "-" + strMonth + "-" + strDay;
    }

    public static long stringDateToMillis(String stringDate) {
        String newFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ZonedDateTime zdt = ZonedDateTime.parse(stringDate);
            newFormat = zdt.format(DateTimeFormatter.ofPattern(fullPattern));
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(fullPattern);
        Date date = null;
        try {
            date = sdf.parse(Objects.requireNonNull(newFormat));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(date).getTime();
    }

    public static String millisToStringDate(long milliSeconds) {
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd MMMM");

        Date date = new Date(milliSeconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(date);
    }

    public static String getDateTimeInCurrentTimezone(String dateTime) {
        if (dateTime.equals("0"))
            return "";

        dateTime = dateTime.substring(0, dateTime.length() - 4).replace("T", " ") + "+0000";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        Date date = null;


        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat formatter = new SimpleDateFormat("dd MMMM в HH:mm");
        formatter.setTimeZone(Calendar.getInstance().getTimeZone());
        String dateFormatted = formatter.format(date);

        return dateFormatted;
    }

    public static String getTimeInCurrentTimezone(String dateTime) {
        if (dateTime.equals("0"))
            return "";

        dateTime = dateTime.substring(0, dateTime.length() - 4).replace("T", " ") + "+0000";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        Date date = null;


        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(Calendar.getInstance().getTimeZone());
        String dateFormatted = formatter.format(date);

        return dateFormatted;
    }

    public String parseCreated(String created) {
        String newFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ZonedDateTime zdt = ZonedDateTime.parse(created);
            newFormat = zdt.format(DateTimeFormatter.ofPattern("dd/MM hh:mm"));
        }

        SimpleDateFormat month_date = new SimpleDateFormat("dd MMMM в hh:mm", new Locale("RU"));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM hh:mm");

        Date date = null;
        try {
            date = sdf.parse(Objects.requireNonNull(newFormat));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            return month_date.format(date);
        }

        return "";
    }
}
