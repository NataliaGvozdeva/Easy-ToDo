package com.haraevanton.easytodo.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.haraevanton.easytodo.R;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(getString(R.string.slider1_title),
                getString(R.string.slider1_description),
                R.drawable.main_screen,
                ContextCompat.getColor(getApplicationContext(), R.color.slider1)));

        addSlide(AppIntroFragment.newInstance(getString(R.string.slider2_title),
                getString(R.string.slider2_description),
                R.drawable.add_task,
                ContextCompat.getColor(getApplicationContext(), R.color.slider2)));

        addSlide(AppIntroFragment.newInstance(getString(R.string.slider3_title),
                getString(R.string.slider3_description),
                R.drawable.remove_task,
                ContextCompat.getColor(getApplicationContext(), R.color.slider3)));

        addSlide(AppIntroFragment.newInstance(getString(R.string.slider4_title),
                getString(R.string.slider4_description),
                R.drawable.widget,
                ContextCompat.getColor(getApplicationContext(), R.color.slider4)));


        showStatusBar(false);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(Fragment oldFragment, Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
