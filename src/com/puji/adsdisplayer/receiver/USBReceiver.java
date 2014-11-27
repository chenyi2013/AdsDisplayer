package com.puji.adsdisplayer.receiver;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.puji.adsdisplayer.AlertDialogActivity;
import com.puji.adsdisplayer.config.Config;

public class USBReceiver extends BroadcastReceiver {

	/**
	 * USB存储设备插入时系统发出的广播
	 */
	private static final String USB_ATTACHED_ACTION = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

	public USBReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		switch (intent.getAction()) {

		case USB_ATTACHED_ACTION:
			break;

		case Intent.ACTION_MEDIA_CHECKING:

			break;
		case Intent.ACTION_MEDIA_MOUNTED:

			File file = new File(Config.USB_DIR_PATH);
			System.out.println("ACTION_MEDIA_MOUNTED");
			if (file.exists()) {
				Intent newIntent = new Intent(context,
						AlertDialogActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);
			}

			break;
		case Intent.ACTION_MEDIA_EJECT:
			System.out.println("ACTION_MEDIA_EJECT");
			break;
		case Intent.ACTION_MEDIA_REMOVED:
			System.out.println("ACTION_MEDIA_REMOVED");
			break;

		default:
			break;
		}
	}
}
