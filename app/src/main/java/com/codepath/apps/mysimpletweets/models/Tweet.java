package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by kzai on 6/27/16.
 */
@Parcel
public class Tweet {
    // List out the attributes
    String body;
    long uid; // unique id for tweet
    User user;
    String createdAt;
    long retweetUid;

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setRetweetUid(long id) {
        retweetUid = id;
    }

    public long getRetweetUid() {
        return retweetUid;
    }


    public Tweet() {
    }

    // Deserialize the json
    // Tweet.fromJSON("{...}") -> <Tweet>
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        // Extract values from json, store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.retweetUid = -1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);

                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }


    // Parse the JSON and store the data, encapsulate state or display logic
}
