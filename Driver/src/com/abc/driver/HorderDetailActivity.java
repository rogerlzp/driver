package com.abc.driver;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.driver.model.Horder;
import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteConstants;

public class HorderDetailActivity extends BaseActivity {

	private static final String TAG = "HorderDetailActivity";
	Horder mHorder;
	TextView mCTtv; // 货物类型
	TextView mCVtv; // 货物体积
	TextView mCWtv; // 货物重量
	TextView mTTtv; // 货车类型
	TextView mTLtv; // 货车长度
	TextView mSAtv; // 发货地址
	TextView mCAtv; // 送货地址
	TextView mHSDtv; // 发送时间
	TextView mHDtv; // 货源描述
	TextView mHItv; // 货源id
	Button mReqHorderBtn;
	
	RelativeLayout mCWrl;// 货物重量
	RelativeLayout mCVrl;//货物体积
	private String phoneNum;
	private String horderId;
	private ReqHorderTask mReqHorderTask;
	private ProgressDialog mProgressdialog;
	
	boolean ALREADY_REQUESTED = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.horder_detail);
		mHorder = new Horder();

		Intent intent = getIntent();

		phoneNum = intent.getStringExtra(CellSiteConstants.SHIPPER_PHONE);
		horderId = intent.getStringExtra(CellSiteConstants.HORDER_ID);
		ALREADY_REQUESTED = intent.getIntExtra(CellSiteConstants.ALREADY_REPLIED, 0)==1?true:false;
		
	
		initView(intent);
	}

	public void initView(Intent intent) {
		mCTtv = (TextView) findViewById(R.id.cargo_type_tv);
		mCWtv = (TextView) findViewById(R.id.cargo_weight_tv);
		mCVtv = (TextView) findViewById(R.id.cargo_volume_tv);
		mTTtv = (TextView) findViewById(R.id.truck_type_tv);
		mTLtv = (TextView) findViewById(R.id.truck_length_tv);
		mSAtv = (TextView) findViewById(R.id.shipper_address_tv);
		mCAtv = (TextView) findViewById(R.id.consignee_address_tv);
		mHSDtv = (TextView) findViewById(R.id.shipper_date_tv);
		mHDtv = (TextView) findViewById(R.id.horder_desc_tv);
		mHItv = (TextView) findViewById(R.id.horder_id_tv);
		mCWrl = (RelativeLayout)findViewById(R.id.cargo_weight_rl);
		mCVrl = (RelativeLayout)findViewById(R.id.cargo_volume_rl);
		mReqHorderBtn = (Button)findViewById(R.id.req_horder_btn);
		
		mCTtv.setText(CellSiteConstants.CargoTypes[Integer.valueOf(intent
				.getStringExtra(CellSiteConstants.CARGO_TYPE)) - 1]);
		mTTtv.setText(CellSiteConstants.TruckTypes[Integer.valueOf(intent
				.getStringExtra(CellSiteConstants.TRUCK_TYPE)) - 1]);
		if (Integer.valueOf(intent
				.getStringExtra(CellSiteConstants.CARGO_VOLUME)) == 0) {
			mCVrl.setVisibility(View.GONE);
		} else {
			mCVtv.setText(intent.getStringExtra(CellSiteConstants.CARGO_VOLUME)
					+ "方");
		}

		if (Integer.valueOf(intent
				.getStringExtra(CellSiteConstants.CARGO_WEIGHT)) == 0) {
			mCWrl.setVisibility(View.GONE);
		} else {
			mCWtv.setText(intent.getStringExtra(CellSiteConstants.CARGO_WEIGHT)
					+ "吨");
		}
		mSAtv.setText(intent
				.getStringExtra(CellSiteConstants.SHIPPER_ADDRESS_NAME));
		mCAtv.setText(intent
				.getStringExtra(CellSiteConstants.CONSIGNEE_ADDRESS_NAME));
		mHSDtv.setText(intent.getStringExtra(CellSiteConstants.SHIPPER_DATE));
		mHDtv.setText(intent
				.getStringExtra(CellSiteConstants.HORDER_DESCRIPTION));
		mTTtv.setText(CellSiteConstants.TruckTypes[Integer.valueOf(intent
				.getStringExtra(CellSiteConstants.TRUCK_TYPE)) - 1]);
		mHItv.setText(intent.getStringExtra(CellSiteConstants.HORDER_ID));

	}
	
	public void callPhone(View v){
		Intent intent = new Intent(Intent.ACTION_CALL, Uri
				.parse("tel:" + phoneNum));
		startActivity(intent);
	}
	
	public void requestHorder(View v){
		mReqHorderTask = new ReqHorderTask();
		mReqHorderTask.execute(horderId, ""+ app.getUser().getId());
	}
	
	int reqHorder(String _horderId, String _driverId) {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(CellSiteConstants.HORDER_ID,
				_horderId));
		postParameters.add(new BasicNameValuePair(CellSiteConstants.DRIVER_ID,
				_driverId));

		JSONObject response = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(
					CellSiteConstants.REQUEST_HODER_URL, postParameters);

			int resultCode = Integer.parseInt(response.get(
					CellSiteConstants.RESULT_CODE).toString());
			Log.d(TAG, "ResultCode = " + resultCode);
			if (CellSiteConstants.RESULT_SUC == resultCode) {

				// app.startToSearchLoc();
			} else  {
				
			}
			return resultCode;
		} catch (Exception e) {
			Log.d(TAG, "Register by mail fails." + e.getMessage());
			return CellSiteConstants.UNKNOWN_ERROR;
		}
	}

	private class ReqHorderTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return reqHorder(params[0], params[1]);
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
					// 更新状态
				mReqHorderBtn.setText(res.getText(R.string.already_requested));
				mReqHorderBtn.setBackgroundColor(111); //TODO: 添加disable 状态
				ALREADY_REQUESTED = true; //TODO: 将更新状态返回
				
				
			} else {

			}

		}
	}

}