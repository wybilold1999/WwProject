package com.cyanbirds.wwjy.activity;

import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.ProgressDialogUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 
 * @Description:免责声明
 * @author wangyb
 * @Date:2015年7月13日下午2:21:46
 */
public class NoResponsibilityActivity extends BaseActivity {

	private TextView mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_responsibility);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupViews();
		setupEvent();
		setupData();
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mContent = findViewById(R.id.content);
	}

	/**
	 * 设置事件
	 */
	private void setupEvent() {
	}

	/**
	 * 设置数据
	 */
	private void setupData() {
		ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_data);
		RetrofitFactory.getRetrofit().create(IUserApi.class)
				.getFareActivityInfo(AppManager.getClientUser().sessionId)
				.subscribeOn(Schedulers.io())
				.map(responseBody -> JsonUtils.parseNoResponsibilityModel(responseBody.string()))
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(model -> {
					ProgressDialogUtils.getInstance(NoResponsibilityActivity.this).dismiss();
					if (model != null) {
						mContent.setText(model.getRule);
					}
				}, throwable -> {
					ProgressDialogUtils.getInstance(NoResponsibilityActivity.this).dismiss();
					ToastUtil.showMessage(R.string.data_load_error);
				});
	}



	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
		MobclickAgent.onPause(this);
	}

}
