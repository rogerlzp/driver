package com.abc.driver.cache;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abc.driver.R;
import com.abc.driver.utility.CellSiteConstants;

public class TruckAdapter extends BaseAdapter {

	public ArrayList<HashMap<String, Object>> nTrucks = new ArrayList<HashMap<String, Object>>();
	private Context ctx;

	public TruckAdapter(Context context) {
		this.ctx = context;
	}

	public void setTrucks(ArrayList<HashMap<String, Object>> trucks) {
		nTrucks = trucks;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(ctx).inflate(
					R.layout.truck_item, null);
			holder = new ViewHolder();
			holder.tv_truck_id = (TextView) convertView
					.findViewById(R.id.truck_id_tv);
			holder.tv_name = (TextView) convertView.findViewById(R.id.name_tv);

			holder.iv_truck_phpoto = (ImageView) convertView
					.findViewById(R.id.truck_phpoto_iv);
			holder.tv_type = (TextView) convertView.findViewById(R.id.truck_tv);
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
		holder.tv_type.setText(CellSiteConstants.TruckTypes[(Integer) truckData
				.get(CellSiteConstants.TRUCK_TYPE)]
				+ " "
				+ CellSiteConstants.TruckLengths[(Integer) truckData
						.get(CellSiteConstants.TRUCK_LENGTH)]);

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
