package com.abc.driver.cache;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.abc.driver.MyHorderFragment.ReplyListener;

public class MyHorderType {
	int nIndex;
	public ArrayList<HashMap<String, Object>> nHorders;
	public MyHorderAdapter nHorderAdapter;

	public int nDisplayNum;
	public Boolean hasShowAllHorders;

	public MyHorderType(int aIndex, Context ctx, String currentDriverId,
			ReplyListener replyListener) {
		nHorders = new ArrayList<HashMap<String, Object>>();
		hasShowAllHorders = false;
		nIndex = aIndex;
		nHorderAdapter = new MyHorderAdapter(ctx, currentDriverId, replyListener);
	}

}
