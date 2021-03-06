package com.repina.anastasia.momandbaby.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Format the dates
 */
public class FormattedDate {
    private static SimpleDateFormat sd = new SimpleDateFormat(
            "yyyy-MM-dd", java.util.Locale.getDefault());

    private static SimpleDateFormat sdText = new SimpleDateFormat(
            "dd MMMM yyyy", java.util.Locale.getDefault());

    public static String getFormattedDate(Calendar dateAndTime) {
        final Date startDate = dateAndTime.getTime();
        return sd.format(startDate);
    }

    public static String getTextDate(Calendar dateAndTime) {
        final Date startDate = dateAndTime.getTime();
        return sdText.format(startDate);
    }

    public static Date stringToDate(String str) throws ParseException {
        return sd.parse(str);
    }
}
