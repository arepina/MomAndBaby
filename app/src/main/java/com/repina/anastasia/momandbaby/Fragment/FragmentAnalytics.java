package com.repina.anastasia.momandbaby.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.repina.anastasia.momandbaby.Activity.ChartActivity;
import com.repina.anastasia.momandbaby.Activity.StatsActivity;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.R;

import static com.repina.anastasia.momandbaby.Fragment.FragmentMom.google_fit_connected;

/**
 * Analytics fragment
 */
public class FragmentAnalytics extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initAnalytics(inflater, container);
    }

    private View initAnalytics(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_analytics, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.INVISIBLE);

        CardView cardViewMom = (CardView) v.findViewById(R.id.momAnalytics);
        cardViewMom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    if (google_fit_connected) {
                        Intent nextActivity = new Intent(v.getContext(), ChartActivity.class);
                        nextActivity.putExtra("Type", "Mom");
                        startActivity(nextActivity);
                    } else
                        NotificationsShow.showToast(getContext(), R.string.need_to_sync);
                }
            }
        });

        CardView cardViewBaby = (CardView) v.findViewById(R.id.babyAnalytics);
        cardViewBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    Intent nextActivity = new Intent(v.getContext(), StatsActivity.class);
                    startActivity(nextActivity);
                }
            }
        });

        return v;
    }
}
