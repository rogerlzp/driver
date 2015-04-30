package com.abc.driver.utility.image;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.BitmapCache;
import com.abc.driver.utility.CellSiteConstants;
import com.abc.driver.utility.FileUtil;

public class ImageLoad {

	// 最大线程数
	private static final int MAX_THREAD_NUM = 5;

	// 线程池
	private ExecutorService threadPools = null;

	// 一级内存缓存基于LruCache
	private static BitmapCache bitmapCache;

	// 二级文件缓存
	private static FileUtil fileUtil;

	static MyHandler handler;

	public ImageLoad(Context context) {
		bitmapCache = BitmapCache.getBitmapCache();
		fileUtil = FileUtil.getInstance();
		// (context);
		threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM);
	}

	public Bitmap loadImage(final ImageView imageView, final String imageUrl,
			final ImageDownloadedCallBack imageDownloadedCallBack) {
		final String filename = imageUrl
				.substring(imageUrl.lastIndexOf("/") + 1);
		final String filepath = FileUtil.CacheFilePath + filename;

		// 先从内存中拿
		Bitmap bitmap = bitmapCache.getBitmap(imageUrl);
		if (bitmap != null) {
			return bitmap;
		}
		if (fileUtil.isBitmapExists(filename)) {
			bitmap = BitmapFactory.decodeFile(filepath);
			// 缓存到内存
			bitmapCache.putBitmap(imageUrl, bitmap);

			return bitmap;
		}

		// 内存和文件没有，再从网络下载
		if (imageUrl != null && !imageUrl.equals("")) {
			handler = new MyHandler(imageDownloadedCallBack, imageView);

			Thread thread = new Thread() {
				@Override
				public void run() {
					InputStream inputStream = CellSiteHttpClient
							.executeHttpGet2(CellSiteConstants.HOST+ imageUrl);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = false;
					options.inSampleSize = 5;
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream,
							null, options);
					if (bitmap != null) {
						bitmapCache.putBitmap(imageUrl, bitmap);
						fileUtil.saveBitmap(filename, bitmap);

						Message msg = new Message();
						msg.what = CellSiteConstants.IMAGE_DOWNLOADED;
						msg.obj = bitmap;

						handler.sendMessage(msg);
					}
				}
			};
			threadPools.execute(thread);

		}
		return null;
	}

	private static class MyHandler extends Handler {
		private ImageDownloadedCallBack imageDownloadedCallBack;
		private ImageView imageView;

		public MyHandler(ImageDownloadedCallBack _imageDownloadedCallBack,
				ImageView _imageView) {
			imageDownloadedCallBack = _imageDownloadedCallBack;
			imageView = _imageView;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CellSiteConstants.IMAGE_DOWNLOADED:
				Bitmap bitmap = (Bitmap) msg.obj;
				imageDownloadedCallBack.onImageDownloaded(imageView, bitmap);
				break;

			}
		}
	};

	public interface ImageDownloadedCallBack {
		void onImageDownloaded(ImageView imageView, Bitmap bitmap);
	}

}
