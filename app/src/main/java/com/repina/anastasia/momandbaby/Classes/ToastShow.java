package com.repina.anastasia.momandbaby.Classes;


import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


public class ToastShow {
    public static void show(Context context, int textId)
    {
        Toast toast = Toast.makeText(context, textId, Toast.LENGTH_SHORT);
        TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
        if (textView != null) textView.setGravity(Gravity.CENTER);
        toast.show();
    }
}
