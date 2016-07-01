package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.SearchTopTweetsFragment;
import com.codepath.apps.mysimpletweets.fragments.SearchTweetsFragment;
import com.codepath.apps.mysimpletweets.fragments.SmartFragmentStatePagerAdapter;

public class SearchActivity extends AppCompatActivity {

    SmartFragmentStatePagerAdapter adapterViewPager;
    SearchTweetsFragment searchTweetsFragment;
    SearchTopTweetsFragment searchTopTweetsFragment;
    private final int REQUEST_CODE = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get viewpager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        // Set viewpager adapter for the pager
        adapterViewPager = new AllTopTweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        // Find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the pager tabs to the viewpager
        tabStrip.setViewPager(vpPager);

        searchTweetsFragment = new SearchTweetsFragment();
        searchTopTweetsFragment = new SearchTopTweetsFragment();

        searchTweetsFragment.setQuery(getIntent().getStringExtra("query"));
        searchTopTweetsFragment.setQuery(getIntent().getStringExtra("query"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchItem.expandActionView();
        searchView.setQuery(getIntent().getStringExtra("query"), false);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                // TODO: Fix it so only populates when necessary (when user clicks on each fragment)
                searchTweetsFragment.populateTimeline(query);
                searchTopTweetsFragment.populateTimeline(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onPostTweet(MenuItem item) {
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    public void onProfileView(MenuItem item) {
        // Launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }


    // Returns the order of the fragments in the view pager
    public class AllTopTweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private String tabTitles[] = {"Top Tweets", "All Tweets"};

        // Adapter gets manager to insert or remove fragments from activity
        public AllTopTweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return searchTopTweetsFragment;
            } else if (position == 1) {
                return searchTweetsFragment;
            } else {
                return null;
            }
        }

        // How many fragments there are to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }

        // Return tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
