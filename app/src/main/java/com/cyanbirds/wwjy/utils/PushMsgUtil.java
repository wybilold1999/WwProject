package com.cyanbirds.wwjy.utils;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.VoipCallActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.db.ConversationSqlManager;
import com.cyanbirds.wwjy.db.IMessageDaoManager;
import com.cyanbirds.wwjy.entity.Conversation;
import com.cyanbirds.wwjy.entity.IMessage;
import com.cyanbirds.wwjy.entity.PushMsgModel;
import com.cyanbirds.wwjy.listener.MessageChangedListener;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.request.DownloadFileRequest;
import com.google.gson.Gson;
import com.yuntongxun.ecsdk.ECMessage;

import java.io.File;

/**
 * 作者：wangyb
 * 时间：2016/10/12 14:02
 * 描述：
 */
public class PushMsgUtil {

	private Gson gson;
	private boolean isPassThrough; //是否是透传消息，是就手动创建通知栏

	private static PushMsgUtil mInstance;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private PushMsgUtil(){
		gson = new Gson();
	}

	public static PushMsgUtil getInstance() {
		if (null == mInstance) {
			synchronized (PushMsgUtil.class) {
				if (null == mInstance) {
					mInstance = new PushMsgUtil();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 处理推送过来的消息，主要是将消息入数据库
	 * @param pushMsgJson
	 */
	public synchronized void handlePushMsg(boolean isPassThroughMsg, String pushMsgJson) {
		isPassThrough = isPassThroughMsg;
		final PushMsgModel pushMsgModel = gson.fromJson(pushMsgJson, PushMsgModel.class);
		if (pushMsgModel != null && !TextUtils.isEmpty(pushMsgModel.sender)) {
			if (pushMsgModel.msgType == PushMsgModel.MessageType.VOIP) {
				if (!AppManager.getTopActivity(CSApplication.getInstance()).equals("com.cyanbirds.wwjy.activity.VoipCallActivity")) {
					if (!AppManager.getClientUser().is_vip || AppManager.getClientUser().gold_num < 100) {
						//当前接收到消息的时间和登录时间相距小于1分钟，就延迟执行
						if (System.currentTimeMillis() - AppManager.getClientUser().loginTime < 60000) {
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									if (!AppManager.getTopActivity(CSApplication.getInstance()).equals("com.cyanbirds.wwjy.activity.VoipCallActivity")) {
										Intent intent = new Intent(CSApplication.getInstance(), VoipCallActivity.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra(ValueKey.IMAGE_URL, pushMsgModel.faceUrl);
										intent.putExtra(ValueKey.USER_NAME, pushMsgModel.senderName);
										CSApplication.getInstance().startActivity(intent);
									}
								}
							}, 60000);//延迟一分钟执行
						} else {
							Intent intent = new Intent(CSApplication.getInstance(), VoipCallActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra(ValueKey.IMAGE_URL, pushMsgModel.faceUrl);
							intent.putExtra(ValueKey.USER_NAME, pushMsgModel.senderName);
							CSApplication.getInstance().startActivity(intent);
						}
					}
				}
			}
			if (System.currentTimeMillis() - AppManager.getClientUser().loginTime < 60000 &&
					pushMsgModel.msgType == PushMsgModel.MessageType.VOIP) {
				if (!AppManager.getClientUser().is_vip || AppManager.getClientUser().gold_num < 100) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							handleConversation(pushMsgModel);
						}
					}, 60000);
				}
			} else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						handleConversation(pushMsgModel);
					}
				});
			}
		}
	}

	private void handleConversation(PushMsgModel pushMsgModel) {
		Conversation conversation = ConversationSqlManager.getInstance(CSApplication.getInstance())
				.queryConversationForByTalkerId(pushMsgModel.sender);
		if (conversation == null) {
			/**
			 * 插入会话
			 */
			conversation = new Conversation();
			if (pushMsgModel.msgType == PushMsgModel.MessageType.TEXT) {//文本消息
				conversation.type = ECMessage.Type.TXT.ordinal();
				conversation.content = pushMsgModel.content;
			} else if (pushMsgModel.msgType == PushMsgModel.MessageType.STICKER ||
					pushMsgModel.msgType == PushMsgModel.MessageType.IMG) {
				conversation.type = ECMessage.Type.IMAGE.ordinal();
				conversation.content = CSApplication.getInstance().getResources()
						.getString(R.string.image_symbol);
			} else if (pushMsgModel.msgType == PushMsgModel.MessageType.VOIP) {
				conversation.type = ECMessage.Type.CALL.ordinal();
				conversation.content = CSApplication.getInstance().getResources()
						.getString(R.string.voip_symbol);
			}
			conversation.talker = pushMsgModel.sender;
			conversation.talkerName = pushMsgModel.senderName;
			conversation.createTime = pushMsgModel.serverTime;
			conversation.faceUrl = pushMsgModel.faceUrl;
			conversation.unreadCount++;
			long conversationId = ConversationSqlManager.getInstance(
					CSApplication.getInstance()).inserConversation(conversation);
			conversation.id = conversationId;
			if (!TextUtils.isEmpty(pushMsgModel.faceUrl)) {
				new DownloadPortraitTask(conversation).request(
						pushMsgModel.faceUrl, FileAccessorUtils.FACE_IMAGE, Md5Util.md5(pushMsgModel.faceUrl) + ".jpg");
			}

			/**
			 * 插入消息
			 */
			insertMessage(conversationId, pushMsgModel);

		} else {//有会话，就判断本地有没有该消息

			if (pushMsgModel != null && !TextUtils.isEmpty(pushMsgModel.msgId)) {
				long count = IMessageDaoManager.getInstance(CSApplication.getInstance())
						.queryIMessageByMsgId(pushMsgModel.msgId);
				if (count == 0) {//本地还没有消息
					/**
					 * 对会话进行更新
					 */
					if (pushMsgModel.msgType == PushMsgModel.MessageType.TEXT) {//文本消息
						conversation.type = ECMessage.Type.TXT.ordinal();
						conversation.content = pushMsgModel.content;
					} else if (pushMsgModel.msgType == PushMsgModel.MessageType.STICKER ||
							pushMsgModel.msgType == PushMsgModel.MessageType.IMG) {
						conversation.type = ECMessage.Type.IMAGE.ordinal();
						conversation.content = CSApplication.getInstance().getResources()
								.getString(R.string.image_symbol);
					} else if (pushMsgModel.msgType == PushMsgModel.MessageType.VOIP) {
						conversation.type = ECMessage.Type.IMAGE.ordinal();
						conversation.content = CSApplication.getInstance().getResources()
								.getString(R.string.voip_symbol);
					}
					conversation.talker = pushMsgModel.sender;
					conversation.talkerName = pushMsgModel.senderName;
					conversation.createTime = pushMsgModel.serverTime;
					conversation.unreadCount++;
					if (!TextUtils.isEmpty(pushMsgModel.faceUrl) &&
							(TextUtils.isEmpty(conversation.localPortrait) ||
									!new File(conversation.localPortrait).exists())) {
						new DownloadPortraitTask(conversation).request(
								pushMsgModel.faceUrl, FileAccessorUtils.FACE_IMAGE, Md5Util.md5(pushMsgModel.faceUrl) + ".jpg");
					} else {
						ConversationSqlManager.getInstance(CSApplication.getInstance())
								.updateConversation(conversation);
					}
					insertMessage(conversation.id, pushMsgModel);
				}

			}
		}
	}

	/**
	 * 将消息插入本地数据库
	 * @param conversationId
	 * @param pushMsgModel
	 */
	private synchronized void insertMessage(long conversationId, PushMsgModel pushMsgModel) {
		IMessage message = new IMessage();
		message.msgId = pushMsgModel.msgId;
		message.talker = AppManager.getClientUser().userId;
		message.sender = pushMsgModel.sender;
		message.sender_name = pushMsgModel.senderName;
		message.isRead = false;
		message.isSend = IMessage.MessageIsSend.RECEIVING;
		message.create_time = pushMsgModel.serverTime;
		message.send_time = message.create_time;
		message.status = IMessage.MessageStatus.RECEIVED;
		message.conversationId = conversationId;
		if (pushMsgModel.msgType == PushMsgModel.MessageType.TEXT) {//文本消息
			message.msgType = IMessage.MessageType.TEXT;
			message.content = pushMsgModel.content;
		} else if (pushMsgModel.msgType == PushMsgModel.MessageType.STICKER ||
				pushMsgModel.msgType == PushMsgModel.MessageType.IMG) {
			message.msgType = IMessage.MessageType.IMG;
			message.fileUrl = pushMsgModel.fileUrl;
			message.content = CSApplication.getInstance().getResources()
					.getString(R.string.image_symbol);
		} else if (pushMsgModel.msgType == PushMsgModel.MessageType.VOIP) {
			message.msgType = IMessage.MessageType.VOIP;
			message.content = "未接听";
		}

		IMessageDaoManager.getInstance(CSApplication.getInstance()).insertIMessage(message);
		/**
		 * 通知会话界面的改变
		 */
		MessageChangedListener.getInstance().notifyMessageChanged(String.valueOf(conversationId));
		/*if (isPassThrough && !AppActivityLifecycleCallbacks.getInstance().getIsForeground()) {
			AppManager.showNotification(message);
		}*/
		/*if (isPassThrough && AppManager.isAppIsInBackground(CSApplication.getInstance())) {
			AppManager.showNotification(message);
		}*/
		/**
		 * 只要是透传消息，就创建通知栏
		 */
		if (isPassThrough && pushMsgModel.msgType != PushMsgModel.MessageType.VOIP) {
			AppManager.showNotification(message);
		}
	}

	/**
	 * 下载头像
	 */
	class DownloadPortraitTask extends DownloadFileRequest {
		private Conversation mConversation;

		public DownloadPortraitTask(Conversation conversation) {
			mConversation = conversation;
		}

		@Override
		public void onPostExecute(String s) {
			mConversation.localPortrait = s;
			ConversationSqlManager.getInstance(CSApplication.getInstance())
					.updateConversation(mConversation);
			MessageChangedListener.getInstance().notifyMessageChanged("");
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}
}
