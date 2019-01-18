package com.cyanbirds.wwjy.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.presenter.SmsCodePresenterImpl;
import com.cyanbirds.wwjy.utils.ProgressDialogUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.cyanbirds.wwjy.view.IUserLoginLogOut;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import cn.smssdk.SMSSDK;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 
 * @ClassName:CaptchaActivity
 * @Description:注册验证码
 * @Author:wangyb
 * @Date:2015年5月11日下午4:14:15
 *
 */
public class RegisterCaptchaActivity extends BaseActivity<IUserLoginLogOut.CheckSmsCodePresenter> implements
		OnClickListener, IUserLoginLogOut.CheckSmsCodeView{

	private EditText mSmsCode;
	private FancyButton mNext;
	private TextView mPhoneNumber;
	private TextView mCountTimer;

	private int mPhoneType;
	private String mPhone;
	private CountDownTimer timer;
	private FancyButton mReGetCap;

	private ClientUser mClientUser;
	private String mCurrrentCity;//定位到的城市

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_captcha);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupViews();
		setupData();
		setEvent();
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mSmsCode = (EditText) findViewById(R.id.sms_code);
		mPhoneNumber = (TextView) findViewById(R.id.phone_number);
		mCountTimer = (TextView) findViewById(R.id.count_timer);
		mNext = (FancyButton) findViewById(R.id.next);
		mReGetCap = (FancyButton) findViewById(R.id.re_get_cap);
	}

	/**
	 * 设置数据
	 */
	private void setupData() {
		mPhoneType = getIntent().getIntExtra(ValueKey.INPUT_PHONE_TYPE, -1);
		mPhone = getIntent().getStringExtra(ValueKey.PHONE_NUMBER);
		mClientUser = (ClientUser) getIntent().getSerializableExtra(ValueKey.USER);
		mCurrrentCity = getIntent().getStringExtra(ValueKey.LOCATION);

		mPhoneNumber.setText("+86" + " " + mPhone);
		// 验证码有效日期
		timer = new CountDownTimer(90000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				mCountTimer.setText((millisUntilFinished / 1000) + "秒后可重发");
			}

			@Override
			public void onFinish() {
				mCountTimer.setEnabled(true);
				mCountTimer.setText(R.string.get_capt_cha);
				mCountTimer.setVisibility(View.GONE);
				mReGetCap.setText("获取验证码");
				mReGetCap.setVisibility(View.VISIBLE);
			}

		};
		timer.start();
	}

	/**
	 * 设置事件
	 */
	private void setEvent() {
		mNext.setOnClickListener(this);
		mReGetCap.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next:
			if (checkInput()) {
				//验证 验证码
				ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_sms_check);
				presenter.checkSmsCode(mSmsCode.getText().toString().trim(), mPhone, mPhoneType);
			}
			break;
		case R.id.count_timer:
		case R.id.re_get_cap:
			mCountTimer.setVisibility(View.VISIBLE);
			mReGetCap.setVisibility(View.GONE);
			//请求验证码
			SMSSDK.getVerificationCode("86", mPhone);
			timer.start();
			break;
		}
	}

	@Override
	public void setPresenter(IUserLoginLogOut.CheckSmsCodePresenter presenter) {
		if (presenter == null) {
			this.presenter = new SmsCodePresenterImpl(this);
		}
	}

	@Override
	public void checkSmsCode(int checkCode) {
		if (checkCode == 200) {
			Intent intent = new Intent();
			if(mPhoneType == 0){//注册
				ProgressDialogUtils.getInstance(RegisterCaptchaActivity.this).dismiss();
				intent.setClass(RegisterCaptchaActivity.this, RegisterSubmitActivity.class);
				intent.putExtra(ValueKey.USER, mClientUser);
				startActivity(intent);
			} else if(mPhoneType == 1){//找回密码
				ProgressDialogUtils.getInstance(RegisterCaptchaActivity.this).dismiss();
				intent.setClass(RegisterCaptchaActivity.this, InputNewPwdActivity.class);
				intent.putExtra(ValueKey.SMS_CODE, mSmsCode.getText().toString().trim());
				intent.putExtra(ValueKey.PHONE_NUMBER, mPhone);
				intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
				startActivity(intent);
			} else if(mPhoneType == 2){//绑定手机
				bandPhone();
			}
		} else {
			ProgressDialogUtils.getInstance(RegisterCaptchaActivity.this).dismiss();
			ToastUtil.showMessage("验证失败");
		}
	}
	
	private void bandPhone() {
		AppManager.getClientUser().isCheckPhone = true;
		AppManager.getClientUser().mobile = mPhone;
		RetrofitFactory.getRetrofit().create(IUserApi.class)
				.updateUserInfo(AppManager.getClientUser().sessionId, getParam(AppManager.getClientUser()))
				.subscribeOn(Schedulers.io())
				.map(responseBody -> {
					AppManager.setClientUser(AppManager.getClientUser());
					AppManager.saveUserInfo();
					JsonObject obj = new JsonParser().parse(responseBody.string()).getAsJsonObject();
					int code = obj.get("code").getAsInt();
					return code;
				})
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(integer -> {
					ProgressDialogUtils.getInstance(RegisterCaptchaActivity.this).dismiss();
					if (integer == 0) {//绑定成功
						ToastUtil.showMessage(R.string.bangding_success);
						setResult(RESULT_OK);
					} else {
						ToastUtil.showMessage(R.string.bangding_faile);
					}
					finish();
				}, throwable -> {
					ProgressDialogUtils.getInstance(RegisterCaptchaActivity.this).dismiss();
					ToastUtil.showMessage(R.string.network_requests_error);
				});
	}
	
	private ArrayMap<String, String> getParam(ClientUser clientUser) {
		ArrayMap<String, String> params = new ArrayMap<>();
		params.put("sex", clientUser.sex);
		params.put("nickName", clientUser.user_name);
		params.put("faceurl", clientUser.face_url);
		if(!TextUtils.isEmpty(clientUser.personality_tag)){
			params.put("personalityTag", clientUser.personality_tag);
		}
		if(!TextUtils.isEmpty(clientUser.part_tag)){
			params.put("partTag", clientUser.part_tag);
		}
		if(!TextUtils.isEmpty(clientUser.intrest_tag)){
			params.put("intrestTag", clientUser.intrest_tag);
		}
		params.put("age", String.valueOf(clientUser.age));
		params.put("signature", clientUser.signature == null ? "" : clientUser.signature);
		params.put("qq", clientUser.qq_no == null ? "" : clientUser.qq_no);
		params.put("wechat", clientUser.weixin_no == null ? "" : clientUser.weixin_no);
		params.put("publicSocialNumber", String.valueOf(clientUser.publicSocialNumber));
		params.put("emotionStatus", clientUser.state_marry == null ? "" : clientUser.state_marry);
		params.put("tall", clientUser.tall == null ? "" : clientUser.tall);
		params.put("weight", clientUser.weight == null ? "" : clientUser.weight);
		params.put("constellation", clientUser.constellation == null ? "" : clientUser.constellation);
		params.put("occupation", clientUser.occupation == null ? "" : clientUser.occupation);
		params.put("education", clientUser.education == null ? "" : clientUser.education);
		params.put("purpose", clientUser.purpose == null ? "" : clientUser.purpose);
		params.put("loveWhere", clientUser.love_where == null ? "" : clientUser.love_where);
		params.put("doWhatFirst", clientUser.do_what_first == null ? "" : clientUser.do_what_first);
		params.put("conception", clientUser.conception == null ? "" : clientUser.conception);
		params.put("isDownloadVip", String.valueOf(clientUser.is_download_vip));
		params.put("goldNum", String.valueOf(clientUser.gold_num));
		params.put("phone", clientUser.mobile);
		params.put("isCheckPhone", String.valueOf(clientUser.isCheckPhone));
		return params;
	}

	/**
	 * 验证输入
	 */
	private boolean checkInput() {
		String message = "";
		boolean bool = true;
		if (TextUtils.isEmpty(mSmsCode.getText().toString())) {
			message = getResources().getString(R.string.input_captcha);
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
