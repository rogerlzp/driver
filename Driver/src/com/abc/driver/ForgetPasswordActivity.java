package com.abc.driver;

import java.util.ArrayList;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteConstants;
import com.abc.driver.utility.Utils;

public class ForgetPasswordActivity extends BaseActivity {

	public final String TAG = "ForgetPasswordActivity";
	EditText loginname_et;
	ForgetPasswordTask mForgetPasswordTask;
	ProgressDialog mProgressdialog;
	String loginName;
	String mAccountName; // the real name of the user

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.forget_password);
		init();
	}

	public void onBackButton(View v) {
		this.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	public void init() {
		loginname_et = (EditText) findViewById(R.id.forgetPassword_tv);
	}

	public void OnForgetPassword(View view) {
		String identifying_code = ProductPassword.getPass();
		loginName = loginname_et.getText().toString().trim();
		Long timeForProduct = Utils.produceTime();

		if (loginName.length() <= 0) {
			Toast.makeText(getApplicationContext(), "�밴�նԻ�����ʾ�����½�˺ţ�",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String loginType = "username";
		if (Utils.isValidEmail(loginName)) {
			loginType = "email";
		} else if (Utils.isValidMobile(loginName)) {
			loginType = "telephone";
		}

		mProgressdialog = new ProgressDialog(this);
		mProgressdialog.setMessage(res.getString(R.string.connecting));
		mProgressdialog.setIndeterminate(true);
		mProgressdialog.setCancelable(true);
		mProgressdialog.show();

		mForgetPasswordTask = new ForgetPasswordTask();
		mForgetPasswordTask.execute(loginName, identifying_code,
				timeForProduct.toString(), loginType);

	}

	public class ForgetPasswordTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return forgetPassword(params[0], params[1], params[2], params[3]);
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

			if (result == CellSiteConstants.SEND_MAIL_SUC) {
				// TODO:
				Toast.makeText(getApplicationContext(), R.string.send_mail_suc,
						Toast.LENGTH_SHORT).show();
				/*
				 * Intent intent = new Intent(ForgetPasswordActivity.this,
				 * TemporaryLoginActivity.class); intent.putExtra(LOGIN_NAME,
				 * mAccountName); startActivity(intent);
				 */

			} else if (result == CellSiteConstants.SEND_MAIL_FAIL) {
				Toast.makeText(app, res.getString(R.string.send_mail_fail),
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, "mail send fail");
			} else if (result == CellSiteConstants.FORGET_PASSWORD_FAIL) {
				Toast.makeText(app,
						res.getString(R.string.forget_password_fail),
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, "request fail,can not insert code to table");
			} else if (result == CellSiteConstants.LOGIN_NAME_WRONG) {
				Toast.makeText(app, res.getString(R.string.login_name_wrong),
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, "login name unexist or could not have an email");
			} else if (result == CellSiteConstants.LOGIN_MAIL_WRONG) {
				Toast.makeText(app, res.getString(R.string.login_mail_wrong),
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, "login name unexist or could not have an email");
			}

		}
	}

	int forgetPassword(String _loginName, String _identifying_code,
			String _timeForProduct, String loginType) {

		int resultCode;
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(loginType, _loginName));
		postParameters.add(new BasicNameValuePair("identifying_code",
				_identifying_code));
		postParameters.add(new BasicNameValuePair("timeForProduct",
				_timeForProduct));

		Log.d(TAG, "input: " + _loginName);
		Log.d(TAG, "input: " + _identifying_code);
		Log.d(TAG, "input: " + _timeForProduct);

		JSONObject response = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(
					CellSiteConstants.FORGET_PASSWORD_URL, postParameters);
			resultCode = Integer.parseInt(response.get(
					CellSiteConstants.FORGET_PASSWORD_RESULT).toString());
			Log.d(TAG, "ResultCode = " + resultCode);

			if (resultCode == CellSiteConstants.FORGET_PASSWORD_FAIL) {

				Log.d(TAG, "Request send fail, could not go next." + response);

			} else if (resultCode == CellSiteConstants.LOGIN_NAME_WRONG) {
				Log.d(TAG, "default login name" + response);

			} else if (resultCode == CellSiteConstants.SEND_MAIL_SUC) {
				mAccountName = (String) response.get("username");
				Log.d(TAG, "send mail success" + response);

			} else if (resultCode == CellSiteConstants.SEND_MAIL_FAIL) {

				Log.d(TAG, "send mail fail" + response);

			}
			return resultCode;
		} catch (Exception e) {
			Log.d(TAG, "There is an error." + e.getMessage());
			return CellSiteConstants.UNKNOWN_ERROR;

		}
	}

	public void OnCloseSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(loginname_et.getWindowToken(), 0);

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mForgetPasswordTask != null) {
			mForgetPasswordTask.cancel(false);
		}

	}

	public static class ProductPassword {
		public static String getPass() {
			int passLenth = 6;
			StringBuffer buffer = new StringBuffer(
					"0123456789abcdefghijklmnopqrstuvwxyz");
			StringBuffer sb = new StringBuffer();
			Random r = new Random();
			int range = buffer.length();
			for (int i = 0; i < passLenth; i++) {
				// ���ָ����Χ��������0���ַ���(����0���������ַ���)
				sb.append(buffer.charAt(r.nextInt(range)));
			}
			System.out.println("try out :" + sb.toString());
			return sb.toString();
		}
	}

}
