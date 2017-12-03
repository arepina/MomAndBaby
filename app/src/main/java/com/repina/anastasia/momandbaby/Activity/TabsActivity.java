package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.repina.anastasia.momandbaby.Fragment.FragmentTab;
import com.repina.anastasia.momandbaby.R;

public class TabsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("Analytics").setIndicator("", null),
                FragmentTab.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Baby").setIndicator("", null),
                FragmentTab.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Mom").setIndicator("", null),
                FragmentTab.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Settings").setIndicator("", null),
                FragmentTab.class, null);

        setTabIcon(mTabHost, 0, R.mipmap.analytics); //for Tab 1
        setTabIcon(mTabHost, 1, R.mipmap.baby); //for Tab 2
        setTabIcon(mTabHost, 2, R.mipmap.mother); //for Tab 3
        setTabIcon(mTabHost, 3, R.mipmap.settings); //for Tab 4

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setTabIcon(TabHost tabHost, int tabIndex, int iconResource) {
        ImageView tabImageView = (ImageView) tabHost.getTabWidget().getChildTabViewAt(tabIndex).findViewById(android.R.id.icon);
        tabImageView.setVisibility(View.VISIBLE);
        tabImageView.setImageResource(iconResource);
    }
}
