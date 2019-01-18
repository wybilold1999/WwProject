package com.cyanbirds.wwjy.activity;

import android.Manifest;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.helper.IMChattingHelper;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.manager.NotificationManagerUtils;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.net.request.DownloadFileRequest;
import com.cyanbirds.wwjy.utils.CheckUtil;
import com.cyanbirds.wwjy.utils.FileAccessorUtils;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.Md5Util;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.cyanbirds.wwjy.utils.PushMsgUtil;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @ClassName:LauncherActivity
 * @Description:启动界面
 * @Author:wangyb
 * @Date:2015年5月4日下午5:18:59
 */
public class LauncherActivity extends AppCompatActivity {

    private long mStartTime;// 开始时间
    private final int SHOW_TIME_MIN = 1500;// 最小显示时间
    private final int LONG_SCUESS = 0;
    private final int LONG_FAIURE = 1;
    private RxPermissions rxPermissions;
    /**
     * 手机权限
     */
    public final static int REQUEST_PERMISSION_READ_PHONE_STATE = 1000;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case LONG_SCUESS:
                    long loadingTime = System.currentTimeMillis() - mStartTime;// 计算一下总共花费的时间
                    if (loadingTime < SHOW_TIME_MIN) {// 如果比最小显示时间还短，就延时进入MainActivity，否则直接进入
                        mHandler.postDelayed(mainActivity, SHOW_TIME_MIN
                                - loadingTime);
                    } else {
                        mHandler.postDelayed(mainActivity, 0);
                    }
                    break;
                case LONG_FAIURE:
                    mHandler.postDelayed(noLogin, 0);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartTime = System.currentTimeMillis();// 记录开始时间
        NotificationManagerUtils.getInstance().cancelNotification();
        NotificationManagerUtils.getInstance().createNotificationChannel();
        getKeys();
        requestPermission();
    }

    private void requestPermission() {
        if (!CheckUtil.isGetPermission(this, Manifest.permission.READ_PHONE_STATE) ||
                !CheckUtil.isGetPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (rxPermissions == null) {
                rxPermissions = new RxPermissions(this);
            }
            rxPermissions.requestEachCombined(Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(permission -> {// will emit 1 Permission object
                        if (permission.granted) {
                            // All permissions are granted !
                            init();
                            loadData();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // At least one denied permission without ask never again
                            init();
                            loadData();
                        } else {
                            // At least one denied permission with ask never again
                            // Need to go to the settings
                            init();
                            loadData();
                        }
                    }, throwable -> {

                    });
        } else {
            init();
            loadData();
        }
    }

    Runnable mainActivity = () -> {
        Intent intent = new Intent(LauncherActivity.this,
                MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    };

    private void init() {
        if (AppManager.isLogin()) {//是否已经登录
            login();
        } else {
            if (AppManager.getClientUser() != null
					&& !TextUtils.isEmpty(AppManager.getClientUser().userId)){
				mHandler.postDelayed(firstLauncher, SHOW_TIME_MIN);
			} else {
				mHandler.postDelayed(firstLauncher, SHOW_TIME_MIN);
			}

        }
    }

    private void getKeys() {
        RetrofitFactory.getRetrofit().create(IUserApi.class)
                .getIdKeys()
                .subscribeOn(Schedulers.io())
                .map(responseBody -> JsonUtils.parseJsonIdKeys(responseBody.string()))
                .doOnNext(allKeys -> {
                    AppConstants.WEIXIN_ID = allKeys.weChatId;
                    AppConstants.WEIXIN_PAY_ID = allKeys.weChatPayId;
                    AppConstants.YUNTONGXUN_ID = allKeys.ytxId;
                    AppConstants.YUNTONGXUN_TOKEN = allKeys.ytxKey;
                    AppConstants.CHAT_LIMIT = allKeys.chatLimit;
                    registerWeiXin();
                })
                .doOnError(throwable -> registerWeiXin())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.bindAutoDispose())
                .subscribe(allKeys -> {

                }, throwable -> {});
    }

    private void registerWeiXin() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        AppManager.setIWXAPI(WXAPIFactory.createWXAPI(this, AppConstants.WEIXIN_ID, true));
        AppManager.getIWXAPI().registerApp(AppConstants.WEIXIN_ID);

        AppManager.setIWX_PAY_API(WXAPIFactory.createWXAPI(this, AppConstants.WEIXIN_PAY_ID, true));
        AppManager.getIWX_PAY_API().registerApp(AppConstants.WEIXIN_PAY_ID);
    }

	/**
     * 点击通知栏的消息，将消息入库
     */
    private void loadData() {
        String msg = getIntent().getStringExtra(ValueKey.DATA);
        if (!TextUtils.isEmpty(msg)) {
            PushMsgUtil.getInstance().handlePushMsg(false, msg);
        }
    }

    /**
     * 没有登录
     */
    Runnable noLogin = () -> {
        Intent intent = new Intent(LauncherActivity.this,
                LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    };

    /**
     * 第一次进入
     */
    Runnable firstLauncher = () -> {
        Intent intent = new Intent(LauncherActivity.this,
                EntranceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    };

    /**
     * 登录
     */
    private void login() {
        try {
            String userId = AppManager.getClientUser().userId;
            String userPwd = AppManager.getClientUser().userPwd;
            if(!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userPwd)){
                ArrayMap<String, String> params = new ArrayMap<>();
                params.put("account", userId);
                params.put("pwd", userPwd);
                params.put("deviceName", AppManager.getDeviceName());
                params.put("appVersion", String.valueOf(AppManager.getVersionCode()));
                params.put("systemVersion", AppManager.getDeviceSystemVersion());
                params.put("deviceId", AppManager.getDeviceId());
                params.put("channel", CheckUtil.getAppMetaData(CSApplication.getInstance(), "UMENG_CHANNEL"));
                if (!TextUtils.isEmpty(AppManager.getClientUser().currentCity)) {
                    params.put("currentCity", AppManager.getClientUser().currentCity);
                } else {
                    params.put("currentCity", "");
                }
                params.put("province", PreferencesUtils.getCurrentProvince(CSApplication.getInstance()));
                params.put("latitude", PreferencesUtils.getLatitude(CSApplication.getInstance()));
                params.put("longitude", PreferencesUtils.getLongitude(CSApplication.getInstance()));
                params.put("loginTime", String.valueOf(PreferencesUtils.getLoginTime(CSApplication.getInstance())));
                RetrofitFactory.getRetrofit().create(IUserApi.class)
                        .userLogin(AppManager.getClientUser().sessionId, params)
                        .subscribeOn(Schedulers.io())
                        .flatMap(responseBody -> {
                            ClientUser clientUser = JsonUtils.parseClientUser(responseBody.string());
                            return Observable.just(clientUser);
                        })
                        .doOnNext(clientUser -> {
                            File faceLocalFile = new File(FileAccessorUtils.FACE_IMAGE,
                                    Md5Util.md5(clientUser.face_url) + ".jpg");
                            if(!faceLocalFile.exists()
                                    && !TextUtils.isEmpty(clientUser.face_url)){
                                new DownloadPortraitTask().request(clientUser.face_url,
                                        FileAccessorUtils.FACE_IMAGE,
                                        Md5Util.md5(clientUser.face_url) + ".jpg");
                            } else {
                                clientUser.face_local = faceLocalFile.getAbsolutePath();
                            }
                            AppManager.setClientUser(clientUser);
                            AppManager.saveUserInfo();
                            AppManager.getClientUser().loginTime = System.currentTimeMillis();
                            PreferencesUtils.setLoginTime(LauncherActivity.this, System.currentTimeMillis());
                            IMChattingHelper.getInstance().sendInitLoginMsg();
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .as(this.bindAutoDispose())
                        .subscribe(clientUser -> {
                            if (clientUser == null) {
                                ToastUtil.showMessage(R.string.network_requests_error);
                                mHandler.sendEmptyMessage(LONG_FAIURE);
                            } else {
                                mHandler.sendEmptyMessage(LONG_SCUESS);
                            }
                        }, throwable -> {
                            ToastUtil.showMessage(R.string.network_requests_error);
                            mHandler.sendEmptyMessage(LONG_FAIURE);
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(LONG_FAIURE);
        }
    }

    class DownloadPortraitTask extends DownloadFileRequest {
        @Override
        public void onPostExecute(String s) {
            AppManager.getClientUser().face_local = s;
            PreferencesUtils.setFaceLocal(LauncherActivity.this, s);
        }

        @Override
        public void onErrorExecute(String error) {
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {// 禁用返回键
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mainActivity);
        mHandler.removeCallbacks(firstLauncher);
        mHandler.removeCallbacks(noLogin);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_READ_PHONE_STATE) {
            requestPermission();
        }
    }

    public <X> AutoDisposeConverter<X> bindAutoDispose() {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
                .from(this, Lifecycle.Event.ON_DESTROY));
    }

}
