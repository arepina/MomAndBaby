package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.matthewtamlin.sliding_intro_screen_library.background.BackgroundManager;
import com.matthewtamlin.sliding_intro_screen_library.background.ColorBlender;
import com.matthewtamlin.sliding_intro_screen_library.buttons.IntroButton;
import com.matthewtamlin.sliding_intro_screen_library.core.IntroActivity;
import com.matthewtamlin.sliding_intro_screen_library.pages.ParallaxPage;
import com.matthewtamlin.sliding_intro_screen_library.transformers.MultiViewParallaxTransformer;
import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;
import java.util.Collection;

public class DotsActivity extends IntroActivity {

    private static final int[] BACKGROUND_COLORS = {0xffC75163, 0xffFFD15D, 0xffC75163,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Skip to the next activity if the user has previously completed the introduction
        if (introductionCompletedPreviously()) {
            SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
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

    /**
     * Generate the pages displayed in this activity.
     */
    @Override
    protected Collection<Fragment> generatePages(Bundle savedInstanceState) throws OutOfMemoryError {
        // This variable holds the pages while they are being created
        ArrayList<Fragment> pages = new ArrayList<>();
        ParallaxPage newPage = ParallaxPage.newInstance();
        int resourceId = R.drawable.mother;
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        newPage.setFrontImage(mBitmap);
        pages.add(newPage);

        newPage = ParallaxPage.newInstance();
        resourceId = R.drawable.baby;
        mBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        newPage.setFrontImage(mBitmap);
        pages.add(newPage);

        newPage = ParallaxPage.newInstance();
        resourceId = R.drawable.analytics;
        mBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        newPage.setFrontImage(mBitmap);
        pages.add(newPage);

        return pages;
    }

    /**
     * Generate the Behaviour of the final button.
     */
    @Override
    protected IntroButton.Behaviour generateFinalButtonBehaviour() {
        /* The pending changes to the shared preferences editor will be applied when the
		 * introduction is successfully completed. By setting a flag in the pending edits and
		 * checking the status of the flag when the activity starts, the introduction screen can
		 * be skipped if it has previously been completed.
		 */
        SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(SharedConstants.DISPLAY_ONCE_KEY, true);
        pendingEdits.apply();
        // Define the next activity intent and create the Behaviour to use for the final button
        Intent nextActivity = new Intent(this, SignupActivity.class);
        return new IntroButton.ProgressToNextActivity(nextActivity, pendingEdits);
    }

    /**
     * Checks for a shared preference flag indicating that the introduction has been completed
     * previously.
     */
    private boolean introductionCompletedPreviously() {
        SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        return sp.getBoolean(SharedConstants.DISPLAY_ONCE_KEY, false);
    }

    private void configureTransformer() {
        MultiViewParallaxTransformer transformer = new MultiViewParallaxTransformer();
        transformer.withParallaxView(R.id.page_fragment_imageHolderFront, 1.2f);
        setPageTransformer(false, transformer);
    }

    private void configureBackground() {
        BackgroundManager backgroundManager = new ColorBlender(BACKGROUND_COLORS);
        setBackgroundManager(backgroundManager);
    }
}