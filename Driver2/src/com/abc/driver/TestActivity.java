package com.abc.driver;

import android.os.Bundle;
import android.util.Log;

import com.abc.driver.utility.CityDBReader;

public class TestActivity extends BaseActivity {
	public static final String TAG = "TestActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CityDBReader dbReader = new CityDBReader(this.getApplicationContext());
		String pCode = "110000";
		String cCode = "110100";
		String dCode = "110101";
		
		String provinceName  = dbReader.getProvinceByCode(pCode).trim();
		String cityName  = dbReader.getCityByCode(cCode).trim();
		String districtName  = dbReader.getDistrictByCode(dCode).trim();
		
		
		Log.d(TAG, provinceName + " city name: "+cityName + " district name: "+districtName);

	}
}
