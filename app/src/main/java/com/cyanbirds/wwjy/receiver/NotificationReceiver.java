package com.cyanbirds.wwjy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.activity.ChatActivity;
import com.cyanbirds.wwjy.activity.LauncherActivity;
import com.cyanbirds.wwjy.activity.PersonalInfoActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.db.ConversationSqlManager;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.entity.Conversation;
import com.cyanbirds.wwjy.listener.MessageChangedListener;
import com.cyanbirds.wwjy.listener.MessageUnReadListener;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.manager.NotificationManagerUtils;
import com.cyanbirds.wwjy.service.ConnectServerService;

/**
 * 通知广播
 * Created by Administrator on 2016/3/14.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (AppManager.isAppAlive(context, AppManager.getPackageName())
        if (AppManager.isServiceRunning(context, ConnectServerService.class.getName())
                && AppManager.getClientUser() != null) {
            ClientUser clientUser = (ClientUser) intent.getSerializableExtra(ValueKey.USER);
            if (clientUser != null) {
                NotificationManagerUtils.getInstance().cancelNotification();
                Conversation conversation = ConversationSqlManager.getInstance(CSApplication.getInstance())
                        .queryConversationForByTalkerId(clientUser.userId);
                if (conversation != null) {
                    conversation.unreadCount = 0;
                    ConversationSqlManager.getInstance(context).updateConversation(conversation);
                    MessageUnReadListener.getInstance().notifyDataSetChanged(0);
                    MessageChangedListener.getInstance().notifyMessageChanged("");
                }
                if (clientUser.isLocalMsg) {
                    Intent chatIntent = new Intent(context, PersonalInfoActivity.class);
                    chatIntent.putExtra(ValueKey.USER_ID, clientUser.userId);
                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(chatIntent);
                } else {
                    Intent chatIntent = new Intent(context, ChatActivity.class);
                    chatIntent.putExtra(ValueKey.USER, clientUser);
                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(chatIntent);
                }
            }
        } else {
            Log.e("tanlove_log", "notify");
            Intent launcherIntent = new Intent(context, LauncherActivity.class);
            context.startActivity(launcherIntent);
        }
    }
}
