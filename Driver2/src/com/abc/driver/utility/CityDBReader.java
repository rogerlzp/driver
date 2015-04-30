package com.abc.driver.utility;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.abc.driver.dao.DBHelper;
import com.abc.driver.view.CityListItem;

public class CityDBReader {

	private DBHelper dbHelper;
	private SQLiteDatabase db;

	public Context myContext;

	public CityDBReader(Context context) {
		this.myContext = context;
	}

	public String getNameFromCode(String addressCode) {
		dbHelper = new DBHelper(myContext);
		dbHelper.openDataBase();
		db = dbHelper.getDatabase();

		String addressName = null;

		if (addressCode != null && addressCode.contains("-")) {
			addressName = this.getProvinceByCode(addressCode.split("-")[0])
					.trim()
					+ "-"
					+ this.getCityByCode(addressCode.split("-")[1]).trim()
					+ "-"
					+ this.getDistrictByCode(addressCode.split("-")[2]).trim();

		}
		db.close();
		dbHelper.close();
		return addressName;
	}

	public String getProvinceByCode(String pCode) {
		String name = null;
		Cursor cursor = null;
		try {
			String sql = "select * from province where code=" + pCode;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				byte bytes[] = cursor.getBlob(2);
				name = new String(bytes, "gbk");
			}

		} catch (Exception e) {
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();

			}
		}

		return name;

	}

	public String getCityByCode(String cCode) {
		String name = null;
		Cursor cursor = null;
		try {
			String sql = "select * from city where code=" + cCode;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				byte bytes[] = cursor.getBlob(2);
				name = new String(bytes, "gbk");
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();

			}
		}
		return name;
	}

	public String getDistrictByCode(String dCode) {
		String name = null;
		Cursor cursor = null;
		try {
			String sql = "select * from district where code=" + dCode;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				byte bytes[] = cursor.getBlob(2);
				name = new String(bytes, "gbk");
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();

			}
		}

		return name;
	}

}
