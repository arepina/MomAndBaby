package com.repina.anastasia.momandbaby.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.repina.anastasia.momandbaby.Activity.ChooseFeatureActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Adapters.SwipeListView;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Processing.StatsProcessing;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

public class FragmentBaby extends Fragment implements SwipeListView.SwipeListViewCallback{


    private GridItemArrayAdapter babyArrayAdapter;

    private ListView listViewBaby;

    private Calendar calendar;

    private final int BABY_NEW_FEATURE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        return initBaby(inflater, container);
    }

    //region Init layout

    private View initBaby(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_baby, container, false);

        listViewBaby = (ListView) v.findViewById(R.id.listViewBaby);
        SwipeListView l = new SwipeListView(getContext(), this);
        l.exec();

        babyArrayAdapter = new GridItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.no_data_today), null, null);
        babyArrayAdapter.add(item);
        listViewBaby.setAdapter(babyArrayAdapter);
        if(ConnectionDetector.isConnected(getContext())) {
            // Load today add's from Firebase for baby
            babyArrayAdapter.clear();
            StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);
        }

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setImageResource(R.mipmap.plus);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ConnectionDetector.isConnected(getContext())) {
                    babyArrayAdapter.clear();
                    Intent intent = new Intent(v.getContext(), ChooseFeatureActivity.class);
                    startActivityForResult(intent, BABY_NEW_FEATURE);
                }
            }
        });

        initYesterdayAndTomorrow(v);

        return v;
    }

    private void initYesterdayAndTomorrow(View v)
    {
        final TextView headerDate = (TextView) v.findViewById(R.id.headerBaby);

        TextView yesterday = (TextView) v.findViewById(R.id.yesterdayBaby);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    babyArrayAdapter.clear();
                    goYesterday(headerDate);
                    if (Calendar.getInstance().after(calendar))
                        StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.no_data), null, null);
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
                        StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.no_data), null, null);
                        babyArrayAdapter.add(item);
                        listViewBaby.setAdapter(babyArrayAdapter);
                    }
                }

            }
        });
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

    //endregion

    //region Delete by swipe

    @Override
    public ListView getListView() {
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

    //endregion

    //region Tomorrow and yesterday

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

    //endregion
}
