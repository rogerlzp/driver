package com.abc.driver.cache;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.driver.MyHorderFragment.HorderArrivedListener;
import com.abc.driver.R;
import com.abc.driver.utility.CellSiteConstants;

public class WorkHorderAdapter extends BaseAdapter {
	public ArrayList<HashMap<String, Object>> nHorders = new ArrayList<HashMap<String, Object>>();
	public Context ctx;

	ContactListener mContactListener;
	HorderArrivedListener mHorderArrivedListener;

	public String currentUserId;

	public WorkHorderAdapter(Context context, String _currentUserId,
			HorderArrivedListener _mHorderArrivedListener) {
		this.ctx = context;
		this.currentUserId = _currentUserId;
		this.mHorderArrivedListener = _mHorderArrivedListener;

	}

	public void setHorders(ArrayList<HashMap<String, Object>> horders) {
		nHorders = horders;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(ctx).inflate(
					R.layout.working_horder_item, null);
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
			holder.tv_replied_driver = (TextView) convertView
					.findViewById(R.id.replied_driver_tv);
			holder.tv_contact = (TextView) convertView
					.findViewById(R.id.contact_tv);
			holder.tv_reply = (TextView) convertView
					.findViewById(R.id.replyHorder_tv);
			mContactListener = new ContactListener();
			holder.tv_contact.setOnClickListener(mContactListener);

			holder.tv_status = (TextView) convertView
					.findViewById(R.id.horder_status_tv);

			holder.tv_reply.setOnClickListener(mHorderArrivedListener);

			/*
			 * holder.progress = (ViewGroup) convertView
			 * .findViewById(R.id.progressLayout);
			 */

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HashMap<String, Object> horderData = nHorders.get(position);

		// holder.progress.setVisibility(View.INVISIBLE);

		holder.tv_horder_id.setText((String) horderData.get("horder_id"));

		mHorderArrivedListener.setHorderId((String) horderData.get("horder_id"));
		holder.tv_location.setText((String) horderData
				.get(CellSiteConstants.SHIPPER_ADDRESS_NAME)
				+ "~"
				+ (String) horderData
						.get(CellSiteConstants.CONSIGNEE_ADDRESS_NAME));

		holder.tv_truck
				.setText(CellSiteConstants.TruckTypes[Integer
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
		holder.tv_cargo
				.setText((CellSiteConstants.CargoTypes[Integer
						.valueOf((String) horderData
								.get(CellSiteConstants.CARGO_TYPE)) - 1])
						+ cargoWeight + cargoVolume);

		holder.tv_time.setText((String) horderData
				.get(CellSiteConstants.SHIPPER_DATE));

		if ((Integer) horderData.get(CellSiteConstants.STATUS) == CellSiteConstants.HORDER_GETTING_CARGO) {
			holder.tv_status.setText(ctx.getResources().getString(
					R.string.getting_cargo));

			holder.tv_reply.setVisibility(View.GONE);
		} else if ((Integer) horderData.get(CellSiteConstants.STATUS) == CellSiteConstants.HORDER_SENTTING_CARGO) {
			holder.tv_status.setText(ctx.getResources().getString(
					R.string.sending_cargo));
		} else if ((Integer) horderData.get(CellSiteConstants.STATUS) == CellSiteConstants.HORDER_ARRIVED_CARGO) {
			holder.tv_status.setText(ctx.getResources().getString(
					R.string.sent_cargo));
			holder.tv_reply.setVisibility(View.GONE);
		}

		mContactListener.setPhone((String) horderData
				.get(CellSiteConstants.SHIPPER_PHONE));

		return convertView;
	}

	class ContactListener implements View.OnClickListener {
		private String phoneNum;

		public void setPhone(String _phoneNum) {
			phoneNum = _phoneNum;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phoneNum));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.startActivity(intent);

		}
	}

	private class ViewHolder {
		ImageView organizerPortrait;
		TextView tv_replied_driver;
		TextView tv_location;
		TextView tv_horder_id;
		TextView tv_reply_horder;
		TextView tv_truck;
		TextView tv_cargo;
		TextView tv_time;
		TextView tv_contact;
		TextView tv_reply;
		TextView tv_status; // horder 状态标示
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