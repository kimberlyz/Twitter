package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kzai on 6/27/16.
 */
public class User {
    // list attributes
    private String name;
    private long uid;
    private String screenName;
    private String profileImage;
    private String tagline;
    private int followersCount;
    private int followingsCount;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getTagline() {
        return tagline;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingsCount() {
        return followingsCount;
    }

    // deserialize the user json
    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImage = jsonObject.getString("profile_image_url");
            user.tagline = jsonObject.getString("description");
            user.followersCount = jsonObject.getInt("followers_count");
            user.followingsCount = jsonObject.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
