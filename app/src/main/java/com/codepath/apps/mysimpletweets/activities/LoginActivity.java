package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.oauth.OAuthLoginActionBarActivity;

// Where the user will sign in
public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Typeface gotham_book = Typeface.createFromAsset(getAssets(), "fonts/GothamNarrow-Book.ttf");
		Typeface gotham_bold = Typeface.createFromAsset(getAssets(), "fonts/GothamNarrow-Bold.ttf");

		TextView tvWelcome = (TextView) findViewById(R.id.tvWelcome);
		TextView tvDescription = (TextView) findViewById(R.id.tvDescription);
		Button btnLogin = (Button) findViewById(R.id.btnLogin);

		tvWelcome.setTypeface(gotham_book);
		tvDescription.setTypeface(gotham_book);
		btnLogin.setTypeface(gotham_bold);
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		Intent i = new Intent(this, TimelineActivity.class);
		startActivity(i);
		//Toast.makeText(this, "SUCCESS!", Toast.LENGTH_SHORT).show();
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
