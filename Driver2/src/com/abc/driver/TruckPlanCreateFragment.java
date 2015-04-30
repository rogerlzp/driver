package com.abc.driver;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.abc.driver.CityDialog.InputListener;
import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteApplication;
import com.abc.driver.utility.CellSiteConstants;

public class TruckPlanCreateFragment extends Fragment {

	CityDialog mCityDialog = null;
	InputListener listener1;
	CityChooseListener cityChooseListener1;

	InputListener listener2;
	CityChooseListener cityChooseListener2;

	private Button mCreateBtn;

	CellSiteApplication app;

	// 创建行车计划
	private String mTruckShipperAddressCode;
	private String mTruckConsigneeAddressCode;
	private String mTruckShipperDate;
	private String mTruckPlanDesc;
	private TextView mTSAtv;
	private TextView mTCAtv;
	private TextView mTSDtv;
	private EditText mTPDet;

	public static TruckPlanCreateFragment newInstance() {
		TruckPlanCreateFragment mHCFragment = new TruckPlanCreateFragment();
		return mHCFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (CellSiteApplication) this.getActivity().getApplication();

		// initData();
		initView();
		initChooseDate();

	}

	public void gotoMyhorder() {
		((MainFragmentActivity) this.getActivity()).gotoMyHorder();
	}

	public void initView() {
		mTPDet = (EditText) this.getView()
				.findViewById(R.id.truck_plan_desc_et);
		mCreateBtn = (Button) this.getView().findViewById(
				R.id.create_truck_plan_btn);

		mTSAtv = (TextView) this.getView().findViewById(
				R.id.truck_shipper_address_tv);
		mTCAtv = (TextView) this.getView().findViewById(
				R.id.truck_consignee_address_tv);

		CreateTruckPlanListener createListener = new CreateTruckPlanListener();
		mCreateBtn.setOnClickListener(createListener);

		listener1 = new InputListener() {
			@Override
			public void getText(String str, String str2) {
				// TODO Auto-generated method stub
				mTSAtv.setText(str);
				mTruckShipperAddressCode = str2;
			}
		};
		cityChooseListener1 = new CityChooseListener(this.getActivity(),
				listener1);
		mTSAtv.setOnClickListener(cityChooseListener1);

		listener2 = new InputListener() {
			@Override
			public void getText(String str, String str2) {
				// TODO Auto-generated method stub
				mTCAtv.setText(str);
				mTruckConsigneeAddressCode = str2;
			}
		};
		cityChooseListener2 = new CityChooseListener(this.getActivity(),
				listener2);
		mTCAtv.setOnClickListener(cityChooseListener2);

		mTSDtv = (TextView) this.getView().findViewById(R.id.truck_shipper_date_tv);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_truckplan, container,
				false);

		return view;
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

	class CreateTruckPlanListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			CreateTruckPlanTask mCreateTruckPlanTask = new CreateTruckPlanTask();
			mCreateTruckPlanTask.execute(mTruckShipperAddressCode,
					mTruckShipperDate, mTruckConsigneeAddressCode,
					mTruckPlanDesc, "" + app.getUser().getId(), ""
							+ app.getUser().getMyTruck().getTruckId());
		}

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

	public void initChooseDate() {
		
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
		DateChooseListener mDateChooseListener = new DateChooseListener(
				this.getActivity(), dateListener);
		mTSDtv.setOnClickListener(mDateChooseListener);

	}

	class DateChooseListener implements View.OnClickListener {

		public Context ctx;
		private DatePickerDialog.OnDateSetListener dateListener;
		Calendar calendar = Calendar.getInstance();

		public DateChooseListener(Context _ctx,
				DatePickerDialog.OnDateSetListener _dateListener) {
			this.ctx = _ctx;
			this.dateListener = _dateListener;
		}

		@Override
		public void onClick(View v) {
			DatePickerDialog dialog = new DatePickerDialog(ctx, dateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			dialog.show();
		}
	}

}
