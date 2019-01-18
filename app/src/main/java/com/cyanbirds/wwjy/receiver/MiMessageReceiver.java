package com.cyanbirds.wwjy.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.LauncherActivity;
import com.cyanbirds.wwjy.activity.MainActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.cyanbirds.wwjy.utils.PushMsgUtil;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import static android.os.Looper.getMainLooper;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 DemoMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.xiaomi.mipushdemo.DemoMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、DemoMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、DemoMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、DemoMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、DemoMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、DemoMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author mayixiang
 */
public class MiMessageReceiver extends PushMessageReceiver {

    private Handler mHandler = new Handler(getMainLooper());

    @Override
    public void onReceivePassThroughMessage(Context context, final MiPushMessage message) {
        if (AppManager.getClientUser().isShowVip) {
            mHandler.post(()-> PushMsgUtil.getInstance().handlePushMsg(true, message.getContent()));
        }
    }

    @Override
    public void onNotificationMessageClicked(final Context context, final MiPushMessage message) {
        //当前app未运行则启动，否则直接进入主界面
        //打开自定义的Activity
        if (AppManager.isAppAlive(context, AppManager.pkgName)) {
            if (AppManager.isAppIsInBackground(context)) {
                if (PreferencesUtils.getIsLogin(context)) {
                    Intent mainIntent = new Intent(context, MainActivity.class);
                    if (!TextUtils.isEmpty(message.getContent())) {
                        mainIntent.putExtra(ValueKey.DATA, message.getContent());
                    }
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mainIntent);
                } else {
                    ToastUtil.showMessage(R.string.login_tips);
                }
            } else {
                if (!PreferencesUtils.getIsLogin(context)) {
                    ToastUtil.showMessage(R.string.login_tips);
                }
            }
        } else {
            Intent intent = new Intent();
            if (!TextUtils.isEmpty(message.getContent())) {
                intent.putExtra(ValueKey.DATA, message.getContent());
            }
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.setClass(context, LauncherActivity.class);
            context.startActivity(intent);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, final MiPushMessage message) {
        mHandler.post(()-> PushMsgUtil.getInstance().handlePushMsg(false, message.getContent()));
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                Log.d("test", "小米推送注册成功");
            } else {
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {//设置别名成功
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                Log.d("test", "小米推送设置别名成功");
            } else {
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
            } else {
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
            } else {
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
            } else {
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                Log.d("test", "小米推送订阅主题成功");
            } else {
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
            } else {
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
            } else {
            }
        } else {
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {//注册成功
                if (!"-1".equals(AppManager.getClientUser().userId)) {
                    MiPushClient.setAlias(context, AppManager.getClientUser().userId, null);
                    if ("1".equals(AppManager.getClientUser().sex)) {
                        MiPushClient.subscribe(context, "female", null);
                    } else {
                        MiPushClient.subscribe(context, "male", null);
                    }
                }
            } else {
            }
        } else {
        }
    }
}
