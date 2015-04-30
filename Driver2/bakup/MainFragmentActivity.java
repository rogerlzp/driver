package com.abc.huoyun;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.huoyun.CargoWeightVolumeDialog.CargoWeightVolumeInputListener;
import com.abc.huoyun.CityDialog.InputListener;
import com.abc.huoyun.cache.HorderType;
import com.abc.huoyun.net.CellSiteHttpClient;
import com.abc.huoyun.utility.CellSiteApplication;
import com.abc.huoyun.utility.CellSiteConstants;
import com.abc.huoyun.utility.CityDBReader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainFragmentActivity extends FragmentActivity {

	private ViewPager mTabPager;
	private MyFragmentPageAdapter mFPAdapter;
	private static int NUM_PAGES = 4;
	private ImageView mTabImg;//
	private ImageView mTab1, mTab2, mTab3, mTab4;
	private int zero = 0;
	private int currIndex = 1;
	private int one;
	private int two;
	private int three;

	public static final String TAG = MainFragmentActivity.class.getSimpleName();

	// create horder
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
	ArrayList<HashMap<String, Object>> mCargoTypeList = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> mTruckLengthList = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> mTruckWeightList = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> mTruckTypeList = new ArrayList<HashMap<String, Object>>();

	private TextView mSDtv;
	private TextView mCTtv;
	private TextView mCWVtv; // 货物重量和体积
	private TextView mTTtv;

	private TextView mTLtv;

	CellSiteApplication app;
	CityDialog mCityDialog = null;

	// 货单列举
	// 货单
	PullToRefreshListView mHorderLv;
	HorderType[] mHorderTypes = new HorderType[3];
	ViewGroup mHorderMore, mEmptyHorderView;
	TextView mHorderMoreTv;
	boolean isForceRefreshHorder = false;
	Boolean mHasExceptionHorder = false;
	int mLvHistoryPosHorder = 0;
	int mCurrRadioIdx = 0;

	ProgressDialog mProgressdialog;
	HorderDownLoadTask mHorderDownLoadTask;

	OnItemClickListener mHorderDetailListener = new OnItemClickListener() {

		// / @Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			ArrayList<HashMap<String, Object>> aHorders = mHorderTypes[mCurrRadioIdx].nHorders;

			position--; // for the header view has taken the first position, so
						// all the data view must decrease 1.

			if (position == aHorders.size()) {
				if (mHorderTypes[mCurrRadioIdx].hasShowAllHorders) {
					mHorderMoreTv.setText(R.string.hasShowAll);
				} else {
					// mLvHistoryPosHorder =
					// mHorderLv.getFirstVisiblePosition();
					mHorderDownLoadTask = new HorderDownLoadTask();
					mHorderDownLoadTask
							.execute(CellSiteConstants.MORE_OPERATION);
				}
				return;
			}

			Intent intent = new Intent(MainFragmentActivity.this,
					HorderDetailActivity.class);
			intent.putExtra(
					CellSiteConstants.HORDER_ID,
					(String) aHorders.get(position).get(
							CellSiteConstants.HORDER_ID));
			intent.putExtra(
					CellSiteConstants.SHIPPER_USERNAME,
					(String) aHorders.get(position).get(
							CellSiteConstants.SHIPPER_USERNAME));
			intent.putExtra(
					CellSiteConstants.REPLIED_DRIVER_COUNT,
					(Integer) aHorders.get(position).get(
							CellSiteConstants.REPLIED_DRIVER_COUNT));
			intent.putExtra(
					CellSiteConstants.REPLIED_DRIVER_LIST,
					(ArrayList<String>) aHorders.get(position).get(
							CellSiteConstants.REPLIED_DRIVER_LIST));
			intent.putExtra(
					CellSiteConstants.SHIPPER_ADDRESS_NAME,
					(String) aHorders.get(position).get(
							CellSiteConstants.SHIPPER_ADDRESS_NAME));
			intent.putExtra(
					CellSiteConstants.CONSIGNEE_ADDRESS_NAME,
					(String) aHorders.get(position).get(
							CellSiteConstants.CONSIGNEE_ADDRESS_NAME));
			intent.putExtra(CellSiteConstants.CARGO_TYPE, (String) aHorders
					.get(position).get(CellSiteConstants.CARGO_TYPE));
			intent.putExtra(CellSiteConstants.CARGO_WEIGHT, (String) aHorders
					.get(position).get(CellSiteConstants.CARGO_WEIGHT));
			intent.putExtra(CellSiteConstants.CARGO_VOLUME, (String) aHorders
					.get(position).get(CellSiteConstants.CARGO_VOLUME));
			intent.putExtra(CellSiteConstants.TRUCK_TYPE, (String) aHorders
					.get(position).get(CellSiteConstants.TRUCK_TYPE));
			intent.putExtra(CellSiteConstants.HORDER_STATUS, (String) aHorders
					.get(position).get(CellSiteConstants.HORDER_STATUS));
			intent.putExtra(CellSiteConstants.SHIPPER_DATE, (String) aHorders
					.get(position).get(CellSiteConstants.SHIPPER_DATE));
			intent.putExtra(
					CellSiteConstants.SHIPPER_USERNAME,
					(String) aHorders.get(position).get(
							CellSiteConstants.SHIPPER_USERNAME));
			intent.putExtra(
					CellSiteConstants.HORDER_DESCRIPTION,
					(String) aHorders.get(position).get(
							CellSiteConstants.HORDER_DESCRIPTION));

			startActivity(intent);

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (CellSiteApplication) getApplication();
		setContentView(R.layout.main_weixin);
		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mFPAdapter = new MyFragmentPageAdapter(this.getSupportFragmentManager());
		mTabPager.setAdapter(mFPAdapter);

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
		initData();
	}

	public void initData() {

		for (int i = 0; i < 14; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("PIC", R.drawable.ic_launcher);
			map.put("TITLE", CellSiteConstants.TruckTypes[i]);
			map.put("TTYPE", i + 1);
			mTruckTypeList.add(map);
		}

		for (int i = 0; i < 17; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("TITLE", CellSiteConstants.TruckLengths[i]);
			map.put("TLENGTH", i + 1);
			mTruckLengthList.add(map);
		}

		for (int i = 0; i < 15; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("TITLE", CellSiteConstants.CargoTypes[i]);
			map.put("CARGOTYPE", i + 1);
			mCargoTypeList.add(map);
		}

	}

	public void initHorderType() {
		mHorderTypes[0] = new HorderType(0, MainFragmentActivity.this);
		mHorderTypes[1] = new HorderType(1, MainFragmentActivity.this);
		mHorderTypes[2] = new HorderType(2, MainFragmentActivity.this);
	}

	public static class MyFragmentPageAdapter extends FragmentStatePagerAdapter {
		public MyFragmentPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return HorderCreateFragment.newInstance();
			case 1:
				return TruckFragment.newInstance();
			case 2:

				return MyHorderFragment.newInstance();
			case 3:
				return MeFragment.newInstance();
			default:
				return null;
			}
		}

	}

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
			boolean needAnimation = true;
			switch (arg0) {
			case 0:

				mTab1.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_weixin_pressed));

				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					needAnimation = false;
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					needAnimation = false;
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				// initCreateHorderView();

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
					needAnimation = false;
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}
				/*
				 * initTrucks(); mTruckMore.setVisibility(View.INVISIBLE);
				 * mTruckMoreTv.setText(R.string.show_more);
				 * 
				 * if (mProgressdialogTruck == null ||
				 * !mProgressdialogTruck.isShowing()) { mProgressdialogTruck =
				 * new ProgressDialog(MainActivity.this);
				 * mProgressdialogTruck.setMessage("正在加载数据");
				 * mProgressdialogTruck.setIndeterminate(true);
				 * mProgressdialogTruck.setCancelable(true);
				 * mProgressdialogTruck.show(); }
				 * 
				 * mTruckLv.setAdapter(mTrucks.nTruckAdapter);
				 * 
				 * mTruckDownLoadTask = new TruckDownLoadTask();
				 * mTruckDownLoadTask
				 * .execute(CellSiteConstants.NORMAL_OPERATION);
				 */

				break;

			case 2:

				mTab3.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_find_frd_pressed));

				initHorderType(); // TODO: 优化

				if (currIndex == 0) {
					needAnimation = false;
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

				initHorders(); // mHorderMore.setVisibility(View.GONE); //
				mHorderMoreTv.setText(R.string.show_more);

				if (mProgressdialog == null || !mProgressdialog.isShowing()) {
					mProgressdialog = new ProgressDialog(
							MainFragmentActivity.this);
					mProgressdialog.setMessage("正在加载数据");
					mProgressdialog.setIndeterminate(true);
					mProgressdialog.setCancelable(true);
					mProgressdialog.show();
				}
				mHorderDownLoadTask = new HorderDownLoadTask();
				mHorderDownLoadTask.execute(CellSiteConstants.MORE_OPERATION);

				break;
			case 3:

				mTab4.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_settings_pressed));
				/*
				 * if (currIndex == 0) { needAnimation = false; animation = new
				 * TranslateAnimation(zero, three, 0, 0);
				 * mTab1.setImageDrawable(getResources().getDrawable(
				 * R.drawable.tab_weixin_normal)); } else if (currIndex == 1) {
				 * needAnimation = false; animation = new
				 * TranslateAnimation(one, three, 0, 0);
				 * mTab2.setImageDrawable(getResources().getDrawable(
				 * R.drawable.tab_address_normal)); } else if (currIndex == 2) {
				 * animation = new TranslateAnimation(two, three, 0, 0);
				 * mTab3.setImageDrawable(getResources().getDrawable(
				 * R.drawable.tab_find_frd_normal)); } initMeView(); // download
				 * Truck information mUpdateTruckTask = new UpdateTruckTask();
				 * mUpdateTruckTask.execute("" + app.getUser().getId());
				 * 
				 * break;
				 */
				
			}

			FragmentTransaction txn = getSupportFragmentManager().beginTransaction();
			txn.hide(((MyFragmentPageAdapter)mTabPager.getAdapter()).getItem(currIndex));

			currIndex = arg0;

			txn.show(((MyFragmentPageAdapter)mTabPager.getAdapter()).getItem(arg0)).commit();
		
			/*
			 * if (needAnimation) { animation.setFillAfter(true);// True:
			 * animation.setDuration(150);
			 * 
			 * mTabImg.startAnimation(animation); }
			 */

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public void initHorders() {

		mHorderLv = (PullToRefreshListView) findViewById(R.id.myPartyLv);
		mHorderLv.setMode(Mode.BOTH);

		mHorderMore = (ViewGroup) LayoutInflater
				.from(MainFragmentActivity.this).inflate(R.layout.more_list,
						null);
		mHorderMore.setVisibility(View.GONE);
		mEmptyHorderView = (ViewGroup) LayoutInflater.from(
				MainFragmentActivity.this).inflate(R.layout.empty_horder, null);

		mHorderMoreTv = (TextView) mHorderMore.getChildAt(0);

		// mHorderLv.getRefreshableView().addFooterView(mHorderMore);
		mHorderLv.setOnItemClickListener(mHorderDetailListener);
		mHorderLv.setAdapter(mHorderTypes[mCurrRadioIdx].nHorderAdapter);
		// Set a listener to be invoked when the list should be refreshed.
		mHorderLv.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isForceRefreshHorder = true;

				String label = DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);// 加上时间

				// mHorderTypes[mCurrRadioIdx] = new HorderType(mCurrRadioIdx);

				mHorderDownLoadTask = new HorderDownLoadTask();
				mHorderDownLoadTask.execute(CellSiteConstants.NORMAL_OPERATION);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isForceRefreshHorder = true;
				// mHorderTypes[mCurrRadioIdx] = new HorderType(mCurrRadioIdx);
				String label = DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);// 加上时间

				mHorderDownLoadTask = new HorderDownLoadTask();
				mHorderDownLoadTask.execute(CellSiteConstants.NORMAL_OPERATION);
			}

		});

	}

	public void createHorder(View v) {

		mShipperUsername = ((EditText) this
				.findViewById(R.id.shipper_username_et)).getText().toString();
		mHorderDesc = ((EditText) this.findViewById(R.id.horder_description_et))
				.getText().toString();

		CreateHorderTask mCreateHorderTask = new CreateHorderTask();

		Log.d(TAG, "user id:" + app.getUser().getId());

		mCreateHorderTask.execute(mShipperAddressCode, mShipperDate,
				mConsigneeAddressCode, mShipperUsername, mCargoType,
				mCargoWeight, mCargoVolume, mTruckType, mTruckLength,
				mHorderDesc, "" + app.getUser().getId(), app.getUser()
						.getMobileNum());

	}

	private class CreateHorderTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return createHorderTask(params[0], params[1], params[2], params[3],
					params[4], params[5], params[6], params[7], params[8],
					params[9], params[10], params[11]);
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
				String _truckLength, String _orderDesc, String _userId,
				String _shipperPhone) {

			Log.d(TAG, "_shipperDate: " + _shipperDate + "\n_cargoType: "
					+ _cargoType + "\n_cargoWeight: " + _cargoWeight
					+ "\n_cargoVolume" + _cargoVolume + "\n_truckType:"
					+ _truckType + "_truckLength: " + _truckLength);
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
					CellSiteConstants.SHIPPER_PHONE, _shipperPhone));
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
	 * 弹出对话框，输入重量和体积
	 * 
	 * @param v
	 */
	public void chooseCargoWeightVolume(View v) {
		mCWVtv = ((TextView) this.findViewById(R.id.cargo_weight_tv));

		CargoWeightVolumeInputListener listener = new CargoWeightVolumeInputListener() {

			@Override
			public void getText(String str, String str2) {
				// TODO Auto-generated method stub
				String finalStr = "";
				if (!str.equals("0")) {
					finalStr = "  " + str + "吨  ";
				}
				if (!str2.equals("0")) {
					finalStr += str2 + "立方";
				}

				mCWVtv.setText(finalStr);
				mCargoWeight = str;
				mCargoVolume = str2;
			}
		};
		CargoWeightVolumeDialog mCWVD = new CargoWeightVolumeDialog(this,
				listener);
		mCWVD.setTitle(R.string.cargo_weight_vloume);
		mCWVD.show();

	}

	/**
	 * 选择地址
	 * 
	 * @param v
	 */
	public void chooseAddress(View v) {
		mSAtv = (TextView) this.findViewById(R.id.shipper_address_tv);
		mCAtv = (TextView) this.findViewById(R.id.consignee_address_tv);

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
			mCityDialog = new CityDialog(this, listener);
			mCityDialog.setTitle(R.string.choose_address);
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
			mCityDialog = new CityDialog(this, listener);
			mCityDialog.setTitle(R.string.choose_address);
			mCityDialog.show();

		}
	}

	public void chooseDate(View v) {
		mSDtv = (TextView) this.findViewById(R.id.shipper_date_tv);
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
		SimpleAdapter adapter = new SimpleAdapter(this, mCargoTypeList,
				R.layout.cargo_type_griditem, new String[] { "TITLE",
						"CARGOTYPE" }, new int[] { R.id.griditem_title,
						R.id.griditem_type, });

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
		mTLtv = (TextView) this.findViewById(R.id.truck_length_tv);
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

	class HorderDownLoadTask extends AsyncTask<Integer, String, String> {
		static final String TAG_FAIL = "FAIL";
		static final String TAG_SUCC = "SUCCESS";

		List<HashMap<String, String>> nTmpNewsData;

		@Override
		protected String doInBackground(Integer... params) {
			int moreOperation = params[0];

			try {
				if (isForceRefreshHorder
						|| moreOperation == CellSiteConstants.MORE_OPERATION
						|| app.getHorderTypeCache(mCurrRadioIdx) == null) {
					Log.d(TAG,
							"Will connect the network and download the horders");
					getHorder(mCurrRadioIdx);

					if (mHorderTypes[mCurrRadioIdx].nHorders.size() < mHorderTypes[mCurrRadioIdx].nDisplayNum
							+ CellSiteConstants.PAGE_COUNT
							&& !mHasExceptionHorder) {
						mHorderTypes[mCurrRadioIdx].hasShowAllHorders = true;
					}
					Log.d(TAG, "after download the horder");
				} else {
					Log.d(TAG, "Will use the cache, current radio index "
							+ mCurrRadioIdx);
					;

					mHorderTypes[mCurrRadioIdx] = app
							.getHorderTypeCache(mCurrRadioIdx);
					Log.d(TAG, "++++++++++++++++Number of My horders :"
							+ mHorderTypes[mCurrRadioIdx].nHorders.size());
				}

			} catch (Exception e) {
				e.printStackTrace();
				return TAG_FAIL;
			}

			Log.d(TAG, "after download the horders: TAG_SUCC");
			return TAG_SUCC;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			Log.d(TAG, "HorderDownLoadTask onPostExecute()");
			if (!this.isCancelled()) {
				if (mProgressdialog != null) {
					Log.d(TAG, "Cancel the progress dialog");
					mProgressdialog.cancel();
				}

				if (isForceRefreshHorder) {
					isForceRefreshHorder = false;
					mHorderLv.onRefreshComplete();
				}
				mHorderTypes[mCurrRadioIdx].nHorderAdapter
						.setHorders(mHorderTypes[mCurrRadioIdx].nHorders);
				mHorderLv
						.setAdapter(mHorderTypes[mCurrRadioIdx].nHorderAdapter);
				mHorderTypes[mCurrRadioIdx].nHorderAdapter
						.notifyDataSetChanged();
				mHorderMore.setVisibility(View.VISIBLE);
				mHorderLv.setEmptyView(mEmptyHorderView);

				if (mHorderTypes[mCurrRadioIdx].hasShowAllHorders) {

					mHorderMoreTv.setText(R.string.hasShowAll);
				} else {
					mHorderMoreTv.setText(R.string.show_more);
				}

				mHorderTypes[mCurrRadioIdx].nDisplayNum = mHorderTypes[mCurrRadioIdx].nHorders
						.size();
				Log.d(TAG,
						"++++++++++++++++onPostExecute() Number of My Parties to save :"
								+ mHorderTypes[mCurrRadioIdx].nHorders.size());
				app.setHorderTypeCache(mHorderTypes[mCurrRadioIdx],
						mCurrRadioIdx);
				Log.d(TAG, "++++++++++++++++onPostExecute() saved in the app :"
						+ app.getHorderTypeCache(mCurrRadioIdx).nHorders.size());

				if (mHorderTypes[mCurrRadioIdx].nDisplayNum > 0) {
					Log.d(TAG, "set more tv to visible");

					mHorderMoreTv.setVisibility(View.VISIBLE);
				} else {
					Log.d(TAG, "set more tv to INVISIBLE");
					mHorderMoreTv.setVisibility(View.INVISIBLE);
				}
				if (mLvHistoryPosHorder > 0) {
					// mHorderLv.setSelectionFromTop(mLvHistoryPosHorder, 0);
					mLvHistoryPosHorder = 0;
				}

			}

		}
	}

	public JSONObject getHorder(Integer horder_status) {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

		postParameters.add(new BasicNameValuePair(CellSiteConstants.USER_ID, ""
				+ app.getUser().getId()));
		postParameters.add(new BasicNameValuePair(
				CellSiteConstants.HORDER_STATUS, "" + horder_status));

		postParameters.add(new BasicNameValuePair("offset", String
				.valueOf(mHorderTypes[mCurrRadioIdx].nDisplayNum)));
		postParameters.add(new BasicNameValuePair("pagecount", String
				.valueOf(CellSiteConstants.PAGE_COUNT)));
		JSONObject response = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(
					CellSiteConstants.GET_MY_HORDER_URL, postParameters);
			int resultCode = response.getInt(CellSiteConstants.RESULT_CODE);
			if (CellSiteConstants.RESULT_SUC == resultCode) {
				parseJson(response);
			} else {
				Log.d(TAG, "QUERY RESULT FAILED");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * horder状态 {"result_code":"0","horders":[{"id":6,"shipper_username":
	 * "\u674e\u674e\u90bb\u5c45\u5929",
	 * "shipper_phone":"","shipper_date":"2015-03-15 00:00:00"
	 * ,"shipper_address_code":"110000-110100-110101",
	 * "consignee_username":"","consignee_phone"
	 * :"","consignee_address_code":"110000-110100-110101",
	 * "delivery_time":"0000-00-00 00:00:00"
	 * ,"truck_type":"","truck_length":"","cargo_type":"CT1",
	 * "cargo_volume":"\u6d4b\u8bd5",
	 * "cargo_weight":"\u6d4b\u8bd5\u554a","horder_desc"
	 * :"","user_id":9,"status":0, "created_at":"2015-03-15 03:30:50",
	 * "updated_at":"2015-03-15 03:30:50"},}]}
	 * 
	 * @param jsonResult
	 */
	public void parseJson(JSONObject jsonResult) {
		HashMap<String, Object> mHorder;
		try {
			if (jsonResult.get("horders") != JSONObject.NULL) {
				JSONArray results = jsonResult.getJSONArray("horders");
				if (results.length() > 0
						&& results.length() < CellSiteConstants.PAGE_COUNT) {
					mHorderTypes[mCurrRadioIdx].hasShowAllHorders = true;
				}

				for (int i = 0; i < results.length(); i++) {
					try {
						JSONObject resultObj = (JSONObject) results.get(i);
						mHorder = new HashMap<String, Object>();
						mHorder.put(
								CellSiteConstants.SHIPPER_USERNAME,
								resultObj
										.getString(CellSiteConstants.SHIPPER_USERNAME));
						mHorder.put(CellSiteConstants.HORDER_ID,
								(resultObj).getString(CellSiteConstants.ID));
						mHorder.put(CellSiteConstants.DRIVER_ID, (resultObj)
								.getString(CellSiteConstants.DRIVER_ID));

						CityDBReader dbReader = new CityDBReader(
								this.getApplicationContext());
						mHorder.put(
								CellSiteConstants.SHIPPER_ADDRESS_NAME,
								dbReader.getNameFromCode((resultObj)
										.getString(CellSiteConstants.SHIPPER_ADDRESS_CODE_IN)));
						mHorder.put(
								CellSiteConstants.CONSIGNEE_ADDRESS_NAME,
								dbReader.getNameFromCode((resultObj)
										.getString(CellSiteConstants.CONSIGNEE_ADDRESS_CODE2)));
						mHorder.put(CellSiteConstants.CARGO_TYPE, (resultObj)
								.getString(CellSiteConstants.CARGO_TYPE));
						mHorder.put(CellSiteConstants.CARGO_WEIGHT, (resultObj)
								.getString(CellSiteConstants.CARGO_WEIGHT));
						mHorder.put(CellSiteConstants.CARGO_VOLUME, (resultObj)
								.getString(CellSiteConstants.CARGO_VOLUME));
						mHorder.put(CellSiteConstants.TRUCK_TYPE, (resultObj)
								.getString(CellSiteConstants.TRUCK_TYPE));
						mHorder.put(CellSiteConstants.HORDER_STATUS,
								(resultObj).getString(CellSiteConstants.STATUS));
						mHorder.put(CellSiteConstants.SHIPPER_DATE, (resultObj)
								.getString(CellSiteConstants.SHIPPER_DATE));
						mHorder.put(
								CellSiteConstants.HORDER_DESCRIPTION,
								(resultObj)
										.getString(CellSiteConstants.HORDER_DESCRIPTION));
						mHorder.put(
								CellSiteConstants.SHIPPER_USERNAME,
								(resultObj)
										.getString(CellSiteConstants.SHIPPER_USERNAME));

						JSONArray repliedDriversObj = null;
						try {

							repliedDriversObj = resultObj
									.getJSONArray(CellSiteConstants.REPLIED_DRIVERS);
							ArrayList<String> driverIdList = new ArrayList<String>();
							if (repliedDriversObj != null) {

								for (int j = 0; j < repliedDriversObj.length(); j++) {

									driverIdList
											.add(((JSONObject) repliedDriversObj
													.get(i))
													.getString(CellSiteConstants.DRIVER_ID));
								}
								mHorder.put(
										CellSiteConstants.REPLIED_DRIVER_LIST,
										driverIdList);
								mHorder.put(
										CellSiteConstants.REPLIED_DRIVER_COUNT,
										repliedDriversObj.length());
							}

						} catch (Exception e) {

						}

						// TODO :

						mHorderTypes[mCurrRadioIdx].nHorders.add(mHorder);
					} catch (Exception e) {
						mHasExceptionHorder = true;
						continue;
					}

				}

			}

		} catch (JSONException e) {
			Log.d(TAG, "JSONException" + e.toString());

		}

	}
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				moveTaskToBack(false);
				finish();
			}
			return true;

		}
		return super.onKeyDown(keyCode, event);

	}

}
