package com.valterc.mindcrackfront.app.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Valter on 21/12/2014.
 */
public abstract class DateFormatter {

    private static SimpleDateFormat dateFormat;

    public static void setUp(Context context) {

        if (DateFormat.is24HourFormat(context)) {
            dateFormat = new SimpleDateFormat("dd' of 'MMMM' at 'HH':'mm");
        } else {
            dateFormat = new SimpleDateFormat("dd' of 'MMMM' at 'hh':'mma");
        }
        dateFormat.setTimeZone(TimeZone.getDefault());

    }

    public static String format(Date date) {
        long totalMillis = Calendar.getInstance().getTimeInMillis() - date.getTime();

        int seconds = (int) (totalMillis / 1000) % 60;
        int minutes = ((int) (totalMillis / 1000) / 60) % 60;
        int hours = (int) (totalMillis / 1000) / 3600;

        if (hours > 48) {
            return dateFormat.format(date);
        } else if (hours > 24) {
            return "1 day ago";
        } else if (hours > 0) {
            if (hours == 1) {
                return hours + " hour ago";
            }
            return hours + " hours ago";
        } else if (minutes > 0) {
            if (minutes == 1) {
                return minutes + " minute ago";
            }
            return minutes + " minutes ago";
        } else {
            return "a moment ago";
        }

    }
}
