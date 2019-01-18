package com.cyanbirds.wwjy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.eventtype.LocationEvent;
import com.cyanbirds.wwjy.eventtype.WeinXinEvent;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.net.request.DownloadFileRequest;
import com.cyanbirds.wwjy.presenter.UserLoginPresenterImpl;
import com.cyanbirds.wwjy.utils.CheckUtil;
import com.cyanbirds.wwjy.utils.FileAccessorUtils;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.Md5Util;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.cyanbirds.wwjy.utils.ProgressDialogUtils;
import com.cyanbirds.wwjy.utils.RxBus;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.cyanbirds.wwjy.utils.Util;
import com.cyanbirds.wwjy.view.IUserLoginLogOut;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;

import cn.smssdk.SMSSDK;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Administrator on 2016/4/23.
 */
public class RegisterActivity extends BaseActivity<IUserLoginLogOut.Presenter> implements View.OnClickListener, IUserLoginLogOut.View{

    EditText phoneNum;
    FancyButton next;
    ImageView qqLogin;
    ImageView weiXinLogin;
    ImageView mSelectMan;
    ImageView mSelectLady;

    public static Tencent mTencent;
    private UserInfo mInfo;
    private String token;
    private String openId;

    private ClientUser mClientUser;
    private boolean activityIsRunning;
    private String mCurrrentCity;//定位到的城市

    private Observable<?> observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        rxBusSub();
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        if (mTencent == null) {
            mTencent = Tencent.createInstance(AppConstants.mAppid, this);
        }

        mCurrrentCity = getIntent().getStringExtra(ValueKey.LOCATION);

        setupView();
        setupEvent();
    }

    private void setupView() {
        phoneNum = findViewById(R.id.phone_num);
        next = findViewById(R.id.next);
        weiXinLogin = findViewById(R.id.weixin_login);
        qqLogin = findViewById(R.id.qq_login);
        mSelectMan = findViewById(R.id.select_man);
        mSelectLady = findViewById(R.id.select_lady);

    }

    private void setupEvent() {
        next.setOnClickListener(this);
        mSelectMan.setOnClickListener(this);
        mSelectLady.setOnClickListener(this);
        qqLogin.setOnClickListener(this);
        weiXinLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                if(checkInput()){
                    ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_sms_code);
                    checkPhoneIsRegister();
                }
                break;
            case R.id.qq_login:
                if (!TextUtils.isEmpty(AppManager.getClientUser().sex)) {
                    sendQQRequest();
                } else {
                    ToastUtil.showMessage(R.string.please_select_sex);
                }
                break;
            case R.id.select_man :
                showSelectSexDialog(R.id.select_man);
                break;
            case R.id.select_lady :
                showSelectSexDialog(R.id.select_lady);
                break;
            case R.id.weixin_login:
                if (!TextUtils.isEmpty(AppManager.getClientUser().sex)) {
                    sendWeChatRequest();
                } else {
                    ToastUtil.showMessage(R.string.please_select_sex);
                }
                break;
        }
    }

    private void sendWeChatRequest() {
        ProgressDialogUtils.getInstance(this).show(R.string.wait);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        if (null != AppManager.getIWXAPI()) {
            AppManager.getIWXAPI().sendReq(req);
        } else {
            CSApplication.api.sendReq(req);
        }
    }

    private void sendQQRequest() {
        ProgressDialogUtils.getInstance(this).show(R.string.wait);
        if (!mTencent.isSessionValid() &&
                mTencent.getQQToken().getOpenId() == null) {
            mTencent.login(this, "all", loginListener);
        }
    }

    /**
     * rx订阅
     */
    private void rxBusSub() {
        observable = RxBus.getInstance().register(AppConstants.CITY_WE_CHAT_RESP_CODE);
        observable.subscribe(o -> {
            if (o instanceof LocationEvent) {
                LocationEvent event = (LocationEvent) o;
                mCurrrentCity = event.city;
            } else {
                ProgressDialogUtils.getInstance(this).dismiss();
                WeinXinEvent event = (WeinXinEvent) o;
                if (null != presenter) {
                    onShowLoading();
                    presenter.onWXLogin(event.code);
                }
            }
        });
    }

    @Override
    public void onShowLoading() {
        ProgressDialogUtils.getInstance(RegisterActivity.this).show(R.string.dialog_request_login);
    }

    @Override
    public void onHideLoading() {
        ProgressDialogUtils.getInstance(RegisterActivity.this).dismiss();
    }

    @Override
    public void onShowNetError() {
        onHideLoading();
        ToastUtil.showMessage(R.string.network_requests_error);
    }

    @Override
    public void setPresenter(IUserLoginLogOut.Presenter presenter) {
        if (presenter == null) {
            this.presenter = new UserLoginPresenterImpl(this);
        }
    }

    @Override
    public void loginLogOutSuccess(ClientUser clientUser) {
        onHideLoading();
        if (clientUser != null) {
            File faceLocalFile = new File(FileAccessorUtils.FACE_IMAGE,
                    Md5Util.md5(clientUser.face_url) + ".jpg");
            if(!faceLocalFile.exists()
                    && !TextUtils.isEmpty(clientUser.face_url)){
                new DownloadPortraitTask().request(clientUser.face_url,
                        FileAccessorUtils.FACE_IMAGE,
                        Md5Util.md5(clientUser.face_url) + ".jpg");
            }

            Intent intent = new Intent();
            intent.setClass(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finishAll();
        } else {
            ToastUtil.showMessage(R.string.phone_pwd_error);
        }
    }

    private void checkPhoneIsRegister() {
        RetrofitFactory.getRetrofit().create(IUserApi.class)
                .checkIsRegister(phoneNum.getText().toString().trim())
                .subscribeOn(Schedulers.io())
                .map(responseBody -> JsonUtils.parseCheckIsRegister(responseBody.string()))
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.bindAutoDispose())
                .subscribe(aBoolean -> {
                    onHideLoading();
                    hideSoftKeyboard();
                    if(aBoolean){
                        ToastUtil.showMessage(R.string.phone_already_register);
                    } else {
                        //获取验证码
                        String phone_num = phoneNum.getText().toString().trim();
                        mClientUser.mobile = phone_num;
                        mClientUser.currentCity = mCurrrentCity;
                        SMSSDK.getVerificationCode("86", phone_num);
                        Intent intent = new Intent(RegisterActivity.this, RegisterCaptchaActivity.class);
                        intent.putExtra(ValueKey.PHONE_NUMBER, phone_num);
                        intent.putExtra(ValueKey.INPUT_PHONE_TYPE, 0);
                        intent.putExtra(ValueKey.USER, mClientUser);
                        startActivity(intent);
                    }
                }, throwable -> onShowNetError());
    }

    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                ToastUtil.showMessage("登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                ToastUtil.showMessage("登录失败");
                return;
            }
            doComplete((JSONObject)response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            Util.toastMessage(RegisterActivity.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(RegisterActivity.this, "取消授权");
        }
    }

    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                }
                @Override
                public void onComplete(final Object response) {
                    if (presenter != null) {
                        if (activityIsRunning) {
                            onShowLoading();
                        }
                        presenter.onQQLogin(token, openId);
                    }
                }

                @Override
                public void onCancel() {

                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);
        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
    }

    private void showSelectSexDialog(final int sexId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_sex);
        if(R.id.select_man == sexId) {
            builder.setMessage(String.format(getResources().getString(R.string.select_sex_tips), "男生"));
        } else {
            builder.setMessage(String.format(getResources().getString(R.string.select_sex_tips), "女生"));
        }
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mSelectMan.setEnabled(false);
                        mSelectLady.setEnabled(false);
                        if(mClientUser == null){
                            mClientUser = new ClientUser();
                        }
                        if (sexId == R.id.select_man) {
                            AppManager.getClientUser().sex = "1";
                            mClientUser.sex = "1";
                            mSelectMan.setImageResource(R.mipmap.radio_men_focused_bg);
                        } else {
                            AppManager.getClientUser().sex = "0";
                            mClientUser.sex = "0";
                            mSelectLady.setImageResource(R.mipmap.radio_women_focused_bg);
                        }
                        dialog.dismiss();
                        mClientUser.age = 20;
                    }
                });
        builder.show();
    }



    class DownloadPortraitTask extends DownloadFileRequest {
        @Override
        public void onPostExecute(String s) {
            AppManager.getClientUser().face_local = s;
            PreferencesUtils.setFaceLocal(RegisterActivity.this, s);
        }

        @Override
        public void onErrorExecute(String error) {
        }
    }

    /**
     * 验证输入
     */
    private boolean checkInput() {
        String message = "";
        boolean bool = true;
        if(mSelectMan.isEnabled() && mSelectLady.isEnabled()){
            message = getResources().getString(R.string.please_select_sex);
            bool = false;
        } else if (TextUtils.isEmpty(phoneNum.getText().toString())) {
            message = getResources().getString(R.string.input_phone);
            bool = false;
        } else if (!CheckUtil.isMobileNO(phoneNum.getText().toString())) {
            message = getResources().getString(
                    R.string.input_phone_number_error);
            bool = false;
        }
        if (!bool)
            ToastUtil.showMessage(message);
        return bool;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityIsRunning = true;

        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityIsRunning = false;
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ProgressDialogUtils.getInstance(this).dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(AppConstants.CITY_WE_CHAT_RESP_CODE, observable);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
