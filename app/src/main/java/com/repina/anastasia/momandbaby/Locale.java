package com.repina.anastasia.momandbaby;


import android.content.Context;
import android.os.Build;

public class Locale {
    public static java.util.Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }
}
