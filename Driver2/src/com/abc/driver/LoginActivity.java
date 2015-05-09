package com.abc.driver;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.abc.driver.model.User;
import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteApplication.NETWORK_STATUS;
import com.abc.driver.utility.CellSiteConstants;
import com.abc.driver.utility.CellSiteDefException;
import com.abc.driver.utility.PasswordService;
import com.abc.driver.utility.Utils;
import com.tencent.android.tpush.XGPushConfig;

public class LoginActivity extends BaseActivity {

	private final String TAG = "LoginActivity";
	private EditText usernameEt;
	private EditText passwordEt;
	ProgressDialog mProgressdialog;
	NormalLoginTask mNormalLoginTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
		initView();

		// stopService(new Intent(this, CheckNotificationService.class));

		if (app.ConnectNetwork() == NETWORK_STATUS.OFFLINE) {
			Utils.createNetworkStatusDlg(LoginActivity.this);
		}

		deviceToken = XGPushConfig.getToken(this);
	}

	public void initView() {
		usernameEt = (EditText) findViewById(R.id.username);
		passwordEt = (EditText) findViewById(R.id.password);

	}

	/**
	 * login
	 * 
	 * @param view
	 */
	public void onLogin(View view) {
		if (view.getId() == R.id.loginButton) {

			mProgressdialog = new ProgressDialog(this);
			mProgressdialog.setMessage(res.getString(R.string.connecting));
			mProgressdialog.setIndeterminate(true);
			mProgressdialog.setCancelable(true);
			mProgressdialog.show();

			String username = usernameEt.getText().toString().trim();
			String password = passwordEt.getText().toString().trim();
			// 获取toekn, 每次login 的时候 update devicetoken

			if (username.length() == 0) {
				Toast.makeText(this, R.string.login_username_hint,
						Toast.LENGTH_SHORT).show();
				usernameEt.setText("");
				mProgressdialog.hide();
				return;
			} else if (password.length() == 0) {
				Toast.makeText(this, R.string.login_password_hint,
						Toast.LENGTH_SHORT).show();
				passwordEt.setText("");
				mProgressdialog.hide();
				return;
			}
			String loginType = "username";
			if (Utils.isValidEmail(username)) {
				loginType = "email";
			} else if (Utils.isValidMobile(username)) {
				loginType = "telephone";
			} else {
				// username
			}

			// TODO: normal login
			/*
			 * Intent intent = new Intent(LoginActivity.this,
			 * MainActivity.class); startActivity(intent);
			 * 
			 * mProgressdialog.dismiss(); finish();
			 */

			mNormalLoginTask = new NormalLoginTask();
			mNormalLoginTask.execute(new String[] { username, password,
					deviceToken, loginType });
		}
	}

	private class NormalLoginTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return normalLogin(params[0], params[1], params[2], params[3]);
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
				Intent intent = new Intent(LoginActivity.this,
						MainFragmentActivity.class);
				startActivity(intent);
				finish();
			} else if (result == CellSiteConstants.LOGIN_FAILED) {
				passwordEt.setText("");
				Toast.makeText(getApplicationContext(), "用户名或者密码错误",
						Toast.LENGTH_LONG).show();
			}
		}

		protected Integer normalLogin(String _username, String _password,
				String _deviceToken, String account_type) {
			String passwordHash = "";
			try {
				passwordHash = PasswordService.getInstance().encrypt(_password);
			} catch (CellSiteDefException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.USER_NAME, _username));
			postParameters
					.add(new BasicNameValuePair("password", passwordHash));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.DEVICE_TOKEN, _deviceToken));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.ROLE_ID, ""
							+ CellSiteConstants.DRIVER_ROLE_ID));

			JSONObject response = null;
			try {
				response = CellSiteHttpClient.executeHttpPost(
						CellSiteConstants.LOGIN_URL, postParameters);

				int resultCode = Integer.parseInt(response.get(
						CellSiteConstants.RESULT_CODE).toString());
				if (resultCode == CellSiteConstants.RESULT_SUC) {
					// store the username and password in sharedPreference

					User normalUser = new User();
					normalUser.setUsername(_username);

					JSONObject userJson = response
							.getJSONObject(CellSiteConstants.USER);
					JSONObject profileJson = null;
					try {
						profileJson = response
								.getJSONObject(CellSiteConstants.PROFILE);
					} catch (JSONException e) {
						Log.d(TAG, " The profile is not inited");
						profileJson = null;
						// response.get(CellSiteConstants.PROFILE);
					}

					normalUser.setId(userJson.getLong(CellSiteConstants.ID));
					normalUser.setMobileNum(_username);

					Editor sharedUser = getSharedPreferences(
							CellSiteConstants.CELLSITE_CONFIG, MODE_PRIVATE)
							.edit();

					if (profileJson != null) {
						if (profileJson.getString(CellSiteConstants.NAME) != null) {
							normalUser.setName(profileJson
									.getString(CellSiteConstants.NAME));
						}
						sharedUser.putString(CellSiteConstants.NAME,
								profileJson.getString(CellSiteConstants.NAME));

						if (profileJson
								.get(CellSiteConstants.PROFILE_IMAGE_URL) == null) {
							Log.d(TAG, "portrait is NULL");
						} else {
							sharedUser
									.putString(
											CellSiteConstants.PROFILE_IMAGE_URL,
											profileJson
													.getString(CellSiteConstants.PROFILE_IMAGE_URL));
							normalUser
									.setProfileImageUrl(profileJson
											.getString(CellSiteConstants.PROFILE_IMAGE_URL));
						}
						if (profileJson
								.get(CellSiteConstants.IDENTITY_FRONT_IMAGE_URL) != null) {
							sharedUser
									.putString(
											CellSiteConstants.IDENTITY_FRONT_IMAGE_URL,
											profileJson
													.getString(CellSiteConstants.IDENTITY_FRONT_IMAGE_URL));
							normalUser
									.setIdentityFrontImageUrl(profileJson
											.getString(CellSiteConstants.IDENTITY_FRONT_IMAGE_URL));
						}
						if (profileJson
								.get(CellSiteConstants.IDENTITY_BACK_IMAGE_URL) != null) {
							sharedUser
									.putString(
											CellSiteConstants.IDENTITY_BACK_IMAGE_URL,
											profileJson
													.getString(CellSiteConstants.IDENTITY_BACK_IMAGE_URL));
							normalUser
									.setIdentityBackImageUrl(profileJson
											.getString(CellSiteConstants.IDENTITY_BACK_IMAGE_URL));
						}
					}

					sharedUser
							.putString(CellSiteConstants.USER_NAME, _username);
					sharedUser.putString(CellSiteConstants.PASSWORD,
							passwordHash);
					sharedUser.putString(CellSiteConstants.USER_ID, ""
							+ userJson.getLong(CellSiteConstants.ID));
					sharedUser.putString(CellSiteConstants.MOBILE, _username);

					app.attachUser(normalUser);
					sharedUser.commit();

				}
				return resultCode;

			} catch (UnknownHostException e) {
				return CellSiteConstants.UNKNOWN_HOST_ERROR;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return CellSiteConstants.UNKNOWN_ERROR;
		}
	}

	/**
	 * jump to register
	 * 
	 * @param view
	 */
	public void onRegister(View view) {
		if (view.getId() == R.id.register) {
			Intent intent = new Intent(this, RegisterByMobileActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
		}
	}

	/**
	 * jump to forgetPassword
	 * 
	 * @param view
	 */
	public void onForgetPassword(View view) {
		if (view.getId() == R.id.forget_password) {
			Intent intent = new Intent(this, ForgetPasswordActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
		}
	}

}
