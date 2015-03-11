package com.abc.driver;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.driver.CityDialog.InputListener;
import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteConstants;

public class MainActivity extends BaseActivity {

	public static final String TAG = MainActivity.class.getSimpleName();
	private TextView mSAtv;
	private TextView mCAtv;
	private String mShipperAddressCode;
	private String mConsigneeAddressCode;
	private String mShipperDate;
	private String mCargoType;
	private String mCargoWeight;
	private String mCargoVolume;
	private String mTruckType;
	private String mShipperUsername;
	private String mHorderDesc;
	private String mTruckLength;
	ArrayList<HashMap<String, Object>> cargoTypeList = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> mTruckLengthList = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> mTruckTypeList = new ArrayList<HashMap<String, Object>>();

	private TextView mSDtv;
	private TextView mCTtv;
	private TextView mTTtv;
	private TextView mTLtv;

	CityDialog mCityDialog = null;

	private ViewPager mTabPager;
	private ImageView mTabImg;//
	private ImageView mTab1, mTab2, mTab3, mTab4;
	private int zero = 0;
	private int currIndex = 0;
	private int one;
	private int two;
	private int three;
	private LinearLayout mClose;
	private LinearLayout mCloseBtn;
	private View layout;
	private boolean menu_display = false;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;
	
	private UpdateTruckTask mUpdateTruckTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_weixin);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		mTab1 = (ImageView) findViewById(R.id.img_weixin);
		mTab2 = (ImageView) findViewById(R.id.img_address);
		mTab3 = (ImageView) findViewById(R.id.img_friends);
		mTab4 = (ImageView) findViewById(R.id.img_settings);
		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		mTab1.setOnClickListener(new MyOnClickListener(0));
		mTab2.setOnClickListener(new MyOnClickListener(1));
		mTab3.setOnClickListener(new MyOnClickListener(2));
		mTab4.setOnClickListener(new MyOnClickListener(3));
		Display currDisplay = getWindowManager().getDefaultDisplay();
		int displayWidth = currDisplay.getWidth();
		int displayHeight = currDisplay.getHeight();
		one = displayWidth / 4;
		two = one * 2;
		three = one * 3;

		LayoutInflater mLi = LayoutInflater.from(this);
		View view1 = mLi.inflate(R.layout.main_tab_horder_create, null);
		View view2 = mLi.inflate(R.layout.main_tab_address, null);
		View view3 = mLi.inflate(R.layout.main_tab_friends, null);
		View view4 = mLi.inflate(R.layout.main_tab_me, null);

		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);

		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};

		mTabPager.setAdapter(mPagerAdapter);

		initData();
	}

	public void initData() {

		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("PIC", R.drawable.ic_launcher);
			map.put("TITLE", "Test Title");
			map.put("CTYPE", "CT1");
			cargoTypeList.add(map);
		}

		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("PIC", R.drawable.ic_launcher);
			map.put("TITLE", "Test Title");
			map.put("TTYPE", "TT1");
			mTruckTypeList.add(map);
		}
		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("TITLE", "Test Title");
			map.put("TLENGTH", "TL1");
			mTruckLengthList.add(map);
		}

	}

	/**
		 * 
		 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	};


	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_weixin_pressed));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_address_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_find_frd_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				}
				// download Truck information
				mUpdateTruckTask  = new UpdateTruckTask();
				mUpdateTruckTask.execute(""+app.getUser().getId());
				
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private class UpdateTruckTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return updateTruckTask(params[0]);
		}

		@Override
		public void onPostExecute(Integer result) {
			if (this.isCancelled()) {
				return;
			}
			Integer resCode = result;// Integer.parseInt(result);
			if (resCode == CellSiteConstants.RESULT_SUC) {
				// TODO: set for different user
			}
		}

		protected Integer updateTruckTask(String _userId) {

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.USER_ID, _userId));

			JSONObject response = null;
			try {
				response = CellSiteHttpClient.executeHttpPost(
						CellSiteConstants.GET_TRUCK_URL, postParameters);

				int resultCode = Integer.parseInt(response.get(
						CellSiteConstants.RESULT_CODE).toString());
				if (resultCode == CellSiteConstants.RESULT_SUC) {
					Log.d(TAG, "");
					// SET TRUCK Information 
					JSONObject truckObj = (JSONObject)response.get(CellSiteConstants.TRUCK);
					app.getUser().getMyTruck().setTruckId(Integer.parseInt(truckObj.get(
						CellSiteConstants.ID).toString()));
					app.getUser().getMyTruck().setLengthId(Integer.parseInt(truckObj.get(
							CellSiteConstants.TRUCK_LENGTH).toString()));
					app.getUser().getMyTruck().setTypeId(Integer.parseInt(truckObj.get(
							CellSiteConstants.TRUCK_TYPE).toString()));
					app.getUser().getMyTruck().setAuditStatusId(Integer.parseInt(truckObj.get(
							CellSiteConstants.TRUCK_AUDIT_STATUS).toString()));
					app.getUser().getMyTruck().setLicenseImageUrl(truckObj.get(
							CellSiteConstants.TRUCK_LICENSE_URL).toString());
					// 
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
																				

			if (menu_display) { 
				menuWindow.dismiss();
				menu_display = false;
			} else {
				onBackPressed();
			}
		}

		else if (keyCode == KeyEvent.KEYCODE_MENU) { 
			if (!menu_display) {
				// ����LayoutInflater����
				inflater = (LayoutInflater) this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				layout = inflater.inflate(R.layout.main_menu, null);

				menuWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT);
				menuWindow.showAtLocation(this.findViewById(R.id.mainweixin),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				mClose = (LinearLayout) layout.findViewById(R.id.menu_close);
				mCloseBtn = (LinearLayout) layout
						.findViewById(R.id.menu_close_btn);

				mCloseBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// Toast.makeText(Main.this, "����",
						// Toast.LENGTH_LONG).show();
						Intent intent = new Intent();
						// intent.setClass(MainWeixin.this,Exit.class);
						// startActivity(intent);
						menuWindow.dismiss(); // ��������������������Menu
					}
				});
				menu_display = true;
			} else {
				menuWindow.dismiss();
				menu_display = false;
			}

			return false;
		}
		return false;
	}

	public void createHorder(View v) {

		mShipperUsername = ((EditText) findViewById(R.id.shipper_username_et))
				.getText().toString();
		mHorderDesc = ((EditText) findViewById(R.id.horder_description_et))
				.getText().toString();
		mCargoWeight = ((EditText) findViewById(R.id.cargo_weight_et))
				.getText().toString();
		mCargoVolume = ((EditText) findViewById(R.id.cargo_volume_et))
				.getText().toString();

		CreateHorderTask mCreateHorderTask = new CreateHorderTask();

		Log.d(TAG, "user id:" + app.getUser().getId());

		mCreateHorderTask.execute(mShipperAddressCode, mShipperDate,
				mConsigneeAddressCode, mShipperUsername, mCargoType,
				mCargoWeight, mCargoVolume, mTruckType, mTruckLength,
				mHorderDesc, "" + app.getUser().getId());

	}

	private class CreateHorderTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return createHorderTask(params[0], params[1], params[2], params[3],
					params[4], params[5], params[6], params[7], params[8],
					params[9], params[10]);
		}

		@Override
		public void onPostExecute(Integer result) {
			if (this.isCancelled()) {
				return;
			}
			Integer resCode = result;// Integer.parseInt(result);
			if (resCode == CellSiteConstants.RESULT_SUC) {
				// TODO: set for different user
			}
		}

		protected Integer createHorderTask(String _shipperAddressCode,
				String _shipperDate, String _consigneeAddressCode,
				String _shipperUsername, String _cargoType,
				String _cargoWeight, String _cargoVolume, String _truckType,
				String _truckLength, String _orderDesc, String _userId) {

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			postParameters
					.add(new BasicNameValuePair(
							CellSiteConstants.SHIPPER_ADDRESS_CODE,
							_shipperAddressCode));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.SHIPPER_DATE, _shipperDate));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.CONSIGNEE_ADDRESS_CODE,
					_consigneeAddressCode));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.SHIPPER_USERNAME, _shipperUsername));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.CARGO_TYPE, _cargoType));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.CARGO_WEIGHT, _cargoWeight));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.CARGO_VOLUME, _cargoVolume));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_TYPE, _truckType));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_LENGTH, _truckLength));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.HORDER_DESCRIPTION, _orderDesc));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.USER_ID, _userId));

			JSONObject response = null;
			try {
				response = CellSiteHttpClient.executeHttpPost(
						CellSiteConstants.CREATE_HORDER_URL, postParameters);

				int resultCode = Integer.parseInt(response.get(
						CellSiteConstants.RESULT_CODE).toString());
				if (resultCode == CellSiteConstants.RESULT_SUC) {
					// TODO
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

	/**
	 * 根据view的id 来选择地点
	 * 
	 * @param v
	 */
	public void chooseAddress(View v) {
		mSAtv = (TextView) findViewById(R.id.shipper_address_tv);
		mCAtv = (TextView) findViewById(R.id.consignee_address_tv);

		if (v.getId() == R.id.shipper_address_btn
				|| v.getId() == R.id.shipper_address_tv) {

			InputListener listener = new InputListener() {

				@Override
				public void getText(String str, String str2) {
					// TODO Auto-generated method stub
					mSAtv.setText(str);
					mShipperAddressCode = str2;
				}
			};
			mCityDialog = new CityDialog(MainActivity.this, listener);
			mCityDialog.setTitle("选择地址");
			mCityDialog.show();

		} else if (v.getId() == R.id.consignee_address_btn
				|| v.getId() == R.id.consignee_address_tv) {
			InputListener listener = new InputListener() {

				@Override
				public void getText(String str, String str2) {
					// TODO Auto-generated method stub
					mCAtv.setText(str);
					mConsigneeAddressCode = str2;
				}
			};
			mCityDialog = new CityDialog(MainActivity.this, listener);
			mCityDialog.setTitle("选择地址");
			mCityDialog.show();

		}
	}

	public void chooseDate(View v) {
		mSDtv = (TextView) findViewById(R.id.shipper_date_tv);
		Calendar calendar = Calendar.getInstance();
		Dialog dialog = null;
		DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				String tmpMonthOfYear, tmpDayofMonth;
				if (monthOfYear < 9) {
					tmpMonthOfYear = "0" + (monthOfYear + 1);
				} else {
					tmpMonthOfYear = "" + (monthOfYear + 1);
				}

				if (dayOfMonth < 9) {
					tmpDayofMonth = "0" + dayOfMonth;
				} else {
					tmpDayofMonth = "" + dayOfMonth;
				}

				mShipperDate = year + "-" + tmpMonthOfYear + "-"
						+ tmpDayofMonth;
				mSDtv.setText(mShipperDate);
			}
		};
		dialog = new DatePickerDialog(this, dateListener,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	public void chooseCargoType(View v) {
		mCTtv = (TextView) this.findViewById(R.id.cargo_type_tv);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		GridView gridView1 = new GridView(this);
		gridView1.setNumColumns(3);
		// (GridView)findViewById(R.id.gridView1);
		SimpleAdapter adapter = new SimpleAdapter(this, cargoTypeList,
				R.layout.cargo_type_griditem, new String[] { "PIC", "TITLE",
						"CTYPE" }, new int[] { R.id.griditem_pic,
						R.id.griditem_title, R.id.griditem_type, });

		gridView1.setAdapter(adapter);
		builder.setTitle("Please Choose");
		builder.setInverseBackgroundForced(true);
		builder.setView(gridView1);
		final Dialog dialog = builder.create();

		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long rowId) {
				// doing something in here and then close
				mCTtv.setText(((TextView) view
						.findViewById(R.id.griditem_title)).getText());
				mCargoType = ((TextView) view.findViewById(R.id.griditem_type))
						.getText().toString();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void chooseTruckType(View v) {
		mTTtv = (TextView) this.findViewById(R.id.truck_type_tv);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		GridView gridView1 = new GridView(this);
		gridView1.setNumColumns(3);
		// (GridView)findViewById(R.id.gridView1);
		SimpleAdapter adapter = new SimpleAdapter(this, mTruckTypeList,
				R.layout.truck_type_griditem, new String[] { "PIC", "TITLE",
						"TTYPE" }, new int[] { R.id.griditem_pic,
						R.id.griditem_title, R.id.griditem_type, });

		gridView1.setAdapter(adapter);
		builder.setTitle("Please Choose");
		builder.setInverseBackgroundForced(true);
		builder.setView(gridView1);
		final Dialog dialog = builder.create();

		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long rowId) {
				mTTtv.setText(((TextView) view
						.findViewById(R.id.griditem_title)).getText());
				mTruckType = ((TextView) view.findViewById(R.id.griditem_type))
						.getText().toString();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void chooseTruckLength(View v) {
		mTLtv = (TextView) findViewById(R.id.truck_length_tv);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		GridView gridView1 = new GridView(this);
		gridView1.setNumColumns(3);
		// (GridView)findViewById(R.id.gridView1);
		SimpleAdapter adapter = new SimpleAdapter(this, mTruckLengthList,
				R.layout.truck_length_griditem, new String[] { "TITLE",
						"TLENGTH" }, new int[] { R.id.griditem_title,
						R.id.griditem_length, });

		gridView1.setAdapter(adapter);
		builder.setTitle("Please Choose");
		builder.setInverseBackgroundForced(true);
		builder.setView(gridView1);
		final Dialog dialog = builder.create();

		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long rowId) {
				mTLtv.setText(((TextView) view
						.findViewById(R.id.griditem_title)).getText());
				mTruckLength = ((TextView) view
						.findViewById(R.id.griditem_length)).getText()
						.toString();
				dialog.dismiss();
			}
		});
		// */

		dialog.show();

	}

	public void gotoPersonal(View v) {
		Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
		startActivity(intent);
	}

	public void gotoTruck(View v) {
		Intent intent = new Intent(MainActivity.this, TruckActivity.class);
		startActivity(intent);
	}

	public void gotoSetting(View v) {
		Intent intent = new Intent(MainActivity.this, SettingActivity.class);
		startActivity(intent);
	}


}
