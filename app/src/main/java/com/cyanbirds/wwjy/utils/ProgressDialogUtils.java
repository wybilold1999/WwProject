package com.cyanbirds.wwjy.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

/**
 * Created by zxj on 15/6/20.
 */
public class ProgressDialogUtils {

	private final static ProgressDialogUtils instance = new ProgressDialogUtils();
	private Context context;
	private ProgressDialog progressDialog;

	private ProgressDialogUtils() {
	}

	public static ProgressDialogUtils getInstance(Context context) {
		if (instance.context != context) {
			instance.context = context;
			instance.progressDialog = new ProgressDialog(context);
		}

		return instance;
	}

	/**
	 * 单例的progressDialog显示
	 *
	 * @param message
	 */
	public void show(String message) {
		ProgressDialogUtils.show(message, progressDialog);
	}

	/**
	 * 单例的progressDialog显示
	 *
	 * @param message
	 */
	public void show(int message) {
		ProgressDialogUtils.show(context.getResources().getString(message),
				progressDialog);
	}

	/**
	 * 单例的progressDialog隐藏
	 */
	public void dismiss() {
		ProgressDialogUtils.dismiss(progressDialog);
	}

	/**
	 * 单例的progressDialog影藏，在线程中使用
	 */
	public void dismiss(Handler handler) {
		ProgressDialogUtils.dismiss(handler, progressDialog);
	}

	/**
	 * 显示（在UI线程中使用）
	 *
	 * @param message
	 */
	public static void show(String message, ProgressDialog progressDialog) {
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		try {
			progressDialog.show();
		} catch (Exception e) {

		}
	}

	/**
	 * 隐藏（在线程中使用）
	 *
	 * @param handler
	 */
	public static void dismiss(Handler handler,
			final ProgressDialog progressDialog) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
			}
		});
	}

	/**
	 * 隐藏（在UI线程中使用）
	 */
	public static void dismiss(ProgressDialog progressDialog) {
		progressDialog.dismiss();
	}
}
