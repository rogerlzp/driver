package com.abc.driver.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.abc.driver.model.Truck;
import com.abc.driver.model.User;
import com.abc.driver.net.CellSiteHttpClient;

public class CellSiteApplication extends Application {

	final static String TAG = CellSiteApplication.class.getSimpleName();

	int mNetworkUnknowHostTimes = 0;
	public NETWORK_STATUS mNetworkStatus = NETWORK_STATUS.NORMAL;

	public enum NETWORK_STATUS {
		NORMAL, OFFLINE, OTHERS
	}

	public enum NETWORK_UPDATE_MODE {
		WIFI_ONLY, ALL_UPDATE
	}
	
	List<String> hasDownloadBmpList = new ArrayList<String>();
	List<String> hasDownloadJsonList = new ArrayList<String>();

	public boolean isWillShowFirstRecomm = true;
	public boolean isShowHelp = true;
	public boolean mIsSdCardAvaible = false;
	CacheData cacheData;

	public String userMode;
	public String CacheFilePath;
	String PicsDirPath;

	protected String username = null;
	protected String password = null;
	protected User user = null;

	File cacheFileDir; // Download the file, dir
	public String regUserPath;
	
	protected Truck truck = null; // truck 信息放在app 中
	
	BitmapFactory.Options mBitmapDecodeOption;
	final int IO_BUFFER_SIZE = 1024;
	final int MEMORY_FOR_BITMAP_DECODE = 12 * 1024;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// must be initialization first!!!

		checkSDCardAvaible();
		initCacheFileDir();
		
		initUser();

		// initial the unnecessarily-started component
		// startService(new Intent(this, InitService.class));
	}

	public void CreatBitmapCache() {
		mBitmapDecodeOption = new BitmapFactory.Options();
		mBitmapDecodeOption.inDither = false;
		mBitmapDecodeOption.inPurgeable = true;
		mBitmapDecodeOption.inTempStorage = new byte[MEMORY_FOR_BITMAP_DECODE];
	}
	
	
	public void initUser() {
		SharedPreferences sp = getSharedPreferences(
				CellSiteConstants.CELLSITE_CONFIG, MODE_PRIVATE);
		String username = sp.getString(CellSiteConstants.USER_NAME, null);
		String userId = sp.getString(CellSiteConstants.USER_ID, null);

		user = new User();
		if (username != null) 
		{
				user.setUsername(username);
				if (userId != null) {
					user.setId(Long.parseLong(sp.getString(
							CellSiteConstants.USER_ID, null)));
				}
		} else 
		{ // visitor mode
			user.setId(User.INVALID_ID);
		//	setUserMode(CellSiteConstants.VISITOR_MODE);
		}

		return ;
	}
	
	
	
	
	boolean checkSDCardAvaible() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			mIsSdCardAvaible = true;
		} else {
			mIsSdCardAvaible = false;
		}
		return mIsSdCardAvaible;

	}

	void initConfig() // TODO: change later
	{
		/*
		 * SharedPreferences sp = getSharedPreferences(CellSiteConstants.CONFIG,
		 * MODE_PRIVATE); Boolean bFirstLogin =
		 * sp.getBoolean(CellSiteConstants.FIRST_LOGIN_FLAG, true);
		 * 
		 * if(bFirstLogin) { Editor sharedUser =
		 * getSharedPreferences(CellSiteConstants.CONFIG, MODE_PRIVATE).edit();
		 * sharedUser.putBoolean(CellSiteConstants.FIRST_LOGIN_FLAG, true);
		 * sharedUser.putLong(CellSiteConstants.NOTIFY_INTERNAL,
		 * CheckNotificationService.DELAY); sharedUser.commit(); }
		 */

	}

	private void initCacheFileDir() {
		Log.d(TAG, Environment.getExternalStorageDirectory().toString());
		CacheFilePath = Environment.getExternalStorageDirectory().getPath()
				+ "/cellsite/";
		Log.d(TAG, CacheFilePath);

		cacheFileDir = new File(CacheFilePath);
		if (!cacheFileDir.exists()) {
			cacheFileDir.mkdirs();
		}
		
		regUserPath = CacheFilePath + "portraits";
		File regUser = new File(regUserPath);
		if (!regUser.exists()) {
			regUser.mkdirs();
		}

	}

	public CacheData getCacheData() {
		if (cacheData == null) {
			cacheData = new CacheData(this);
		}
		return cacheData;
	}

	public void attachUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return this.user;
	}

	public NETWORK_STATUS ConnectNetwork() {
		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {

			return NETWORK_STATUS.NORMAL;
		} else {
			return NETWORK_STATUS.OFFLINE;
		}
	}

	public void setNetworkStatus(NETWORK_STATUS aStatus) {
		mNetworkStatus = aStatus;
	}
	
	
	
	 public Bitmap downloadBmpByUrl(String relativePath, String userTag) {
			if (relativePath == null || relativePath.trim().length() == 0) {
				return null;
			}

			String localPath = null;
			String UrlStr = null;
			String localDir = null;
			localPath = relativePath.substring(relativePath.lastIndexOf("/")+1, relativePath.length());
				
			UrlStr = CellSiteConstants.USER_IMAGE_URL + relativePath;  // image path from the server
			localDir = userTag;

			// 如果是离线状态，并且在本地有的话，则返回本地动路径
			try {
				File localFile = new File(userTag +"/"+localPath);
				boolean localFileExits = localFile.exists() && localFile.isFile();
				FileInputStream inputStream = null;
				Bitmap result;

				if ((mNetworkStatus == NETWORK_STATUS.OFFLINE)
						|| hasDownloadBmpList.contains(relativePath)) {
					if (localFileExits) {
						try {
							inputStream = new FileInputStream(localPath);
							result = BitmapFactory.decodeFileDescriptor(
									inputStream.getFD(), null, mBitmapDecodeOption);
						} catch (Exception e) {
							e.printStackTrace();
							result = null;
						} finally {
							if (inputStream != null)
								try {
									inputStream.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}

						return result;
					} else { 
						return null;
					}

				}

				//  从服务器重新获取
				try {
					URL url = new URL(UrlStr);
					HttpURLConnection httpConn = (HttpURLConnection) url
							.openConnection();

					long localLastModify;
					long sererLastModified = httpConn.getLastModified();

					// 更新获取时间，不需要下载
					if (localFileExits) {
						localLastModify = localFile.lastModified();
						if (localLastModify == sererLastModified) {
							inputStream = new FileInputStream(localPath);
							try {
								result = BitmapFactory.decodeFileDescriptor(
										inputStream.getFD(), null,
										mBitmapDecodeOption);
								hasDownloadBmpList.add(relativePath);
							} finally {
								inputStream.close();
							}

							return result;
						} else {
							localFile.delete();
						}
					}

					
					// 从服务器下载
					File fileDir = new File(localDir);
					if (!fileDir.exists())
						fileDir.mkdirs();
					if (httpConn != null) {
						httpConn.disconnect();
					}

			
					 url = new URL(UrlStr);
					HttpURLConnection fileDldConn = (HttpURLConnection) url
							.openConnection();
					
					fileDldConn.connect();
					Log.d(TAG, "++++++++++++++++++++++++++++++++++++++++++Get length :         " + fileDldConn.getContentLength());
					InputStream is = fileDldConn.getInputStream();
				

					// read from new
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buffer = new byte[IO_BUFFER_SIZE];

					int len1 = 0;
					while ((len1 = is.read(buffer)) != -1) {
						bos.write(buffer, 0, len1);
					}
					bos.flush();
					byte[] allBuffer = bos.toByteArray();

					if (mIsSdCardAvaible) // only when SD is not occupied by other
											// programs
					{
						// File newLocalFile = new File(localFile);
						Log.d(TAG, "newLocalFile=" + localFile);
						localFile.createNewFile();
						FileOutputStream stream = new FileOutputStream(localFile);
						stream.write(allBuffer);
						stream.close();
						localFile.setLastModified(sererLastModified);
						hasDownloadBmpList.add(relativePath);
						// end to save file
					}
					
					/*
					final BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true; 
					
					BufferedInputStream bufferStream = new BufferedInputStream(is);
					Log.d(TAG, "mark !!!!!!!!1111111111111");
					BitmapFactory.decodeStream(bufferStream,null,options);
					Log.d(TAG, "mark 222222222222222");
					if (is.markSupported()) {
						Log.d(TAG, "mark supported");
					bufferStream.reset();
					}
					

				    // Calculate inSampleSize
				options.inSampleSize = 1;

				    // Decode bitmap with inSampleSize set
				options.inJustDecodeBounds = false; 
			//	BitmapFactory.decodeStream(bufferStream,null,options);
					
					Bitmap resultBitmap = BitmapFactory.decodeStream(bufferStream,null,options);
					*/
					
					Bitmap resultBitmap = BitmapFactory.decodeFile(localFile.getPath());
					//		decodeByteArray(allBuffer, 0, allBuffer.length);

					bos.close();
					is.close();
			

					return resultBitmap;
				} catch (UnknownHostException unreachEx) {
					mNetworkUnknowHostTimes++;
					if (mNetworkUnknowHostTimes >= 3) {
						mNetworkUnknowHostTimes = 0;
					}

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				System.gc();
			}
			return null;
		}

}
