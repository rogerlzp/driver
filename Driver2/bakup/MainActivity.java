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
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.abc.huoyun.CargoWeightVolumeDialog.CargoWeightVolumeInputListener;
import com.abc.huoyun.CityDialog.InputListener;
import com.abc.huoyun.net.CellSiteHttpClient;
import com.abc.huoyun.utility.CellSiteConstants;
import com.abc.huoyun.utility.CityDBReader;
import com.abc.huoyun.view.PullToRefreshListView;
import com.abc.huoyun.view.PullToRefreshListView.OnRefreshListener;

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
	ArrayList<HashMap<String, Object>> mCargoTypeList = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> mTruckLengthList = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> mTruckWeightList = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> mTruckTypeList = new ArrayList<HashMap<String, Object>>();

	private TextView mSDtv;
	private TextView mCTtv;
	private TextView mCWVtv; // 货物重量和体积
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
	int mCurrRadioIdx = 0;

	private UpdateTruckTask mUpdateTruckTask;

	//
	// 货单
	PullToRefreshListView mHorderLv;
	HorderType[] mHorderTypes = new HorderType[3];
	ViewGroup mHorderMore;
	TextView mHorderMoreTv;
	boolean isForceRefreshHorder = false;
	Boolean mHasExceptionHorder = false;
	int mLvHistoryPosHorder = 0;

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
					mLvHistoryPosHorder = mHorderLv.getFirstVisiblePosition();
					mHorderDownLoadTask = new HorderDownLoadTask();
					mHorderDownLoadTask
							.execute(CellSiteConstants.MORE_OPERATION);
				}
				return;
			}

			Intent intent = new Intent(MainActivity.this,
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

	// 货车列表

	PullToRefreshListView mTruckLv;
	ViewGroup mTruckMore;
	TextView mTruckMoreTv;
	Trucks mTrucks = new Trucks();
	boolean isForceRefreshTruck = false;
	Boolean mHasExceptionTruck = false;
	int mLvHistoryPosTruck = 0;

	TruckDownLoadTask mTruckDownLoadTask;
	ProgressDialog mProgressdialogTruck;

	OnItemClickListener mTruckDetailListener = new OnItemClickListener() {
		// / @Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ArrayList<HashMap<String, Object>> aTrucks = mTrucks.nTrucks;

			position--; // for the header view has taken the first position, so
						// all the data view must decrease 1.

			if (position == aTrucks.size()) {
				if (mTrucks.hasShowAllTrucks) {
					mTruckMoreTv.setText(R.string.hasShowAll);
				} else {
					mLvHistoryPosTruck = mTruckLv.getFirstVisiblePosition();
					mTruckDownLoadTask = new TruckDownLoadTask();
					mTruckDownLoadTask
							.execute(CellSiteConstants.MORE_OPERATION);
				}
				return;
			}

			Intent intent = new Intent(MainActivity.this,
					TruckDetailActivity.class);

			intent.putExtra(
					CellSiteConstants.TRUCK_ID,
					(String) aTrucks.get(position).get(
							CellSiteConstants.TRUCK_ID));
			intent.putExtra(CellSiteConstants.TRUCK_LENGTH, (String) aTrucks
					.get(position).get(CellSiteConstants.TRUCK_LENGTH));
			intent.putExtra(
					CellSiteConstants.TRUCK_TYPE,
					(String) aTrucks.get(position).get(
							CellSiteConstants.TRUCK_TYPE));
			intent.putExtra(CellSiteConstants.TRUCK_IMAGE_URL, (String) aTrucks
					.get(position).get(CellSiteConstants.TRUCK_IMAGE_URL));
			intent.putExtra(
					CellSiteConstants.TRUCK_LICENSE_URL,
					(String) aTrucks.get(position).get(
							CellSiteConstants.TRUCK_LICENSE_URL));
			intent.putExtra(CellSiteConstants.TRUCK_LICENSE, (String) aTrucks
					.get(position).get(CellSiteConstants.TRUCK_LICENSE));
			intent.putExtra(
					CellSiteConstants.TRUCK_MOBILE_NUM,
					(String) aTrucks.get(position).get(
							CellSiteConstants.TRUCK_MOBILE_NUM));

			startActivity(intent);

		}
	};

	// 个人信息
	TextView mNameTv;
	TextView mMobileTv;
	ImageView mPortraitIv;
	DownloadImageTask mDownloadImageTask;

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
		View view2 = mLi.inflate(R.layout.main_tab_trucks, null);
		View view3 = mLi.inflate(R.layout.main_tab_horder, null);
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
		initCreateHorderView();
	}

	//
	public void initMeView() {
		mNameTv = (TextView) findViewById(R.id.name_tv);
		mMobileTv = (TextView) findViewById(R.id.mobile_tv);
		mPortraitIv = (ImageView) findViewById(R.id.portrait_iv);

		// init Data
		if (app.getUser().getName() != null) {
			mNameTv.setText(app.getUser().getName());
		}
		if (app.getUser().getMobileNum() != null) {
			mMobileTv.setText(app.getUser().getMobileNum());
		}
		setPortraitImage();
	}

	public void setPortraitImage() {

		String profileImageUrl = app.getUser().getProfileImageUrl();

		Log.d(TAG, "setPortraitImage");

		if (profileImageUrl == null || profileImageUrl.equalsIgnoreCase("null")) {

			mPortraitIv.setImageResource(R.drawable.ic_launcher); // TODO:
																	// 更新默认图片

		} else {
			mDownloadImageTask = new DownloadImageTask();
			mDownloadImageTask.execute(profileImageUrl, app.regUserPath);
		}
	}

	class DownloadImageTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			Log.d(TAG, "URL=" + (String) params[0]);
			app.setPortaritBitmap(app.downloadBmpByUrl((String) params[0],
					params[1]));
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			super.onPostExecute(result);
			if (!this.isCancelled()) {
				if (app.getPortaritBitmap() != null) {
					mPortraitIv.setImageDrawable(new BitmapDrawable(app
							.getPortaritBitmap()));
					// TODO
				}
			}
		}
	}

	public void initHorders() {

		mHorderLv = (PullToRefreshListView) findViewById(R.id.myPartyLv);

		mHorderMore = (ViewGroup) LayoutInflater.from(MainActivity.this)
				.inflate(R.layout.more_list, null);
		mHorderMore.setVisibility(View.GONE);

		mHorderMoreTv = (TextView) mHorderMore.getChildAt(0);

		mHorderLv.addFooterView(mHorderMore);
		mHorderLv.setOnItemClickListener(mHorderDetailListener);
		mHorderLv.setAdapter(mHorderTypes[mCurrRadioIdx].nHorderAdapter);
		// Set a listener to be invoked when the list should be refreshed.
		mHorderLv.setOnRefreshListener(new OnRefreshListener() {

			public void onRefresh() {
				// TODO Auto-generated method stub
				isForceRefreshHorder = true;
				mHorderTypes[mCurrRadioIdx] = new HorderType(mCurrRadioIdx);

				mHorderDownLoadTask = new HorderDownLoadTask();
				mHorderDownLoadTask.execute(CellSiteConstants.NORMAL_OPERATION);
			}

		});

	}

	public void initTrucks() {

		mTrucks = new Trucks();

		mTruckMore = (ViewGroup) LayoutInflater.from(MainActivity.this)
				.inflate(R.layout.more_list, null);
		mTruckMore.setVisibility(View.GONE);

		mTruckMoreTv = (TextView) mTruckMore.getChildAt(0);

		mTruckLv.addFooterView(mTruckMore);
		mTruckLv.setOnItemClickListener(mTruckDetailListener);
		mTruckLv.setAdapter(mTrucks.nTruckAdapter);
		// Set a listener to be invoked when the list should be refreshed.
		mTruckLv.setOnRefreshListener(new OnRefreshListener() {

			public void onRefresh() {
				// TODO Auto-generated method stub
				isForceRefreshTruck = true;
				mTrucks = new Trucks();

				mTruckDownLoadTask = new TruckDownLoadTask();
				mTruckDownLoadTask.execute(CellSiteConstants.NORMAL_OPERATION);
			}

		});

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

		mHorderTypes[0] = new HorderType(0);
		mHorderTypes[1] = new HorderType(1);
		mHorderTypes[2] = new HorderType(2);
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
				initCreateHorderView();
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

				mTruckLv = (PullToRefreshListView) findViewById(R.id.trucks_lv);
				initTrucks();
				mTruckMore.setVisibility(View.INVISIBLE);
				mTruckMoreTv.setText(R.string.show_more);

				if (mProgressdialogTruck == null
						|| !mProgressdialogTruck.isShowing()) {
					mProgressdialogTruck = new ProgressDialog(MainActivity.this);
					mProgressdialogTruck.setMessage("正在加载数据");
					mProgressdialogTruck.setIndeterminate(true);
					mProgressdialogTruck.setCancelable(true);
					mProgressdialogTruck.show();
				}

				mTruckLv.setAdapter(mTrucks.nTruckAdapter);

				mTruckDownLoadTask = new TruckDownLoadTask();
				mTruckDownLoadTask.execute(CellSiteConstants.NORMAL_OPERATION);
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_find_frd_pressed));
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

				initHorders();
				mHorderMore.setVisibility(View.INVISIBLE);
				mHorderMoreTv.setText(R.string.show_more);
				mHorderDownLoadTask = new HorderDownLoadTask();
				mHorderDownLoadTask.execute(CellSiteConstants.MORE_OPERATION);
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					needAnimation = false;
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					needAnimation = false;
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				}
				initMeView();
				// download Truck information
				mUpdateTruckTask = new UpdateTruckTask();
				mUpdateTruckTask.execute("" + app.getUser().getId());

				break;
			}
			currIndex = arg0;
			if (needAnimation) {
				animation.setFillAfter(true);// True:
				animation.setDuration(150);

				mTabImg.startAnimation(animation);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	// init view for crate horder
	public void initCreateHorderView() {
		mCWVtv = ((TextView) findViewById(R.id.cargo_weight_tv));
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
					JSONObject truckObj = (JSONObject) response
							.get(CellSiteConstants.TRUCK);
					app.getUser()
							.getMyTruck()
							.setTruckId(
									Integer.parseInt(truckObj.get(
											CellSiteConstants.ID).toString()));
					app.getUser()
							.getMyTruck()
							.setLengthId(
									Integer.parseInt(truckObj.get(
											CellSiteConstants.TRUCK_LENGTH)
											.toString()));
					app.getUser()
							.getMyTruck()
							.setTypeId(
									Integer.parseInt(truckObj.get(
											CellSiteConstants.TRUCK_TYPE)
											.toString()));
					app.getUser()
							.getMyTruck()
							.setAuditStatusId(
									Integer.parseInt(truckObj
											.get(CellSiteConstants.TRUCK_AUDIT_STATUS)
											.toString()));
					app.getUser()
							.getMyTruck()
							.setLicenseImageUrl(
									truckObj.get(
											CellSiteConstants.TRUCK_LICENSE_URL)
											.toString());
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
						Intent intent = new Intent();
						menuWindow.dismiss();
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
	 * 弹出对话框，输入重量和体积
	 * 
	 * @param v
	 */
	public void chooseCargoWeightVolume(View v) {
		mCWVtv = ((TextView) findViewById(R.id.cargo_weight_tv));

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
		CargoWeightVolumeDialog mCWVD = new CargoWeightVolumeDialog(
				MainActivity.this, listener);
		mCWVD.setTitle(R.string.cargo_weight_vloume);
		mCWVD.show();

	}

	/**
	 * 选择地址
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
			mCityDialog = new CityDialog(MainActivity.this, listener);
			mCityDialog.setTitle(R.string.choose_address);
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

	// get horders
	public void getHorders(View v) {

		if (v.getId() == R.id.waiting_ll) {
			mCurrRadioIdx = 0;
		} else if (v.getId() == R.id.sent_ll) {
			mCurrRadioIdx = 1;
		} else if (v.getId() == R.id.history_ll) {
			mCurrRadioIdx = 2;
		}

		mHorderMore.setVisibility(View.INVISIBLE);
		mHorderMoreTv.setText(R.string.show_more);

		if (mProgressdialog == null || !mProgressdialog.isShowing()) {
			mProgressdialog = new ProgressDialog(MainActivity.this);
			mProgressdialog.setMessage("正在加载数据");
			mProgressdialog.setIndeterminate(true);
			mProgressdialog.setCancelable(true);
			mProgressdialog.show();
		}

		mHorderDownLoadTask = new HorderDownLoadTask();
		mHorderDownLoadTask.execute(CellSiteConstants.NORMAL_OPERATION);

	}

	public class HorderType {
		int nIndex;
		public ArrayList<HashMap<String, Object>> nHorders;
		public HorderAdapter nHorderAdapter;

		int nDisplayNum;
		Boolean hasShowAllHorders;

		public HorderType(int aIndex) {
			nHorders = new ArrayList<HashMap<String, Object>>();
			hasShowAllHorders = false;
			nIndex = aIndex;
			nHorderAdapter = new HorderAdapter(MainActivity.this);
		}

	}

	class HorderAdapter extends BaseAdapter {
		public ArrayList<HashMap<String, Object>> nHorders = new ArrayList<HashMap<String, Object>>();

		public HorderAdapter(Context context) {
		}

		public void setHorders(ArrayList<HashMap<String, Object>> horders) {
			nHorders = horders;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = (ViewGroup) LayoutInflater
						.from(MainActivity.this).inflate(R.layout.horder_item,
								null);
				holder = new ViewHolder();
				holder.tv_horder_id = (TextView) convertView
						.findViewById(R.id.horder_id_tv);
				holder.tv_truck = (TextView) convertView
						.findViewById(R.id.truck_tv);
				holder.tv_cargo = (TextView) convertView
						.findViewById(R.id.cargo_tv);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.shipper_time_tv);
				holder.tv_location = (TextView) convertView
						.findViewById(R.id.location_tv);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			HashMap<String, Object> horderData = nHorders.get(position);

			holder.tv_horder_id.setText((String) horderData.get("horder_id"));
			holder.tv_location.setText((String) horderData
					.get(CellSiteConstants.SHIPPER_ADDRESS_NAME)
					+ "~"
					+ (String) horderData
							.get(CellSiteConstants.CONSIGNEE_ADDRESS_NAME));

			holder.tv_truck.setText(CellSiteConstants.TruckTypes[Integer
					.valueOf((String) horderData
							.get(CellSiteConstants.TRUCK_TYPE)) - 1]);

			int cargoVolumeValue = Integer.valueOf((String) horderData
					.get(CellSiteConstants.CARGO_VOLUME));
			int cargoWeightValue = Integer.valueOf((String) horderData
					.get(CellSiteConstants.CARGO_WEIGHT));
			String cargoVolume = cargoVolumeValue != 0 ? cargoVolumeValue + "方"
					: "";
			String cargoWeight = cargoWeightValue != 0 ? cargoWeightValue + "吨"
					: "";
			holder.tv_cargo.setText((CellSiteConstants.CargoTypes[Integer
					.valueOf((String) horderData
							.get(CellSiteConstants.CARGO_TYPE)) - 1])
					+ cargoWeight + cargoVolume);

			holder.tv_time.setText((String) horderData
					.get(CellSiteConstants.SHIPPER_DATE));

			return convertView;
		}

		private class ViewHolder {
			ImageView organizerPortrait;
			TextView tv_location;
			TextView tv_horder_id;
			TextView tv_distance;
			TextView tv_time;
			TextView tv_cargo;
			TextView tv_truck;
			ViewGroup progress;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return nHorders.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	// 查看 horder

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
					Log.d(TAG, "set more tv to visible" );
					
					mHorderMoreTv.setVisibility(View.VISIBLE);
				} else {
					Log.d(TAG, "set more tv to INVISIBLE" );
					mHorderMoreTv.setVisibility(View.INVISIBLE);
				}
				if (mLvHistoryPosHorder > 0) {
					mHorderLv.setSelectionFromTop(mLvHistoryPosHorder, 0);
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

	public class Trucks {
		public ArrayList<HashMap<String, Object>> nTrucks;
		public TruckAdapter nTruckAdapter;

		int nDisplayNum;
		Boolean hasShowAllTrucks;

		public Trucks() {
			nTrucks = new ArrayList<HashMap<String, Object>>();
			hasShowAllTrucks = false;
			nTruckAdapter = new TruckAdapter(MainActivity.this);
		}

	}

	class TruckAdapter extends BaseAdapter {
		public ArrayList<HashMap<String, Object>> nTrucks = new ArrayList<HashMap<String, Object>>();

		public TruckAdapter(Context context) {
		}

		public void setTrucks(ArrayList<HashMap<String, Object>> trucks) {
			nTrucks = trucks;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = (ViewGroup) LayoutInflater
						.from(MainActivity.this).inflate(R.layout.truck_item,
								null);
				holder = new ViewHolder();
				holder.tv_truck_id = (TextView) convertView
						.findViewById(R.id.truck_id_tv);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.name_tv);

				holder.iv_truck_phpoto = (ImageView) convertView
						.findViewById(R.id.truck_phpoto_iv);
				holder.tv_type = (TextView) convertView
						.findViewById(R.id.truck_tv);
				holder.tv_truck_location = (TextView) convertView
						.findViewById(R.id.truck_location_tv);
				holder.tv_contact = (TextView) convertView
						.findViewById(R.id.contact_tv);
				holder.tv_tuisong = (TextView) convertView
						.findViewById(R.id.tuisong_tv);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			HashMap<String, Object> truckData = nTrucks.get(position);
			holder.tv_name.setText("TODO");// TODO: add name

			holder.tv_truck_id.setText((String) truckData
					.get(CellSiteConstants.TRUCK_ID));
			holder.tv_type.setText((String) truckData
					.get(CellSiteConstants.TRUCK_TYPE)
					+ " "
					+ (String) truckData.get(CellSiteConstants.TRUCK_LENGTH));

			return convertView;
		}

		private class ViewHolder {
			TextView tv_truck_id;
			ImageView iv_truck_phpoto;
			TextView tv_name;
			TextView tv_type;
			TextView tv_truck_location;
			TextView tv_contact;
			TextView tv_tuisong;

		}

		public int getCount() {
			// TODO Auto-generated method stub
			return nTrucks.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	class TruckDownLoadTask extends AsyncTask<Integer, String, String> {
		static final String TAG_FAIL = "FAIL";
		static final String TAG_SUCC = "SUCCESS";

		List<HashMap<String, String>> nTmpNewsData;

		@Override
		protected String doInBackground(Integer... params) {
			int moreOperation = params[0];

			try {
				if (isForceRefreshTruck
						|| moreOperation == CellSiteConstants.MORE_OPERATION
						|| app.getTrucksCache() == null) {
					Log.d(TAG,
							"Will connect the network and download the parties");
					getTrucks();

					if (mTrucks.nTrucks.size() < mTrucks.nDisplayNum
							+ CellSiteConstants.PAGE_COUNT
							&& !mHasExceptionTruck) {
						mTrucks.hasShowAllTrucks = true;
					}

				} else {

					mTrucks = app.getTrucksCache();

				}

			} catch (Exception e) {
				e.printStackTrace();
				return TAG_FAIL;
			}

			Log.d(TAG, "after download the parties: TAG_SUCC");
			return TAG_SUCC;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!this.isCancelled()) {
				if (mProgressdialogTruck != null) {
					Log.d(TAG, "Cancel the progress truck dialog");
					mProgressdialogTruck.cancel();
				}

				if (isForceRefreshTruck) {
					isForceRefreshTruck = false;
					mTruckLv.onRefreshComplete();
				}
				mTrucks.nTruckAdapter.setTrucks(mTrucks.nTrucks);
				mTruckLv.setAdapter(mTrucks.nTruckAdapter);
				mTrucks.nTruckAdapter.notifyDataSetChanged();

				mTruckMore.setVisibility(View.VISIBLE);

				if (mTrucks.hasShowAllTrucks) {
					mTruckMoreTv.setText(R.string.hasShowAll);
				} else {
					mTruckMoreTv.setText(R.string.show_more);
				}

				mTrucks.nDisplayNum = mTrucks.nTrucks.size();

				app.setTrucksCache(mTrucks);

				if (mTrucks.nDisplayNum > 0) {
					mTruckMoreTv.setVisibility(View.VISIBLE);
				} else {
					mTruckMoreTv.setVisibility(View.INVISIBLE);
				}
				if (mLvHistoryPosTruck > 0) {
					mTruckLv.setSelectionFromTop(mLvHistoryPosTruck, 0);
					mLvHistoryPosTruck = 0;
				}

			}

		}
	}

	public JSONObject getTrucks() {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

		postParameters.add(new BasicNameValuePair(CellSiteConstants.USER_ID, ""
				+ app.getUser().getId()));

		postParameters.add(new BasicNameValuePair("offset", String
				.valueOf(mTrucks.nDisplayNum)));
		postParameters.add(new BasicNameValuePair("pagecount", String
				.valueOf(CellSiteConstants.PAGE_COUNT)));
		JSONObject response = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(
					CellSiteConstants.GET_TRUCKS_URL, postParameters);
			int resultCode = response.getInt(CellSiteConstants.RESULT_CODE);
			if (CellSiteConstants.RESULT_SUC == resultCode) {
				parseTruckJson(response);
			} else {
				Log.d(TAG, "QUERY RESULT FAILED");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Trucks result
	 * trucks:[{"id":8,"user_id":9,"tstatus_id":0,"taudit_status_id":0,
	 * "tweight_id"
	 * :0,"tlength_id":11,"ttype_id":8,"tmobile_num":"","tlicense":"",
	 * "tl_image_url":"\/img\/users\/upload\/9_8_1425992634.jpg",
	 * "tphoto_image_url":null,"created_at":"2015-03-10 13:03:55",
	 * "updated_at":"2015-03-10 13:03:55"}]
	 * 
	 * @param jsonResult
	 */
	public void parseTruckJson(JSONObject jsonResult) {
		HashMap<String, Object> mTruck;
		try {
			if (jsonResult.get("trucks") != JSONObject.NULL) {
				JSONArray results = jsonResult.getJSONArray("trucks");
				if (results.length() < CellSiteConstants.PAGE_COUNT) {
					mTrucks.hasShowAllTrucks = true;
				}

				for (int i = 0; i < results.length(); i++) {
					try {
						JSONObject resultObj = (JSONObject) results.get(i);
						mTruck = new HashMap<String, Object>();
						mTruck.put(CellSiteConstants.TRUCK_ID,
								resultObj.getString(CellSiteConstants.ID));
						mTruck.put(CellSiteConstants.TRUCK_LENGTH, (resultObj)
								.getString(CellSiteConstants.TRUCK_LENGTH));
						mTruck.put(CellSiteConstants.TRUCK_TYPE,
								(resultObj).getString("ttype_id"));
						mTruck.put(
								CellSiteConstants.TRUCK_IMAGE_URL,
								(resultObj)
										.getString(CellSiteConstants.TRUCK_IMAGE_URL));
						mTruck.put(
								CellSiteConstants.TRUCK_LICENSE_URL,
								(resultObj)
										.getString(CellSiteConstants.TRUCK_LICENSE_URL));
						mTruck.put(CellSiteConstants.TRUCK_LICENSE, (resultObj)
								.getString(CellSiteConstants.TRUCK_LICENSE));
						mTruck.put(
								CellSiteConstants.TRUCK_MOBILE_NUM,
								(resultObj)
										.getString(CellSiteConstants.TRUCK_MOBILE_NUM));
						// down load image

						mTrucks.nTrucks.add(mTruck);
					} catch (Exception e) {
						mHasExceptionTruck = true;
						continue;
					}

				}

			}

		} catch (JSONException e) {
			Log.d(TAG, "JSONException" + e.toString());

		}

	}

}
