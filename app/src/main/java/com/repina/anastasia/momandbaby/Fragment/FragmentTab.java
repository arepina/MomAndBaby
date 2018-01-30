package com.repina.anastasia.momandbaby.Fragment;

import android.Manifest;
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
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.repina.anastasia.momandbaby.Activity.AppInfoActivity;
import com.repina.anastasia.momandbaby.Activity.ChartActivity;
import com.repina.anastasia.momandbaby.Activity.ChooseFeatureActivity;
import com.repina.anastasia.momandbaby.Activity.SignupActivity;
import com.repina.anastasia.momandbaby.Activity.StatsActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Adapters.SwipeListView;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.SendEmail;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.Helpers.Processing.StatsProcessing;
import com.repina.anastasia.momandbaby.Helpers.ToastShow;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.repina.anastasia.momandbaby.Activity.TabsActivity.googleFit;


public class FragmentTab extends Fragment implements SwipeListView.SwipeListViewCallback {

    private Calendar calendar;

    private GridItemArrayAdapter babyArrayAdapter;
    private GridItemArrayAdapter momArrayAdapter;

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
                Intent nextActivity = new Intent(v.getContext(), StatsActivity.class);
                startActivity(nextActivity);
            }
        });

        return v;
    }

    private View initMom(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_mom, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setImageResource(R.mipmap.google_fit);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                momArrayAdapter.clear();
//                Intent intent = new Intent(v.getContext(), ChooseFeatureActivity.class);
//                intent.putExtra("requestCode", MOM_NEW_FEATURE);
//                startActivityForResult(intent, MOM_NEW_FEATURE);
                ToastShow.show(getContext(), getString(R.string.need_to_sync));
            }
        });

        listViewMom = (ListView) v.findViewById(R.id.listViewMom);
        momArrayAdapter = new GridItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        StatsProcessing.getMomStatsForOneDay(googleFit, momArrayAdapter, calendar, getActivity(), listViewMom, false);// Load today data for mom from google fit

        final TextView headerDate = (TextView) v.findViewById(R.id.headerMom);

        TextView yesterday = (TextView) v.findViewById(R.id.yesterdayMom);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    momArrayAdapter.clear();
                    goYesterday(headerDate);
                    if (Calendar.getInstance().after(calendar))
                        StatsProcessing.getMomStatsForOneDay(googleFit, momArrayAdapter, calendar, getActivity(), listViewMom, false);
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.google_fit_no_data), null, null);
                        momArrayAdapter.add(item);
                        listViewMom.setAdapter(momArrayAdapter);
                    }
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
                    if (Calendar.getInstance().after(calendar))
                        StatsProcessing.getMomStatsForOneDay(googleFit, momArrayAdapter, calendar, getActivity(), listViewMom, false);
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.google_fit_no_data), null, null);
                        momArrayAdapter.add(item);
                        listViewMom.setAdapter(momArrayAdapter);
                    }
                }
            }
        });

        return v;
    }

    private View initBaby(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_baby, container, false);

        listViewBaby = (ListView) v.findViewById(R.id.listViewBaby);
        SwipeListView l = new SwipeListView(getContext(), this);
        l.exec();

        babyArrayAdapter = new GridItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        StatsProcessing.getBabyStatsForOneDay(babyArrayAdapter, calendar, getContext(), listViewBaby);// Load today add's from Firebase for baby

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setImageResource(R.mipmap.plus);
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
                    if (Calendar.getInstance().after(calendar))
                        StatsProcessing.getBabyStatsForOneDay(babyArrayAdapter, calendar, getContext(), listViewBaby);
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.need_to_sync), null, null);
                        babyArrayAdapter.add(item);
                        listViewBaby.setAdapter(babyArrayAdapter);
                    }
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
                    if (Calendar.getInstance().after(calendar))
                        StatsProcessing.getBabyStatsForOneDay(babyArrayAdapter, calendar, getContext(), listViewBaby);
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.need_to_sync), null, null);
                        babyArrayAdapter.add(item);
                        listViewBaby.setAdapter(babyArrayAdapter);
                    }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        builder.setView(view);
        Button day = (Button) view.findViewById(R.id.day);
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendEmail.createEmail(getContext(), 0, null, null,
                        whoFlag, getActivity(),
                        new GridItemArrayAdapter(getActivity().getApplicationContext(),
                                R.layout.custom_row));
            }
        });
        Button week = (Button) view.findViewById(R.id.week);
        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendEmail.createEmail(getContext(), 1, null, null,
                        whoFlag, getActivity(),
                        new GridItemArrayAdapter(getActivity().getApplicationContext(),
                                R.layout.custom_row));
            }
        });
        Button month = (Button) view.findViewById(R.id.month);
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendEmail.createEmail(getContext(), 2, null, null,
                        whoFlag, getActivity(),
                        new GridItemArrayAdapter(getActivity().getApplicationContext(),
                                R.layout.custom_row));
            }
        });
        Button custom = (Button) view.findViewById(R.id.custom);
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder calendarBuilder = new AlertDialog.Builder(getContext());
                final View view = getActivity().getLayoutInflater().inflate(R.layout.custom_calendar, null);
                calendarBuilder.setView(view);
                calendarBuilder.create().show();
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
                        SendEmail.createEmail(getContext(), 3, from, to,
                                whoFlag, getActivity(),
                                new GridItemArrayAdapter(getActivity().getApplicationContext(),
                                        R.layout.custom_row));
                    }
                });
            }
        });

        builder.create().show();
    }

    @Override
    public ListView getListView() {
        if (this.getTag().equals("Mom"))
            return listViewMom;
        return listViewBaby;
    }

    @Override
    public void onSwipeItem(boolean isRight, int position) {
        if (this.getTag().equals("Baby")) {
            if (!(babyArrayAdapter.getCount() == 1 &&
                    babyArrayAdapter.getItem(0)
                            .getItemDesc().equals(getString(R.string.no_data_today)))) // do not need to delete no data item
                babyArrayAdapter.onSwipeItem(isRight, position);
        }
    }

    @Override
    public void onItemClickListener(ListAdapter adapter, int position) {
        // do nothing
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BABY_NEW_FEATURE: {
                StatsProcessing.getBabyStatsForOneDay(babyArrayAdapter, calendar, getContext(), listViewBaby);
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