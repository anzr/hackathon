/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.animationsdemo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;
/*
TODO:
1. No need to pass entire json instead make api calls and queries to fetch the contents
2. Update each time pager changes the pages
3. get Columns and pass object to the fragment creator to be received later on
 */

public class MainActivity extends FragmentActivity {
    //TODO make REST API calls to fill these string
    // account name also to be provided later on
    private int MAX_COL_DISPLAYED = 2;
    /* TODO : get the orientation and device type and update MAX_COL_DISPLAYED accordingly
    private int _getMaxColDisplayed();
      */
    private int numPages;
    private ViewPager pager;
    ApiCalls apiCalls;
    Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        OAuth2Helper oAuth2Helper = new OAuth2Helper(prefs);
        board = new Board();
        board.startCreateColumnsThread(oAuth2Helper);
        board.stopCreateColumnsThread();
        numPages = board.getColNo() / MAX_COL_DISPLAYED + 1;
        // Instantiate a ViewPager and a PagerAdapter.
        pager = (ViewPager) findViewById(R.id.pager);

        Constants.BOARD=board;
        setViewPagerAnimationSpeed();

        // For viewing multiple pages
        pager.setClipToPadding(false);
        pager.setPageMargin(10);
        pager.setOffscreenPageLimit(numPages);
        pager.setClipChildren(false);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);
        Constants.PAGER = pager;
        // Attach the page change listener inside the activity
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
    }

    public Board getBoardObj(){
     return board;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_layout_changes, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_add_item:
                // Hide the "empty" view since there is now at least one item in the list.
                //findViewById(android.R.id.empty).setVisibility(View.GONE);
                //addItem();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


         //For multiple pages
        @Override
        public float getPageWidth (int position) {
            return 1.0f;
        }

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BoardFragment.create(position, MAX_COL_DISPLAYED);
        }

        @Override
        public int getCount() {

            return numPages;
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    private void setViewPagerAnimationSpeed() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new DecelerateInterpolator();
            FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext(), sInterpolator);
            // scroller.setFixedDuration(5000);
            mScroller.set(pager, scroller);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
        }
    }
}


