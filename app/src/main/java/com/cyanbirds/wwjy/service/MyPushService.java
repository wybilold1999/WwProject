package com.cyanbirds.wwjy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.igexin.sdk.GTServiceManager;

/**
 * 核心服务, 继承 android.app.Service, 必须实现以下几个接口, 并在 AndroidManifest 声明该服务并配置成
 * android:process=":pushservice", 具体参考 {@link com.getui.demo.GetuiSdkDemoActivity}
 * PushManager.getInstance().initialize(this.getApplicationContext(), userPushService), 其中
 * userPushService 为 用户自定义服务 即 DemoPushService.
 */
public class MyPushService extends Service {

	public static final String TAG = MyPushService.class.getName();

	@Override
	public void onCreate() {
		super.onCreate();
		GTServiceManager.getInstance().onCreate(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return GTServiceManager.getInstance().onBind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		GTServiceManager.getInstance().onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		GTServiceManager.getInstance().onLowMemory();
	}
}
