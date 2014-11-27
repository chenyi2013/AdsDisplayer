package com.puji.adsdisplayer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;

public class FileHelper {

	public interface Callback {
		public void getCopyFileCount(int total, int copied);

		public void getErrorInfo(String info);
	}

	/**
	 * 将srcDir目录下的所有文件拷贝到destDir目录下
	 * 
	 * @param srcDir
	 * @param destDir
	 * @return
	 */
	public static boolean copyFile(String srcDir, String destDir,
			Callback callback) {

		File dir = new File(srcDir);
		if (dir.exists() && dir.isDirectory()) {

			File dest = new File(destDir);
			if (!dest.exists()) {
				dest.mkdirs();
			}

			int len = -1;
			byte buffer[] = new byte[1024];

			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			File files[] = dir.listFiles();

			for (int i = 0; i < files.length; i++) {

				if (files[i].isFile()
						&& isValidImageSuffix(files[i].getAbsolutePath())
						&& FileHelper.isValidImageFormat(files[i]
								.getAbsolutePath())) {
					try {
						input = new BufferedInputStream(new FileInputStream(
								files[i]));
						output = new BufferedOutputStream(new FileOutputStream(
								destDir + File.separator + files[i].getName()));

						while ((len = input.read(buffer, 0, buffer.length)) > 0) {
							output.write(buffer, 0, len);
						}

						if (callback != null) {
							callback.getCopyFileCount(files.length, i);
						}

						output.flush();
						output.close();
						input.close();

					} catch (FileNotFoundException e) {
						if (callback != null) {
							callback.getErrorInfo("找不到文件:" + files[i]);
						}

					} catch (IOException e) {
						if (callback != null) {
							callback.getErrorInfo("读写文件出现异常");
						}

					}
				}

			}

		} else if (!dir.exists()) {
			if (callback != null) {
				callback.getErrorInfo("目录不存在");
			}
			return false;
		}
		return true;

	}

	/**
	 * 得到图片的缓存目录
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 判断文件的后缀是否是有效的图片格式
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isValidImageSuffix(String file) {
		if (file.endsWith(".jpg") || file.endsWith(".png")
				|| file.endsWith(".bmp") || file.endsWith(".gif")) {

			return true;

		}

		return false;

	}

	/**
	 * 判断是否是可用的图片格式 通过文件头判断文件格式 JPEG (jpg)，文件头：FFD8FF PNG (png)，文件头：89504E47 GIF
	 * (gif)，文件头：47494638 TIFF (tif)，文件头：49492A00
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isValidImageFormat(String file) {

		int param1 = 0;
		int param2 = 0;
		int param3 = 0;
		int param4 = 0;

		try {
			InputStream input = new FileInputStream(new File(file));

			param1 = input.read();
			param2 = input.read();
			param3 = input.read();
			param4 = input.read();

			input.close();

			if (Integer.toHexString(param1).equalsIgnoreCase("42")
					&& Integer.toHexString(param2).equalsIgnoreCase("4d")
					|| Integer.toHexString(param1).equalsIgnoreCase("47")
					&& Integer.toHexString(param2).equalsIgnoreCase("49")
					&& Integer.toHexString(param3).equalsIgnoreCase("46")
					&& Integer.toHexString(param4).equalsIgnoreCase("38")
					|| Integer.toHexString(param1).equalsIgnoreCase("ff")
					&& Integer.toHexString(param2).equalsIgnoreCase("d8")
					&& Integer.toHexString(param3).equalsIgnoreCase("ff")
					|| Integer.toHexString(param1).equalsIgnoreCase("89")
					&& Integer.toHexString(param2).equalsIgnoreCase("50")
					&& Integer.toHexString(param3).equalsIgnoreCase("4e")
					&& Integer.toHexString(param4).equalsIgnoreCase("47")) {

				return true;

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;

	}
}
