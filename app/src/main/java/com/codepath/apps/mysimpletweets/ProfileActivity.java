package com.codepath.apps.mysimpletweets;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        client = TwitterApplication.getRestClient();
        // Get the account info
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = user.fromJSON(response);
                // My current user account's info
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                getSupportActionBar().setTitle(user.getScreenName());
                populateProfileHeader(user);
            }
        });

        // Get the screen name from the activity that launches this
        String screenName = getIntent().getStringExtra("screen_name");

        if (savedInstanceState == null) {
            // Create the user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newFragment(screenName);
            // Display user fragment within this activity (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit(); // changes the fragments
        }
    }

    private void populateProfileHeader(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(String.valueOf(user.getFollowersCount()));
        tvFollowing.setText(String.valueOf(user.getFollowingsCount()));
        Picasso.with(this).load(user.getProfileImage()).into(ivProfileImage);
    }
}
