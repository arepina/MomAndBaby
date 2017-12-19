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

import com.repina.anastasia.momandbaby.Activity.ChartActivity;
import com.repina.anastasia.momandbaby.Activity.GoogleFitActivity;
import com.repina.anastasia.momandbaby.Activity.NewActivity;
import com.repina.anastasia.momandbaby.Activity.SignupActivity;
import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.Classes.ToastShow;
import com.repina.anastasia.momandbaby.R;

import static android.content.Context.MODE_PRIVATE;


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

        Button rate = (Button) v.findViewById(R.id.rate);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastShow.show(v.getContext(), R.string.soon);
                //Try Google play
               /* Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.cubeactive.qnotelistfree"));
                if (!MyStartActivity(intent)) {
                    //Market (Google play) app seems not installed, let's try to open a webbrowser
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.cubeactive.qnotelistfree"));
                    if (!MyStartActivity(intent)) {
                        //Well if this also fails, we have run out of options, inform the user.
                        Toast.makeText(getApplicationContext(), "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                    }
                }*/
                //TODO: add link to google play
            }
        });

        Button appInfo = (Button) v.findViewById(R.id.appInfo);
        appInfo.setOnClickListener(new View.OnClickListener() {
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
        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.band_dark);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(v.getContext(), GoogleFitActivity.class);
                startActivity(intent);
            }
        });

        //todo

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
                Intent intent = new Intent(v.getContext(), NewActivity.class);
                startActivity(intent);
            }
        });

        //todo

        return v;
    }
}