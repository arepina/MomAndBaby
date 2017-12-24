package com.repina.anastasia.momandbaby.Classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormattedDate {
    public static String getFormattedDateWithTime(Calendar dateAndTime) {
        SimpleDateFormat sd = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        final Date startDate = dateAndTime.getTime();
        return sd.format(startDate);
    }

    public static String getFormattedDateWithoutTime(Calendar dateAndTime) {
        SimpleDateFormat sd = new SimpleDateFormat(
                "yyyy-MM-dd", java.util.Locale.getDefault());
        final Date startDate = dateAndTime.getTime();
        return sd.format(startDate);
    }
}
