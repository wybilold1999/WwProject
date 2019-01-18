package com.cyanbirds.wwjy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.presenter.UserLoginPresenterImpl;
import com.cyanbirds.wwjy.utils.AESEncryptorUtil;
import com.cyanbirds.wwjy.utils.CheckUtil;
import com.cyanbirds.wwjy.utils.ProgressDialogUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.cyanbirds.wwjy.view.IUserLoginLogOut;
import com.umeng.analytics.MobclickAgent;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 
 * @Description:注册提交
 * @Author:wangyb
 * @Date:2015年5月12日上午11:43:42
 *
 */
public class RegisterSubmitActivity extends BaseActivity<IUserLoginLogOut.Presenter> implements
		OnClickListener,IUserLoginLogOut.View {

	private EditText mNickname;
	private EditText mPassword;
	private EditText mConfirmPassword;
	private FancyButton mRegister;

	private ClientUser mClientUser;
	private String channelId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_submit);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupViews();
		setEvent();
		setupData();
		channelId = CheckUtil.getAppMetaData(this, "UMENG_CHANNEL");
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mNickname = findViewById(R.id.nickname);
		mPassword = findViewById(R.id.password);
		mConfirmPassword = findViewById(R.id.confirm_password);
		mRegister = findViewById(R.id.register);
	}

	/**
	 * 设置数据
	 */
	private void setupData() {
		mClientUser = (ClientUser) getIntent().getSerializableExtra(ValueKey.USER);
	}

	/**
	 * 设置事件
	 */
	private void setEvent() {
		mRegister.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register:
			if (checkInput()) {
				mClientUser.user_name = mNickname.getText().toString().trim();
				String securityPwd = AESEncryptorUtil.crypt(mPassword.getText().toString().trim(),
						AppConstants.SECURITY_KEY);
				mClientUser.userPwd = securityPwd;
				ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_register_login);
				presenter.onRegist(mClientUser, channelId);
			}
			break;
		}
	}

	@Override
	public void loginLogOutSuccess(ClientUser clientUser) {
		ProgressDialogUtils.getInstance(RegisterSubmitActivity.this).dismiss();
		if (clientUser != null) {
			hideSoftKeyboard();
			Intent intent = new Intent(RegisterSubmitActivity.this, MainActivity.class);
			startActivity(intent);
			finishAll();
		} else {
			ToastUtil.showMessage(R.string.register_error);
		}
	}

	@Override
	public void setPresenter(IUserLoginLogOut.Presenter presenter) {
		if (presenter == null) {
			this.presenter = new UserLoginPresenterImpl(this);
		}
	}


	/**
	 * 验证输入
	 */
	private boolean checkInput() {
		String message = "";
		boolean bool = true;
		if (TextUtils.isEmpty(mNickname.getText().toString())) {
			message = getResources().getString(R.string.input_nickname);
			bool = false;
		} else if (TextUtils.isEmpty(mPassword.getText().toString())) {
			message = getResources().getString(R.string.input_password);
			bool = false;
		} else if (!mPassword.getText().toString()
				.equals(mConfirmPassword.getText().toString())) {
			message = getResources().getString(
					R.string.input_password_different);
			bool = false;
		}
		if (!bool)
			ToastUtil.showMessage(message);
		return bool;
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
