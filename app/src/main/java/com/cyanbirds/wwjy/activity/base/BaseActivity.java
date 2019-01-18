package com.cyanbirds.wwjy.activity.base;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;


/**
 * 
 * @ClassName:CSSuperActivity
 * @Description:通用父类Activity
 * @Author:wangyb
 * @Date:2015年5月4日下午5:17:01
 *
 */
public class BaseActivity<T extends IBasePresenter> extends AppCompatActivity implements IBaseView<T> {

	protected T presenter;

	private Toolbar mActionBarToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityStackManager.getScreenManager().pushActivity(this);

		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}
		setPresenter(presenter);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		getActionBarToolbar();
	}

	protected Toolbar getActionBarToolbar() {
		if (mActionBarToolbar == null) {
			mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
			if (mActionBarToolbar != null) {
				setSupportActionBar(mActionBarToolbar);
			}
		}
		return mActionBarToolbar;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 显示软键盘
	 */
	public void showSoftKeyboard(View view) {
		if (view.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param
	 */
	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 0);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityStackManager.getScreenManager().popActivity(this);
		if (presenter != null) {
			presenter.detachView();
			presenter = null;
		}
	}

	/**
	 * 退出所有activity栈
	 */
	public static void finishAll() {
		ActivityStackManager.getScreenManager().popAllActivityExceptOne(null);
	}

	/**
	 * 退出程序
	 */
	public static void exitApp() {
		finishAll();
//		System.exit(0);
	}

	@Override
	public void onShowLoading() {

	}

	@Override
	public void onHideLoading() {

	}

	@Override
	public void onShowNetError() {
		onHideLoading();
		ToastUtil.showMessage(R.string.network_requests_error);
	}

	@Override
	public void setPresenter(T presenter) {

	}

	@Override
	public <X> AutoDisposeConverter<X> bindAutoDispose() {
		return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
				.from(this, Lifecycle.Event.ON_DESTROY));
	}
}
