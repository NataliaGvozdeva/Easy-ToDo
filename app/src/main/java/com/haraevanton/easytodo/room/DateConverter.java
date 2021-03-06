package com.haraevanton.easytodo.room;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

public class DateConverter {

    @TypeConverter
    public static Calendar toDate(Long timeInMillis) {
        if (timeInMillis == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeInMillis);
            return cal;
        }
    }

    @TypeConverter
    public static Long fromDate(Calendar calendar) {
        if (calendar == null) return null;
        else return calendar.getTimeInMillis();
    }

}
