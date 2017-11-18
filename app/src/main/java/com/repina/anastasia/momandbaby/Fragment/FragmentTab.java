package com.repina.anastasia.momandbaby.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
                v = inflater.inflate(R.layout.fragment_analytics, container, false);
                break;
            }
            case "Baby": {
                v = inflater.inflate(R.layout.fragment_baby, container, false);
                break;
            }
            case "Mom": {
                v = inflater.inflate(R.layout.fragment_mom, container, false);
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
}