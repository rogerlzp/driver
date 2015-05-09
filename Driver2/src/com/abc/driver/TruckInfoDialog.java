package com.abc.driver;

import com.abc.driver.CityDialog.InputListener;
import com.abc.driver.utility.CellSiteConstants;
import com.abc.driver.wheel.widget.OnWheelChangedListener;
import com.abc.driver.wheel.widget.OnWheelScrollListener;
import com.abc.driver.wheel.widget.WheelView;
import com.abc.driver.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.abc.driver.wheel.widget.adapters.ArrayWheelAdapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class TruckInfoDialog extends Dialog {

	public boolean scrolling = false;
	private Context myContext;
	WheelView truckType;
	WheelView truckLength;
	WheelView truckWeight;
	TruckListener mTruckListener;
	Button okBtn, cancelBtn;

	public TruckInfoDialog(Context context) {
		super(context);
		this.myContext = context;
	}

	public TruckInfoDialog(Context context, TruckListener truckListener) {
		super(context);
		this.myContext = context;
		mTruckListener = truckListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.truckinfo_dialog);

		okBtn = (Button) findViewById(R.id.ok_btn);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);

		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mTruckListener.getText(truckType.getCurrentItem(),
						truckLength.getCurrentItem(),
						truckWeight.getCurrentItem());
				dismiss();
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});

		truckType = (WheelView) findViewById(R.id.truck_type);
		truckType.setVisibleItems(6);
		truckType.setCyclic(true);
		truckType.setViewAdapter(new TruckTypeAdapter(this.myContext));

		truckLength = (WheelView) findViewById(R.id.truck_length);
		truckLength.setVisibleItems(6);
		truckLength.setCyclic(true);
		truckLength.setViewAdapter(new TruckLengthAdapter(this.myContext));

		truckLength.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					// updateCities(city, cities, newValue);
				}
			}
		});

		truckLength.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				// updateCities(city, cities, country.getCurrentItem());
			}
		});

		truckLength.setCurrentItem(1);

		truckWeight = (WheelView) findViewById(R.id.truck_weight);
		truckWeight.setVisibleItems(6);
		truckWeight.setCyclic(true);
		truckWeight.setViewAdapter(new TruckWeightAdapter(this.myContext));

	}

	/**
	 * Updates the truck wheel
	 */
	private void updateTruck(WheelView truckType, String cities[][], int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(
				this.myContext, cities[index]);
		adapter.setTextSize(18);
		// city.setViewAdapter(adapter);
		// city.setCurrentItem(cities[index].length / 2);
	}

	/**
	 * Adapter for countries
	 */
	private class TruckTypeAdapter extends AbstractWheelTextAdapter {
		// TruckType names
		// TruckType flags
		private int flags[] = new int[] { R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher };

		/**
		 * Constructor
		 */
		protected TruckTypeAdapter(Context context) {
			super(context, R.layout.trucktype_layout, NO_RESOURCE);

			setItemTextResource(R.id.trucktype_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			ImageView img = (ImageView) view.findViewById(R.id.flag);
			img.setImageResource(flags[index]);
			return view;
		}

		@Override
		public int getItemsCount() {
			return CellSiteConstants.TruckTypes.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return CellSiteConstants.TruckTypes[index];
		}

	}

	/**
	 * Adapter for countries
	 */
	private class TruckLengthAdapter extends AbstractWheelTextAdapter {

		/**
		 * Constructor
		 */
		protected TruckLengthAdapter(Context context) {
			super(context, R.layout.trucklength_layout, NO_RESOURCE);

			setItemTextResource(R.id.trucklength_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return CellSiteConstants.TruckLengths.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return CellSiteConstants.TruckLengths[index];
		}

	}

	/**
	 * Adapter for countries
	 */
	private class TruckWeightAdapter extends AbstractWheelTextAdapter {

		/**
		 * Constructor
		 */
		protected TruckWeightAdapter(Context context) {
			super(context, R.layout.truckweight_layout, NO_RESOURCE);

			setItemTextResource(R.id.truckweight_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return CellSiteConstants.TruckWeights.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return CellSiteConstants.TruckWeights[index];
		}

	}

	public interface TruckListener {
		void getText(int str, int str2, int str3);
	}

}
