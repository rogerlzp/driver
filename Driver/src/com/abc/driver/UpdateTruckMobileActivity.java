package com.abc.driver;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteConstants;

public class UpdateTruckMobileActivity extends BaseActivity {

	public static final String TAG = "UpdateTruckMobileActivity";
	UpdateTruckMobileTask mUpdateTruckMobileTask;
	EditText updateMobileEt;
	String truckId;
	String userId;
	String newMobileNum;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_truck_mobile);
		Intent intent = getIntent();
		String mobileNum = intent
				.getStringExtra(CellSiteConstants.TRUCK_MOBILE_NUM);
		truckId = intent.getStringExtra(CellSiteConstants.TRUCK_ID);
		userId = intent.getStringExtra(CellSiteConstants.USER_ID);

		updateMobileEt = (EditText) findViewById(R.id.update_mobile_et);
		updateMobileEt.setText(mobileNum);
	}

	public void saveTruckMobile(View v) {
		mUpdateTruckMobileTask = new UpdateTruckMobileTask();
		mUpdateTruckMobileTask.execute(""+app.getUser().getId(), 
				""+app.getUser().getMyTruck().getTruckId(), 
				updateMobileEt
				.getText().toString().trim());

		
	}

	private class UpdateTruckMobileTask extends
			AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return updateTruckMobile(params[0], params[1], params[2]);
		}

		@Override
		public void onPostExecute(Integer result) {
			if (this.isCancelled()) {
				return;
			}
			Integer resCode = result;// Integer.parseInt(result);ØØØ
			if (resCode == CellSiteConstants.RESULT_SUC) {
				// TODO: set for different user
				Intent intent = new Intent(UpdateTruckMobileActivity.this,
						TruckActivity.class);
				intent.putExtra(CellSiteConstants.TRUCK_MOBILE_NUM, newMobileNum);
				startActivity(intent);
			}
		}

		protected Integer updateTruckMobile(String _userId, String _truckId,
				String _mobile) {

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.USER_ID, _userId));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_ID, _truckId));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_MOBILE_NUM, _mobile));

			JSONObject response = null;
			try {
				response = CellSiteHttpClient.executeHttpPost(
						CellSiteConstants.UPDATE_TRUCK_MOBILE_URL,
						postParameters);

				int resultCode = Integer.parseInt(response.get(
						CellSiteConstants.RESULT_CODE).toString());
				if (resultCode == CellSiteConstants.RESULT_SUC) {
					newMobileNum =  _mobile;
					Editor sharedUser = getSharedPreferences(
							CellSiteConstants.CELLSITE_CONFIG, MODE_PRIVATE)
							.edit();

					sharedUser.putString(CellSiteConstants.TRUCK_MOBILE_NUM,
							_mobile);
					sharedUser.commit();

				}
				return resultCode;

			} catch (UnknownHostException e) {
				return CellSiteConstants.UNKNOWN_HOST_ERROR;
			} catch (Exception e) {
				// TODO
			}
			return CellSiteConstants.UNKNOWN_ERROR;
		}
	}

}
