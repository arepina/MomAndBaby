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

/**
 * Baby fragment
 */
public class FragmentBaby extends Fragment implements SwipeListView.SwipeListViewCallback {


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

    /**
     * Initialise baby layout
     *
     * @param inflater  LayoutInflater
     * @param container ViewGroup
     * @return View
     */
    private View initBaby(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_baby, container, false);

        listViewBaby = v.findViewById(R.id.listViewBaby);
        SwipeListView l = new SwipeListView(getContext(), this);
        l.exec();

        babyArrayAdapter = new GridItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.no_data_today), null, null);
        babyArrayAdapter.add(item);
        listViewBaby.setAdapter(babyArrayAdapter);
        if (ConnectionDetector.isConnected(getContext())) {
            // Load today add's from Firebase for baby
            babyArrayAdapter.clear();
            StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);
        }

        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButton);
        fab.setImageResource(R.mipmap.plus);
        fab.setVisibility(View.VISIBLE);
        fab.setEnabled(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionDetector.isConnected(getContext())) {
                    babyArrayAdapter.clear();
                    Intent intent = new Intent(v.getContext(), ChooseFeatureActivity.class);
                    startActivityForResult(intent, BABY_NEW_FEATURE);
                }
            }
        });

        initYesterdayAndTomorrow(v);

        return v;
    }

    /**
     * Initialise yesterday and tomorrow buttons
     *
     * @param v View
     */
    private void initYesterdayAndTomorrow(View v) {
        final TextView headerDate = v.findViewById(R.id.headerBaby);

        TextView yesterday = v.findViewById(R.id.yesterdayBaby);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    babyArrayAdapter.clear();
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    Calendar today = Calendar.getInstance();
                    //boolean isToday = false;
                    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                            && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                        headerDate.setText(R.string.today);
                        //isToday = true;
                    } else {
                        String date = FormattedDate.getTextDate(calendar);
                        headerDate.setText(date);
                    }
                    //if (Calendar.getInstance().after(calendar) || isToday)
                        StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);
                   /* else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.no_data), null, null);
                        babyArrayAdapter.add(item);
                        listViewBaby.setAdapter(babyArrayAdapter);
                    }*/
                }
            }
        });

        TextView tomorrow = v.findViewById(R.id.tomorrowBaby);
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    babyArrayAdapter.clear();
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    Calendar today = Calendar.getInstance();
                    //boolean isToday = false;
                    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                            && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                        headerDate.setText(R.string.today);
                        //isToday = true;
                    } else {
                        String date = FormattedDate.getTextDate(calendar);
                        headerDate.setText(date);
                    }
                    //if (Calendar.getInstance().after(calendar) || isToday)
                        StatsProcessing.getBabyStats(babyArrayAdapter, calendar, getContext(), listViewBaby);
                   /* else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.no_data), null, null);
                        babyArrayAdapter.add(item);
                        listViewBaby.setAdapter(babyArrayAdapter);
                    }*/
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
}
