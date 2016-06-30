package com.codepath.apps.mysimpletweets;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    User user;

    private Typeface gotham_book;
    private Typeface gotham_bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        gotham_book = Typeface.createFromAsset(getAssets(), "fonts/GothamNarrow-Book.ttf");
        gotham_bold = Typeface.createFromAsset(getAssets(), "fonts/GothamNarrow-Bold.ttf");

        client = TwitterApplication.getRestClient();

        // My current user account's info
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        // Get screenName passed when profile activity is being launched
        // If no screenName is passed, then load the logged in user's data
        String screenName = getIntent().getStringExtra("screen_name");
        loadUserInfo(screenName);
        // Get the account info

        if (savedInstanceState == null) {
            // Create the user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newFragment(screenName);
            // Display user fragment within this activity (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit(); // changes the fragments
        }
    }

    public void loadUserInfo(String screenName) {
        if (screenName != null && !screenName.isEmpty()) {
            // Trigger call to "users/show" endpoint
            // to load user profile data
            // populate the top of the profile view
            client.getUserInfo(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = user.fromJSON(response);
                    populateProfileHeader(user);
                }
            });

        } else {
            // no screenName was passed
            // Trigger call to "account/verifyCredentials" endpoint
            // to load current user profile data
            // populate the top of the profile view
            client.getMyInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = user.fromJSON(response);
                    populateProfileHeader(user);
                }
            });
        }
    }

    private void populateProfileHeader(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        tvName.setTypeface(gotham_bold);
        tvScreenName.setTypeface(gotham_book);
        tvTagline.setTypeface(gotham_book);
        tvFollowers.setTypeface(gotham_book);
        tvFollowing.setTypeface(gotham_book);

        tvName.setText(user.getName());
        tvScreenName.setText(user.getScreenName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(String.valueOf(user.getFollowersCount()) + " FOLLOWERS");
        tvFollowing.setText(String.valueOf(user.getFollowingsCount()) + " FOLLOWING");
        Picasso.with(this).load(user.getProfileImage()).into(ivProfileImage);
    }
}
