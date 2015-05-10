package com.abc.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.abc.driver.CityDialog.InputListener;
import com.abc.driver.cache.HorderType;
import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteApplication;
import com.abc.driver.utility.CellSiteConstants;
import com.abc.driver.utility.CityDBReader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class FHorderFragment extends Fragment {

	private static final String TAG = "FHorderFragment";

	PullToRefreshListView mFHorderLv;
	HorderType mFHorderTypes;
	ViewGroup mFHorderMore;
	TextView mFHolderMoreTv;
	boolean isForceRefreshFH = false;
	Boolean mHasExceptionFHorder = false;
	int mLvHistoryPosFH = 0;

	ProgressDialog mFHProgressdialog;
	FHorderDownLoadTask mFHorderDownLoadTask;

	CellSiteApplication app;

	TextView mSSAtv, mSCAtv;
	String mShipperAddressCode, mConsigneeAddressCode, mShipperAddressCode_Old,
			mConsigneeAddressCode_Old;

	CityDialog mCityDialog = null;
	InputListener listener1;
	CityChooseListener cityChooseListener1;

	InputListener listener2;
	CityChooseListener cityChooseListener2;

	private boolean isViewShown;
	private boolean isPrepared;

	public static FHorderFragment newInstance() {
		FHorderFragment mHCFragment = new FHorderFragment();
		return mHCFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		app = (CellSiteApplication) this.getActivity().getApplication();
		initChooseAddressListener();
		lazyLoad();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.main_tab_huoyun, container, false);

		isPrepared = true;
		return view;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (this.getView() != null) {

			isViewShown = true;

			// 相当于Fragment的onResume
		} else {
			isViewShown = false;
			// 相当于Fragment的onPause
		}
	}

	public void lazyLoad() {
		if (isViewShown && isPrepared) {
			Log.d(TAG, "lazyLoad");
			initFHorders();
		}
	}

	class FHorderDetailListener implements OnItemClickListener {
		public Context ctx;

		public FHorderDetailListener(Context _ctx) {
			ctx = _ctx;
		}

		@Override
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

			Intent intent = new Intent(ctx, HorderDetailActivity.class);
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

		}
	}

	public void initFHorders() {

		mFHorderTypes = new HorderType(0, this.getActivity(), ""
				+ app.getUser().getId(), new ReplyListener());
		mFHorderLv = (PullToRefreshListView) this.getView().findViewById(
				R.id.huoyun_lv);
		mFHorderLv.setMode(Mode.BOTH);

		mFHorderMore = (ViewGroup) LayoutInflater.from(this.getActivity())
				.inflate(R.layout.more_list, null);
		mFHorderMore.setVisibility(View.GONE);

		mFHolderMoreTv = (TextView) mFHorderMore.getChildAt(0);

		FHorderDetailListener mFHorderDetailListener = new FHorderDetailListener(
				this.getActivity());

		HorderRefreshListener mHorderRefreshListener = new HorderRefreshListener(
				this.getActivity());
		// mHorderLv.getRefreshableView().addFooterView(mHorderMore);
		mFHorderLv.setOnItemClickListener(mFHorderDetailListener);
		mFHorderLv.setAdapter(mFHorderTypes.nHorderAdapter);
		// Set a listener to be invoked when the list should be refreshed.

		mFHorderLv.setOnRefreshListener(mHorderRefreshListener);
		mFHorderDownLoadTask = new FHorderDownLoadTask();
		mFHorderDownLoadTask.execute(CellSiteConstants.NORMAL_OPERATION);
		if (mFHProgressdialog == null || !mFHProgressdialog.isShowing()) {
			mFHProgressdialog = new ProgressDialog(this.getActivity());
			mFHProgressdialog.setMessage("正在加载数据2");
			mFHProgressdialog.setIndeterminate(true);
			mFHProgressdialog.setCancelable(true);
			mFHProgressdialog.show();
		}

	}

	class HorderRefreshListener implements OnRefreshListener2<ListView> {
		public Context ctx;

		public HorderRefreshListener(Context _ctx) {
			ctx = _ctx;
		}

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			isForceRefreshFH = true;

			String label = DateUtils.formatDateTime(ctx,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);// 加上时间

			// mHorderTypes[mCurrRadioIdx] = new HorderType(mCurrRadioIdx);

			mFHorderDownLoadTask = new FHorderDownLoadTask();
			mFHorderDownLoadTask.execute(CellSiteConstants.MORE_OPERATION);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			isForceRefreshFH = true;
			// mHorderTypes[mCurrRadioIdx] = new HorderType(mCurrRadioIdx);
			String label = DateUtils.formatDateTime(ctx,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);// 加上时间

			mFHorderDownLoadTask = new FHorderDownLoadTask();
			mFHorderDownLoadTask.execute(CellSiteConstants.MORE_OPERATION);
		}
	}

	class FHorderDownLoadTask extends AsyncTask<Integer, String, String> {
		static final String TAG_FAIL = "FAIL";
		static final String TAG_SUCC = "SUCCESS";
		static final String TAG_RETURN = "RETURN";

		List<HashMap<String, String>> nTmpNewsData;

		boolean IsShipperAddressChanged = false;
		boolean IsConsigneeAddressChanged = false;

		@Override
		protected String doInBackground(Integer... params) {
			int moreOperation = params[0];

			// 检查是否是重新设置了筛选条件，如果是的话，则清空后，重新设置
			if (mShipperAddressCode_Old == null) {
				mShipperAddressCode_Old = mShipperAddressCode;
				IsShipperAddressChanged = true;
			} else {
				if (mShipperAddressCode_Old.equals(mShipperAddressCode)) {
					IsShipperAddressChanged = false;
				} else {
					IsShipperAddressChanged = true;
				}
			}
			if (mConsigneeAddressCode_Old == null) {
				mConsigneeAddressCode_Old = mConsigneeAddressCode;
				IsConsigneeAddressChanged = true;
			} else {
				if (mConsigneeAddressCode_Old.equals(mConsigneeAddressCode)) {
					IsConsigneeAddressChanged = false;
				} else {
					IsConsigneeAddressChanged = true;
				}
			}

			if (IsShipperAddressChanged || IsConsigneeAddressChanged) {
				app.setFHorderTypeCache(null); // 清空条件
				if (mFHorderTypes.nHorders != null) {
					mFHorderTypes.nHorders.clear();
				}
				mFHorderTypes.nDisplayNum = 0;
			} else {
				Log.d(TAG, "nothing changed");
				// return TAG_RETURN;
			}

			try {
				if (isForceRefreshFH
						|| moreOperation == CellSiteConstants.MORE_OPERATION
						|| app.getFHorderTypeCache() == null) {
					getFHorder();
					if (mFHorderTypes.nHorders.size() < mFHorderTypes.nDisplayNum
							+ CellSiteConstants.PAGE_COUNT
							&& !mHasExceptionFHorder) {
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

				mFHorderTypes.nHorderAdapter.setHorders(mFHorderTypes.nHorders);
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

						CityDBReader dbReader = new CityDBReader(this
								.getActivity().getApplicationContext());
						mHorder.put(
								CellSiteConstants.SHIPPER_ADDRESS_NAME,
								dbReader.getNameFromCode((resultObj)
										.getString(CellSiteConstants.SHIPPER_ADDRESS_CODE)));
						mHorder.put(
								CellSiteConstants.CONSIGNEE_ADDRESS_NAME,
								dbReader.getNameFromCode((resultObj)
										.getString(CellSiteConstants.CONSIGNEE_ADDRESS_CODE)));
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

			if (mFHProgressdialog != null) {
				mFHProgressdialog.cancel();
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

	public void initChooseAddressListener() {

		mSSAtv = (TextView) this.getView().findViewById(
				R.id.select_shipper_address_tv);
		mSCAtv = (TextView) this.getView().findViewById(
				R.id.select_consignee_address_tv);

		listener1 = new InputListener() {
			@Override
			public void getText(String str, String str2) {
				// TODO Auto-generated method stub
				mSSAtv.setText(str);
				mShipperAddressCode = str2;

			}
		};
		cityChooseListener1 = new CityChooseListener(this.getActivity(),
				listener1);
		mSSAtv.setOnClickListener(cityChooseListener1);

		listener2 = new InputListener() {
			@Override
			public void getText(String str, String str2) {
				// TODO Auto-generated method stub
				mSCAtv.setText(str);
				mConsigneeAddressCode = str2;
				// 重新根据城市筛选
				FHorderDownLoadTask mFHorderDownLoadTask = new FHorderDownLoadTask();
				mFHorderDownLoadTask
						.execute(CellSiteConstants.NORMAL_OPERATION);

			}
		};
		cityChooseListener2 = new CityChooseListener(this.getActivity(),
				listener2);
		mSCAtv.setOnClickListener(cityChooseListener2);
	}

	class CityChooseListener implements View.OnClickListener {

		public Context ctx;
		private InputListener listener;

		public CityChooseListener(Context _ctx, InputListener _listener) {
			this.ctx = _ctx;
			this.listener = _listener;
		}

		@Override
		public void onClick(View v) {
			mCityDialog = new CityDialog(ctx, listener);
			mCityDialog.setTitle(R.string.choose_address);
			mCityDialog.show();
		}
	}

}