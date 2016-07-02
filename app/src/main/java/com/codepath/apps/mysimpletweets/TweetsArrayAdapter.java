package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.activities.ComposeActivity;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kzai on 6/27/16.
 */

// Taking the Tweet objects and turning them into views
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private Typeface gotham_book;
    private Typeface gotham_bold;
    TwitterClient client;

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);

        client = TwitterApplication.getRestClient();

        gotham_book = Typeface.createFromAsset(context.getAssets(), "fonts/GothamNarrow-Book.ttf");
        gotham_bold = Typeface.createFromAsset(context.getAssets(), "fonts/GothamNarrow-Bold.ttf");
    }

    // Override and setup custom template
    private static class Viewholder {
        ImageView profileImage;
        TextView username;
        TextView screenName;
        TextView body;
        TextView timestamp;
        Button reply;
        Button retweet;
        Button heart;
        TextView retweetCount;
        TextView favoriteCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Get the tweet
        final Tweet tweet = getItem(position);
        // 2. Find or inflate the template
        final Viewholder viewholder;
        if (convertView == null) {
            viewholder = new Viewholder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewholder.profileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewholder.username = (TextView) convertView.findViewById(R.id.tvUsername);
            viewholder.screenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewholder.body = (TextView) convertView.findViewById(R.id.tvBody);

            viewholder.timestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
            viewholder.reply = (Button) convertView.findViewById(R.id.btnReply);
            viewholder.retweet = (Button) convertView.findViewById(R.id.btnRetweet);
            viewholder.heart = (Button) convertView.findViewById(R.id.btnHeart);

            viewholder.retweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewholder.favoriteCount = (TextView) convertView.findViewById(R.id.tvFavoriteCount);

            viewholder.username.setTypeface(gotham_bold);
            viewholder.screenName.setTypeface(gotham_book);
            viewholder.body.setTypeface(gotham_book);
            viewholder.timestamp.setTypeface(gotham_book);

            convertView.setTag(viewholder);
        } else {
            viewholder = (Viewholder) convertView.getTag();
        }
        // 3. Find the subviews to fill with data in the template

        // 4. Populate data into the subviews
        viewholder.username.setText(tweet.getUser().getName());
        viewholder.screenName.setText(tweet.getUser().getScreenName());
        viewholder.body.setText(tweet.getBody());
        viewholder.profileImage.setImageResource(android.R.color.transparent);
        viewholder.timestamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));

        viewholder.retweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        viewholder.favoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));

        Picasso.with(getContext()).load(tweet.getUser().getProfileImage()).into(viewholder.profileImage);

        if (!tweet.getRetweeted()) {
            viewholder.retweet.setBackgroundResource(R.drawable.retweet);
            viewholder.retweetCount.setTextColor(Color.parseColor("#66757f"));
        } else {
            viewholder.retweet.setBackgroundResource(R.drawable.retweet_active);
            viewholder.retweetCount.setTextColor(Color.parseColor("#33cc33"));
        }

        if (!tweet.getFavorited()) {
            viewholder.heart.setBackgroundResource(R.drawable.heart);
            viewholder.favoriteCount.setTextColor(Color.parseColor("#66757f"));
        } else {
            viewholder.heart.setBackgroundResource(R.drawable.heart_active);
            viewholder.favoriteCount.setTextColor(Color.parseColor("#e53935"));
        }

        viewholder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("screen_name", tweet.getUser().getScreenName());
                getContext().startActivity(i);
            }
        });

        viewholder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ComposeActivity.class);
                i.putExtra("screen_name", tweet.getUser().getScreenName());
                getContext().startActivity(i);
            }
        });

        viewholder.retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tweet.getRetweeted()) {
                    client.retweet(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getContext(), "Ugh", Toast.LENGTH_SHORT).show();
                            try {
                                tweet.setRetweeted(true);
                                tweet.setRetweetUid(response.getLong("id"));
                                tweet.setRetweetCount(tweet.getRetweetCount() + 1);
                                viewholder.retweet.setBackgroundResource(R.drawable.retweet_active);
                                viewholder.retweetCount.setTextColor(Color.parseColor("#33cc33"));
                                viewholder.retweetCount.setText(String.valueOf(tweet.getRetweetCount()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    });
                } else {
                    client.unretweet(tweet.getRetweetUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getContext(), "UnTweeted", Toast.LENGTH_SHORT).show();
                            tweet.setRetweeted(false);
                            tweet.setRetweetUid(-1);
                            tweet.setRetweetCount(tweet.getRetweetCount() - 1);
                            viewholder.retweet.setBackgroundResource(R.drawable.retweet);
                            viewholder.retweetCount.setTextColor(Color.parseColor("#66757f"));
                            viewholder.retweetCount.setText(String.valueOf(tweet.getRetweetCount()));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    });
                }
            }
        });

        viewholder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tweet.getFavorited()) {
                    client.favorite(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getContext(), "Favorited", Toast.LENGTH_SHORT).show();
                            tweet.setFavorited(true);
                            tweet.setFavoriteCount(tweet.getFavoriteCount() + 1);
                            viewholder.heart.setBackgroundResource(R.drawable.heart_active);
                            viewholder.favoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
                            viewholder.favoriteCount.setTextColor(Color.parseColor("#e53935"));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    });
                } else {
                    client.unfavorite(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getContext(), "Unfavorited", Toast.LENGTH_SHORT).show();
                            tweet.setFavorited(false);
                            tweet.setFavoriteCount(tweet.getFavoriteCount() - 1);
                            viewholder.heart.setBackgroundResource(R.drawable.heart);
                            viewholder.favoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
                            viewholder.favoriteCount.setTextColor(Color.parseColor("#66757f"));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    });
                }

            }
        });

        // 5. Return the view to be inserted in the list
        return convertView;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
