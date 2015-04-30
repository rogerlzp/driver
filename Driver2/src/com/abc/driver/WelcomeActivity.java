package com.abc.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
	}

	public void welcome_login(View v) {
		Intent intent = new Intent();
		intent.setClass(WelcomeActivity.this, LoginActivity.class);
		startActivity(intent);
		// this.finish();
	}

}
