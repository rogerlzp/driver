package com.abc.driver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;

import com.abc.driver.model.User;
import com.abc.driver.utility.CellSiteConstants;

public class SettingActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
	}
	/**
	 * logout to remove current account
	 * 
	 * @param v
	 */
	public void logout(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(res.getString(R.string.KindReminder));
		// builder.setIcon(R.drawable.ic_menu_help);
		builder.setMessage(res.getString(R.string.confirmUnrigster));
		builder.setPositiveButton(res.getString(R.string.Confirm),
				new DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int which) {

						Editor sharedUser = getSharedPreferences(
								CellSiteConstants.CELLSITE_CONFIG, MODE_PRIVATE)
								.edit();

						sharedUser.clear().commit();
						app.attachUser(new User());

						Intent intent = new Intent(SettingActivity.this,
								LoginActivity.class);
						startActivity(intent);

					}
				});
		builder.setNegativeButton(res.getString(android.R.string.cancel), null);
		builder.create().show();

	}
}
