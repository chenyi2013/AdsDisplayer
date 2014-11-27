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

	private static final int COUNTER_WHAT = 1;

	private TextView mCurrentState;
	private TextView mCountDown;

	private int counter = 0;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case COUNTER_WHAT:

				if (counter == 10) {
					Intent intent = new Intent();
					intent.setAction(MainActivity.UPDATE_DATA_ACTION);
					LocalBroadcastManager.getInstance(AlertDialogActivity.this)
							.sendBroadcast(intent);
					finish();

				}

				mCountDown.setText(String.format(
						getString(R.string.close_window), 10 - counter));
				counter++;
				sendEmptyMessageDelayed(COUNTER_WHAT, 1000);

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
		
		if (dir.exists()) {

			boolean isSuccess = FileHelper
					.copyFile(Config.USB_DEFAULT_SUB_DIR, FileHelper
							.getDiskCacheDir(this, "image").getAbsolutePath(),
							null);

			if (isSuccess) {
				mCurrentState.append(String
						.format(getString(R.string.copy_file_success)));
				handler.sendEmptyMessage(COUNTER_WHAT);
			} else {
				mCurrentState.append(String
						.format(getString(R.string.copy_file_fail)));
			}

		} else {
			mCurrentState.append(String.format(
					getString(R.string.dir_inexistence), dir));
		}
	}

	public void initView() {
		mCurrentState = (TextView) findViewById(R.id.current_state);
		mCountDown = (TextView) findViewById(R.id.count_down);
	}

}
