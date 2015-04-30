package com.abc.driver.cache;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.abc.driver.FHorderFragment.ReplyListener;

public class HorderType {
	int nIndex;
	public ArrayList<HashMap<String, Object>> nHorders;
	public HorderAdapter nHorderAdapter;

	public int nDisplayNum;
	public Boolean hasShowAllHorders;

	public HorderType(int aIndex, Context ctx, String currentDriverId,
			ReplyListener replyListener) {
		nHorders = new ArrayList<HashMap<String, Object>>();
		hasShowAllHorders = false;
		nIndex = aIndex;
		nHorderAdapter = new HorderAdapter(ctx, currentDriverId, replyListener);
	}

}
