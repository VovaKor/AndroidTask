package com.androidtask.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 27.06.17.
 */

public class DateManager {

    public static Date createDate(String banDay) {
        String days = checkNotNull(banDay,"banDay cannot be null");
        DateTime now = DateTime.now(TimeZone.getDefault());
        DateTime result = now.plusDays(Integer.parseInt(days));
        
        return convertDateTimeToDate(result);

    }

    private static Date convertDateTimeToDate(DateTime dateTime) {
        int year = dateTime.getYear();
        int datetimeMonth = dateTime.getMonth();
        int day = dateTime.getDay();

        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        // datetimeMonth start at 1. Need to minus 1 to get javaMonth
        calendar.set(year, datetimeMonth - 1, day);

        return calendar.getTime();
    }

    public static boolean isDateAfterNow(Date date) {
        return date.after(Calendar.getInstance(TimeZone.getDefault()).getTime());
    }
}
