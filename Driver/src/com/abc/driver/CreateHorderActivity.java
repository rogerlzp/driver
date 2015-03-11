package com.abc.driver;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.abc.driver.CityDialog.InputListener;

public class CreateHorderActivity extends BaseActivity {

	private TextView mSAtv;
	private TextView mCAtv;

	CityDialog mCityDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_horder_create);
		mSAtv = (TextView) findViewById(R.id.shipper_address_tv);
		mCAtv = (TextView) findViewById(R.id.consignee_address_tv);
	}

	/*
	public void chooseAddress(View v) {
		if (v.getId() == R.id.shipper_address_btn
				|| v.getId() == R.id.shipper_address_tv) {

			InputListener listener = new InputListener() {

				@Override
				public void getText(String str) {
					// TODO Auto-generated method stub
					mSAtv.setText(str);
				}
			};
			mCityDialog = new CityDialog(CreateHorderActivity.this, listener);
			mCityDialog.setTitle("选择地址");
			mCityDialog.show();

		} else if (v.getId() == R.id.consignee_address_btn
				|| v.getId() == R.id.consignee_address_tv) {
			InputListener listener = new InputListener() {

				@Override
				public void getText(String str) {
					// TODO Auto-generated method stub
					mCAtv.setText(str);
				}
			};
			mCityDialog = new CityDialog(CreateHorderActivity.this, listener);
			mCityDialog.setTitle("选择地址");
			mCityDialog.show();

		}
		
	
	
	} */
	

}
