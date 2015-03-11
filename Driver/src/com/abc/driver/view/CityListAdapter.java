package com.abc.driver.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CityListAdapter extends BaseAdapter {

	private Context mContext;
	private List<CityListItem> mCityList;
	
	public CityListAdapter(Context context, List<CityListItem> cityList) {
		this.mContext = context;
		this.mCityList = cityList;
	}
	
	public int getCount() {
		return mCityList.size();
	}
	
	public Object getItem(int position) {
		return mCityList.get(position);
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		CityListItem mCityListItem  = mCityList.get(position);
		return new CityAdapterView(this.mContext, mCityListItem);
	} 
	
	class CityAdapterView extends LinearLayout {
		public CityAdapterView(Context context, CityListItem cityListItem) {
			super(context);
			this.setOrientation(HORIZONTAL);
			
			LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(200, LayoutParams.WRAP_CONTENT);
			llparams.setMargins(1, 1, 1, 1);
			
			TextView name = new TextView(context);
			name.setText(cityListItem.getName());
			addView(name, llparams);
			
			LinearLayout.LayoutParams llparams2 = new LinearLayout.LayoutParams(200, LayoutParams.WRAP_CONTENT);
			llparams2.setMargins(1, 1, 1, 1);
			
			TextView pCode = new TextView(context);
			pCode.setText(cityListItem.getPcode());
			addView(pCode, llparams2);
			pCode.setVisibility(GONE);
			
			
		}
	}
}

