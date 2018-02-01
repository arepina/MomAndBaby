package com.repina.anastasia.momandbaby.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.repina.anastasia.momandbaby.Activity.AppInfoActivity;
import com.repina.anastasia.momandbaby.Activity.SignupActivity;
import com.repina.anastasia.momandbaby.Activity.TabsActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.SendEmail;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class FragmentSettings extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initSettings(inflater, container);
    }

    private View initSettings(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.INVISIBLE);

        Button exit = (Button) v.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = v.getContext().getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SharedConstants.MOM_ID_KEY, null);
                editor.putString(SharedConstants.MOM_NAME_KEY, null);
                editor.apply();

                Intent nextActivity = new Intent(v.getContext(), SignupActivity.class);
                startActivity(nextActivity);
                getActivity().finish();
            }
        });

        Button feedback = (Button) v.findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Mom&Baby");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                intent.setData(Uri.parse("mailto:" + R.string.my_email));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Button appInfo = (Button) v.findViewById(R.id.appInfo);
        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(v.getContext(), AppInfoActivity.class);
                startActivity(nextActivity);
            }
        });

        Button sendReportBaby = (Button) v.findViewById(R.id.send_report_baby);
        sendReportBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    else
                        showAlertDialog(true, getActivity());
                }
            }
        });

        Button sendReportMom = (Button) v.findViewById(R.id.send_report_mom);
        sendReportMom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext()))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    else
                        showAlertDialog(false, getActivity());
            }
        });

        return v;
    }

    public void showAlertDialog(final boolean whoFlag, final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        builder.setView(view);
        final AlertDialog alert = builder.create();
        Button day = (Button) view.findViewById(R.id.day);
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                SendEmail.createEmail(activity.getApplicationContext(), 0, null, null,
                        whoFlag, (FragmentActivity) activity,
                        new GridItemArrayAdapter(activity.getApplicationContext(),
                                R.layout.custom_row));
            }
        });
        Button week = (Button) view.findViewById(R.id.week);
        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                SendEmail.createEmail(activity.getApplicationContext(), 1, null, null,
                        whoFlag, (FragmentActivity) activity,
                        new GridItemArrayAdapter(activity.getApplicationContext(),
                                R.layout.custom_row));
            }
        });
        Button month = (Button) view.findViewById(R.id.month);
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                SendEmail.createEmail(activity.getApplicationContext(), 2, null, null,
                        whoFlag, (FragmentActivity) activity,
                        new GridItemArrayAdapter(activity.getApplicationContext(),
                                R.layout.custom_row));
            }
        });
        Button custom = (Button) view.findViewById(R.id.custom);
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder calendarBuilder = new AlertDialog.Builder(activity.getApplicationContext());
                final View view = activity.getLayoutInflater().inflate(R.layout.custom_calendar, null);
                calendarBuilder.setView(view);
                final AlertDialog calendarAlert = calendarBuilder.create();
                calendarAlert.show();
                Button submit = (Button) view.findViewById(R.id.submit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePicker fromPicker = (DatePicker) view.findViewById(R.id.from);
                        Calendar from = Calendar.getInstance();
                        from.set(Calendar.YEAR, fromPicker.getYear());
                        from.set(Calendar.MONTH, fromPicker.getMonth());
                        from.set(Calendar.DATE, fromPicker.getDayOfMonth());
                        DatePicker toPicker = (DatePicker) view.findViewById(R.id.to);
                        Calendar to = Calendar.getInstance();
                        to.set(Calendar.YEAR, toPicker.getYear());
                        to.set(Calendar.MONTH, toPicker.getMonth());
                        to.set(Calendar.DATE, toPicker.getDayOfMonth());
                        if(from.after(to) || Calendar.getInstance().before(to))
                            NotificationsShow.showToast(activity.getApplicationContext(), getString(R.string.incorrect_dates));
                        else {
                            calendarAlert.cancel();
                            alert.cancel();
                            SendEmail.createEmail(activity.getApplicationContext(), 3, from, to,
                                    whoFlag, (FragmentActivity) activity,
                                    new GridItemArrayAdapter(activity.getApplicationContext(),
                                            R.layout.custom_row));
                        }
                    }
                });
            }
        });
        alert.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(TabsActivity.dialog != null)
            TabsActivity.dialog.dismiss();
    }
}
