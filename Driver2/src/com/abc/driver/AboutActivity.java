package com.abc.driver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.driver.model.VersionInfo;
import com.abc.driver.utility.CellSiteConstants;

public class AboutActivity extends Activity {
	
	private final String TAG = this.getClass().getName();
	private static final int UPDATE_NONEED = 0;
	private static final int UPDATE_CLIENT = 1;
	private static final int GET_UNDATE_INFO_ERROR = 2;
	private static final int DOWN_ERROR = 4;

	private Button getVersionBtn;
	private VersionInfo info;
	private String localVersion;
	
	private final Handler handler = new MyHandler(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);
		localVersion = getVersionName();
		TextView currentVersionTv = (TextView) findViewById(R.id.tvVersionCode);
		currentVersionTv.setText(localVersion);

		getVersionBtn = (Button) findViewById(R.id.btn_getVersion);
		getVersionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					CheckVersionTask cv = new CheckVersionTask();
					new Thread(cv).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	private String getVersionName() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public class CheckVersionTask implements Runnable {
		InputStream is;

		public void run() {
			try {
				URL url = new URL(CellSiteConstants.VERSION_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				int responseCode = conn.getResponseCode();
				if (responseCode == 200) {
					is = conn.getInputStream();
				}
				info = getUpdateInfo(is);

				if (info.getVersion().equals(localVersion)) {
					Log.i(TAG, "版本号相同");
					Message msg = new Message();
					msg.what = UPDATE_NONEED;
					handler.sendMessage(msg);
				} else {
					Log.i(TAG, "版本号不相同 ");
					Message msg = new Message();
					msg.what = UPDATE_CLIENT;
					handler.sendMessage(msg);
				}

			} catch (Exception e) {
				Message msg = new Message();
				msg.what = GET_UNDATE_INFO_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}

	private static class MyHandler extends Handler {
		private final WeakReference<AboutActivity> mActivity;

		public MyHandler(AboutActivity activity) {
			mActivity = new WeakReference<AboutActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_NONEED:
				Toast.makeText(mActivity.get().getApplicationContext(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
				break;
			case UPDATE_CLIENT:
				mActivity.get().showUpdataDialog();
				break;
			case GET_UNDATE_INFO_ERROR:
				Toast.makeText(mActivity.get().getApplicationContext(), "获取服务器更新信息失败", 1).show();
				break;
			case DOWN_ERROR:
				Toast.makeText(mActivity.get().getApplicationContext(), "下载新版本失败", 1).show();
				break;
			}
		}
	};

	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("版本升级");
		String versionMsg = "当前版本：" + localVersion + " ,发现新版本：" + info.getVersion() + " ,是否更新？";
		builer.setMessage(versionMsg);
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "下载apk,更新");
				downLoadApk();
			}
		});

		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}

	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File file = getFileFromServer(info.getUrl(), pd);
					sleep(3000);
					installApk(file);
					pd.dismiss();
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = DOWN_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
				}

			}
		}.start();

	}

	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		startActivity(intent);
	}

	private VersionInfo getUpdateInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		VersionInfo info = new VersionInfo();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("version".equals(parser.getName())) {
					info.setVersion(parser.nextText());
				} else if ("url".equals(parser.getName())) {
					info.setUrl(parser.nextText());
				} else if ("description".equals(parser.getName())) {
					info.setDescription(parser.nextText());
				}
				break;
			}
			type = parser.next();
		}
		return info;
	}

	// 下载apk
	public File getFileFromServer(String path, ProgressDialog pd) throws Exception {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(), "CellSite.apk");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
	}
}