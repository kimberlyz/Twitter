package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.SmartFragmentStatePagerAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;

import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 56;
    SmartFragmentStatePagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Get viewpager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        // Set viewpager adapter for the pager
        adapterViewPager = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        // Find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the pager tabs to the viewpager
        tabStrip.setViewPager(vpPager);
        tabStrip.setTextColor(Color.parseColor("#55acee"));
        tabStrip.setIndicatorColor(Color.parseColor("#55acee"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        int searchTextID = android.support.v7.appcompat.R.id.search_src_text;
        TextView textView = (TextView) searchView.findViewById(searchTextID);
        textView.setTextColor(Color.WHITE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
                i.putExtra("query", query);
                startActivity(i);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            HomeTimelineFragment fragmentHomeTweets =
                    (HomeTimelineFragment) adapterViewPager.getRegisteredFragment(0);
            fragmentHomeTweets.appendTweet(tweet);
        }
    }


    // Returns the order of the fragments in the view pager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        // Adapter gets manager to insert or remove fragments from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
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
