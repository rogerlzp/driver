package com.abc.driver;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteConstants;

public class TestSendDataActivity extends Activity {

	private final String TAG = TestSendDataActivity.class.getSimpleName();
	
	NormalRegisterTask mNormalRegTask;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.d(TAG, "TEST = " );
		setContentView(R.layout.test_send_data);

	}
	
	
	protected Integer normalLogin(String _username, String _password,
			String account_type) {
		String passwordHash = "";


		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(account_type, _username));
		postParameters
				.add(new BasicNameValuePair("phoneNum", "15800911536"));

		JSONObject response = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(
					CellSiteConstants.REGISTER_URL, postParameters);
			int resultCode = Integer.parseInt(response.get(
					CellSiteConstants.LOGIN_RESULT).toString());
			if (resultCode == CellSiteConstants.LOGIN_SUCC) {}
			return resultCode;

		} catch (UnknownHostException e) {
			
		} catch (Exception e) {
			
		}
		return CellSiteConstants.UNKNOWN_ERROR;
	}



	private class NormalRegisterTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return normalLogin(params[0], params[1], params[2]);
		}

		@Override
		public void onPostExecute(Integer result) {

			Integer resCode = result;// Integer.parseInt(result);
			if (resCode == CellSiteConstants.LOGIN_SUCC) {
				Log.d(TAG, "result is fine");
				
			} else if (resCode == CellSiteConstants.LOGIN_BAD_PASSWORD) {
			} else if (resCode == CellSiteConstants.LOGIN_BAD_USERNAME) {

			}
		}
	}

	public void sendData(View v) {
		
		 mNormalRegTask = new NormalRegisterTask();
			mNormalRegTask.execute(new String[]{"test1", "test2", "test32"} );

		

	}

}