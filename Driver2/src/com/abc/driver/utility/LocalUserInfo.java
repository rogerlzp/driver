package com.abc.driver.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalUserInfo {

	public static final String PREFERENCE_NAME = "local_user";
	private static SharedPreferences mSharedPreferences;
	private static LocalUserInfo mPreferenceUtils;
	private static SharedPreferences.Editor editor;

	private LocalUserInfo(Context ctx) {
		mSharedPreferences = ctx.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
	}

	public static LocalUserInfo getInstance(Context ctx) {
		if (mPreferenceUtils == null) {
			mPreferenceUtils = new LocalUserInfo(ctx);
		}
		editor = mSharedPreferences.edit();
		return mPreferenceUtils;
	}

	public void setUserInfo(String _name, String _value) {
		editor.putString(_name, _value);
		editor.commit();
	}

	public String getUserInfo(String _name) {
		return mSharedPreferences.getString(_name, "");
	}

}
