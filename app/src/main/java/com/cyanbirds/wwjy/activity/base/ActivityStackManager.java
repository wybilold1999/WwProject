package com.cyanbirds.wwjy.activity.base;

import android.support.v7.app.AppCompatActivity;

import java.util.Stack;

/**
 * 
 * @ClassName:ActivityStackManager
 * @Description:Activity管理
 * @Author: wangyb
 * @Date:2015年3月30日下午4:39:26
 *
 */
public class ActivityStackManager {

	private static Stack<AppCompatActivity> activityStack;
	private static ActivityStackManager instance;

	private ActivityStackManager() {
	}

	public static ActivityStackManager getScreenManager() {
		if (instance == null) {
			instance = new ActivityStackManager();
		}
		return instance;
	}

	// 退出栈顶Activity
	public void popActivity(AppCompatActivity activity) {
		if (activity != null) {
			// 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}

	// 获得当前栈顶Activity
	public AppCompatActivity currentActivity() {
		AppCompatActivity activity = null;
		if (!activityStack.empty())
			activity = activityStack.lastElement();
		return activity;
	}

	// 将当前Activity推入栈中
	public void pushActivity(AppCompatActivity activity) {
		if (activityStack == null) {
			activityStack = new Stack<AppCompatActivity>();
		}
		activityStack.add(activity);
	}
	
	
	public void removeActivity(Class<?> cls){
		for (android.support.v7.app.AppCompatActivity AppCompatActivity : activityStack) {
			if (AppCompatActivity.getClass().equals(cls)) {
				popActivity(AppCompatActivity);
			}
		}
	}

	// 退出栈中所有Activity
	public void popAllActivityExceptOne(Class<?> cls) {
		while (true) {
			AppCompatActivity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}
}