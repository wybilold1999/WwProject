package com.cyanbirds.wwjy.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.db.ConversationSqlManager;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.entity.IMessage;
import com.cyanbirds.wwjy.receiver.NotificationReceiver;
import com.cyanbirds.wwjy.utils.PreferencesUtils;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 通知管理
 * Created by Administrator on 2016/3/14.
 */
public class NotificationManagerUtils {

    public static Context mContext;

    private NotificationManager mNotificationManager;


    public static NotificationManagerUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final NotificationManagerUtils INSTANCE = new NotificationManagerUtils();
    }

    private NotificationManagerUtils() {
        mContext = CSApplication.getInstance().getApplicationContext();
        if (mNotificationManager == null && mContext != null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        }
    }

    /**
     * 显示消息通知
     *
     * @param message
     */
    public void showMessageNotification(IMessage message) {
        try {
            Notification notification = getNotification(message);
            if (notification != null) {
                notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
                mNotificationManager.notify(0, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置消息通知数据
     *
     * @param message
     * @return
     */
    public Notification getNotification(IMessage message) {
        if (message == null)
            return null;
        cancelNotification();

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, AppManager.pkgName);
        String title;
        String content;
        int unReadNum = ConversationSqlManager.getInstance(mContext).getAnalyticsUnReadConversation();
        if (!TextUtils.isEmpty(message.face_url)) {
            title = mContext.getResources().getString(R.string.app_name);
        } else {
            title = message.sender_name;
        }
        content = getTickerText(message);
        builder.setTicker(title + "\n" + content);
        // 设置通知内容的标题
        builder.setContentTitle(title);
        builder.setContentText(content);
        // 设置打开通知，该通知自动消失
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        // 是否震动
        if (PreferencesUtils.getNoticeShock(mContext)) {
            builder.setVibrate(new long[]{300, 100, 300, 100});
        }
        // 是否显示消息声音
        if (PreferencesUtils.getNoticeVoice(mContext)) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }
        builder.setNumber(unReadNum);
        // 通知时间
        builder.setWhen(System.currentTimeMillis());

        ClientUser clientUser = new ClientUser();
        clientUser.user_name = message.sender_name;
        clientUser.userId = message.sender;
        if (!TextUtils.isEmpty(message.face_url)) {
            clientUser.face_url = message.face_url;
            clientUser.isLocalMsg = true;
        }

        Intent intent = new Intent(mContext, NotificationReceiver.class);
        intent.putExtra(ValueKey.USER, clientUser);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 设置通知将要启动程序的intent
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        return notification;
    }

    public Notification getNotification() {
        Notification notification = new NotificationCompat.Builder(mContext, AppManager.pkgName).build();
        return notification;
    }

    /**
     * 获取显示的内容
     *
     * @param message
     * @return
     */
    public final String getTickerText(IMessage message) {
        if (message.msgType == IMessage.MessageType.TEXT) {
            return message.content;
        } else if (message.msgType == IMessage.MessageType.VOICE) {
            return mContext.getResources().getString(R.string.voice_symbol);
        } else if (message.msgType == IMessage.MessageType.IMG) {
            return mContext.getResources().getString(R.string.image_symbol);
        } else if (message.msgType == IMessage.MessageType.LOCATION) {
            return mContext.getResources().getString(R.string.location_symbol);
        } else if (message.msgType == IMessage.MessageType.RED_PKT) {
            return mContext.getResources().getString(R.string.rpt_symbol);
        }
        return "";
    }

    /**
     * 关闭通知
     */
    public void cancelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.deleteNotificationChannel(AppManager.pkgName);
            mNotificationManager.cancelAll();
        } else {
            NotificationManagerCompat.from(mContext).cancelAll();
        }
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = mContext.getString(R.string.chat_message);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(AppManager.pkgName, channelName, importance);
            channel.shouldShowLights();//是否会闪光
            channel.enableLights(true);//是否显示通知指示灯
            channel.enableVibration(true);//是否振动
            channel.setBypassDnd(true);//设置可以绕过请勿打扰模式
            channel.canBypassDnd();//可否绕过请勿打扰模式
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);//锁屏显示通知
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
