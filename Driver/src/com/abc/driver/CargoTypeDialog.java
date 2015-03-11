package com.abc.driver;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class CargoTypeDialog extends Dialog {
	private Context context;
	// private InputListener IListener;
	private Spinner spinner1 = null;
	private GridView gridView1;

	public ArrayList<HashMap<String, Object>> cargoTypeList = new ArrayList<HashMap<String, Object>>();

	public CargoTypeDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/*
	 * public CargoTypeDialog(Context context, InputListener inputListener) {
	 * super(context); // TODO Auto-generated constructor stub this.context =
	 * context; // IListener = inputListener; }
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cargo_type_gridview);
		gridView1 = (GridView) findViewById(R.id.gridView1);

		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("PIC", R.drawable.ic_launcher);
			map.put("TITLE", "Test Title");
			cargoTypeList.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(this.context, cargoTypeList,
				R.layout.cargo_type_griditem, new String[] { "PIC", "TITLE" },
				new int[] { R.id.griditem_pic, R.id.griditem_title, });

		gridView1.setAdapter(adapter);
		gridView1.setOnItemClickListener(new ItemClickListener());
	}

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
			setTitle((String)item.get("TITLE"));
		
		}

	}

}
