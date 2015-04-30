package com.abc.driver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.widget.EditText;

public class RegisterCodeSmsContent extends ContentObserver {

	public static final String SMS_URI_INBOX = "content://sms/inbox";
	public static final String SMS_ADDRESS = "1065502004955118252";

	private Activity activity = null;

	private String smsContent = "";

	private EditText verifyText = null;

	public RegisterCodeSmsContent(Activity activity, Handler handler, EditText verifyText) {
		super(handler);
		this.activity = activity;
		this.verifyText = verifyText;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Cursor cursor = null;
		cursor = activity.managedQuery(Uri.parse(SMS_URI_INBOX), new String[] { "_id", "address", "body", "read" },
				"address=? and read=?", new String[] { SMS_ADDRESS, "0" }, "_id desc");

		if (cursor != null && cursor.getCount() > 0) {// 如果短信为未读模式
			ContentValues values = new ContentValues();
			values.put("read", "1"); // 修改短信为已读模式
			cursor.moveToNext();

			String smsbody = cursor.getString(cursor.getColumnIndex("body"));
			System.out.println("smsbody=======================" + smsbody);
			String regEx = "[^0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(smsbody.toString());
			smsContent = m.replaceAll("").trim().toString();
			verifyText.setText(smsContent);

		}
		if (Build.VERSION.SDK_INT < 14) {
			cursor.close();
		}

	}

}