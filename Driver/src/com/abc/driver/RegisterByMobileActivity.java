package com.abc.driver;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.abc.driver.model.User;
import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteApplication;
import com.abc.driver.utility.CellSiteConstants;
import com.abc.driver.utility.CellSiteDefException;
import com.abc.driver.utility.PasswordService;
import com.abc.driver.utility.Utils;
import com.tencent.android.tpush.XGPushConfig;

public class RegisterByMobileActivity extends Activity {

	private final String TAG = RegisterByMobileActivity.class.getSimpleName();
	CellSiteApplication app;
	Resources res;

	EditText mobile_number_tv;
	EditText password_tv;
	EditText verify_code_tv;
	EditText name_tv;

	String mPhoneNumber;
	String mVerifyCode = null;
	RegisterWithServerTask mRegisterWithServerTask;
	GetVerifyCodeTask mGetVerifyCodeTask;
	ProgressDialog mProgressdialog;

	String deviceToken;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		app = (CellSiteApplication) getApplication();
		res = getResources();

		setContentView(R.layout.register_via_mobile);
		init();

		deviceToken = XGPushConfig.getToken(this);
		Log.d(TAG, "device token:" + deviceToken);
	}

	public void init() {
		mobile_number_tv = (EditText) findViewById(R.id.mobile_number_tv);
		password_tv = (EditText) findViewById(R.id.password_tv);
		verify_code_tv = (EditText) findViewById(R.id.verify_code_tv);
		// name_tv = (EditText)findViewById(R.id.reg_by_mail_name_tv);
	}

	public void onBackButton(View v) {
		this.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	public void getVerifycode(View v) {
		String sMobile = mobile_number_tv.getText().toString().trim();
		if (!Utils.isValidMobile(sMobile)) {
			Toast.makeText(RegisterByMobileActivity.this, "请填写正确的手机号码",
					Toast.LENGTH_SHORT).show();
		}
		mGetVerifyCodeTask = new GetVerifyCodeTask();
		mGetVerifyCodeTask.execute(sMobile);
	}

	private class GetVerifyCodeTask extends AsyncTask<String, String, Integer> {

		@Override
		public Integer doInBackground(String... params) {
			return getVerifycode(params[0]);
		}

		@Override
		public void onPostExecute(Integer result) {
			if (mProgressdialog != null) {
				mProgressdialog.cancel();
			}
			if (this.isCancelled()) {
				return;
			}
			if (result == CellSiteConstants.RESULT_SUC) {
				Log.d(TAG, "succeed to get verify code");
			} else {
				// TODO
			}
		}
	}

	protected Integer getVerifycode(String _phoneNum) {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(CellSiteConstants.MOBILE,
				_phoneNum));

		JSONObject response = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(
					CellSiteConstants.VERIFY_URL, postParameters);
			int resultCode = Integer.parseInt(response.get(
					CellSiteConstants.RESULT_CODE).toString());
			if (resultCode == CellSiteConstants.RESULT_SUC) {
				mVerifyCode = response.get(CellSiteConstants.VERIFY_CODE)
						.toString();
			}
			return resultCode;

		} catch (UnknownHostException e) {
			// TODO
		} catch (Exception e) {
			// TODO
		}
		return CellSiteConstants.UNKNOWN_ERROR;
	}

	public void onClick(View view) {
		String sMobile = mobile_number_tv.getText().toString().trim();
		if (!Utils.isValidMobile(sMobile)) {
			Toast.makeText(RegisterByMobileActivity.this, "请填写正确的手机号码",
					Toast.LENGTH_SHORT).show();
			return;
		}
		/*
		 * String mInputCode = verify_code_tv.getText().toString().trim(); if
		 * (!mInputCode.equals(mVerifyCode)) {
		 * Toast.makeText(RegisterByMobileActivity.this, "请填写正确的验证码",
		 * Toast.LENGTH_SHORT).show(); return; }
		 */
		// String username = name_tv.getText().toString().trim();
		String mobileNum = mobile_number_tv.getText().toString();

		String password = password_tv.getText().toString();
		String passwordHash = "";
		try {
			passwordHash = PasswordService.getInstance().encrypt(password);
		} catch (CellSiteDefException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mProgressdialog = new ProgressDialog(this);
		mProgressdialog.setMessage(res.getString(R.string.connecting));
		mProgressdialog.setIndeterminate(true);
		mProgressdialog.setCancelable(true);
		mProgressdialog.show();

		mRegisterWithServerTask = new RegisterWithServerTask();
		mRegisterWithServerTask.execute(passwordHash, mobileNum, deviceToken);

	}

	int registerWithMobile(String _passwordHash, String _mobile,
			String _deviceToken) {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(CellSiteConstants.PASSWORD,
				_passwordHash));
		Log.d(TAG, "password: " + _passwordHash);
		postParameters.add(new BasicNameValuePair(CellSiteConstants.MOBILE,
				_mobile));
		postParameters.add(new BasicNameValuePair(
				CellSiteConstants.DEVICE_TOKEN, _deviceToken));
		postParameters.add(new BasicNameValuePair(CellSiteConstants.ROLE_ID, ""
				+ CellSiteConstants.DRIVER_ROLE_ID));

		JSONObject response = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(
					CellSiteConstants.REGISTER_URL, postParameters);

			int resultCode = Integer.parseInt(response.get(
					CellSiteConstants.RESULT_CODE).toString());
			Log.d(TAG, "ResultCode = " + resultCode);
			if (CellSiteConstants.RESULT_SUC == resultCode) {
				Log.d(TAG, "register successfully");

				User user = new User();
				user.setId(response.getLong(CellSiteConstants.USER_ID));

				app.attachUser(user);

				// store the username and password in sharedPreference
				Editor sharedUser = getSharedPreferences(
						CellSiteConstants.CELLSITE_CONFIG, MODE_PRIVATE).edit();
				sharedUser.putString(CellSiteConstants.MOBILE, _mobile);
				sharedUser.putString(CellSiteConstants.PASSWORD, _passwordHash);
				sharedUser.putString(CellSiteConstants.USER_ID,
						response.get(CellSiteConstants.USER_ID).toString());
				sharedUser.commit();

				// app.startToSearchLoc();
			} else if (resultCode == CellSiteConstants.REGISTER_USER_EXISTS) {
				// 用户名已经被注册

			}
			return resultCode;
		} catch (Exception e) {
			Log.d(TAG, "Register by mail fails." + e.getMessage());
			return CellSiteConstants.UNKNOWN_ERROR;
		}
	}

	private class RegisterWithServerTask extends
			AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return registerWithMobile(params[0], params[1], params[2]);
		}

		@Override
		public void onPostExecute(Integer result) {
			Log.d(TAG, "onPostExecute" + result);

			if (mProgressdialog != null) {
				mProgressdialog.cancel();
			}

			if (this.isCancelled()) {
				return;
			}
			if (CellSiteConstants.RESULT_SUC == result) {
				Intent intent = new Intent(RegisterByMobileActivity.this,
						MainActivity.class);
				// intent.putExtra(MainActivity.TURN_TO_ACTIVITY,
				// EditUserinfoActivity.class.getSimpleName());
				startActivity(intent);
				finish();
			} else if (CellSiteConstants.REGISTER_USER_EXISTS == result) {
				Toast.makeText(RegisterByMobileActivity.this,
						res.getString(R.string.mobile_registered),
						Toast.LENGTH_SHORT).show();

				// name_tv.setText("");
				mobile_number_tv.setText("");
				password_tv.setText("");
			} else // if( result == YouYuanConstants.REGISTER_FAIL ||
					// YouYuanConstants.UNKNOWN_ERROR )
			{
				Toast.makeText(RegisterByMobileActivity.this,
						res.getString(R.string.CheckNetwork),
						Toast.LENGTH_SHORT).show();

				// name_tv.setText("");
				mobile_number_tv.setText("");
				password_tv.setText("");
			}

		}
	}

	public void OnCloseSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(name_tv.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(password_tv.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mobile_number_tv.getWindowToken(), 0);

	}

	class CheckMobilePhoneNumTask extends AsyncTask<String, Integer, Boolean> {
		String[] nParams;

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			String response = null;
			nParams = params;
			try {
				response = CellSiteHttpClient
						.getJsonFileCache(CellSiteConstants.VERIFY_PHONE_NUMBE_URL
								+ params[2]);

				return response.contains(res.getString(R.string.city))
						&& response.contains(res.getString(R.string.province))
						&& response.contains(res.getString(R.string.carrier));

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (mProgressdialog != null) {
				mProgressdialog.cancel();
			}
			if (result) {
				mRegisterWithServerTask = new RegisterWithServerTask();
				mRegisterWithServerTask.execute(nParams);
			} else {
				Toast.makeText(RegisterByMobileActivity.this,
						res.getString(R.string.wrongphonenumber),
						Toast.LENGTH_SHORT).show();

			}

		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mRegisterWithServerTask != null) {
			mRegisterWithServerTask.cancel(false);
		}

	}

}
