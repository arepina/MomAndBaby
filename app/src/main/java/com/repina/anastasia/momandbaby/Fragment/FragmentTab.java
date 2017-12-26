package com.repina.anastasia.momandbaby.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.repina.anastasia.momandbaby.Activity.ChartActivity;
import com.repina.anastasia.momandbaby.Activity.GoogleFitActivity;
import com.repina.anastasia.momandbaby.Activity.ChooseFeatureActivity;
import com.repina.anastasia.momandbaby.Activity.SignupActivity;
import com.repina.anastasia.momandbaby.Activity.AppInfoActivity;
import com.repina.anastasia.momandbaby.Adapter.ItemArrayAdapter;
import com.repina.anastasia.momandbaby.Classes.FirebaseData;
import com.repina.anastasia.momandbaby.Classes.FormattedDate;
import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.Classes.ToastShow;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


public class FragmentTab extends Fragment {

    private Calendar calendar;

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
                //todo
            }
        });

        Button sendReportMom = (Button) v.findViewById(R.id.send_report_mom);
        sendReportMom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
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
        fab.setImageResource(R.drawable.band_dark);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(v.getContext(), GoogleFitActivity.class);
                startActivity(intent);
            }
        });

        ListView listViewMom = (ListView) v.findViewById(R.id.listViewMom);
        ItemArrayAdapter momArrayAdapter = new ItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        FirebaseData.updateTodayMom();// Load today add's from Firebase for mom
        listViewMom.setAdapter(momArrayAdapter);

        final TextView headerDate = (TextView)v.findViewById(R.id.headerMom);
        headerDate.setText(R.string.today);

        TextView yesterday = (TextView)v.findViewById(R.id.yesterdayMom);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goYesterday(headerDate);
            }
        });

        TextView tomorrow = (TextView)v.findViewById(R.id.tomorrowMom);
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTomorrow(headerDate);
            }
        });

        return v;
    }

    private View initBaby(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_baby, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.mipmap.plus);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(v.getContext(), ChooseFeatureActivity.class);
                startActivity(intent);
            }
        });

        ListView listViewBaby = (ListView) v.findViewById(R.id.listViewBaby);
        ItemArrayAdapter babyArrayAdapter = new ItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        FirebaseData.updateTodayBaby(getActivity().getResources(), babyArrayAdapter);// Load today add's from Firebase for baby
        listViewBaby.setAdapter(babyArrayAdapter);

        final TextView headerDate = (TextView)v.findViewById(R.id.headerBaby);
        headerDate.setText(R.string.today);

        TextView yesterday = (TextView)v.findViewById(R.id.yesterdayBaby);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goYesterday(headerDate);
            }
        });

        TextView tomorrow = (TextView)v.findViewById(R.id.tomorrowBaby);
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTomorrow(headerDate);
            }
        });

        return v;
    }

    private void goYesterday(TextView headerDate)
    {
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Calendar today = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            headerDate.setText(R.string.today);
        else {
            String date = FormattedDate.getFormattedDateWithoutTime(calendar);
            headerDate.setText(date);
        }
    }

    private void goTomorrow(TextView headerDate)
    {
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Calendar today = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            headerDate.setText(R.string.today);
        else {
            String date = FormattedDate.getFormattedDateWithoutTime(calendar);
            headerDate.setText(date);
        }
    }

}