package com.repina.anastasia.momandbaby.Classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormattedDate {
    private static SimpleDateFormat sd = new SimpleDateFormat(
            "yyyy-MM-dd", java.util.Locale.getDefault());

    public static String getFormattedDateWithTime(Calendar dateAndTime) {
        final Date startDate = dateAndTime.getTime();
        return sd.format(startDate);
    }

    public static String getFormattedDateWithoutTime(Calendar dateAndTime) {
        final Date startDate = dateAndTime.getTime();
        return sd.format(startDate);
    }

    public static Date stringToDate(String str) throws ParseException {
        return sd.parse(str);
    }
}
