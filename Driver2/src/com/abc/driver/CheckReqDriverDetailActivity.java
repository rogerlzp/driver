package com.abc.driver;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteConstants;

public class CheckReqDriverDetailActivity extends BaseActivity {

	ImageView mDPiv, mTruckImageIv;;
	TextView mNameTv;
	TextView mTuckLicenseTv;
	TextView mTruckTypeTv;
	TextView mTruckLengthTv;
	TextView mTruckWeightTv;
	Button mToggleDriverBtn;

	String phoneNum;
	int horderId;
	int selectedDriverId, newSelectedDriverId;
	int currentDriverId;
	boolean changedDriver;
	boolean isSelectedDriver;

	SelectDriverTask mTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_req_detail);

		Intent intent = getIntent();
		phoneNum = intent.getStringExtra(CellSiteConstants.MOBILE);

		selectedDriverId = intent.getIntExtra(
				CellSiteConstants.SELECTED_DRIVER_ID, 1);
		currentDriverId = intent.getIntExtra(CellSiteConstants.DRIVER_ID, 1);

		isSelectedDriver = intent.getBooleanExtra(
				CellSiteConstants.IS_SELECTED_DRIVER, false);

		horderId = intent.getIntExtra(CellSiteConstants.HORDER_ID, 1);
		initView();

		app.setSelectDriverId(0);
		app.setRemovedDriverId(0);
	}

	public void initView() {
		mDPiv = (ImageView) findViewById(R.id.driver_portrait_iv);
		mTruckImageIv = (ImageView) findViewById(R.id.truck_image_iv);

		mNameTv = (TextView) findViewById(R.id.name_tv);
		mTuckLicenseTv = (TextView) findViewById(R.id.truck_license_tv);
		mTruckTypeTv = (TextView) findViewById(R.id.truck_type_tv);
		mTruckLengthTv = (TextView) findViewById(R.id.truck_length_tv);
		mTruckWeightTv = (TextView) findViewById(R.id.truck_weight_tv);
		mToggleDriverBtn = (Button) findViewById(R.id.confirm_driver_btn);

		if (selectedDriverId == currentDriverId) {
			mToggleDriverBtn.setText(res.getString(R.string.cancel_driver));
		} else {
			mToggleDriverBtn.setText(res.getString(R.string.confirm_driver));
		}

	}

	// contact driver
	public void contactDriver(View v) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ phoneNum));
		startActivity(intent);

	}

	public void confirmDriver(View v) {
		mTask = new SelectDriverTask();
		mTask.execute();
		newSelectedDriverId = currentDriverId;
	}

	class SelectDriverTask extends AsyncTask<Void, String, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.DRIVER_ID, "" + currentDriverId));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.HORDER_ID, "" + horderId));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.USER_ID, "" + app.getUser().getId()));

			JSONObject response = null;
			int resultCode = -1;
			try {
				response = CellSiteHttpClient.executeHttpPost(
						CellSiteConstants.TOGGLE_DRIVER_FOR_HORDER_URL,
						postParameters);
				resultCode = response.getInt(CellSiteConstants.RESULT_CODE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return resultCode;

		}

		@Override
		protected void onPostExecute(Integer result) {
			if (CellSiteConstants.RESULT_SUC == result) {
				// update ui result
				app.setRemovedDriverId(selectedDriverId);
				app.setSelectDriverId(currentDriverId);
				selectedDriverId = currentDriverId;
				mToggleDriverBtn.setText(res.getString(R.string.cancel_driver));
			} else if (CellSiteConstants.RESULT_CANCEL_DRIVER == result) { // 取消了司机
				app.setRemovedDriverId(currentDriverId);
				app.setSelectDriverId(1);
				selectedDriverId = 1;
				mToggleDriverBtn
						.setText(res.getString(R.string.confirm_driver));
			}
		}
	}

	@Override
	public void onBackPressed() {
		finish();

	}

}
