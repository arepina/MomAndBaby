package com.repina.anastasia.momandbaby.Connectors;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.repina.anastasia.momandbaby.R;

/**
 * Check the internet connection class
 */
public class ConnectionDetector {
    private Context _context;

    private ConnectionDetector(Context context) {
        this._context = context;
    }

    /**
     * Try to connect to the internet
     */
    private boolean ConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            return info != null && info.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    /**
     * Check if there is a connection
     */
    public static boolean isConnected(Context context) {
        ConnectionDetector cd = new ConnectionDetector(context.getApplicationContext());
        Boolean isInternetPresent = cd.ConnectingToInternet();
        if(!isInternetPresent)
            showToast(context);
        return isInternetPresent;
    }

    /**
     * Show the toast that there is no connection
     */
    private static void showToast(Context context) {
        Toast toast = Toast.makeText(context.getApplicationContext(), R.string.internet, Toast.LENGTH_LONG);
        TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
        if (textView != null) textView.setGravity(Gravity.CENTER);
        toast.show();
    }
}

