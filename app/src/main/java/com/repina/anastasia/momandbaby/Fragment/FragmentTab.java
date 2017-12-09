package com.repina.anastasia.momandbaby.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.repina.anastasia.momandbaby.Activity.ChartActivity;
import com.repina.anastasia.momandbaby.Activity.MiBandActivity;
import com.repina.anastasia.momandbaby.Activity.NewActivity;
import com.repina.anastasia.momandbaby.R;


public class FragmentTab extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
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
                v = inflater.inflate(R.layout.fragment_settings, container, false);
                break;
            }
            default: {
                v = null;
                break;
            }
        }

        return v;
    }

    private View initAnalytics(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_analytics, container, false);

        CardView cardViewMom = (CardView) v.findViewById(R.id.momAnalytics);
        cardViewMom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(v.getContext(), ChartActivity.class);
                nextActivity.putExtra("Type", "Mom");
                //todo send bandCode in Extras here
                startActivity(nextActivity);
            }
        });

        CardView cardViewBaby = (CardView) v.findViewById(R.id.babyAnalytics);
        cardViewBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(v.getContext(), ChartActivity.class);
                nextActivity.putExtra("Type", "Baby");
                //todo send bandCode in Extras here
                startActivity(nextActivity);
            }
        });

        return v;
    }

    private View initMom(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_mom, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setImageResource(R.drawable.band_dark);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(v.getContext(), MiBandActivity.class);
                startActivity(intent);
            }
        });

        //todo

        return v;
    }

    private View initBaby(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_baby, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setImageResource(R.mipmap.plus);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(v.getContext(), NewActivity.class);
                startActivity(intent);
            }
        });

        //todo

        return v;
    }
}