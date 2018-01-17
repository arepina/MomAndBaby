package com.repina.anastasia.momandbaby.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;

public class ListViewArrayAdapter extends ArrayAdapter<ListViewItem> {
    private ArrayList<ListViewItem> modelItems = null;
    private Context context;

    public ListViewArrayAdapter(Context context, ArrayList<ListViewItem> resource) {
        super(context, R.layout.listview_single, resource);
        this.context = context;
        this.modelItems = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_single, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.textView);
            CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox);
            name.setText(modelItems.get(position).getName());
            if (modelItems.get(position).getValue() == 1)
                cb.setChecked(true);
            else
                cb.setChecked(false);
        }

        return convertView;
    }
}