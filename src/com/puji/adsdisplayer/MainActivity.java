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

import com.puji.adsdisplayer.config.Config;
import com.puji.adsdisplayer.utils.BitmapHelper;
import com.puji.adsdisplayer.utils.FileHelper;

/**
 * 用于展示广告的Activity
 * 
 * @author Administrator
 * 
 */
public class MainActivity extends Activity {

	/**
	 * 当从U盘更新图片到设备的硬盘成功后会发送此action用来通知程序更新广告图片的数据源
	 */
	public static final String UPDATE_DATA_ACTION = "com.puji.adsdisplayer.UPDATE_DATA_ACTION";

	/**
	 * 用来标识此条消息用于处理计数器
	 */
	private static final int COUNTER_WHAT = 1;
	/**
	 * 用来标识此条消息用于更新数据源
	 */
	private static final int UPDATE_DATA_WHAT = 2;

	/**
	 * ViewPager中更新广告图片的时间间隔
	 */
	private static final int SWITCH_IMAGE_DURATION = 10000;

	/**
	 * 用于显示广告图片的ViewPager
	 */
	private ViewPager mViewPager;
	private ViewPagerAdater mAdater;

	/**
	 * 用于接收UPDATE_DATA_ACTION的广播接收者
	 */
	private UpdateDataReceiver mUpdateDataReceiver;

	/**
	 * 屏幕尺寸
	 */
	private Point screenSize;

	/**
	 * 用于存放广告图片的路径的数据源
	 */
	private String[] datas;

	/**
	 * 展示的广告图片在数据源的索引
	 */
	private int index = 0;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case COUNTER_WHAT:

				index = index >= datas.length ? 0 : index;
				mViewPager.setCurrentItem(index++);
				sendEmptyMessageDelayed(COUNTER_WHAT, SWITCH_IMAGE_DURATION);
				break;
			case UPDATE_DATA_WHAT:
				initData();
				if (datas != null) {

					if (mAdater != null) {
						mAdater.notifyDataSetChanged();
					} else {

						mAdater = new ViewPagerAdater();
						mViewPager.setAdapter(mAdater);
						mAdater.notifyDataSetChanged();
						sendEmptyMessageDelayed(COUNTER_WHAT,
								SWITCH_IMAGE_DURATION);
					}

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
		registerReceiver();

	}

	private void registerReceiver() {
		mUpdateDataReceiver = new UpdateDataReceiver();
		IntentFilter intentFilter = new IntentFilter(UPDATE_DATA_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mUpdateDataReceiver, intentFilter);
	}

	private void unRegisterReceiver() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mUpdateDataReceiver);

	}

	private void initData() {
		File dir = FileHelper.getDiskCacheDir(this, Config.IMAGE_CACHE_DIR);
		if (dir.exists()) {
			datas = dir.list();
			for (int i = 0; i < datas.length; i++) {
				datas[i] = dir.getAbsolutePath() + File.separator + datas[i];
			}
		}
	}

	private void initView() {

		screenSize = getScreenSize();
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		if (datas != null) {
			mAdater = new ViewPagerAdater();
			mViewPager.setAdapter(mAdater);
			mHandler.sendEmptyMessage(COUNTER_WHAT);
		}

	}

	private Point getScreenSize() {
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
		unRegisterReceiver();
	}
}
