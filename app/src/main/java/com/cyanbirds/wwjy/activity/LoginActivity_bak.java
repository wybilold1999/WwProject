package com.cyanbirds.wwjy.activity;

import android.Manifest;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.presenter.UserLoginPresenterImpl;
import com.cyanbirds.wwjy.utils.AESEncryptorUtil;
import com.cyanbirds.wwjy.utils.CheckUtil;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.cyanbirds.wwjy.utils.ProgressDialogUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.cyanbirds.wwjy.utils.Utils;
import com.cyanbirds.wwjy.view.IUserLoginLogOut;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.cyanbirds.wwjy.config.AppConstants.BAIDU_LOCATION_API;

/**
 * Created by Administrator on 2016/4/23.
 */
public class LoginActivity_bak extends BaseActivity<IUserLoginLogOut.Presenter> implements AMapLocationListener, View.OnClickListener, IUserLoginLogOut.View {

    EditText loginAccount;
    EditText loginPwd;
    FancyButton btnLogin;
    FancyButton btnRegister;
    TextView forgetPwd;

    private String mPhoneNum;
    private final int REQUEST_LOCATION_PERMISSION = 1000;
    private boolean isSecondAccess = false;
    private RxPermissions rxPermissions;

    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mlocationClient;
    private String mCurrrentCity;//定位到的城市
    private String curLat;
    private String curLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        setupView();
        setupEvent();
        setupData();

        getIPAddress();
        initLocationClient();
        requestLocationPermission();
    }

    private void setupView() {
        loginAccount = findViewById(R.id.login_account);
        loginPwd = findViewById(R.id.login_pwd);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        forgetPwd = findViewById(R.id.forget_pwd);

    }

    private void setupEvent() {
        btnLogin.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void setupData(){
        mPhoneNum = getIntent().getStringExtra(ValueKey.PHONE_NUMBER);
        if(!TextUtils.isEmpty(mPhoneNum)){
            loginAccount.setText(mPhoneNum);
            loginAccount.setSelection(mPhoneNum.length());
        }
        mCurrrentCity = getIntent().getStringExtra(ValueKey.LOCATION);
    }

    private void getIPAddress() {
        RetrofitFactory.getRetrofit().create(IUserApi.class)
                .getIPAddress()
                .subscribeOn(Schedulers.io())
                .map(responseBody -> JsonUtils.parseIPJson(responseBody.string()))
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(ipAddress -> {
                    if (!TextUtils.isEmpty(ipAddress)) {
                        getCityByIP(ipAddress);
                    }
                }, throwable -> {});
    }

    private void getCityByIP(String ip) {
        String url = BAIDU_LOCATION_API + ip;
        RetrofitFactory.getRetrofit().create(IUserApi.class)
                .getCityByIP(url)
                .subscribeOn(Schedulers.io())
                .map(responseBody -> JsonUtils.parseCityJson(responseBody.string()))
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(result -> {}, throwable -> {});
    }

    /**
     * 初始化定位
     */
    private void initLocationClient() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取最近3s内精度最高的一次定位结果：
        mLocationOption.setOnceLocationLatest(true);
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //启动定位
        mlocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    private void stopLocation(){
        // 停止定位
        mlocationClient.stopLocation();
    }

    /**
     * 销毁定位
     */
    private void destroyLocation(){
        if (null != mlocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mlocationClient.onDestroy();
            mlocationClient = null;
            mLocationOption = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && !TextUtils.isEmpty(aMapLocation.getCity())) {
            curLat = String.valueOf(aMapLocation.getLatitude());
            curLon = String.valueOf(aMapLocation.getLongitude());
            mCurrrentCity = aMapLocation.getCity();
            PreferencesUtils.setCurrentCity(this, mCurrrentCity);
            PreferencesUtils.setCurrentProvince(LoginActivity_bak.this, aMapLocation.getProvince());
            PreferencesUtils.setLatitude(this, curLat);
            PreferencesUtils.setLongitude(this, curLon);
            if (!TextUtils.isEmpty(mCurrrentCity)) {
                stopLocation();
                PreferencesUtils.setIsLocationSuccess(this, true);
            }
        }
    }

    private void requestLocationPermission() {
        if (!CheckUtil.isGetPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                !CheckUtil.isGetPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (rxPermissions == null) {
                rxPermissions = new RxPermissions(this);
            }
            rxPermissions.requestEachCombined(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe(permission -> {// will emit 1 Permission object
                        if (permission.granted) {
                            // All permissions are granted !
                            startLocation();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // At least one denied permission without ask never again
                            if (!isSecondAccess) {
                                showAccessLocationDialog();
                            }
                        } else {
                            // At least one denied permission with ask never again
                            // Need to go to the settings
                            if (!isSecondAccess) {
                                showAccessLocationDialog();
                            }
                        }
                    }, throwable -> {

                    });
        } else {
            startLocation();
        }
    }

    private void showAccessLocationDialog() {
        isSecondAccess = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_request);
        builder.setMessage(R.string.access_location);
        builder.setPositiveButton(R.string.ok, (dialog, i) -> {
            dialog.dismiss();
            Utils.goToSetting(LoginActivity_bak.this, REQUEST_LOCATION_PERMISSION);
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
            intent.putExtra(ValueKey.LATITUDE, curLat);
            intent.putExtra(ValueKey.LONGITUDE, curLon);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            isSecondAccess = false;
            requestLocationPermission();
        }
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
            case R.id.btn_register:
                intent.setClass(this, RegisterActivity.class);
                intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
                intent.putExtra(ValueKey.LATITUDE, curLat);
                intent.putExtra(ValueKey.LONGITUDE, curLon);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onShowLoading() {
        ProgressDialogUtils.getInstance(LoginActivity_bak.this).show(R.string.dialog_request_login);
    }

    @Override
    public void onHideLoading() {
        ProgressDialogUtils.getInstance(LoginActivity_bak.this).dismiss();
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
            Intent intent = new Intent();
            intent.setClass(LoginActivity_bak.this, MainActivity.class);
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
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        destroyLocation();
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
