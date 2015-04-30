package com.abc.driver.utility;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache {
	private static LruCache<String, Bitmap> mBitmapCache;
	
	private  static BitmapCache instance ;
	
	private BitmapCache() {
		int maxMemory = (int)Runtime.getRuntime().maxMemory();
		int maxSize = maxMemory/8;
		mBitmapCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
	}
	
	public static BitmapCache getBitmapCache() {
		if(instance == null) {
			instance = new BitmapCache() ;
		}
		return instance;
	}
	
	public  Bitmap getBitmap(String url) {
		
		return mBitmapCache.get(url);
	}
    public void putBitmap(String url, Bitmap bitmap) {
    	mBitmapCache.put(url, bitmap);
    }
	
}
