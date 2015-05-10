package com.abc.driver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.driver.utility.CellSiteConstants;

public class TruckDetailActivity extends BaseActivity {

	String truckId;
	ImageView mDPiv, mTIiv;
	TextView mNtv;
	TextView mTTtv;
	TextView mTLtv;
	TextView mTPNtv;
	String phoneNum;
	int selectedDriverId, newSelectedDriverId;
	int currentDriverId;

	Bitmap dpBitmap, tiBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.truck_detail);
		Intent intent = getIntent();

		phoneNum = intent.getStringExtra(CellSiteConstants.MOBILE);

		currentDriverId = intent.getIntExtra(CellSiteConstants.DRIVER_ID, 1);
		truckId = intent.getStringExtra(CellSiteConstants.TRUCK_ID);
		initView(intent);

	}

	public void initView(Intent intent) {

		mDPiv = (ImageView) findViewById(R.id.driver_portrait_iv);
		mTIiv = (ImageView) findViewById(R.id.truck_image_iv);
		mNtv = (TextView) findViewById(R.id.name_tv);
		mTTtv = (TextView) findViewById(R.id.truck_type_tv);
		mTLtv = (TextView) findViewById(R.id.truck_length_tv);
		mTPNtv = (TextView) findViewById(R.id.truck_plate_tv);

		mTTtv.setText(CellSiteConstants.TruckTypes[intent.getIntExtra(
				CellSiteConstants.TRUCK_TYPE, 1)]);
		mTLtv.setText(CellSiteConstants.TruckLengths[intent.getIntExtra(
				CellSiteConstants.TRUCK_LENGTH, 1)]);
		mTPNtv.setText(intent.getStringExtra(CellSiteConstants.TRUCK_PLATE));
		mTPNtv.setText(intent.getStringExtra(CellSiteConstants.TRUCK_PLATE));

	}

	// contact driver
	public void contactDriver(View v) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ phoneNum));
		startActivity(intent);

	}
}
