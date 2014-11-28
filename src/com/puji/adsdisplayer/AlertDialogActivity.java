package com.puji.adsdisplayer;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import com.puji.adsdisplayer.config.Config;
import com.puji.adsdisplayer.utils.FileHelper;

public class AlertDialogActivity extends Activity {

	/**
	 * 标识一条消息是用于计数的消息
	 */
	private static final int COUNTER_WHAT = 1;

	/**
	 * 倒计时总时间
	 */
	private static final int COUNTER = 10;

	/**
	 * 倒计时间间隔
	 */
	private static final int DURATION = 1000;

	private TextView mCurrentState;
	private TextView mCountDown;

	/**
	 * 标识从U盘拷贝文件到设备中是否成功
	 */
	private boolean isCopySuccess = false;

	/**
	 * 计数器
	 */
	private int counter = 0;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case COUNTER_WHAT:

				if (counter == COUNTER) {
					Intent intent = new Intent();
					intent.setAction(MainActivity.UPDATE_DATA_ACTION);

					System.out.println("isCopySuccess" + isCopySuccess);
					if (isCopySuccess) {
						LocalBroadcastManager.getInstance(
								AlertDialogActivity.this).sendBroadcast(intent);
					}
					finish();

				}

				mCountDown.setText(String.format(
						getString(R.string.close_window), COUNTER - counter));
				counter++;
				sendEmptyMessageDelayed(COUNTER_WHAT, DURATION);

				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		initView();

		File dir = new File(Config.USB_DEFAULT_SUB_DIR);
		mCurrentState.append(String
				.format(getString(R.string.usb_have_been_ready)));
		mCurrentState
				.append(String.format(getString(R.string.start_copy_file)));

		File tempDir = FileHelper.getDiskCacheDir(this, Config.TEMP_DIR);
		File imageCacheDir = FileHelper.getDiskCacheDir(this,
				Config.IMAGE_CACHE_DIR);

		if (dir.exists() && dir.list().length > 0) {

			if (tempDir.exists() && tempDir.isDirectory()) {
				FileHelper.deleteFiles(tempDir.getAbsolutePath());
			}

			boolean isSuccess = FileHelper.copyFile(Config.USB_DEFAULT_SUB_DIR,
					tempDir.getAbsolutePath(), null);

			if (isSuccess) {
				mCurrentState.append(String
						.format(getString(R.string.copy_file_success)));
				if (imageCacheDir.exists()) {
					if (imageCacheDir.isDirectory()) {
						FileHelper.deleteFiles(imageCacheDir.getAbsolutePath());
						if (imageCacheDir.delete()
								&& tempDir.renameTo(imageCacheDir)) {
							isCopySuccess = true;

						}

					}

				} else {
					if (tempDir.renameTo(imageCacheDir)) {
						isCopySuccess = true;
					}
				}

			} else {
				mCurrentState.append(String
						.format(getString(R.string.copy_file_fail)));
			}

		} else if (dir.exists() && dir.list().length == 0) {

			mCurrentState.append(String.format(
					getString(R.string.dir_not_file), dir.getName()));

		} else {
			mCurrentState.append(String.format(
					getString(R.string.dir_inexistence), dir.getName()));
		}

		handler.sendEmptyMessage(COUNTER_WHAT);
	}

	public void initView() {
		mCurrentState = (TextView) findViewById(R.id.current_state);
		mCountDown = (TextView) findViewById(R.id.count_down);
	}

}
