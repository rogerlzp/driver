package com.abc.driver;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.driver.TruckActivity.DownloadImageTask;
import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteConstants;
import com.abc.driver.utility.CropOption;
import com.abc.driver.utility.CropOptionAdapter;
import com.abc.driver.utility.Utils;

public class PersonalActivity extends BaseActivity {

	final String TAG = PersonalActivity.class.getSimpleName();

	ImageView portraitIv, identityIv, driverLicenseIv;
	TextView nameTv, driverLicenseTv, mobileNumTv;

	Bitmap  mIdentityImage, mDriverLicenseImage;

	// SigleBmpDownLoadTask mSwitchDownloadTask;

	ProgressDialog mProgressdialog;

	MyUserDownLoadTask userDownLoadTask;
	DownloadImageTask mDownloadImageTask;

	UpdateImageTask mUpdateImageTask;

	boolean isPortraitChanged;

	private Uri imageUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal);

		initView();
		initData();
	}

	public void initView() {
		portraitIv = (ImageView) findViewById(R.id.portrait_iv);
		identityIv = (ImageView) findViewById(R.id.identity_iv);
		driverLicenseIv = (ImageView) findViewById(R.id.driver_license_iv);
		nameTv = (TextView) findViewById(R.id.name_tv);
		driverLicenseTv = (TextView) findViewById(R.id.driver_license_tv);
		mobileNumTv = (TextView) findViewById(R.id.mobile_tv);

	}

	public void initViewData() {
		nameTv.setText(app.getUser().getName());
		mobileNumTv.setTag(app.getUser().getMobileNum());
	
	}
	
	public void initData() {
		userDownLoadTask = new MyUserDownLoadTask();
		userDownLoadTask.execute(app.getUser().getId());
	}

	public void setPortraitImage() {

		if (app.getPortaritBitmap() != null) 
		{
			portraitIv.setImageDrawable(new BitmapDrawable(
					app.getPortaritBitmap()));
		}
	}	

	private class MyUserDownLoadTask extends AsyncTask<Long, Integer, Integer> {
		@Override
		protected Integer doInBackground(Long... params) {
			return downloadUserPeofile(params[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (this.isCancelled()) {
				return;
			}

			if (mProgressdialog != null) {
				mProgressdialog.cancel();
			}

			if (result == CellSiteConstants.RESULT_SUC) {
				 initViewData();
				setPortraitImage();
			}
		}

		public int downloadUserPeofile(long _userId) {
			int resultCode = 0;
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.USER_ID, String.valueOf(_userId)));

			Log.d(TAG, "userId=" + _userId);

			JSONObject response = null;
			try {
				response = CellSiteHttpClient.executeHttpPost(
						CellSiteConstants.USER_QUERY_URL, postParameters);
				resultCode = Integer.parseInt(response.get(
						CellSiteConstants.RESULT_CODE).toString());

				if (resultCode == CellSiteConstants.RESULT_SUC) {
					parseJson(response);
				} else {
					Log.d("downloadUserPeofile", "Query user profile failed");
				}
			} catch (Exception e) {
			}
			return resultCode;
		}
	}

	public void parseJson(JSONObject jsonResult) {
		try {
			JSONObject profileJson = jsonResult
					.getJSONObject(CellSiteConstants.PROFILE);
			JSONObject userJson = jsonResult
					.getJSONObject(CellSiteConstants.USER);

			if (profileJson.get(CellSiteConstants.PROFILE_IMAGE_URL) != JSONObject.NULL) {
				Log.d(TAG, "get the image url");
				app.getUser()
						.setProfileImageUrl(
								profileJson
										.getString(CellSiteConstants.PROFILE_IMAGE_URL));

			}
			if (profileJson.get(CellSiteConstants.DRIVER_LICENSE_URL) != JSONObject.NULL) {
				app.getUser()
						.setDriverLicenseImageUrl(
								profileJson
										.getString(CellSiteConstants.DRIVER_LICENSE_URL));
			}

			if (profileJson.get(CellSiteConstants.NAME) != JSONObject.NULL) {
				app.getUser().setName(
						profileJson.getString(CellSiteConstants.NAME));
			}
			if (userJson.get(CellSiteConstants.MOBILE) != JSONObject.NULL) {
				app.getUser().setMobileNum(
						userJson.getString(CellSiteConstants.MOBILE));
			}


		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void updatePortait(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(res.getString(R.string.choose_portrait));
		builder.setItems(
				new String[] { res.getString(R.string.getPhotoFromCamera),
						res.getString(R.string.getPhotoFromMemory) },
				new DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int id) {
						switch (id) {
						case 0:
							startToCameraActivity();
							break;
						case 1:
							startToMediaActivity();
							break;
						}
					}

				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CellSiteConstants.TAKE_PICTURE
				|| requestCode == CellSiteConstants.PICK_PICTURE) {
			Uri uri = null;
			if (requestCode == CellSiteConstants.TAKE_PICTURE) {
				uri = imageUri;

			} else if (requestCode == CellSiteConstants.PICK_PICTURE) {
				uri = data.getData();

			}

			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(uri, filePathColumn,
					null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				imageUri = Uri.fromFile(new File(filePath));
			} else // This is a bug, in some cases, some images like
			{
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Toast.makeText(this, R.string.sdcard_occupied,
							Toast.LENGTH_SHORT).show();
					return;
				}

				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date());
				File tmpFile = new File(app.regUserPath + File.separator
						+ "IMG_" + timeStamp + ".jpg");
				File srcFile = new File(uri.getPath());
				if (srcFile.exists()) {
					try {
						Utils.copyFile(srcFile, tmpFile);
						app.getUser().getMyTruck()
								.setLicenseImageUrl(tmpFile.getAbsolutePath());
					} catch (Exception e) {
						Toast.makeText(this, R.string.create_tmp_file_fail,
								Toast.LENGTH_SHORT).show();
						return;
					}
				} else {
					Log.d(TAG, "Logic error, should not come to here");
					Toast.makeText(this, R.string.file_not_found,
							Toast.LENGTH_SHORT).show();
					return;
				}

				imageUri = Uri.fromFile(tmpFile);
			}

			doCrop();
			Log.d(TAG, "onActivityResult PICK_PICTURE");

		} else if (requestCode == CellSiteConstants.CROP_PICTURE) {
			Log.d(TAG, "crop picture");
			// processFile();

			if (data != null) {
				Bundle extras = data.getExtras();
				Bitmap photo = extras.getParcelable("data");

				app.setPortaritBitmap(photo);
				portraitIv.setImageBitmap(photo);
				mUpdateImageTask = new UpdateImageTask();
				mUpdateImageTask.execute("" + app.getUser().getId(),
						Utils.bitmap2String(photo),
						CellSiteConstants.UPDATE_USER_PORTRAIT_URL);

				isPortraitChanged = true;
			}
		}
	}

	private void doCrop() {
		Log.d(TAG, "doCrop()");
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = this.getPackageManager()
				.queryIntentActivities(intent, 0);

		int size = list.size();

		if (size == 0) {
			Log.d(TAG, " Crop activity is not found.  List size is zero.");
			Bitmap tmpBmp = BitmapFactory.decodeFile(imageUri.getPath(), null);
			app.setPortaritBitmap(Bitmap.createScaledBitmap(tmpBmp,
					CellSiteConstants.IMAGE_WIDTH,
					CellSiteConstants.IMAGE_HEIGHT, false));

			portraitIv.setImageBitmap(app.getPortaritBitmap());
			isPortraitChanged = true;

			Log.d(TAG, "set bitmap");

			return;
		} else {
			Log.d(TAG, "found the crop activity.");
			intent.setData(imageUri);

			intent.putExtra("outputX", CellSiteConstants.IMAGE_WIDTH);
			intent.putExtra("outputY", CellSiteConstants.IMAGE_HEIGHT);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Log.d(TAG, "Just one as choose it as crop activity.");
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);
				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, CellSiteConstants.CROP_PICTURE);
			} else {
				Log.d(TAG,
						"More that one activity for crop  is found . will chooose one");
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");

				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CellSiteConstants.CROP_PICTURE);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					// @Override
					public void onCancel(DialogInterface dialog) {
						if (imageUri != null) {
							getContentResolver().delete(imageUri, null, null);
							imageUri = null;
							isPortraitChanged = false;
						}
					}
				});
				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

	/**
	 * start to take picture
	 */
	private void startToCameraActivity() {
		Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = new File(app.regUserPath + File.separator + "IMG_"
				+ timeStamp + ".png");
		localIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
		imageUri = Uri.fromFile(mediaFile);

		// isCameraCapture = true;

		startActivityForResult(localIntent, CellSiteConstants.TAKE_PICTURE);
	}

	/**
	 * start to choose pictue
	 */
	private void startToMediaActivity() {
		Intent localIntent = new Intent("android.intent.action.PICK");
		Uri localUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
		localIntent.setDataAndType(localUri, "image/*");
		startActivityForResult(localIntent, CellSiteConstants.PICK_PICTURE);
	}

	private class UpdateImageTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return updateImage(params[0], params[1], params[2]);
		}

		@Override
		public void onPostExecute(Integer result) {
			if (this.isCancelled()) {
				return;
			}
			Integer resCode = result;// Integer.parseInt(result);
			if (resCode == CellSiteConstants.RESULT_SUC) {
				// TODO: set for different user
			}
		}

		protected Integer updateImage(String _userId, String _bitmap,
				String _updateUrl) {

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("bitmap", _bitmap));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.USER_ID, _userId));

			JSONObject response = null;
			try {
				response = CellSiteHttpClient.executeHttpPost(_updateUrl,
						postParameters);

				int resultCode = Integer.parseInt(response.get(
						CellSiteConstants.RESULT_CODE).toString());
				if (resultCode == CellSiteConstants.RESULT_SUC) {
					//   成功
					// int truckId = Integer.parseInt(response.get(
					// CellSiteConstants.TRUCK_ID).toString());
					// app.getUser().getMyTruck().setTruckId(truckId);

				}
				return resultCode;

			} catch (UnknownHostException e) {
				return CellSiteConstants.UNKNOWN_HOST_ERROR;
			} catch (Exception e) {
				// TODO
			}
			return CellSiteConstants.UNKNOWN_ERROR;
		}
	}
	
	
	/**
	 * 跳转到ChangeNameActivity去修改名字
	 */
	public void changeName(View v) {
		Intent intent = new Intent(PersonalActivity.this,
				ChangeNameActivity.class);
		startActivity(intent);

	}

}
