package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.matthewtamlin.sliding_intro_screen_library.background.BackgroundManager;
import com.matthewtamlin.sliding_intro_screen_library.background.ColorBlender;
import com.matthewtamlin.sliding_intro_screen_library.buttons.IntroButton;
import com.matthewtamlin.sliding_intro_screen_library.core.IntroActivity;
import com.matthewtamlin.sliding_intro_screen_library.pages.ParallaxPage;
import com.matthewtamlin.sliding_intro_screen_library.transformers.MultiViewParallaxTransformer;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Dots shown on the first user entry
 */
public class DotsActivity extends IntroActivity {

    private static final int[] BACKGROUND_COLORS = {0xffC75163, 0xffFFD15D, 0xffC75163,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Skip to the next activity if the user has previously completed the introduction
        if (introductionCompletedPreviously()) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String momId = sp.getString(SharedConstants.MOM_ID_KEY, "");
            if (momId.length() != 0) {// User have already registered, go to main page
                Intent nextActivity = new Intent(this, TabsActivity.class);
                startActivity(nextActivity);
                finish();
            } else {// User does not have an account
                Intent nextActivity = new Intent(this, SignupActivity.class);
                startActivity(nextActivity);
                finish();
            }
        }
        configureTransformer();
        configureBackground();
    }

    @Override
    protected Collection<Fragment> generatePages(Bundle savedInstanceState) throws OutOfMemoryError {
        // This variable holds the pages while they are being created
        ArrayList<Fragment> pages = new ArrayList<>();
        ParallaxPage newPage = ParallaxPage.newInstance();
        String lang = Locale.getDefault().getDisplayLanguage();
        int resourceId1, resourceId2, resourceId3;
        if (lang.toLowerCase().equals(getString(R.string.russian))) {
            resourceId1 = R.drawable.mother;
            resourceId2 = R.drawable.baby;
            resourceId3 = R.drawable.analytics;
        } else {
            resourceId1 = R.drawable.mother_eng;
            resourceId2 = R.drawable.baby_eng;
            resourceId3 = R.drawable.analytics_eng;
        }
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), resourceId1);
        newPage.setFrontImage(mBitmap);
        pages.add(newPage);

        newPage = ParallaxPage.newInstance();

        mBitmap = BitmapFactory.decodeResource(getResources(), resourceId2);
        newPage.setFrontImage(mBitmap);
        pages.add(newPage);

        newPage = ParallaxPage.newInstance();

        mBitmap = BitmapFactory.decodeResource(getResources(), resourceId3);
        newPage.setFrontImage(mBitmap);
        pages.add(newPage);

        return pages;
    }

    @Override
    protected IntroButton.Behaviour generateFinalButtonBehaviour() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor pendingEdits = sp.edit();
        // Define the next activity intent and create the Behaviour to use for the final button
        Intent nextActivity = new Intent(this, SignupActivity.class);
        return new IntroButton.ProgressToNextActivity(nextActivity, pendingEdits);
    }

    /**
     * Check if introduction was completed before
     *
     * @return if introduction was completed before
     */
    private boolean introductionCompletedPreviously() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean wasShown = sp.getBoolean(SharedConstants.DISPLAY_ONCE_KEY, false);
        if (!wasShown) {
            SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(SharedConstants.DISPLAY_ONCE_KEY, true);
            pendingEdits.apply();
        }
        return wasShown;
    }

    /**
     * Set the transformer params
     */
    private void configureTransformer() {
        MultiViewParallaxTransformer transformer = new MultiViewParallaxTransformer();
        transformer.withParallaxView(R.id.page_fragment_imageHolderFront, 1.2f);
        setPageTransformer(false, transformer);
    }

    /**
     * Set the background params
     */
    private void configureBackground() {
        BackgroundManager backgroundManager = new ColorBlender(BACKGROUND_COLORS);
        setBackgroundManager(backgroundManager);
    }
}