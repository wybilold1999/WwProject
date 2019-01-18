package com.cyanbirds.wwjy.helper;

import android.content.Context;
import android.util.Log;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.SdkErrorCode;

/**
 * Created by Jorstin on 2015/3/17.
 */
public class SDKCoreHelper implements ECDevice.InitListener , ECDevice.OnECDeviceConnectListener, ECDevice.OnLogoutListener {

    public static final String TAG = "SDKCoreHelper";
    private static SDKCoreHelper sInstance;
    private ECDevice.ECConnectState mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
    private ECInitParams mInitParams;
    private ECInitParams.LoginMode mMode = ECInitParams.LoginMode.FORCE_LOGIN;

    private SDKCoreHelper() {
    }

    public static SDKCoreHelper getInstance() {
        if (sInstance == null) {
            synchronized (SDKCoreHelper.class) {
                if (sInstance == null) {
                    sInstance = new SDKCoreHelper();
                }
            }
        }
        return sInstance;
    }

    public static void init(Context ctx) {
        init(ctx, ECInitParams.LoginMode.AUTO);
    }

    public static void init(Context ctx , ECInitParams.LoginMode mode) {
        ctx = CSApplication.getInstance().getApplicationContext();
        getInstance().mMode = mode;
        // 判断SDK是否已经初始化，没有初始化则先初始化SDK
        if(!ECDevice.isInitialized()) {
            getInstance().mConnect = ECDevice.ECConnectState.CONNECTING;
            ECDevice.initial(ctx, getInstance());
            return;
        }
        // 已经初始化成功，直接进行注册
        getInstance().onInitialized();
    }

    @Override
    public void onInitialized() {
        //初始化聊天管理接口
        IMChattingHelper.getInstance().initECChatManager();
        ECDevice.setOnChatReceiveListener(IMChattingHelper.getInstance());
        ECDevice.setOnDeviceConnectListener(this);

        if (mInitParams == null){
            mInitParams = ECInitParams.createParams();
        }
        mInitParams.reset();
        mInitParams.setUserid(AppManager.getClientUser().userId);
        mInitParams.setAppKey(AppConstants.YUNTONGXUN_ID);
        mInitParams.setToken(AppConstants.YUNTONGXUN_TOKEN);
        mInitParams.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        mInitParams.setMode(getInstance().mMode);

        if(!mInitParams.validate()) {
            ToastUtil.showMessage(R.string.regist_params_error);
            return ;
        } else {
            ECDevice.login(mInitParams);
        }
    }

    @Override
    public void onConnect() {
        // Deprecated
    }

    @Override
    public void onDisconnect(ECError error) {
        // SDK与云通讯平台断开连接
        // Deprecated
    }

    @Override
    public void onConnectState(ECDevice.ECConnectState state, ECError error) {
        if(state == ECDevice.ECConnectState.CONNECT_FAILED ){
//            ToastUtil.showMessage("IM 连接失败="+error.errorCode);
            if(error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                //账号异地登陆
            } else {
                //连接状态失败
            }
            return ;
        }
        else if(state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
//            ToastUtil.showMessage("IM 连接成功");
            // 登陆成功
            mConnect = state;
        }
    }


    @Override
    public void onLogout() {
        getInstance().mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
        if(mInitParams != null && mInitParams.getInitParams() != null) {
            mInitParams.getInitParams().clear();
        }
        mInitParams = null;
    }

    @Override
    public void onError(Exception exception) {
        Log.e(TAG, "ECSDK couldn't start: " + exception.getLocalizedMessage());
        ECDevice.unInitial();
    }


	/**
     * 关闭客户端(true)或者退出当前账号(false)调用的接口
     * @param isNotice
     */
    public static void logout(boolean isNotice) {
    	ECDevice.NotifyMode notifyMode = (isNotice) ? ECDevice.NotifyMode.IN_NOTIFY : ECDevice.NotifyMode.NOT_NOTIFY;
        ECDevice.logout(notifyMode,getInstance());
        release();
    }

    /**
     * 当前SDK注册状态
     * @return
     */
    public static ECDevice.ECConnectState getConnectState() {
        return getInstance().mConnect;
    }

    /**
     * IM聊天功能接口
     * @return
     */
    public static ECChatManager getECChatManager() {
        ECChatManager ecChatManager = ECDevice.getECChatManager();
        Log.d(TAG, "ecChatManager :" + ecChatManager);
        return ecChatManager;
    }

    public static void release() {
        sInstance = null;
        IMChattingHelper.getInstance().destroy();
    }


    /**
     * 判断服务是否自动重启
     * @return 是否自动重启
     */
    public static boolean isUIShowing() {
        return ECDevice.isInitialized();
    }
    
}
