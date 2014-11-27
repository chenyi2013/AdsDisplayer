package com.puji.adsdisplayer;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.puji.adsdisplayer.utils.BitmapHelper;
import com.puji.adsdisplayer.utils.FileHelper;

public class MainActivity extends Activity {

	public static final String UPDATE_DATA_ACTION = "com.puji.adsdisplayer.UPDATE_DATA_ACTION";

	private static final int COUNTER_WHAT = 1;
	private static final int UPDATE_DATA_WHAT = 2;

	ViewPager mViewPager;
	ViewPagerAdater mAdater;

	private UpdateDataReceiver mUpdateDataReceiver;

	private Point screenSize;
	private String[] datas;

	int index = 0;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case COUNTER_WHAT:

				index = index >= datas.length ? 0 : index;
				mViewPager.setCurrentItem(index++);
				sendEmptyMessageDelayed(COUNTER_WHAT, 3000);
				break;
			case UPDATE_DATA_WHAT:

				initData();
				if (mAdater != null) {
					mAdater.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initData();
		initView();
		screenSize = getScreenSize();
		mUpdateDataReceiver = new UpdateDataReceiver();
		IntentFilter intentFilter = new IntentFilter(UPDATE_DATA_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mUpdateDataReceiver, intentFilter);

	}

	public void initData() {
		File dir = FileHelper.getDiskCacheDir(this, "image");
		if (dir.exists()) {
			datas = FileHelper.getDiskCacheDir(this, "image").list();
			for (int i = 0; i < datas.length; i++) {
				datas[i] = dir.getAbsolutePath() + File.separator + datas[i];
				System.out.println(datas[i]);
			}
		}
	}

	public void initView() {

		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mAdater = new ViewPagerAdater();
		if (datas != null) {
			mViewPager.setAdapter(mAdater);
			mHandler.sendEmptyMessage(COUNTER_WHAT);
		}

	}

	public Point getScreenSize() {
		Point outSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(outSize);
		return outSize;
	}

	class ViewPagerAdater extends PagerAdapter {

		@Override
		public int getCount() {
			return datas.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			ImageView img = new ImageView(container.getContext());
			ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
			layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
			layoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;
			img.setLayoutParams(layoutParams);
			img.setScaleType(ScaleType.FIT_XY);
			Bitmap bitmap = BitmapHelper.decodeSampledBitmapFromResource(
					datas[position], screenSize.x, screenSize.y);
			img.setImageBitmap(bitmap);
			container.addView(img);
			return img;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView((View) object);
		}
	}

	private class UpdateDataReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(UPDATE_DATA_ACTION)) {

				mHandler.sendEmptyMessage(UPDATE_DATA_WHAT);

			}

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mUpdateDataReceiver);
	}

}
