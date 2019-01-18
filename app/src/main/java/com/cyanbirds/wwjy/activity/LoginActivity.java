package com.cyanbirds.wwjy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.eventtype.LocationEvent;
import com.cyanbirds.wwjy.eventtype.WeinXinEvent;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.request.DownloadFileRequest;
import com.cyanbirds.wwjy.presenter.UserLoginPresenterImpl;
import com.cyanbirds.wwjy.utils.AESEncryptorUtil;
import com.cyanbirds.wwjy.utils.FileAccessorUtils;
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

import io.reactivex.Observable;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Administrator on 2016/4/23.
 */
public class LoginActivity extends BaseActivity<IUserLoginLogOut.Presenter> implements View.OnClickListener, IUserLoginLogOut.View {

    EditText loginAccount;
    EditText loginPwd;
    FancyButton btnLogin;
    TextView forgetPwd;
    ImageView weiXinLogin;
    ImageView qqLogin;

    public static Tencent mTencent;
    private UserInfo mInfo;
    private String token;
    private String openId;

    private boolean activityIsRunning;
    private String mPhoneNum;
    private String mCurrrentCity;//定位到的城市

    private Observable<?> observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rxBusSub();
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView() {
        loginAccount = findViewById(R.id.login_account);
        loginPwd = findViewById(R.id.login_pwd);
        btnLogin = findViewById(R.id.btn_login);
        forgetPwd = findViewById(R.id.forget_pwd);
        weiXinLogin = findViewById(R.id.weixin_login);
        qqLogin = findViewById(R.id.qq_login);

    }

    private void setupEvent() {
        btnLogin.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
        qqLogin.setOnClickListener(this);
        weiXinLogin.setOnClickListener(this);
    }

    private void setupData(){
        if (mTencent == null) {
            mTencent = Tencent.createInstance(AppConstants.mAppid, this);
        }
        mPhoneNum = getIntent().getStringExtra(ValueKey.PHONE_NUMBER);
        if(!TextUtils.isEmpty(mPhoneNum)){
            loginAccount.setText(mPhoneNum);
            loginAccount.setSelection(mPhoneNum.length());
        }
        mCurrrentCity = getIntent().getStringExtra(ValueKey.LOCATION);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_login:
                if(checkInput()){
                    String cryptLoginPwd = AESEncryptorUtil.crypt(loginPwd.getText().toString().trim(),
                            AppConstants.SECURITY_KEY);
                    onShowLoading();
                    presenter.onUserLogin(loginAccount.getText().toString().trim(),
                            cryptLoginPwd);
                }
                break;
            case R.id.forget_pwd:
                //0=注册1=找回密码2=验证绑定手机
                intent.setClass(this, FindPasswordActivity.class);
                intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
                intent.putExtra(ValueKey.INPUT_PHONE_TYPE, 1);
                startActivity(intent);
                break;
            case R.id.qq_login:
                ProgressDialogUtils.getInstance(this).show(R.string.wait);
                if (!mTencent.isSessionValid() &&
                        mTencent.getQQToken().getOpenId() == null) {
                    mTencent.login(this, "all", loginListener);
                }
                break;
            case R.id.weixin_login:
                ProgressDialogUtils.getInstance(this).show(R.string.wait);
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                if (null != AppManager.getIWXAPI()) {
                    AppManager.getIWXAPI().sendReq(req);
                } else {
                    CSApplication.api.sendReq(req);
                }
                break;
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
                if (!TextUtils.isEmpty(AppManager.getClientUser().sex)) {
                    onShowLoading();
                    presenter.onWXLogin(event.code);
                } else {
                    Intent intent = new Intent(this, SelectSexActivity.class);
                    intent.putExtra("code", event.code);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onShowLoading() {
        ProgressDialogUtils.getInstance(LoginActivity.this).show(R.string.dialog_request_login);
    }

    @Override
    public void onHideLoading() {
        ProgressDialogUtils.getInstance(LoginActivity.this).dismiss();
    }

    @Override
    public void onShowNetError() {
        onHideLoading();
        ToastUtil.showMessage(R.string.network_requests_error);
    }

    @Override
    public void loginLogOutSuccess(ClientUser clientUser) {
        onHideLoading();
        if (clientUser != null) {
            hideSoftKeyboard();
            File faceLocalFile = new File(FileAccessorUtils.FACE_IMAGE,
                    Md5Util.md5(clientUser.face_url) + ".jpg");
            if(!faceLocalFile.exists()
                    && !TextUtils.isEmpty(clientUser.face_url)){
                new DownloadPortraitTask().request(clientUser.face_url,
                        FileAccessorUtils.FACE_IMAGE,
                        Md5Util.md5(clientUser.face_url) + ".jpg");
            }

            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finishAll();
        } else {
            ToastUtil.showMessage(R.string.phone_pwd_error);
        }
    }

    @Override
    public void setPresenter(IUserLoginLogOut.Presenter presenter) {
        if (presenter == null) {
            this.presenter = new UserLoginPresenterImpl(this);
        }
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
            Util.toastMessage(LoginActivity.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(LoginActivity.this, "取消授权");
        }
    }

    public  void initOpenidAndToken(JSONObject jsonObject) {
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
                    if (!TextUtils.isEmpty(AppManager.getClientUser().sex)) {
                        if (null != presenter) {
                            if (activityIsRunning) {
                                onShowLoading();
                            }
                            presenter.onQQLogin(token, openId);
                        }
                    } else {
                        Intent intent = new Intent(LoginActivity.this, SelectSexActivity.class);
                        intent.putExtra("token", token);
                        intent.putExtra("openId", openId);
                        startActivity(intent);
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


    class DownloadPortraitTask extends DownloadFileRequest {
        @Override
        public void onPostExecute(String s) {
            AppManager.getClientUser().face_local = s;
            PreferencesUtils.setFaceLocal(LoginActivity.this, s);
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
        if (TextUtils.isEmpty(loginAccount.getText().toString())) {
            message = getResources().getString(R.string.input_phone_or_account);
            bool = false;
        } else if (TextUtils.isEmpty(loginPwd.getText().toString())) {
            message = getResources().getString(R.string.input_password);
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
