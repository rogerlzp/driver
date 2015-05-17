package com.abc.driver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.abc.driver.utility.CellSiteApplication;

public class MainFragmentActivity extends FragmentActivity {

	private ViewPager mTabPager;
	private MyFragmentPageAdapter mFPAdapter;
	private static int NUM_PAGES = 4;
	private ImageView mTabImg;//
	private ImageView mTab1, mTab2, mTab3, mTab4;
	private int zero = 0;
	private int currIndex = 0;
	private int one;
	private int two;
	private int three;

	public static final String TAG = MainFragmentActivity.class.getSimpleName();

	public CellSiteApplication app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_weixin);

		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		app = (CellSiteApplication) this.getApplication();
		mFPAdapter = new MyFragmentPageAdapter(app,
				this.getSupportFragmentManager());
		mTabPager.setAdapter(mFPAdapter);

		mTab1 = (ImageView) findViewById(R.id.img_weixin);
		mTab2 = (ImageView) findViewById(R.id.img_address);
		mTab3 = (ImageView) findViewById(R.id.img_friends);
		mTab4 = (ImageView) findViewById(R.id.img_settings);
		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		mTab1.setOnClickListener(new MyOnClickListener(0));
		mTab2.setOnClickListener(new MyOnClickListener(1));
		mTab3.setOnClickListener(new MyOnClickListener(2));
		mTab4.setOnClickListener(new MyOnClickListener(3));

		Display currDisplay = getWindowManager().getDefaultDisplay();
		int displayWidth = currDisplay.getWidth();
		int displayHeight = currDisplay.getHeight();
		one = displayWidth / 4;
		two = one * 2;
		three = one * 3;

	}

	public static class MyFragmentPageAdapter extends FragmentStatePagerAdapter {
		private CellSiteApplication app;

		public MyFragmentPageAdapter(CellSiteApplication _app,
				FragmentManager fm) {

			super(fm);
			app = _app;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return TruckPlanCreateFragment.newInstance();
			case 1:
				return FHorderFragment.newInstance();
			case 2:

				return MyHorderFragment.newInstance();
			case 3:
				return MeFragment.newInstance();
			default:
				return null;
			}
		}

	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			boolean needAnimation = true;
			switch (arg0) {
			case 0:

				mTab1.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_weixin_pressed));

				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					needAnimation = false;
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					needAnimation = false;
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}

				break;
			case 1:

				mTab2.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_address_pressed));

				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				} else if (currIndex == 3) {
					needAnimation = false;
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}

				break;

			case 2:

				mTab3.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_find_frd_pressed));

				if (currIndex == 0) {
					needAnimation = false;
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_settings_normal));
				}

				break;
			case 3:

				mTab4.setImageDrawable(getResources().getDrawable(
						R.drawable.tab_settings_pressed));

				if (currIndex == 0) {
					needAnimation = false;
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					needAnimation = false;
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_address_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(
							R.drawable.tab_find_frd_normal));
				}

				break;

			}

			FragmentTransaction txn = getSupportFragmentManager()
					.beginTransaction();
			txn.hide(((MyFragmentPageAdapter) mTabPager.getAdapter())
					.getItem(currIndex));

			currIndex = arg0;

			txn.show(
					((MyFragmentPageAdapter) mTabPager.getAdapter())
							.getItem(arg0)).commit();

			// if (needAnimation) {
			// animation.setFillAfter(true);// True:
			// animation.setDuration(150);

			// mTabImg.startAnimation(animation);
			// }

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				moveTaskToBack(false);
				finish();
			}
			return true;

		}
		return super.onKeyDown(keyCode, event);

	}

	public void gotoCreateHorder(View v) {
		mTabPager.setCurrentItem(0);
	}

	public void gotoMyHorder() {
		mTabPager.setCurrentItem(2);
	}
}
