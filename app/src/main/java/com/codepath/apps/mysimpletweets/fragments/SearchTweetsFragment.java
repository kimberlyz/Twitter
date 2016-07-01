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
public class SearchTweetsFragment extends TweetsListFragment {
    private TwitterClient client;
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String q) {
        query = q;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline(query);
    }

    public static SearchTweetsFragment newFragment(String query) {
        SearchTweetsFragment searchFragment = new SearchTweetsFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        searchFragment.setArguments(args);
        return searchFragment;
    }


    // Send an API request to get the timeline json
    // Fill the list view by creating the Tweet objects from the json
    public void populateTimeline(String q) {
        query = q;

        client.searchTweets(query, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray statuses = null;
                try {
                    aTweets.clear();
                    statuses = response.getJSONArray("statuses");
                    addAll(Tweet.fromJSONArray(statuses));
                    lvTweets.setSelectionAfterHeaderView();
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
