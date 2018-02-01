package com.repina.anastasia.momandbaby.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.repina.anastasia.momandbaby.Activity.TabsActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Processing.StatsProcessing;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

public class FragmentMom extends Fragment {

    private GridItemArrayAdapter momArrayAdapter;

    private ListView listViewMom;

    private Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        return initMom(inflater, container);
    }

    private View initMom(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_mom, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setImageResource(R.mipmap.google_fit);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationsShow.showToast(getContext(), getString(R.string.need_to_sync));
            }
        });

        listViewMom = (ListView) v.findViewById(R.id.listViewMom);
        momArrayAdapter = new GridItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        StatsProcessing.getMomStatsForOneDay( momArrayAdapter, calendar, getActivity(), listViewMom, false);// Load today data for mom from google fit

        final TextView headerDate = (TextView) v.findViewById(R.id.headerMom);

        TextView yesterday = (TextView) v.findViewById(R.id.yesterdayMom);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    momArrayAdapter.clear();
                    goYesterday(headerDate);
                    if (Calendar.getInstance().after(calendar))
                        StatsProcessing.getMomStatsForOneDay(momArrayAdapter, calendar, getActivity(), listViewMom, false);
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
                        StatsProcessing.getMomStatsForOneDay(momArrayAdapter, calendar, getActivity(), listViewMom, false);
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

    @Override
    public void onPause() {
        super.onPause();
        if(TabsActivity.dialog != null)
            TabsActivity.dialog.dismiss();
    }
}