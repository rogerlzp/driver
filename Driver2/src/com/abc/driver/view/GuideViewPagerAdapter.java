package com.abc.driver.view;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.abc.driver.GuideActivity;
import com.abc.driver.LoginActivity;
import com.abc.driver.MainFragmentActivity;
import com.abc.driver.R;
import com.abc.driver.model.User;
import com.abc.driver.utility.CellSiteApplication;
import com.abc.driver.utility.CellSiteConstants;

/**
 * 引导页面适配器
 */
public class GuideViewPagerAdapter extends PagerAdapter {

	// 界面列表
	private List<View> views;
	private GuideActivity activity;

	public GuideViewPagerAdapter(List<View> views, GuideActivity activity) {
		this.views = views;
		this.activity = activity;
	}

	// 销毁arg1位置的界面
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(views.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	// 获得当前界面数
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	// 初始化arg1位置的界面
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1), 0);
		if (arg1 == views.size() - 1) {
			ImageView mStartImageButton = (ImageView) arg0.findViewById(R.id.iv_start);
			mStartImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SharedPreferences sp = activity.getSharedPreferences(CellSiteConstants.CELLSITE_CONFIG,
							Context.MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putBoolean(CellSiteConstants.FIRST_LOGIN_FLAG, false);
					editor.commit();

					CellSiteApplication app = (CellSiteApplication) activity.getApplication();
					app.setFirstLogin(false);
					if (app.getUser().getId() == User.INVALID_ID) {
						enterLoginActivity();
					} else {
						enterMainActivity();
					}
				}

			});
		}
		return views.get(arg1);
	}

	private void enterMainActivity() {
		Intent intent = new Intent(activity, MainFragmentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		activity.finish();
	}

	private void enterLoginActivity() {
		Intent intent = new Intent(activity, LoginActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
