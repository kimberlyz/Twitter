package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kzai on 6/30/16.
 */
public class SearchTopTweetsFragment extends TweetsListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline();
    }

    public static SearchTopTweetsFragment newFragment(String query) {
        SearchTopTweetsFragment searchTopFragment = new SearchTopTweetsFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        searchTopFragment.setArguments(args);
        return searchTopFragment;
    }

    // Send an API request to get the timeline json
    // Fill the list view by creating the Tweet objects from the json
    public void populateTimeline() {
        String query = getArguments().getString("query");
        client.searchTweets(query, "popular", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray statuses = null;
                try {
                    statuses = response.getJSONArray("statuses");
                    addAll(Tweet.fromJSONArray(statuses));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
