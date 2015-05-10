package com.abc.driver;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.driver.TruckActivity.DownloadImageTask;
import com.abc.driver.TruckInfoDialog.TruckListener;
import com.abc.driver.model.User;
import com.abc.driver.net.CellSiteHttpClient;
import com.abc.driver.utility.CellSiteApplication;
import com.abc.driver.utility.CellSiteConstants;
import com.abc.driver.utility.CellSiteDefException;
import com.abc.driver.utility.PasswordService;
import com.abc.driver.utility.Utils;
import com.abc.driver.utility.image.ImageLoad;
import com.abc.driver.utility.image.ImageLoad.ImageDownloadedCallBack;

public class VerifyActivity extends BaseActivity {

	final String TAG = PersonalActivity.class.getSimpleName();

	ImageView mUserIdentityFrontIv, mUserIdentityBackIv, mDriverLicenseIv,
			mVehicleLicenseIv, mVehiclePhotoIv;
	TextView driverLicenseTv;
	EditText mTruckInfoEt;
	EditText mTruckPlateNumberEt; // 车牌号码
	EditText nameEt, mobileNumEt;
	LinearLayout verifyUserll, verifyTruckll, userVerifyll, truckVerifyll;

	Bitmap mIdentityFrontImage, mIdentityBackImage, mDriverLicenseImage,
			mVehicleLicenseImage, mVehiclePhotoImage;
	String userName, userPhone;

	ProgressDialog mProgressdialog;
	String name_current, mobile_current, identityFrontImageUrl,
			identityBackImageUrl, dirverLicenseImageUrl,
			vehicleLicenseImageUrl, vehiclePhotoImageUrl, truckPlateNumber,
			truckInfo;

	MyUserDownLoadTask userDownLoadTask;
	DownloadImageTask mDownloadImageTask;

	UpdateDriverImageTask mUpdateDriverImageTask;

	TruckListener mTruckListener;
	int truckType, truckLength, truckWeight;

	int truckType_current, truckLength_current, truckWeight_current;

	int tmpFlag = 0;

	boolean isPortraitChanged;

	private Uri imageUri;

	ImageLoad mImageLoad;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verify);

		mImageLoad = new ImageLoad(this);

		initView();
		// initData();
		initViewData();
	}

	public void initView() {
		mUserIdentityFrontIv = (ImageView) findViewById(R.id.identity_front_iv);
		mUserIdentityBackIv = (ImageView) findViewById(R.id.identity_back_iv);
		mDriverLicenseIv = (ImageView) findViewById(R.id.driver_license_iv);

		mVehicleLicenseIv = (ImageView) findViewById(R.id.vehicle_license_iv);
		mVehiclePhotoIv = (ImageView) findViewById(R.id.vehicle_photo_iv);

		nameEt = (EditText) findViewById(R.id.verify_name_et);
		driverLicenseTv = (TextView) findViewById(R.id.driver_license_tv);
		mobileNumEt = (EditText) findViewById(R.id.verify_mobile_et);
		verifyUserll = (LinearLayout) findViewById(R.id.verify_user_ll);
		verifyTruckll = (LinearLayout) findViewById(R.id.verify_truck_ll);
		userVerifyll = (LinearLayout) findViewById(R.id.user_verify_ll);
		truckVerifyll = (LinearLayout) findViewById(R.id.truck_verify_ll);

		mTruckInfoEt = (EditText) findViewById(R.id.truck_info_update_tv);
		mTruckInfoEt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				showChoose();
				v.setFocusable(false);
				v.setFocusableInTouchMode(true);
			}

		});

		mTruckPlateNumberEt = (EditText) findViewById(R.id.license_plate_et);

	}

	public void toggleVerify(View v) {
		if (v.getId() == R.id.user_verify_ll) {
			if (verifyUserll.getVisibility() == View.GONE) {
				verifyUserll.setVisibility(View.VISIBLE);
				verifyTruckll.setVisibility(View.GONE);
			}
		} else if (v.getId() == R.id.truck_verify_ll) {
			if (verifyTruckll.getVisibility() == View.GONE) {
				verifyTruckll.setVisibility(View.VISIBLE);
				verifyUserll.setVisibility(View.GONE);
			}
		}
	}

	public void initViewData() {

		mTruckListener = new TruckListener() {
			@Override
			public void getText(int _truckType, int _truckLength,
					int _truckWeight) {
				// TODO Auto-generated method stub
				truckType = _truckType;
				truckLength = _truckLength;
				truckWeight = _truckWeight;

				mTruckInfoEt.setText(CellSiteConstants.TruckTypes[truckType]
						+ " " + CellSiteConstants.TruckLengths[truckLength]
						+ " " + CellSiteConstants.TruckWeights[truckWeight]);
			}
		};

		name_current = app.getUser().getName();
		mobile_current = app.getUser().getMobileNum();
		identityFrontImageUrl = app.getUser().getIdentityFrontImageUrl();
		identityBackImageUrl = app.getUser().getIdentityBackImageUrl();
		dirverLicenseImageUrl = app.getUser().getDriverLicenseImageUrl();

		vehicleLicenseImageUrl = app.getUser().getMyTruck()
				.getLicenseImageUrl();
		vehiclePhotoImageUrl = app.getUser().getMyTruck().getPhotoImageUrl();

		if (app.getUser().getName() != null
				&& !app.getUser().getName().equals("null")) {
			nameEt.setText(name_current);
		}
		mobileNumEt.setText(mobile_current);

		if (identityFrontImageUrl != null && !identityFrontImageUrl.equals("")) {
			showImage(mUserIdentityFrontIv, identityFrontImageUrl);
		}
		if (identityBackImageUrl != null && !identityBackImageUrl.equals("")) {
			showImage(mUserIdentityBackIv, identityBackImageUrl);
		}

		if (dirverLicenseImageUrl != null && !dirverLicenseImageUrl.equals("")) {
			showImage(mDriverLicenseIv, dirverLicenseImageUrl);
		}

		if (vehicleLicenseImageUrl != null
				&& !vehicleLicenseImageUrl.equals("")) {
			showImage(mVehicleLicenseIv, vehicleLicenseImageUrl);
		}

		if (vehiclePhotoImageUrl != null && !vehiclePhotoImageUrl.equals("")) {
			showImage(mVehiclePhotoIv, vehiclePhotoImageUrl);
		}

		truckPlateNumber = app.getUser().getMyTruck().getLincenPlateNumber();
		if (truckPlateNumber != null) {
			mTruckPlateNumberEt.setText(truckPlateNumber);
		}

		truckType = app.getUser().getMyTruck().getTypeId();
		truckLength = app.getUser().getMyTruck().getLengthId();
		truckWeight = app.getUser().getMyTruck().getWeightId();

		if (truckType != -1) {
			truckInfo = CellSiteConstants.TruckTypes[truckType] + " "
					+ CellSiteConstants.TruckLengths[truckLength] + " "
					+ CellSiteConstants.TruckWeights[truckWeight];

			mTruckInfoEt.setText(truckInfo);
		}

	}

	public void initData() {
		userDownLoadTask = new MyUserDownLoadTask();
		userDownLoadTask.execute(app.getUser().getId());
	}

	/**
	 * 1. check name and mobile is not empty 2. check identitiy bitmap is not
	 * empty 3. check driver license is not empty 4. go to next page
	 * 
	 * @param v
	 */
	public void gotoNextStep(View v) {
		// first check name is
		if (nameEt.getText().toString().trim() != null) {
			app.getUser().setName(nameEt.getText().toString().trim());
		} else {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			nameEt.requestFocus();
			return;
		}
		if (mobileNumEt.getText().toString().trim() != null) {
			app.getUser().setMobileNum(mobileNumEt.getText().toString().trim());
		} else {
			Toast.makeText(this, "联系号码不能为空", Toast.LENGTH_SHORT).show();
			mobileNumEt.requestFocus();
			return;
		}
		if (identityFrontImageUrl == null || identityBackImageUrl == null) {
			Toast.makeText(this, "身份证照片不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (dirverLicenseImageUrl == null) {
			Toast.makeText(this, "驾照号码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		verifyUserll.setVisibility(View.GONE);
		verifyTruckll.setVisibility(View.VISIBLE);

	}

	private void showImage(ImageView imageView, String avatar) {
		// imageView.setTag(avatar);
		Bitmap bitmap = mImageLoad.loadImage(imageView, avatar,
				new ImageDownloadedCallBack() {
					@Override
					public void onImageDownloaded(ImageView imageView,
							Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}
				});
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		String name_tmp = app.getUser().getName();
		if (name_tmp != null && !name_tmp.equals(name_current)) {
			name_current = name_tmp;
			nameEt.setText(name_current);
		}
		String mobile_tmp = app.getUser().getMobileNum();
		if (mobile_tmp != null && !mobile_tmp.equals(mobile_current)) {
			mobile_current = mobile_tmp;
			mobileNumEt.setText(mobile_current);
		}

		String identityFrontImageUrl_tmp = app.getUser()
				.getIdentityFrontImageUrl();
		if (identityFrontImageUrl != null
				&& !identityFrontImageUrl_tmp.equals(identityFrontImageUrl)) {
			identityFrontImageUrl = identityFrontImageUrl_tmp;
			showImage(mUserIdentityFrontIv, identityFrontImageUrl);
		}
		String identityBackImageUrl_tmp = app.getUser()
				.getIdentityBackImageUrl();
		if (identityBackImageUrl != null
				&& !identityBackImageUrl_tmp.equals(identityBackImageUrl)) {
			identityBackImageUrl = identityBackImageUrl_tmp;
			showImage(mUserIdentityBackIv, identityBackImageUrl);
		}

		String dirverLicenseImageUrl_tmp = app.getUser()
				.getDriverLicenseImageUrl();
		if (dirverLicenseImageUrl != null
				&& !dirverLicenseImageUrl_tmp.equals(dirverLicenseImageUrl)) {
			dirverLicenseImageUrl = dirverLicenseImageUrl_tmp;
			showImage(mDriverLicenseIv, dirverLicenseImageUrl);
		}

		String vehicleLicenseImageUrl_tmp = app.getUser().getMyTruck()
				.getLicenseImageUrl();
		if (vehicleLicenseImageUrl != null
				&& !vehicleLicenseImageUrl_tmp.equals(vehicleLicenseImageUrl)) {
			vehicleLicenseImageUrl = vehicleLicenseImageUrl_tmp;
			showImage(mVehicleLicenseIv, vehicleLicenseImageUrl);
		}

		String vehiclePhotoImageUrl_tmp = app.getUser().getMyTruck()
				.getPhotoImageUrl();

		if (vehiclePhotoImageUrl != null
				&& !vehiclePhotoImageUrl_tmp.equals(vehiclePhotoImageUrl)) {
			vehiclePhotoImageUrl = vehiclePhotoImageUrl_tmp;
			showImage(mVehiclePhotoIv, vehiclePhotoImageUrl);
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
			}
		}

		public int downloadUserPeofile(long _userId) {
			int resultCode = 0;
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.USER_ID, String.valueOf(_userId)));
			JSONObject response = null;
			try {
				response = CellSiteHttpClient.executeHttpPost(
						CellSiteConstants.USER_QUERY_URL, postParameters);
				resultCode = Integer.parseInt(response.get(
						CellSiteConstants.RESULT_CODE).toString());

				if (resultCode == CellSiteConstants.RESULT_SUC) {
					parseJson(response);
				}
			} catch (Exception e) {
			}
			return resultCode;
		}
	}

	public void parseJson(JSONObject jsonResult) {
		try {
			JSONObject userJson = jsonResult
					.getJSONObject(CellSiteConstants.USER);

			if (userJson.get(CellSiteConstants.PROFILE_IMAGE_URL) != JSONObject.NULL) {
				Log.d(TAG, "get the image url");
				app.getUser()
						.setProfileImageUrl(
								userJson.getString(CellSiteConstants.PROFILE_IMAGE_URL));

			}
			if (userJson.get(CellSiteConstants.DRIVER_LICENSE_URL) != JSONObject.NULL) {
				app.getUser()
						.setDriverLicenseImageUrl(
								userJson.getString(CellSiteConstants.DRIVER_LICENSE_URL));
			}
			if (userJson.get(CellSiteConstants.NAME) != JSONObject.NULL) {
				app.getUser().setName(
						userJson.getString(CellSiteConstants.NAME));
			}
			/*
			 * if (userJson.get(CellSiteConstants.MOBILE) != JSONObject.NULL) {
			 * app.getUser().setMobileNum(
			 * userJson.getString(CellSiteConstants.MOBILE)); }
			 */

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void updateIdentityFrontImage(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(res.getString(R.string.choose_portrait));
		if (v.getId() == R.id.identity_front_iv) {
			tmpFlag = 1;

		} else if (v.getId() == R.id.identity_back_iv) {
			tmpFlag = 2;

		} else if (v.getId() == R.id.driver_license_iv) {
			tmpFlag = 3;

		} else if (v.getId() == R.id.vehicle_license_iv) {
			tmpFlag = 4;

		} else if (v.getId() == R.id.vehicle_photo_iv) {
			tmpFlag = 5;
		}
		builder.setItems(
				new String[] { res.getString(R.string.getPhotoFromCamera),
						res.getString(R.string.getPhotoFromMemory) },
				new DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int id) {
						switch (id) {
						case 0:
							startToCameraActivity(CellSiteConstants.TAKE_ACTION);
							break;
						case 1:
							startToMediaActivity(CellSiteConstants.PICK_ACTION);
							break;
						}
					}

				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Uri uri = null;
		if (requestCode == CellSiteConstants.TAKE_ACTION) {
			uri = imageUri;

		} else if (requestCode == CellSiteConstants.PICK_ACTION) {
			uri = data.getData();

		}

		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, filePathColumn, null,
				null, null);
		if (cursor != null && cursor.moveToFirst()) {
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			imageUri = Uri.fromFile(new File(filePath));
		} else //
		{
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Toast.makeText(this, R.string.sdcard_occupied,
						Toast.LENGTH_SHORT).show();
				return;
			}

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			File tmpFile = new File(CellSiteApplication.getRootDir() + "IMG_"
					+ timeStamp + ".png");
			File srcFile = new File(uri.getPath());
			if (srcFile.exists()) {
				try {
					Utils.copyFile(srcFile, tmpFile);
				} catch (Exception e) {
					Toast.makeText(this, R.string.create_tmp_file_fail,
							Toast.LENGTH_SHORT).show();
					return;
				}
			} else {
				Toast.makeText(this, R.string.file_not_found,
						Toast.LENGTH_SHORT).show();
				return;
			}

			imageUri = Uri.fromFile(tmpFile);
		}

		Bitmap tmpBmp = BitmapFactory.decodeFile(imageUri.getPath(), null);
		Bitmap scaledBmp = Bitmap.createScaledBitmap(tmpBmp,
				CellSiteConstants.IDENTITY_IMAGE_WIDTH,
				CellSiteConstants.IDENTITY_IMAGE_HEIGHT, false);

		// s isChanged = true;
		mUpdateDriverImageTask = new UpdateDriverImageTask();
		switch (tmpFlag) {
		case 1:
			mUpdateDriverImageTask.execute("" + app.getUser().getId(),
					Utils.bitmap2String(scaledBmp),
					CellSiteConstants.UPDATE_USER_IDENTITY_FRONT_URL);
			break;
		case 2:
			mUpdateDriverImageTask.execute("" + app.getUser().getId(),
					Utils.bitmap2String(scaledBmp),
					CellSiteConstants.UPDATE_USER_IDENTITY_BACK_URL);
			break;
		case 3:
			mUpdateDriverImageTask.execute("" + app.getUser().getId(),
					Utils.bitmap2String(scaledBmp),
					CellSiteConstants.UPDATE_DRIVER_LICENSE_URL);
			break;
		case 4:
			mUpdateDriverImageTask.execute("" + app.getUser().getId(),
					Utils.bitmap2String(scaledBmp),
					CellSiteConstants.UPDATE_VEHICLE_LICENSE_URL);
			break;
		case 5:
			mUpdateDriverImageTask.execute("" + app.getUser().getId(),
					Utils.bitmap2String(scaledBmp),
					CellSiteConstants.UPDATE_VEHICLE_PHOTO_URL);
			break;
		}

	}

	/**
	 * start to take picture
	 */
	private void startToCameraActivity(int requestId) {
		Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = new File(app.regUserPath + File.separator + "IMG_"
				+ timeStamp + ".png");
		localIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
		imageUri = Uri.fromFile(mediaFile);

		startActivityForResult(localIntent, requestId);
	}

	/**
	 * start to choose pictue
	 */
	private void startToMediaActivity(int requestId) {
		Intent localIntent = new Intent("android.intent.action.PICK");
		Uri localUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
		localIntent.setDataAndType(localUri, "image/*");
		startActivityForResult(localIntent, requestId);
	}

	protected String updateImage(String _userId, String _bitmap,
			String _updateUrl) {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("bitmap", _bitmap));
		postParameters.add(new BasicNameValuePair(CellSiteConstants.USER_ID,
				_userId));
		if (tmpFlag == 4 || tmpFlag == 5) {
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_ID, ""
							+ app.getUser().getMyTruck().getTruckId()));
		}

		JSONObject response = null;
		String imageUrl = null;
		try {
			response = CellSiteHttpClient.executeHttpPost(_updateUrl,
					postParameters);

			int resultCode = Integer.parseInt(response.get(
					CellSiteConstants.RESULT_CODE).toString());
			if (resultCode == CellSiteConstants.RESULT_SUC) {
				imageUrl = response.get(CellSiteConstants.IMAGE_URL).toString();
				if ((app.getUser().getMyTruck().getTruckId() == 0)
						&& (tmpFlag == 4 || tmpFlag == 5)) {
					app.getUser()
							.getMyTruck()
							.setTruckId(
									(Integer) response
											.get(CellSiteConstants.TRUCK_ID));
					Editor sharedUser = getSharedPreferences(
							CellSiteConstants.CELLSITE_CONFIG, MODE_PRIVATE)
							.edit();
					sharedUser.putInt(CellSiteConstants.TRUCK_ID,
							(Integer) response.get(CellSiteConstants.TRUCK_ID));
					sharedUser.commit();

				}
			}
			return imageUrl;

		} catch (UnknownHostException e) {
			// TODO
		} catch (Exception e) {
			// TODO
		}
		return null;
	}

	private class UpdateDriverImageTask extends
			AsyncTask<String, String, String> {
		@Override
		public String doInBackground(String... params) {
			return updateImage(params[0], params[1], params[2]);
		}

		@Override
		public void onPostExecute(String result) {
			if (this.isCancelled()) {
				return;
			}

			if (result != null) {
				Editor sharedUser = getSharedPreferences(
						CellSiteConstants.CELLSITE_CONFIG, MODE_PRIVATE).edit();
				switch (tmpFlag) {

				case 1:
					String identityFrontImageUrl = result;
					app.getUser().setIdentityFrontImageUrl(
							identityFrontImageUrl);
					showImage(mUserIdentityFrontIv, identityFrontImageUrl);

					sharedUser.putString(
							CellSiteConstants.IDENTITY_FRONT_IMAGE_URL,
							identityFrontImageUrl);
					sharedUser.commit();
					break;
				case 2:
					String identityBackImageUrl = result;
					app.getUser().setIdentityBackImageUrl(identityBackImageUrl);
					showImage(mUserIdentityBackIv, identityBackImageUrl);

					sharedUser.putString(
							CellSiteConstants.IDENTITY_BACK_IMAGE_URL,
							identityBackImageUrl);
					sharedUser.commit();
					break;
				case 3:
					// TODO: set for different user
					dirverLicenseImageUrl = result;
					app.getUser().setDriverLicenseImageUrl(
							dirverLicenseImageUrl);
					showImage(mDriverLicenseIv, dirverLicenseImageUrl);

					sharedUser.putString(CellSiteConstants.DRIVER_LICENSE_URL,
							dirverLicenseImageUrl);
					sharedUser.commit();
					break;

				case 4:
					// TODO: set for different user
					vehicleLicenseImageUrl = result;
					app.getUser().getMyTruck()
							.setLicenseImageUrl(vehicleLicenseImageUrl);
					showImage(mVehicleLicenseIv, vehicleLicenseImageUrl);

					sharedUser.putString(CellSiteConstants.TRUCK_LICENSE_URL,
							vehicleLicenseImageUrl);
					sharedUser.commit();
					break;

				case 5:
					// TODO: set for different user
					vehiclePhotoImageUrl = result;
					app.getUser().getMyTruck()
							.setPhotoImageUrl(vehiclePhotoImageUrl);
					showImage(mVehiclePhotoIv, vehiclePhotoImageUrl);

					sharedUser.putString(CellSiteConstants.TRUCK_IMAGE_URL,
							vehiclePhotoImageUrl);
					sharedUser.commit();
					break;
				}

			}
		}
	}

	public void showChoose() {
		TruckInfoDialog mTruckInfoDialog = new TruckInfoDialog(
				VerifyActivity.this, mTruckListener);
		mTruckInfoDialog.setTitle(R.string.choose_address);
		mTruckInfoDialog.show();
	}

	public void submit(View v) {
		// 1. 检查是否都有值了
		// 2. 提交后，保存一份在本地
		// first check name is
		truckPlateNumber = mTruckPlateNumberEt.getText().toString().trim();

		if (nameEt.getText().toString().trim() == null) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			verifyTruckll.setVisibility(View.GONE);
			verifyUserll.setVisibility(View.VISIBLE);
			nameEt.requestFocus();
			return;
		}
		if (mobileNumEt.getText().toString().trim() == null) {
			Toast.makeText(this, "联系号码不能为空", Toast.LENGTH_SHORT).show();
			verifyUserll.setVisibility(View.VISIBLE);
			verifyTruckll.setVisibility(View.GONE);
			mobileNumEt.requestFocus();
			return;

		}
		if (identityFrontImageUrl == null || identityBackImageUrl == null) {
			Toast.makeText(this, "身份证照片不能为空", Toast.LENGTH_SHORT).show();
			verifyUserll.setVisibility(View.VISIBLE);
			verifyTruckll.setVisibility(View.GONE);
			return;
		}
		if (dirverLicenseImageUrl == null) {
			Toast.makeText(this, "驾驶证不能为空", Toast.LENGTH_SHORT).show();
			verifyUserll.setVisibility(View.VISIBLE);
			verifyTruckll.setVisibility(View.GONE);
			return;
		}

		if (truckPlateNumber == null) {
			Toast.makeText(this, "车牌号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (truckInfo == null) {
			Toast.makeText(this, "车辆型号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		if (vehicleLicenseImageUrl == null) {
			Toast.makeText(this, "车牌不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (vehiclePhotoImageUrl == null) {
			Toast.makeText(this, "车辆图片不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		VerifySubmitTask mVerifySubmitTask = new VerifySubmitTask();

		mVerifySubmitTask.execute("" + truckType, "" + truckLength, ""
				+ truckWeight, truckPlateNumber, mobileNumEt.getText()
				.toString().trim(), nameEt.getText().toString().trim(), ""
				+ app.getUser().getMyTruck().getTruckId());

	}

	private class VerifySubmitTask extends AsyncTask<String, String, Integer> {
		@Override
		public Integer doInBackground(String... params) {
			return verifySubmit(params[0], params[1], params[2], params[3],
					params[4], params[5], params[6]);
		}

		@Override
		public void onPostExecute(Integer result) {
			if (mProgressdialog != null) {
				mProgressdialog.cancel();
			}
			if (this.isCancelled()) {
				return;
			}
			if (result == CellSiteConstants.RESULT_SUC) {
				Editor sharedUser = getSharedPreferences(
						CellSiteConstants.CELLSITE_CONFIG, MODE_PRIVATE).edit();
				sharedUser.putInt(CellSiteConstants.USER_AUDIT_STATUS,
						CellSiteConstants.USER_STATUS_IN_AUDIT);
				sharedUser.putInt(CellSiteConstants.TRUCK_AUDIT_STATUS,
						CellSiteConstants.TRUCK_STATUS_IN_AUDIT);
				sharedUser.putString(CellSiteConstants.NAME, nameEt.getText()
						.toString().trim());

				sharedUser.putString(CellSiteConstants.TRUCK_PLATE,
						truckPlateNumber);

				sharedUser.putInt(CellSiteConstants.TRUCK_LENGTH, truckLength);

				sharedUser.putInt(CellSiteConstants.TRUCK_TYPE, truckType);
				sharedUser.putInt(CellSiteConstants.TRUCK_WEIGHT, truckWeight);
				sharedUser.commit();

				app.getUser().setName(nameEt.getText().toString().trim());
				app.getUser().getMyTruck()
						.setLincenPlateNumber(truckPlateNumber);
				app.getUser().getMyTruck().setTypeId(truckType);
				app.getUser().getMyTruck().setLengthId(truckLength);
				app.getUser().getMyTruck().setWeightId(truckWeight);

				app.getUser().setUserAuditStatus(
						CellSiteConstants.USER_STATUS_IN_AUDIT);
				app.getUser()
						.getMyTruck()
						.setTruckAuditStatus(
								CellSiteConstants.TRUCK_STATUS_IN_AUDIT);
				sendTask();

				//
			}
		}

		protected Integer verifySubmit(String truckType, String truckLength,
				String truckWeight, String licensePlateNumber,
				String mobileNum, String name, String truckID) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_TYPE, truckType));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_LENGTH, truckLength));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_WEIGHT, truckWeight));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_PLATE, licensePlateNumber));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_ID, truckID));
			postParameters.add(new BasicNameValuePair(
					CellSiteConstants.TRUCK_MOBILE_NUM, mobileNum));
			postParameters.add(new BasicNameValuePair(CellSiteConstants.NAME,
					name));

			JSONObject response = null;
			try {
				response = CellSiteHttpClient.executeHttpPost(
						CellSiteConstants.VERIFY_TRUCK_URL, postParameters);

				int resultCode = Integer.parseInt(response.get(
						CellSiteConstants.RESULT_CODE).toString());
				if (resultCode == CellSiteConstants.RESULT_SUC) {

				}
				return resultCode;

			} catch (UnknownHostException e) {
				return CellSiteConstants.UNKNOWN_HOST_ERROR;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return CellSiteConstants.UNKNOWN_ERROR;
		}
	}

	public void sendTask() {
		this.finish();
	}

}
