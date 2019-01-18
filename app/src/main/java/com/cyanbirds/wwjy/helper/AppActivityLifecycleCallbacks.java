package com.cyanbirds.wwjy.helper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * 作者：wangyb
 * 时间：2016/10/14 22:28
 * 描述：
 */
public class AppActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

	private int mForegroundActivities;
	private boolean isForeground;

	private static AppActivityLifecycleCallbacks mInstance;
	private AppActivityLifecycleCallbacks(){}

	public static AppActivityLifecycleCallbacks getInstance() {
		if (mInstance == null) {
			synchronized (AppActivityLifecycleCallbacks.class) {
				if (mInstance == null) {
					mInstance = new AppActivityLifecycleCallbacks();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 是否运行在前台
	 * @return
	 */
	public boolean getIsForeground() {
		return isForeground;
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {
		mForegroundActivities++;
	}

	@Override
	public void onActivityResumed(Activity activity) {
		isForeground = true;
	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {
		mForegroundActivities--;
		if (mForegroundActivities == 0) {
			isForeground = false;
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {

	}
}
