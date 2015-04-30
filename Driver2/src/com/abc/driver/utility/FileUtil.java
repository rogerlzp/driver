package com.abc.driver.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class FileUtil {

	private static final String TAG = FileUtil.class.getSimpleName();

	public static String CacheFilePath;
	private static FileUtil instance;
	

	private FileUtil() {
		initCacheFileDir(); // 初始化 FileUtil
	}
	
	public static FileUtil getInstance() {
		if (instance == null) {
			instance = new FileUtil();
		}
		return instance;
	}
	

	/**
	 * 检查存储可写
	 * 
	 * @return boolean
	 */
	public  boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * 将bitmap 存储到本地文件
	 * 
	 * @param filename
	 * @param bitmap
	 */

	public  void saveBitmap(String filename, Bitmap bitmap) {
		if (!isExternalStorageWritable()) {
			Log.d(TAG, "sd卡不可用");
			return;
		}

		if (bitmap == null) {
			return;
		}

		try {
			File file = new File(CacheFilePath, filename);
			FileOutputStream outputStream = new FileOutputStream(file);
			if ((filename.indexOf("png") != -1)
					|| (filename.indexOf("PNG") != -1)) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			} else {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			}
			outputStream.flush();
			outputStream.close();

		} catch (FileNotFoundException fe) {
			Log.e(TAG, fe.getMessage());
		} catch (IOException ie) {
			Log.e(TAG, ie.getMessage());
		}
	}

	/**
	 * 检查文件名是否存在
	 * 
	 * @param filename
	 * @return boolean
	 */
	public  boolean isBitmapExists(String filename) {
		File file = new File(CacheFilePath + filename);
		return file.exists();
	}

	private static void initCacheFileDir() {
		CacheFilePath = Environment.getExternalStorageDirectory().getPath()
				+ "/cellsite/";

		File rootDir = new File(CacheFilePath);
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}

	}

}
