package com.repina.anastasia.momandbaby.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.repina.anastasia.momandbaby.Activity.ChartActivity;
import com.repina.anastasia.momandbaby.Activity.ChooseFeatureActivity;
import com.repina.anastasia.momandbaby.Activity.SignupActivity;
import com.repina.anastasia.momandbaby.Activity.AppInfoActivity;
import com.repina.anastasia.momandbaby.Adapter.ItemArrayAdapter;
import com.repina.anastasia.momandbaby.Classes.ConnectionDetector;
import com.repina.anastasia.momandbaby.Classes.StatsProcessing;
import com.repina.anastasia.momandbaby.Classes.FormattedDate;
import com.repina.anastasia.momandbaby.Classes.SendEmail;
import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.Classes.ToastShow;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.repina.anastasia.momandbaby.Activity.TabsActivity.googleFit;


public class FragmentTab extends Fragment {

    private Calendar calendar;

    private ItemArrayAdapter babyArrayAdapter;
    private ItemArrayAdapter momArrayAdapter;

    private ListView listViewBaby;
    private ListView listViewMom;

    private final int BABY_NEW_FEATURE = 0;
    private final int MOM_NEW_FEATURE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;

        calendar = Calendar.getInstance();

        switch (this.getTag()) {
            case "Analytics": {
                v = initAnalytics(inflater, container);
                break;
            }
            case "Baby": {
                v = initBaby(inflater, container);
                break;
            }
            case "Mom": {
                v = initMom(inflater, container);
                break;
            }
            case "Settings": {
                v = initSettings(inflater, container);
                break;
            }
            default: {
                v = null;
                break;
            }
        }

        return v;
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
                        showAlertDialog(true);
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
                        showAlertDialog(false);
            }
        });

        return v;
    }

    private View initAnalytics(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_analytics, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.INVISIBLE);

        CardView cardViewMom = (CardView) v.findViewById(R.id.momAnalytics);
        cardViewMom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(v.getContext(), ChartActivity.class);
                nextActivity.putExtra("Type", "Mom");
                startActivity(nextActivity);
            }
        });

        CardView cardViewBaby = (CardView) v.findViewById(R.id.babyAnalytics);
        cardViewBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(v.getContext(), ChartActivity.class);
                nextActivity.putExtra("Type", "Baby");
                startActivity(nextActivity);
            }
        });

        return v;
    }

    private View initMom(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_mom, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                momArrayAdapter.clear();
                Intent intent = new Intent(v.getContext(), ChooseFeatureActivity.class);
                intent.putExtra("requestCode", MOM_NEW_FEATURE);
                startActivityForResult(intent, MOM_NEW_FEATURE);
            }
        });

        listViewMom = (ListView) v.findViewById(R.id.listViewMom);
        momArrayAdapter = new ItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        StatsProcessing.getMomStatsForOneDay(googleFit, momArrayAdapter, calendar, getActivity(), listViewMom, false);// Load today data for mom from google fit

        final TextView headerDate = (TextView) v.findViewById(R.id.headerMom);

        TextView yesterday = (TextView) v.findViewById(R.id.yesterdayMom);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    momArrayAdapter.clear();
                    goYesterday(headerDate);
                    StatsProcessing.getMomStatsForOneDay(googleFit, momArrayAdapter, calendar, getActivity(), listViewMom, false);
                }
            }
        });

        TextView tomorrow = (TextView) v.findViewById(R.id.tomorrowMom);
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    momArrayAdapter.clear();
                    goTomorrow(headerDate);
                    StatsProcessing.getMomStatsForOneDay(googleFit, momArrayAdapter, calendar, getActivity(), listViewMom, false);
                }
            }
        });

        return v;
    }

    private View initBaby(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_baby, container, false);

        listViewBaby = (ListView) v.findViewById(R.id.listViewBaby);
        babyArrayAdapter = new ItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);// Load today add's from Firebase for baby

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                babyArrayAdapter.clear();
                Intent intent = new Intent(v.getContext(), ChooseFeatureActivity.class);
                intent.putExtra("requestCode", BABY_NEW_FEATURE);
                startActivityForResult(intent, BABY_NEW_FEATURE);
            }
        });

        final TextView headerDate = (TextView) v.findViewById(R.id.headerBaby);

        TextView yesterday = (TextView) v.findViewById(R.id.yesterdayBaby);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    babyArrayAdapter.clear();
                    goYesterday(headerDate);
                    StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);
                }
            }
        });

        TextView tomorrow = (TextView) v.findViewById(R.id.tomorrowBaby);
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    babyArrayAdapter.clear();
                    goTomorrow(headerDate);
                    StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);
                }

            }
        });

        return v;
    }

    private void goYesterday(TextView headerDate) {
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Calendar today = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            headerDate.setText(R.string.today);
        else {
            String date = FormattedDate.getTextDate(calendar);
            headerDate.setText(date);
        }
    }

    private void goTomorrow(TextView headerDate) {
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Calendar today = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            headerDate.setText(R.string.today);
        else {
            String date = FormattedDate.getTextDate(calendar);
            headerDate.setText(date);
        }
    }

    private void showAlertDialog(final boolean whoFlag) {
        //todo change later
        // AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.choose_period);

        builder.setPositiveButton(R.string.for_day, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendEmail.createEmail(getContext(), 0, whoFlag, getActivity(), new ItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row));
            }
        });

        builder.setNegativeButton(R.string.for_week, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendEmail.createEmail(getContext(), 1, whoFlag, getActivity(), new ItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row));
            }
        });

        builder.setNeutralButton(R.string.for_month, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendEmail.createEmail(getContext(), 2, whoFlag, getActivity(), new ItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BABY_NEW_FEATURE: {
                StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAlertDialog(true);
                }
            }
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAlertDialog(false);
                }
            }
        }
    }
}