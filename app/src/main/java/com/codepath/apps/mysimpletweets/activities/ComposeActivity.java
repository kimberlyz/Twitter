package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;
    EditText etStatus;
    TextView tvCharacterCount;
    private Typeface gotham_book;
    private Typeface gotham_bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.cancel);
        setSupportActionBar(toolbar);

        gotham_book = Typeface.createFromAsset(getAssets(), "fonts/GothamNarrow-Book.ttf");
        gotham_bold = Typeface.createFromAsset(getAssets(), "fonts/GothamNarrow-Bold.ttf");

        etStatus = (EditText) findViewById(R.id.etStatus);
        tvCharacterCount = (TextView) findViewById(R.id.tvCharacterCount);
        Button btnTweet = (Button) findViewById(R.id.btnTweet);

        etStatus.setTypeface(gotham_book);
        tvCharacterCount.setTypeface(gotham_book);
        btnTweet.setTypeface(gotham_bold);

        etStatus.addTextChangedListener(textEditorWatcher);

        client = TwitterApplication.getRestClient(); // singleton client

        String screenName = getIntent().getStringExtra("screen_name");
        if (screenName != null && !screenName.isEmpty()) {
            etStatus.setText(screenName + " ");
            etStatus.setSelection(etStatus.getText().length());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onSubmitTweet(View view) {
        client.postNewTweet(etStatus.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent data = new Intent();
                Tweet newTweet = Tweet.fromJSON(response);
                data.putExtra("tweet", Parcels.wrap(newTweet));
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void onCancelTweet(MenuItem item) {
        finish();
    }

    private final TextWatcher textEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            int diff = 140 - s.length();

            if (diff < 0) {
                tvCharacterCount.setTextColor(Color.parseColor("#E53935"));
            } else {
                tvCharacterCount.setTextColor(Color.parseColor("#66757F"));
            }
            tvCharacterCount.setText(String.valueOf(diff));
        }

        public void afterTextChanged(Editable s) {
        }
    };
}
