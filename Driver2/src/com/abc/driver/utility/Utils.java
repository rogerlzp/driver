package com.abc.driver.utility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;

import com.abc.driver.utility.CellSiteApplication.NETWORK_STATUS;

public class Utils {
	static final String TAG = "Utils";
	public static final int BUFFER_SIZE = 1024 * 8;
	public File gStorageFile;

	static final String KindReminder = "????????????";
	static final String NoNetwork = "??????????????????????";

	static final String CheckNetwork = "??????????????????????????????????";

	static final String gps_location_closed = "GPS??????????????????????????????????????????????????????????????????????????????";
	static final String wireliess_location_closed = "????????????????????????????????????????????????????????????????????????????????????";
	static final String open_location = "????????????????????";
	static final String location_reminder = "????????????????????";
	static final String LetItBe = "????????????";

	static final String setNetwork = "????????????";
	static final String offlineView = "????????????";

	private static final Pattern rfc2822 = Pattern
			.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

	public void setStorage() {
		Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		gStorageFile = Environment.getExternalStorageDirectory();
	}

	public static boolean isValidEmail(String email) {
		if (rfc2822.matcher(email).matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static void createNetworkStatusDlg(final Activity currAct) {
		AlertDialog.Builder builder = new AlertDialog.Builder(currAct);

		final CellSiteApplication appfromCurrAct = (CellSiteApplication) currAct
				.getApplication();
		builder.setTitle(KindReminder);
		builder.setMessage(NoNetwork);
		builder.setPositiveButton(setNetwork,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						currAct.startActivity(new Intent(
								Settings.ACTION_WIRELESS_SETTINGS));
					}
				});
		builder.setNegativeButton(offlineView,
				new DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int which) {
						appfromCurrAct.setNetworkStatus(NETWORK_STATUS.OFFLINE);
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void openLocationProvideIfNot(final Activity currAct) {
		LocationManager locationManager = (LocationManager) currAct
				.getSystemService(Context.LOCATION_SERVICE);

		String provideNotOpen = null;
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			provideNotOpen = gps_location_closed;
		} else if (!locationManager
				.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			provideNotOpen = wireliess_location_closed;
		} else {
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(currAct);
		builder.setTitle(location_reminder);
		builder.setMessage(provideNotOpen);
		builder.setPositiveButton(open_location,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						currAct.startActivity(new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				});
		builder.setNegativeButton(LetItBe,
				new DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static boolean isValidMobile(String _mobile) {
		if (_mobile.length() == 11
				&& (_mobile.startsWith("13") || _mobile.startsWith("15")
						|| _mobile.startsWith("18") || _mobile
							.startsWith("147"))) {
			return true;
		} else {
			return false;
		}
	}

	public static Bitmap LoadImageFromURL(Uri uri, BitmapFactory.Options options) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = new FileInputStream(new File(uri.getPath()));
			bitmap = BitmapFactory.decodeStream(in, null, options);
			in.close();
		} catch (IOException e1) {
		}
		return bitmap;
	}

	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();

		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);

		return resizedBitmap;

	}

	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	public static void writeExternalToCache(Bitmap bitmap, File file) {
		try {
			// delete the old file
			if (file.exists()) {
				Log.d(TAG, "file exists");
				file.delete();
			} else {
				Log.d(TAG, "file doesn't exist");
			}
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			final BufferedOutputStream bos = new BufferedOutputStream(fos,
					BUFFER_SIZE);
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			fos.close();
			Log.d(TAG, "writeExternalToCache" + file.getAbsolutePath());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}

	}

	public static String bitmap2String(Bitmap bitmap) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

			byte[] ba = os.toByteArray();

			String ba1 = Base64.encodeBytes(ba);

			return ba1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * calDistance
	 */
	public static boolean canCheckin(String distance) {
		Log.d(TAG, "distance = " + distance);
		String tmp = distance.substring(0, distance.length() - 2);
		try {
			if (Float.valueOf(tmp) < 100) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public static int calDistance(double lat1, double lng1, double lat2,
			double lng2) {
		double distance = 1609.344
				* 60
				* 1.1515
				* (180 / Math.PI)
				* Math.acos(Math.sin(lat1 * Math.PI / 180)
						* Math.sin(lat2 * Math.PI / 180)
						+ Math.cos(lat1 * Math.PI / 180)
						* Math.cos(lat2 * Math.PI / 180)
						* Math.cos((lng1 - lng2) * Math.PI / 180));
		return (int) Math.round(distance);

	}

	public static void sendSMS(String phoneNumber, String message) {
		SmsManager sms = SmsManager.getDefault();
		// sms.sendTextMessage(phoneNumber, null, message, null, null);

		List<String> divideContents = sms.divideMessage(message);
		for (String text : divideContents) {
			Log.d(TAG, "Message content :" + text);
			sms.sendTextMessage(phoneNumber, null, text, null, null);
		}

		// Intent intent = this. (ACTION_VIEW, "vnd.android-dir/mms-sms");
		//
		// intent.putExtra("sms_body", message);
		//
		// intent.putExtra("address", phoneNumber);
		//
		// this.startActivity(intent);
	}

	public static String processTime(String begin_time, String end_time) {
		return (processReadableTime(begin_time) + " - " + processReadableTime(end_time));
	}

	// process day
	public static String processReadableTime(String begin_time) {

		Calendar calendar = Calendar.getInstance();
		Date begin_dt1 = null;
		String readableDay;
		try {

			SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			begin_dt1 = sDate.parse(begin_time);

			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(begin_dt1);

			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar1.set(Calendar.MINUTE, 0);
			calendar1.set(Calendar.HOUR, 0);
			calendar1.set(Calendar.HOUR_OF_DAY, 0);
			calendar1.set(Calendar.SECOND, 0);
			calendar1.set(Calendar.MILLISECOND, 0);

			long result = calendar.getTimeInMillis()
					- calendar1.getTimeInMillis();
			System.out.println("long result =" + result);

			System.out
					.println("long result =" + result / (24 * 60 * 60 * 1000));

			// String hoursMinutes =" " + begin_dt1.getHours() + ":" +
			// begin_dt1.getMinutes();

			String hoursMinutes = " "
					+ begin_time.substring(begin_time.length() - 8,
							begin_time.length() - 3);
			Log.d(TAG, " hoursMinutes :" + hoursMinutes);

			switch ((int) result / (24 * 60 * 60 * 1000)) {
			case 0:
				readableDay = "??????";
				break;
			case 1:
				readableDay = "??????";
				break;
			case -1:
				readableDay = "??????";
				break;
			default: {
				if (calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)) {
					readableDay = (calendar1.get(Calendar.MONTH) + 1) + "????"
							+ calendar1.get(Calendar.DAY_OF_MONTH) + "????";
				} else {
					readableDay = calendar1.get(Calendar.YEAR) + "????"
							+ (calendar1.get(Calendar.MONTH) + 1) + "????"
							+ calendar1.get(Calendar.DAY_OF_MONTH) + "????";
				}
			}
				;
			}

			return readableDay + hoursMinutes;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// process day
	public static String processDay(String begin_time) {

		Calendar calendar = Calendar.getInstance();
		Date begin_dt1 = null;
		try {

			SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			begin_dt1 = sDate.parse(begin_time);

			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(begin_dt1);

			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar1.set(Calendar.MINUTE, 0);
			calendar1.set(Calendar.HOUR, 0);
			calendar1.set(Calendar.HOUR_OF_DAY, 0);
			calendar1.set(Calendar.SECOND, 0);
			calendar1.set(Calendar.MILLISECOND, 0);

			long result = calendar.getTimeInMillis()
					- calendar1.getTimeInMillis();
			System.out.println("long result =" + result);

			System.out
					.println("long result =" + result / (24 * 60 * 60 * 1000));

			switch ((int) result / (24 * 60 * 60 * 1000)) {
			case 0:
				return "??????";
			case 1:
				return "??????";
			case -1:
				return "??????";
			default:
				break;
			}

			if (calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)) {
				return (calendar1.get(Calendar.MONTH) + 1) + "/"
						+ calendar1.get(Calendar.DAY_OF_MONTH);
			} else {
				return calendar1.get(Calendar.YEAR) + "/"
						+ (calendar1.get(Calendar.MONTH) + 1) + "/"
						+ calendar1.get(Calendar.DAY_OF_MONTH);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || packageName.equalsIgnoreCase("")) {
			return false;
		}
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static SharedPreferences getSharedPreference(Context ctx) {
		ApplicationInfo info = ctx.getApplicationInfo();
		String prefName = info.packageName + "bshare_save";
		SharedPreferences settings = ctx.getSharedPreferences(prefName, 0);
		return settings;
	}

	public static Long produceTime() {

		Timestamp tt = new Timestamp(System.currentTimeMillis());// ??????????????????
		Long produceTime = tt.getTime();// new Date()????????????????????????????
		return produceTime;
	}
}
