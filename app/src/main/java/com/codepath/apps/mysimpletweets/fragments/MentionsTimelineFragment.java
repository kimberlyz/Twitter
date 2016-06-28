package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kzai on 6/27/16.
 */
public class MentionsTimelineFragment extends TweetsListFragment {

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline();
    }

    // Send an API request to get the timeline json
    // Fill the list view by creating the Tweet objects from the json
    private void populateTimeline() {
        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("DEBUG", response.toString());
                // Deserialize json
                // Create models and add to adapter
                // Load model data into list view
                addAll(Tweet.fromJSONArray(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });

    }
}
