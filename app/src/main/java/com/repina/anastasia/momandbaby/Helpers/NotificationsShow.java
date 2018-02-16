package com.repina.anastasia.momandbaby.Helpers;


import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Notifications
 */
public class NotificationsShow {
    public static void showToast(Context context, int textId)
    {
        Toast toast = Toast.makeText(context, textId, Toast.LENGTH_SHORT);
        TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
        if (textView != null) textView.setGravity(Gravity.CENTER);
        toast.show();
    }

    public static void showToast(Context context, String text)
    {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
        if (textView != null) textView.setGravity(Gravity.CENTER);
        toast.show();
    }
}
