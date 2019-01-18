package com.cyanbirds.wwjy.activity;

import android.Manifest;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.utils.CheckUtil;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.cyanbirds.wwjy.utils.Utils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.cyanbirds.wwjy.config.AppConstants.BAIDU_LOCATION_API;

/**
 * @ClassName:EntranceActivity
 * @Description:登录和注册引导入口
 * @Author:wangyb
 * @Date:2015年5月5日下午5:26:39
 */
public class EntranceActivity extends BaseActivity implements AMapLocationListener {

    @BindView(R.id.login)
    FancyButton mLogin;
    @BindView(R.id.register)
    FancyButton mRegister;

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
        setContentView(R.layout.activity_entrance);
        ButterKnife.bind(this);
        setupViews();
        getIPAddress();
        initLocationClient();
        requestLocationPermission();
    }

    /**
     * 设置视图
     */
    private void setupViews() {
        mLogin = findViewById(R.id.login);
        mRegister = findViewById(R.id.register);
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
            PreferencesUtils.setCurrentProvince(EntranceActivity.this, aMapLocation.getProvince());
            PreferencesUtils.setLatitude(this, curLat);
            PreferencesUtils.setLongitude(this, curLon);
            if (!TextUtils.isEmpty(mCurrrentCity)) {
                stopLocation();
                PreferencesUtils.setIsLocationSuccess(this, true);
            }
        }
    }

    @OnClick({R.id.login, R.id.register})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.login:
                intent.setClass(this, LoginActivity.class);
                if (!TextUtils.isEmpty(AppManager.getClientUser().mobile)) {
                    intent.putExtra(ValueKey.PHONE_NUMBER, AppManager.getClientUser().mobile);
                }
                intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
                intent.putExtra(ValueKey.LATITUDE, curLat);
                intent.putExtra(ValueKey.LONGITUDE, curLon);
                break;
            case R.id.register:
                intent.setClass(this, RegisterActivity.class);
                intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
                intent.putExtra(ValueKey.LATITUDE, curLat);
                intent.putExtra(ValueKey.LONGITUDE, curLon);
                break;
        }
        startActivity(intent);
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
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    private void requestLocationPermission() {
        if (!CheckUtil.isGetPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
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
            Utils.goToSetting(EntranceActivity.this, REQUEST_LOCATION_PERMISSION);
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            isSecondAccess = false;
            requestLocationPermission();
        }
    }
}