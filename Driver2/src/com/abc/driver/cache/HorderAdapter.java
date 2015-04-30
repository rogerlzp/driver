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

import com.abc.driver.FHorderFragment.ReplyListener;
import com.abc.driver.R;
import com.abc.driver.utility.CellSiteConstants;

public class HorderAdapter extends BaseAdapter {
	public ArrayList<HashMap<String, Object>> nHorders = new ArrayList<HashMap<String, Object>>();
	public Context ctx;
	public String currentUserId;

	ContactListener mContactListener;
	ReplyListener mReplyListener;
	

	public HorderAdapter(Context context, String _currentUserId, ReplyListener _mReplyListener) {
		this.ctx = context;
		this.currentUserId =_currentUserId;
		this.mReplyListener = _mReplyListener;
	}

	public void setHorders(ArrayList<HashMap<String, Object>> horders) {
		nHorders = horders;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(ctx).inflate(
					R.layout.horder_item, null);
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

		
			holder.tv_reply.setOnClickListener(mReplyListener);

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
		double cargoWeightValue = Double.valueOf((String) horderData
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

		holder.tv_replied_driver.setText(ctx.getResources().getString(
				R.string.replied_driver)
				+ ": "
				+ horderData.get(CellSiteConstants.REPLIED_DRIVERS_COUNT));
		mContactListener.setPhone((String) horderData
				.get(CellSiteConstants.SHIPPER_PHONE));

		mReplyListener.setHorderId((String) horderData.get("horder_id"));

		if ((Integer) horderData.get(CellSiteConstants.IS_DRIVER_REPLIED) == 1) {
			holder.tv_reply.setText(ctx.getResources().getString(
					R.string.already_requested));
		} else {
			holder.tv_reply.setText(ctx.getResources().getString(
					R.string.request_horder));
		}


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
			ctx.startActivity(intent);

		}
	}

	private class ViewHolder {
		ImageView organizerPortrait;
		TextView tv_replied_driver;
		TextView tv_location;
		TextView tv_horder_id;
		TextView tv_truck;
		TextView tv_cargo;
		TextView tv_time;
		TextView tv_contact;
		TextView tv_reply;
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