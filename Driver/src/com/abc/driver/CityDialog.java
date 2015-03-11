package com.abc.driver;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.abc.driver.dao.DBHelper;
import com.abc.driver.view.CityListAdapter;
import com.abc.driver.view.CityListItem;

public class CityDialog extends Dialog {

	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private Spinner spinner1 = null;
	private Spinner spinner2 = null;
	private Spinner spinner3 = null;

	private Button okBtn;
	private Button cancelBtn;
	private String province = null;
	private String city = null;
	private String district = null;
	private String provinceCode = null;
	private String cityCode = null;
	private String districtCode = null;
	private Context context;
	private InputListener IListener;
	

	public CityDialog(Context context) {
		super(context);
	}

	public CityDialog(Context context, InputListener inputListener) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		IListener = inputListener;
	}

	public interface InputListener {
		void getText(String str, String str2);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spinnerdialog);

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner3 = (Spinner) findViewById(R.id.spinner3);
		okBtn = (Button) findViewById(R.id.ok_btn);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String address = province + "-" + city + "-" + district;
				String addressCode = provinceCode + "-" + cityCode + "-" + districtCode;
				IListener.getText(address, addressCode);
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
		initSpinner1();

	}

	public void initSpinner1() {
		dbHelper = new DBHelper(context);
		dbHelper.openDataBase();
		db = dbHelper.getDatabase();
		List<CityListItem> list = new ArrayList<CityListItem>();

		try {
			String sql = "select * from province";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("code"));
				byte bytes[] = cursor.getBlob(2);
				String name = new String(bytes, "gbk");
				CityListItem myListItem = new CityListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("code"));
			byte bytes[] = cursor.getBlob(2);
			String name = new String(bytes, "gbk");
			CityListItem myListItem = new CityListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		dbHelper.close();
		db.close();

		CityListAdapter myAdapter = new CityListAdapter(context, list);
		spinner1.setAdapter(myAdapter);
		spinner1.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
	}

	public void initSpinner2(String pcode) {
		dbHelper = new DBHelper(context);
		dbHelper.openDataBase();
		db = dbHelper.getDatabase();
		List<CityListItem> list = new ArrayList<CityListItem>();

		try {
			String sql = "select * from city where pcode='" + pcode + "'";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("code"));
				byte bytes[] = cursor.getBlob(2);
				String name = new String(bytes, "gbk");
				CityListItem myListItem = new CityListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("code"));
			byte bytes[] = cursor.getBlob(2);
			String name = new String(bytes, "gbk");
			CityListItem myListItem = new CityListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		dbHelper.close();
		db.close();

		CityListAdapter myAdapter = new CityListAdapter(context, list);
		spinner2.setAdapter(myAdapter);
		spinner2.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
	}

	public void initSpinner3(String pcode) {
		dbHelper = new DBHelper(context);
		dbHelper.openDataBase();
		db = dbHelper.getDatabase();
		List<CityListItem> list = new ArrayList<CityListItem>();

		try {
			String sql = "select * from district where pcode='" + pcode + "'";
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("code"));
				byte bytes[] = cursor.getBlob(2);
				String name = new String(bytes, "gbk");
				CityListItem myListItem = new CityListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				list.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("code"));
			byte bytes[] = cursor.getBlob(2);
			String name = new String(bytes, "gbk");
			CityListItem myListItem = new CityListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			list.add(myListItem);

		} catch (Exception e) {
		}
		dbHelper.close();
		db.close();

		CityListAdapter myAdapter = new CityListAdapter(context, list);
		spinner3.setAdapter(myAdapter);
		spinner3.setOnItemSelectedListener(new SpinnerOnSelectedListener3());
	}

	class SpinnerOnSelectedListener1 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			province = ((CityListItem) adapterView.getItemAtPosition(position))
					.getName();
			String pcode = ((CityListItem) adapterView
					.getItemAtPosition(position)).getPcode();
			provinceCode = pcode;
			initSpinner2(pcode);
			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub
		}
	}

	class SpinnerOnSelectedListener2 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			city = ((CityListItem) adapterView.getItemAtPosition(position))
					.getName();
			String pcode = ((CityListItem) adapterView
					.getItemAtPosition(position)).getPcode();
			cityCode = pcode;
			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub
		}
	}

	class SpinnerOnSelectedListener3 implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			district = ((CityListItem) adapterView.getItemAtPosition(position))
					.getName();
		    districtCode = ((CityListItem) adapterView
					.getItemAtPosition(position)).getPcode();
			
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub
		}
	}

}
