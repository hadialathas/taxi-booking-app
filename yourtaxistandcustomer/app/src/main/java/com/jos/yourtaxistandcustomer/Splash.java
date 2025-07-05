package com.jos.yourtaxistandcustomer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;
	SessionManager session;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		session=new SessionManager(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(session!=null &&session.isLoggedIn()){
					Intent i = new Intent(Splash.this, MainActivity.class);
					startActivity(i);
				}else{
				Intent i = new Intent(Splash.this, LoginActivity.class);
				startActivity(i);
				}
				finish();
			}
		},SPLASH_TIME_OUT);
	}
}
