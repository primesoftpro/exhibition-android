/*
 * Copyright 2016 Promtech. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.gkpromtech.exhibition;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import ru.gkpromtech.exhibition.about.AboutActivity;
import ru.gkpromtech.exhibition.utils.AnalyticsManager;


public abstract class NavigationActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle = "";
    private Fragment mFragment;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        if (isNavigationDrawerEnabled()) {
            setContentView(R.layout.activity_main);

            View drawer = findViewById(R.id.drawer_layout);
            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            if (drawer != null) {
                // Set up the drawer.
                mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) drawer);
            }
        } else {
            setContentView(R.layout.activity_main_no_nav);
            onNavigationDrawerShowView(getIntent().getIntExtra("position", 0));
        }

        mTitle = getTitle();
    }

    protected boolean isNavigationDrawerEnabled() {
        return true;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // start activity
        Class cls = Navigation.ITEMS[position].getActivity();
        Intent intent = new Intent(this, cls);
        intent.putExtra("position", position);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onNavigationDrawerShowView(int position) {
        Navigation.Item item = Navigation.ITEMS[position];

        // update the main content by replacing fragments
        mFragment = getFragment();
        if (mFragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();
        }
        mTitle = getString(item.getTitle());
    }

    protected Fragment getFragment() {
        return null;
    }

    protected Fragment getActiveFragment() {
        return mFragment;
    }

    protected void setActiveFragment(Fragment fragment) {
        mFragment = fragment;
        getFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isNavigationDrawerEnabled() && !mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void onAboutClicked(View v) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (ExhibitionApplication.getActivityLevel() == 1) {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                AnalyticsManager.sendEvent(this, R.string.application_category, R.string.action_close);

                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.exit_help_toast, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
        else
            super.onBackPressed();
    }

}
