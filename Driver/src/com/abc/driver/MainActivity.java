package com.abc.driver;

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
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.driver.CityDialog.InputListener;
import com.abc.driver.cache.HorderType;
import com.abc.driver.cache.WorkHorderType;
import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteConstants;
import com.abc.driver.utility.CityDBReader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainActivity extends BaseActivity {

	public static final String TAG = MainActivity.class.getSimpleName();
	private TextView mSAtv;
	private TextView mCAtv;
	private String mShipperAddressCode = "";
	private String mConsigneeAddressCode = "";
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
	private TextView mTTtv;
	private TextView mTLtv;

	public String phoneNum;
	HashMap<String, Object> mHorderData;

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

	private GetTruckTask mGetTruckTask;

	ReplyHorderTask mReplyHorderTask;

	// 创建行车计划
	private String mTruckShipperAddressCode;
	private String mTruckConsigneeAddressCode;
	private String mTruckShipperDate;
	private String mTruckPlanDesc;
	private TextView mTSAtv;
	private TextView mTCAtv;
	private TextView mTSDtv;
	private EditText mTPDet;

	public void initTruckPlanView() {
		mTPDet = (EditText) findViewById(R.id.truck_plan_desc_et);
	}

	//
	// h货单
	PullToRefreshListView mHorderLv;
	private TextView mSSAtv; // 选择出发地点
	private TextView mSCAtv; // 选择目标地点
	WorkHorderType[] mHorderTypes = new WorkHorderType[3];
	// static final int PAGE_COUNT = 2; // 每页多少个horder
	ViewGroup mPartyMore;
	TextView mMoreTv;
	boolean isForceRefresh = false;
	Boolean mHasExceptionHorder = false;
	int mLvHistoryPos = 0;

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
					mMoreTv.setText(R.string.hasShowAll);
				} else {
					// mLvHistoryPos = mHorderLv.getFirstVisiblePosition();
					mHorderDownLoadTask = new HorderDownLoadTask();
					mHorderDownLoadTask
							.execute(CellSiteConstants.MORE_OPERATION);
				}
				return;
			}

			String partyId = (String) aHorders.get(position).get("party_id");
			// boolean hasJoined = (Boolean) parties.get(position).get(
			// "has_joined");

			Intent intent = new Intent(MainActivity.this,
					HorderDetailActivity.class);
			startActivity(intent);
			// intent.putExtra(PartyActivity.PARTY_ID, );

		}
	};

	// 找货
	// h货单
	PullToRefreshListView mFHorderLv;
	HorderType mFHorderTypes;
	ViewGroup mFHorderMore;
	TextView mFHolderMoreTv;
	boolean isForceRefreshFH = false;
	Boolean mHasExceptionFHorder = false;
	int mLvHistoryPosFH = 0;

	ProgressDialog mFHProgressdialog;
	FHorderDownLoadTask mFHorderDownLoadTask;

	OnItemClickListener mFHorderDetailListener = new OnItemClickListener() {
		// / @Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ArrayList<HashMap<String, Object>> aHorders = mFHorderTypes.nHorders;

			position--; // for the header view has taken the first position, so
						// all the data view must decrease 1.

			if (position == aHorders.size()) {
				if (mFHorderTypes.hasShowAllHorders) {
					mFHolderMoreTv.setText(R.string.hasShowAll);
				} else {
					// mLvHistoryPosFH = mFHorderLv.getFirstVisiblePosition();
					mFHorderDownLoadTask = new FHorderDownLoadTask();
					mFHorderDownLoadTask
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
					CellSiteConstants.IS_DRIVER_REPLIED,
					(Integer) aHorders.get(position).get(
							CellSiteConstants.IS_DRIVER_REPLIED));
			intent.putExtra(
					CellSiteConstants.SHIPPER_USERNAME,
					(String) aHorders.get(position).get(
							CellSiteConstants.SHIPPER_USERNAME));
			intent.putExtra(
					CellSiteConstants.HORDER_DESCRIPTION,
					(String) aHorders.get(position).get(
							CellSiteConstants.HORDER_DESCRIPTION));
			startActivity(intent);
			// intent.putExtra(PartyActivity.PARTY_ID, );

		}
	};

	public void initFHorders() {

		mSSAtv = (TextView) findViewById(R.id.select_shipper_address_tv);
		mSCAtv = (TextView) findViewById(R.id.select_consignee_address_tv);

		mFHorderTypes = new HorderType(0, MainActivity.this, ""
				+ app.getUser().getId(), new ReplyListener());
		mFHorderLv = (PullToRefreshListView) findViewById(R.id.huoyun_lv);
		mFHorderLv.setMode(Mode.BOTH);

		mFHorderMore = (ViewGroup) LayoutInflater.from(MainActivity.this)
				.inflate(R.layout.more_list, null);
		mFHorderMore.setVisibility(View.GONE);

		mFHolderMoreTv = (TextView) mFHorderMore.getChildAt(0);

		// mFHorderLv.addFooterView(mFHorderMore);
		mFHorderLv.setOnItemClickListener(mFHorderDetailListener);
		mFHorderLv.setAdapter(mFHorderTypes.nHorderAdapter);
		// Set a listener to be invoked when the list should be refreshed.

		mFHorderLv.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isForceRefreshFH = true;

				String label = DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);// 加上时间

				// mHorderTypes[mCurrRadioIdx] = new HorderType(mCurrRadioIdx);
				mFHorderDownLoadTask = new FHorderDownLoadTask();
				mFHorderDownLoadTask
						.execute(CellSiteConstants.NORMAL_OPERATION);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isForceRefreshFH = true;
				// mHorderTypes[mCurrRadioIdx] = new HorderType(mCurrRadioIdx);
				String label = DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);// 加上时间

				mFHorderDownLoadTask = new FHorderDownLoadTask();
				mFHorderDownLoadTask
						.execute(CellSiteConstants.NORMAL_OPERATION);
			}

		});

	}

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
		// View view1 = mLi.inflate(R.layout.main_tab_horder_create, null);
		View view1 = mLi.inflate(R.layout.main_tab_truckplan, null);
		View view2 = mLi.inflate(R.layout.main_tab_huoyun, null);
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
	}

	public void initHorders() {

		for (int i = 0; i < 3; i++) {
			mHorderTypes[i] = new WorkHorderType(i, MainActivity.this, ""
					+ app.getUser().getId());

		}

		mPartyMore = (ViewGroup) LayoutInflater.from(MainActivity.this)
				.inflate(R.layout.more_list, null);
		mPartyMore.setVisibility(View.GONE);

		mMoreTv = (TextView) mPartyMore.getChildAt(0);

		// mHorderLv.addFooterView(mPartyMore);
		mHorderLv.setOnItemClickListener(mHorderDetailListener);
		mHorderLv.setAdapter(mHorderTypes[mCurrRadioIdx].nHorderAdapter);
		// Set a listener to be invoked when the list should be refreshed.

		mHorderLv.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isForceRefresh = true;

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
				isForceRefresh = true;
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

		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("TITLE", "Test Title");
			map.put("TWEIGHT", "TW1");
			mTruckWeightList.add(map);
		}

		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("PIC", R.drawable.ic_launcher);
			map.put("TITLE", "Test Title");
			map.put("CTYPE", "CT1");
			mCargoTypeList.add(map);
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
				initTruckPlanView();
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
				initFHorders();

				if (mFHProgressdialog == null || !mFHProgressdialog.isShowing()) {
					mFHProgressdialog = new ProgressDialog(MainActivity.this);
					mFHProgressdialog.setMessage("正在加载数据");
					mFHProgressdialog.setIndeterminate(true);
					mFHProgressdialog.setCancelable(true);
					mFHProgressdialog.show();
				}

				mFHorderDownLoadTask = new FHorderDownLoadTask();
				mFHorderDownLoadTask
						.execute(CellSiteConstants.NORMAL_OPERATION);
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
				// download user information
				initMeView();
				// download Truck information
				mGetTruckTask = new GetTruckTask();
				mGetTruckTask.execute("" + app.getUser().getId());

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

	private class GetTruckTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return getTruckTask(params[0]);
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

		protected Integer getTruckTask(String _userId) {

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
					app.getUser()
							.getMyTruck()
							.setPhotoImageUrl(
									truckObj.get(
											CellSiteConstants.TRUCK_PHOTO_URL)
											.toString());
					app.getUser()
							.getMyTruck()
							.setMobileNum(
									truckObj.get(
											CellSiteConstants.TRUCK_MOBILE_NUM)
											.toString());
					app.getUser()
							.getMyTruck()
							.setLicense(
									truckObj.get(
											CellSiteConstants.TRUCK_LICENSE)
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

	/**
	 * 根据view的id 来选择地点
	 * 
	 * @param v
	 */
	public void chooseAddress(View v) {
		mSSAtv = (TextView) findViewById(R.id.select_shipper_address_tv);
		mSCAtv = (TextView) findViewById(R.id.select_consignee_address_tv);

		if (v.getId() == R.id.select_shipper_address_tv) {

			InputListener listener = new InputListener() {

				@Override
				public void getText(String str, String str2) {
					// TODO Auto-generated method stub
					mSSAtv.setText(str);
					mShipperAddressCode = str2;
				}
			};
			mCityDialog = new CityDialog(MainActivity.this, listener);
			mCityDialog.setTitle("选择地址");
			mCityDialog.show();

		} else if (v.getId() == R.id.select_consignee_address_tv) {
			InputListener listener = new InputListener() {

				@Override
				public void getText(String str, String str2) {
					// TODO Auto-generated method stub
					mSCAtv.setText(str);
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
		SimpleAdapter adapter = new SimpleAdapter(this, mCargoTypeList,
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
		// mPortaritBitmap
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
		mHorderLv = (PullToRefreshListView) findViewById(R.id.myPartyLv);
		initHorders();

		if (v.getId() == R.id.waiting_ll) {
			mCurrRadioIdx = 0;
		} else if (v.getId() == R.id.sent_ll) {
			mCurrRadioIdx = 1;
		} else if (v.getId() == R.id.history_ll) {
			mCurrRadioIdx = 2;
		}

		mPartyMore.setVisibility(View.INVISIBLE);
		mMoreTv.setText(R.string.show_more);

		if (mProgressdialog == null || !mProgressdialog.isShowing()) {
			mProgressdialog = new ProgressDialog(MainActivity.this);
			mProgressdialog.setMessage("正在加载数据");
			mProgressdialog.setIndeterminate(true);
			mProgressdialog.setCancelable(true);
			mProgressdialog.show();
		}

		mHorderLv.setAdapter(mHorderTypes[mCurrRadioIdx].nHorderAdapter);

		mHorderDownLoadTask = new HorderDownLoadTask();
		mHorderDownLoadTask.execute(CellSiteConstants.NORMAL_OPERATION);
	}

	// 从服务器端下载 horder

	class HorderDownLoadTask extends AsyncTask<Integer, String, String> {
		static final String TAG_FAIL = "FAIL";
		static final String TAG_SUCC = "SUCCESS";

		List<HashMap<String, String>> nTmpNewsData;

		@Override
		protected String doInBackground(Integer... params) {
			int moreOperation = params[0];

			try {
				if (isForceRefresh
						|| moreOperation == CellSiteConstants.MORE_OPERATION
						|| app.getWorkHorderTypeCache(mCurrRadioIdx) == null) {
					Log.d(TAG,
							"Will connect the network and download the parties");
					getHorder(mCurrRadioIdx);

					if (mHorderTypes[mCurrRadioIdx].nHorders.size() < mHorderTypes[mCurrRadioIdx].nDisplayNum
							+ CellSiteConstants.PAGE_COUNT
							&& !mHasExceptionHorder) {
						mHorderTypes[mCurrRadioIdx].hasShowAllHorders = true;
					}
					Log.d(TAG, "after download the parties");
				} else {
					Log.d(TAG, "Will use the cache, current radio index "
							+ mCurrRadioIdx);
					;

					mHorderTypes[mCurrRadioIdx] = app
							.getWorkHorderTypeCache(mCurrRadioIdx);
					Log.d(TAG, "++++++++++++++++Number of My Parties :"
							+ mHorderTypes[mCurrRadioIdx].nHorders.size());
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

			Log.d(TAG, "PartyDownLoadTask onPostExecute()");
			if (!this.isCancelled()) {
				if (mProgressdialog != null) {
					Log.d(TAG, "Cancel the progress dialog");
					mProgressdialog.cancel();
				}

				if (isForceRefresh) {
					isForceRefresh = false;
					mHorderLv.onRefreshComplete();
				}
				mHorderTypes[mCurrRadioIdx].nHorderAdapter
						.setParties(mHorderTypes[mCurrRadioIdx].nHorders);
				mHorderLv
						.setAdapter(mHorderTypes[mCurrRadioIdx].nHorderAdapter);
				mHorderTypes[mCurrRadioIdx].nHorderAdapter
						.notifyDataSetChanged();

				mPartyMore.setVisibility(View.VISIBLE);

				if (mHorderTypes[mCurrRadioIdx].hasShowAllHorders) {
					mMoreTv.setText(R.string.hasShowAll);
				} else {
					mMoreTv.setText(R.string.show_more);
				}

				mHorderTypes[mCurrRadioIdx].nDisplayNum = mHorderTypes[mCurrRadioIdx].nHorders
						.size();

				app.setWorkHorderTypeCache(mHorderTypes[mCurrRadioIdx],
						mCurrRadioIdx);

				if (mHorderTypes[mCurrRadioIdx].nDisplayNum > 0) {
					mMoreTv.setVisibility(View.VISIBLE);
				} else {
					mMoreTv.setVisibility(View.INVISIBLE);
				}
				if (mLvHistoryPos > 0) {
					// mHorderLv.setSelectionFromTop(mLvHistoryPos, 0);
					mLvHistoryPos = 0;
				}

			}

		}
	}

	public JSONObject getHorder(Integer horder_status) {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

		postParameters.add(new BasicNameValuePair(CellSiteConstants.DRIVER_ID,
				"" + app.getUser().getId()));
		postParameters.add(new BasicNameValuePair(
				CellSiteConstants.HORDER_STATUS, "" + horder_status));

		postParameters.add(new BasicNameValuePair("offset", String
				.valueOf(mHorderTypes[mCurrRadioIdx].nDisplayNum)));
		postParameters.add(new BasicNameValuePair("pagecount", String
				.valueOf(CellSiteConstants.PAGE_COUNT)));
		JSONObject response = null;
		try {
			response = CellSiteHttpClient
					.executeHttpPost(
							CellSiteConstants.GET_HORDER_FOR_DRIVER_URL,
							postParameters);
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
	 * 根据得到的结果来解析 返回结果为
	 * {"result_code":"0","horders":[{"id":6,"shipper_username":
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
				if (results.length() < CellSiteConstants.PAGE_COUNT) {
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
						mHorder.put("horder_id",
								(resultObj).getString(CellSiteConstants.ID));
						// TODO :    完善其他属性
						int counter = 0;

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

	public void createTruckplan(View v) {

		CreateTruckPlanTask mCreateTruckPlanTask = new CreateTruckPlanTask();

		Log.d(TAG, "user id:" + app.getUser().getId());

		mCreateTruckPlanTask.execute(mTruckShipperAddressCode,
				mTruckShipperDate, mTruckConsigneeAddressCode, mTruckPlanDesc,
				"" + app.getUser().getId(), ""
						+ app.getUser().getMyTruck().getTruckId());

	}

	/**
	 * 根据view的id 来选择地点
	 * 
	 * @param v
	 */
	public void chooseAddress2(View v) {
		mTSAtv = (TextView) findViewById(R.id.truck_shipper_address_tv);
		mTCAtv = (TextView) findViewById(R.id.truck_consignee_address_tv);

		if (v.getId() == R.id.truck_shipper_address
				|| v.getId() == R.id.truck_shipper_address_btn) {

			InputListener listener = new InputListener() {

				@Override
				public void getText(String str, String str2) {
					// TODO Auto-generated method stub
					mTSAtv.setText(str);
					mTruckShipperAddressCode = str2;
				}
			};
			mCityDialog = new CityDialog(MainActivity.this, listener);
			mCityDialog.setTitle("选择地址");
			mCityDialog.show();

		} else if (v.getId() == R.id.truck_consignee_address_btn
				|| v.getId() == R.id.truck_consignee_address_tv) {
			InputListener listener = new InputListener() {

				@Override
				public void getText(String str, String str2) {
					// TODO Auto-generated method stub
					mTCAtv.setText(str);
					mTruckConsigneeAddressCode = str2;
				}
			};
			mCityDialog = new CityDialog(MainActivity.this, listener);
			mCityDialog.setTitle("选择地址");
			mCityDialog.show();

		}
	}

	public void chooseDate2(View v) {
		mTSDtv = (TextView) findViewById(R.id.truck_shipper_date_tv);
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

				mTruckShipperDate = year + "-" + tmpMonthOfYear + "-"
						+ tmpDayofMonth;
				mTSDtv.setText(mTruckShipperDate);
			}
		};
		dialog = new DatePickerDialog(this, dateListener,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	private class CreateTruckPlanTask extends
			AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return createTruckPlanTask(params[0], params[1], params[2],
					params[3], params[4], params[5]);
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

		protected Integer createTruckPlanTask(String _shipperAddressCode,
				String _shipperDate, String _consigneeAddressCode,
				String _truckDesc, String _userId, String _truckId) {

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
					CellSiteConstants.TRUCK_PLAN_DESCRIPTION, _truckDesc));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.USER_ID, _userId));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_ID, _truckId));

			JSONObject response = null;
			try {
				response = CellSiteHttpClient
						.executeHttpPost(
								CellSiteConstants.CREATE_TRUCK_PLAN_URL,
								postParameters);

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

	class FHorderDownLoadTask extends AsyncTask<Integer, String, String> {
		static final String TAG_FAIL = "FAIL";
		static final String TAG_SUCC = "SUCCESS";

		List<HashMap<String, String>> nTmpNewsData;

		@Override
		protected String doInBackground(Integer... params) {
			int moreOperation = params[0];

			try {
				if (isForceRefreshFH
						|| moreOperation == CellSiteConstants.MORE_OPERATION
						|| app.getFHorderTypeCache() == null) {
					getFHorder();
					if (mFHorderTypes.nHorders.size() < mFHorderTypes.nDisplayNum
							+ CellSiteConstants.PAGE_COUNT
							&& !mHasExceptionHorder) {
						mFHorderTypes.hasShowAllHorders = true;
					}
				} else {
					mFHorderTypes = app.getFHorderTypeCache();
				}

			} catch (Exception e) {
				e.printStackTrace();
				return TAG_FAIL;
			}
			return TAG_SUCC;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			Log.d(TAG, "FHorderDownLoadTask onPostExecute()");
			if (!this.isCancelled()) {
				if (mFHProgressdialog != null) {
					Log.d(TAG, "Cancel the progress dialog");
					mFHProgressdialog.cancel();
				}

				if (isForceRefreshFH) {
					isForceRefreshFH = false;
					mFHorderLv.onRefreshComplete();
				}
				mFHorderTypes.nHorderAdapter.setParties(mFHorderTypes.nHorders);
				mFHorderLv.setAdapter(mFHorderTypes.nHorderAdapter);
				mFHorderTypes.nHorderAdapter.notifyDataSetChanged();

				mFHorderMore.setVisibility(View.VISIBLE);

				if (mFHorderTypes.hasShowAllHorders) {
					mFHolderMoreTv.setText(R.string.hasShowAll);

				} else {
					mFHolderMoreTv.setText(R.string.show_more);

				}

				mFHorderTypes.nDisplayNum = mFHorderTypes.nHorders.size();
				// TODO
				app.setFHorderTypeCache(mFHorderTypes);

				if (mFHorderTypes.nDisplayNum > 0) {
					mFHolderMoreTv.setVisibility(View.VISIBLE);
				} else {
					mFHolderMoreTv.setVisibility(View.INVISIBLE);
				}
				if (mLvHistoryPosFH > 0) {
					// mFHorderLv.setSelectionFromTop(mLvHistoryPosFH, 0);
					mLvHistoryPosFH = 0;
				}

			}

		}
	}

	public JSONObject getFHorder() {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

		postParameters.add(new BasicNameValuePair(CellSiteConstants.DRIVER_ID,
				"" + app.getUser().getId()));

		postParameters.add(new BasicNameValuePair(
				CellSiteConstants.SHIPPER_ADDRESS_CODE, mShipperAddressCode));
		postParameters
				.add(new BasicNameValuePair(
						CellSiteConstants.CONSIGNEE_ADDRESS_CODE,
						mConsigneeAddressCode));

		postParameters.add(new BasicNameValuePair("offset", String
				.valueOf(mFHorderTypes.nDisplayNum)));
		postParameters.add(new BasicNameValuePair("pagecount", String
				.valueOf(CellSiteConstants.PAGE_COUNT)));
		JSONObject response = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(
					CellSiteConstants.GET_NEW_HORDER_URL, postParameters);
			int resultCode = response.getInt(CellSiteConstants.RESULT_CODE);
			if (CellSiteConstants.RESULT_SUC == resultCode) {
				parseFHorderJson(response);
			} else {
				Log.d(TAG, "QUERY RESULT FAILED");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * 根据得到的结果来解析 返回结果为
	 * {"result_code":"0","horders":[{"id":6,"shipper_username":
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
	public void parseFHorderJson(JSONObject jsonResult) {
		HashMap<String, Object> mHorder;
		try {
			if (jsonResult.get("horders") != JSONObject.NULL) {
				JSONArray results = jsonResult.getJSONArray("horders");
				if (results.length() < CellSiteConstants.PAGE_COUNT) {
					mFHorderTypes.hasShowAllHorders = true;
				}

				for (int i = 0; i < results.length(); i++) {
					try {
						JSONObject resultObj = (JSONObject) results.get(i);
						mHorder = new HashMap<String, Object>();

						mHorder.put(
								CellSiteConstants.IS_DRIVER_REPLIED,
								(Integer) resultObj
										.get(CellSiteConstants.IS_DRIVER_REPLIED));
						mHorder.put(
								CellSiteConstants.REPLIED_DRIVERS_COUNT,
								(Integer) resultObj
										.get(CellSiteConstants.REPLIED_DRIVERS_COUNT));

						mHorder.put(
								CellSiteConstants.SHIPPER_USERNAME,
								resultObj
										.getString(CellSiteConstants.SHIPPER_USERNAME));
						mHorder.put(CellSiteConstants.HORDER_ID,
								(resultObj).getString(CellSiteConstants.ID));
						mHorder.put(
								CellSiteConstants.SHIPPER_PHONE,
								(resultObj)
										.getString(CellSiteConstants.SHIPPER_PHONE));
						mHorder.put(CellSiteConstants.USER_ID, (resultObj)
								.getString(CellSiteConstants.USER_ID));

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
						int counter = 0;

						mFHorderTypes.nHorders.add(mHorder);
					} catch (Exception e) {
						e.printStackTrace();
						mHasExceptionFHorder = true;
						continue;
					}

				}

			}

		} catch (JSONException e) {
			Log.d(TAG, "JSONException" + e.toString());

		}

	}

	int replyHorder(String _horderId) {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(CellSiteConstants.HORDER_ID,
				_horderId));
		postParameters.add(new BasicNameValuePair(CellSiteConstants.DRIVER_ID,
				"" + app.getUser().getId()));

		JSONObject response = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(
					CellSiteConstants.REQUEST_HODER_URL, postParameters);

			int resultCode = Integer.parseInt(response.get(
					CellSiteConstants.RESULT_CODE).toString());
			Log.d(TAG, "ResultCode = " + resultCode);
			if (CellSiteConstants.RESULT_SUC == resultCode) {

				// app.startToSearchLoc();
			} else if (resultCode == CellSiteConstants.REGISTER_USER_EXISTS) {
				// 用户名已经被注册

			}
			return resultCode;
		} catch (Exception e) {
			Log.d(TAG, "Register by mail fails." + e.getMessage());
			return CellSiteConstants.UNKNOWN_ERROR;
		}
	}

	public class ReplyHorderTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return replyHorder(params[0]);
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

			} else {

			}

		}
	}

	public class ReplyListener implements View.OnClickListener {
		private String horderId;

		public void setHorderId(String _horderId) {
			horderId = _horderId;
		}

		@Override
		public void onClick(View v) {
			ReplyHorderTask mReplyHorderTask = new ReplyHorderTask();
			mReplyHorderTask.execute(horderId);

		}
	}

}
