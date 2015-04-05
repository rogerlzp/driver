package com.abc.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.driver.model.Horder;
import com.abc.driver.utility.CellSiteConstants;

public class HorderDetailActivity extends BaseActivity {

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
	
	RelativeLayout mCWrl;// 货物重量
	RelativeLayout mCVrl;//货物体积

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.horder_detail);
		mHorder = new Horder();

		Intent intent = getIntent();

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

}