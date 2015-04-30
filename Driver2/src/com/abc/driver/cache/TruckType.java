package com.abc.driver.cache;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

public class TruckType {
	public ArrayList<HashMap<String, Object>> nTrucks;
	public TruckAdapter nTruckAdapter;
	private Context ctx;

	public int nDisplayNum;
	public Boolean hasShowAllTrucks;

	public TruckType(Context _ctx) {
		nTrucks = new ArrayList<HashMap<String, Object>>();
		hasShowAllTrucks = false;
		this.ctx = _ctx;
		nTruckAdapter = new TruckAdapter(ctx);
	}

}
