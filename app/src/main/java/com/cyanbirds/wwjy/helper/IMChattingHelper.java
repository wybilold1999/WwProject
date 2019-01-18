package com.cyanbirds.wwjy.helper;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.db.ConversationSqlManager;
import com.cyanbirds.wwjy.db.IMessageDaoManager;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.entity.Conversation;
import com.cyanbirds.wwjy.entity.IMessage;
import com.cyanbirds.wwjy.listener.FileProgressListener;
import com.cyanbirds.wwjy.listener.MessageCallbackListener;
import com.cyanbirds.wwjy.listener.MessageChangedListener;
import com.cyanbirds.wwjy.listener.MessageStatusReportListener;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.utils.CheckUtil;
import com.cyanbirds.wwjy.utils.FileUtils;
import com.cyanbirds.wwjy.utils.ImageUtil;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.im.ECFileMessageBody;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECLocationMessageBody;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECUserStateMessageBody;
import com.yuntongxun.ecsdk.im.ECVideoMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/28.
 */
public class IMChattingHelper implements OnChatReceiveListener{
	private static IMChattingHelper mInstance;
	private Context mContext;
	private ECChatManager mChatManager; //聊天接口

	private int mHistoryMsgCount = 0; //离线消息数量
	/** 是否是同步消息 */
	private boolean isSyncOffline = false;
	private List<IMessage> offlineMsg = new ArrayList<>();
	private static final String CITY = "深圳市";

	public static IMChattingHelper getInstance() {
		return IMChattingHelper.SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		private static final IMChattingHelper INSTANCE = new IMChattingHelper();
	}

	private IMChattingHelper() {
		mContext = CSApplication.getInstance().getApplicationContext();
	}

	public void initECChatManager() {
		mChatManager = SDKCoreHelper.getECChatManager();
	}

	public long sendTextMsg(final ClientUser clientUser, final String msgContent) {
		// 组建一个待发送的ECMessage
		ECMessage ecMessagee = ECMessage.createECMessage(ECMessage.Type.TXT);
		ecMessagee.setDirection(ECMessage.Direction.SEND);
		ecMessagee.setMsgId(AppManager.getUUID());

		String channel = CheckUtil.getAppMetaData(mContext, "UMENG_CHANNEL");
//		String channel = "oppo";
		ecMessagee.setFrom(AppManager.getClientUser().userId);
		ecMessagee.setNickName(AppManager.getClientUser().user_name);
		String toUserId = "";
		if ("-1".equals(clientUser.userId)) {//给客服发送消息
			toUserId = "-1";
		} else {
			if (!TextUtils.isEmpty(AppManager.getClientUser().currentCity)) {
				if (!"oppo".equals(channel) || !CITY.contains(AppManager.getClientUser().currentCity)) {
					toUserId = "-2";//不是oppo渠道或者CITY没有包含当前城市，发送消息给-2，否则发送消息给-3
				} else {
					toUserId = "-3";//只接收oppo渠道且当前城市是CITY中某一个城市的用户发送的消息
				}
			} else {
				toUserId = "-3";
			}
		}
		ecMessagee.setTo(toUserId);
		StringBuilder userData = new StringBuilder();
		userData.append(AppManager.getClientUser().userId)
				.append(";")
				.append(AppManager.getClientUser().user_name)
				.append(";")
				.append(AppManager.getClientUser().face_url)
				.append(";")//真实用户信息
				.append(clientUser.userId)
				.append(";")
				.append(clientUser.user_name)
				.append(";")
				.append(clientUser.face_url)
				.append(";")
				.append(channel)
				.append(";")
				.append(AppManager.getClientUser().currentCity);
		ecMessagee.setUserData(userData.toString());

		ecMessagee.setMsgTime(System.currentTimeMillis());
		ecMessagee.setType(ECMessage.Type.TXT);
		ECTextMessageBody msgBody = new ECTextMessageBody(msgContent);
		ecMessagee.setBody(msgBody);

		/**
		 * 本地消息
		 */
		final IMessage message = new IMessage();
		message.msgId = ecMessagee.getMsgId();
		message.talker = ecMessagee.getTo();
		message.sender = ecMessagee.getForm();
		message.sender_name = ecMessagee.getNickName();
		ECTextMessageBody body = (ECTextMessageBody) ecMessagee.getBody();
		message.content = body.getMessage();
		message.msgType = IMessage.MessageType.TEXT;
		message.isRead = true;
		message.isSend = IMessage.MessageIsSend.SEND;
		message.status = IMessage.MessageStatus.SENDING;
		message.create_time = ecMessagee.getMsgTime();
		message.send_time = message.create_time;

		long convsId = ConversationSqlManager.getInstance(mContext).insertConversation(ecMessagee);
		message.conversationId = convsId;
		MessageCallbackListener.getInstance().notifyPushMessage(message);//刷新UI

		if (mChatManager != null) {
			// 调用SDK发送接口发送消息到服务器
			mChatManager.sendMessage(ecMessagee, new ECChatManager.OnSendMessageListener() {
				@Override
				public void onSendMessageComplete(ECError error, ECMessage ecMessage) {
					// 处理消息发送结果
					if (ecMessage == null || error.errorCode != 200) {
						message.status = IMessage.MessageStatus.FAILED;
					}
					/**
					 * 通知消息发送的状态，发送成功，目的是让环形进度条消失
					 */
					message.status = IMessage.MessageStatus.SENT;
					IMessageDaoManager.getInstance(mContext).insertIMessage(message);
					//通知消息发送的状态
					MessageStatusReportListener.getInstance().notifyMessageStatus(message);
				}

				@Override
				public void onProgress(String msgId, int totalByte, int progressByte) {
					// 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
				}
			});
		}
		RingtoneManager.getRingtone(
				CSApplication.getInstance(),
				Uri.parse("android.resource://" + AppManager.getPackageName()
						+ "/" + R.raw.sound_send)).play();
		return convsId;
	}

	/**
	 * 发送图片消息
	 */
	public void sendImgMsg(final ClientUser clientUser, final String imgUrl) {
		if (new File(imgUrl).exists()) {
			ECMessage ecMessagee = ECMessage.createECMessage(ECMessage.Type.IMAGE);
			ecMessagee.setDirection(ECMessage.Direction.SEND);
			ecMessagee.setMsgId(AppManager.getUUID());

			String channel = CheckUtil.getAppMetaData(mContext, "UMENG_CHANNEL");
//			String channel = "oppo";
			ecMessagee.setFrom(AppManager.getClientUser().userId);
			ecMessagee.setNickName(AppManager.getClientUser().user_name);
			String toUserId = "";
			if ("-1".equals(clientUser.userId)) {//给客服发送消息
				toUserId = "-1";
			} else {
				if (!TextUtils.isEmpty(AppManager.getClientUser().currentCity)) {
					if (!"oppo".equals(channel) || !CITY.contains(AppManager.getClientUser().currentCity)) {
						toUserId = "-2";//不是oppo渠道或者CITY没有包含当前城市，发送消息给-2，否则发送消息给-3
					} else {
						toUserId = "-3";//只接收oppo渠道且当前城市是CITY中某一个城市的用户发送的消息
					}
				} else {
					toUserId = "-3";
				}
			}
			ecMessagee.setTo(toUserId);

			StringBuilder userData = new StringBuilder();
			userData.append(AppManager.getClientUser().userId)
					.append(";")
					.append(AppManager.getClientUser().user_name)
					.append(";")
					.append(AppManager.getClientUser().face_url)
					.append(";")//真实用户信息
					.append(clientUser.userId)
					.append(";")
					.append(clientUser.user_name)
					.append(";")
					.append(clientUser.face_url)
					.append(";")
					.append(channel)
					.append(";")
					.append(AppManager.getClientUser().currentCity);
			ecMessagee.setUserData(userData.toString());

			ecMessagee.setMsgTime(System.currentTimeMillis());
			ECImageMessageBody msgBody = new ECImageMessageBody();
			msgBody.setFileName(new File(imgUrl).getName());
			msgBody.setFileExt(FileUtils.getExtensionName(imgUrl));
			msgBody.setLocalUrl(imgUrl);
			ecMessagee.setBody(msgBody);

			final IMessage message = new IMessage();
			message.msgId = ecMessagee.getMsgId();
			message.talker = ecMessagee.getTo();
			message.sender = ecMessagee.getForm();
			message.sender_name = ecMessagee.getNickName();
			message.msgType = IMessage.MessageType.IMG;
			message.isRead = true;
			message.status = IMessage.MessageStatus.SENDING;
			message.isSend = IMessage.MessageIsSend.SEND;
			message.create_time = System.currentTimeMillis();
			message.send_time = message.create_time;
			message.fileName = new File(imgUrl).getName();
			message.localPath = imgUrl;

			BitmapFactory.Options options = ImageUtil
					.getBitmapOptions(new File(imgUrl).getAbsolutePath());
			message.imgWidth = options.outWidth;
			message.imgHigh = options.outHeight;
			message.content = message.imgWidth + ";" + message.imgHigh;

			long convsId = ConversationSqlManager.getInstance(mContext)
					.insertConversation(ecMessagee);
			message.conversationId = convsId;
			MessageCallbackListener.getInstance().notifyPushMessage(message);//刷新UI

			mChatManager.sendMessage(ecMessagee, new ECChatManager.OnSendMessageListener() {
				@Override
				public void onSendMessageComplete(ECError error, ECMessage ecMessage) {
					if (ecMessage == null || error.errorCode != 200) {
						message.status = IMessage.MessageStatus.FAILED;
					}
					message.status = IMessage.MessageStatus.SENT;
					IMessageDaoManager.getInstance(mContext).insertIMessage(message);
					//通知消息发送的状态
					MessageStatusReportListener.getInstance().notifyMessageStatus(message);
				}

				@Override
				public void onProgress(String msgId, int totalByte, int progressByte) {
					// 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
					int progress = (int) ((totalByte > 0) ? (progressByte * 1.0 / totalByte) * 100 : -1);
					FileProgressListener.getInstance().notifyFileProgressChanged(message, progress);
				}
			});

			RingtoneManager.getRingtone(
					CSApplication.getInstance(),
					Uri.parse("android.resource://" + AppManager.getPackageName()
							+ "/" + R.raw.sound_send)).play();
		}
	}

	/**
	 * 发送位置消息
	 */
	public void sendLocationMsg(final ClientUser clientUser, final double latitude,
								final double longitude,
								final String address, final String imgUrl) {
		if (new File(imgUrl).exists()) {
			ECMessage ecMessagee = ECMessage.createECMessage(ECMessage.Type.LOCATION);
			ecMessagee.setDirection(ECMessage.Direction.SEND);
			ecMessagee.setMsgId(AppManager.getUUID());

			String channel = CheckUtil.getAppMetaData(mContext, "UMENG_CHANNEL");
//			String channel = "oppo";
			ecMessagee.setFrom(AppManager.getClientUser().userId);
			ecMessagee.setNickName(AppManager.getClientUser().user_name);
			String toUserId = "";
			if ("-1".equals(clientUser.userId)) {//给客服发送消息
				toUserId = "-1";
			} else {
				if (!TextUtils.isEmpty(AppManager.getClientUser().currentCity)) {
					if (!"oppo".equals(channel) || !CITY.contains(AppManager.getClientUser().currentCity)) {
						toUserId = "-2";//不是oppo渠道或者CITY没有包含当前城市，发送消息给-2，否则发送消息给-3
					} else {
						toUserId = "-3";//只接收oppo渠道且当前城市是CITY中某一个城市的用户发送的消息
					}
				} else {
					toUserId = "-3";
				}
			}
			ecMessagee.setTo(toUserId);

			StringBuilder userData = new StringBuilder();
			userData.append(AppManager.getClientUser().userId)
					.append(";")
					.append(AppManager.getClientUser().user_name)
					.append(";")
					.append(AppManager.getClientUser().face_url)
					.append(";")//真实用户信息
					.append(clientUser.userId)
					.append(";")
					.append(clientUser.user_name)
					.append(";")
					.append(clientUser.face_url)
					.append(";")
					.append(channel)
					.append(";")
					.append(AppManager.getClientUser().currentCity);
			ecMessagee.setUserData(userData.toString());

			ecMessagee.setMsgTime(System.currentTimeMillis());
			ECLocationMessageBody msgBody = new ECLocationMessageBody(latitude, longitude);
			msgBody.setTitle(address);
			msgBody.setFileName(new File(imgUrl).getName());
			msgBody.setFileExt(FileUtils.getExtensionName(imgUrl));
			msgBody.setLocalUrl(imgUrl);
			ecMessagee.setBody(msgBody);

			final IMessage message = new IMessage();
			message.msgId = ecMessagee.getMsgId();
			message.talker = ecMessagee.getTo();
			message.sender = ecMessagee.getForm();
			message.sender_name = ecMessagee.getNickName();
			message.msgType = IMessage.MessageType.LOCATION;
			message.isRead = true;
			message.content = address;
			message.status = IMessage.MessageStatus.SENDING;
			message.isSend = IMessage.MessageIsSend.SEND;
			message.latitude = latitude;
			message.longitude = longitude;
			message.create_time = System.currentTimeMillis();
			message.send_time = message.create_time;
			message.fileName = new File(imgUrl).getName();
			message.localPath = imgUrl;

			BitmapFactory.Options options = ImageUtil
					.getBitmapOptions(new File(imgUrl).getAbsolutePath());
			message.imgWidth = options.outWidth;
			message.imgHigh = options.outHeight;

			long convsId = ConversationSqlManager.getInstance(mContext)
					.insertConversation(ecMessagee);
			message.conversationId = convsId;
			MessageCallbackListener.getInstance().notifyPushMessage(message);//刷新UI

			mChatManager.sendMessage(ecMessagee, new ECChatManager.OnSendMessageListener() {
				@Override
				public void onSendMessageComplete(ECError error, ECMessage ecMessage) {
					if (ecMessage == null || error.errorCode != 200) {
						message.status = IMessage.MessageStatus.FAILED;
					}
					message.status = IMessage.MessageStatus.SENT;
					IMessageDaoManager.getInstance(mContext).insertIMessage(message);
					//通知消息发送的状态
					MessageStatusReportListener.getInstance().notifyMessageStatus(message);
				}

				@Override
				public void onProgress(String msgId, int totalByte, int progressByte) {
				}
			});

		}
		RingtoneManager.getRingtone(
				CSApplication.getInstance(),
				Uri.parse("android.resource://" + AppManager.getPackageName()
						+ "/" + R.raw.sound_send)).play();
	}

	/**
	 * 发送文件消息
	 */
	public void sendFileMsg(final ClientUser clientUser, final String imgUrl) {
		// 组建一个待发送的ECMessage
		ECMessage msg = ECMessage.createECMessage(ECMessage.Type.FILE);
		// 设置消息接收者
		msg.setTo("John的账号");
		// 或者创建一个创建附件消息体
		// 比如我们发送SD卡里面的一个Tony_2015.zip文件
		ECFileMessageBody msgBody = new ECFileMessageBody();
		// 设置附件名
		msgBody.setFileName("Tony_2015.zip");
		// 设置附件扩展名
		msgBody.setFileExt("zip");
		// 设置附件本地路径
		msgBody.setLocalUrl("../Tony_2015.zip");
		// 设置附件长度
		msgBody.setLength("Tony_2015.zip文件大小".length());
		// 将消息体存放到ECMessage中
		msg.setBody(msgBody);
		// 调用SDK发送接口发送消息到服务器
		mChatManager.sendMessage(msg, new ECChatManager.OnSendMessageListener() {
			@Override
			public void onSendMessageComplete(ECError error, ECMessage message) {
				// 处理消息发送结果
				if (message == null) {
					return;
				}
				// 将发送的消息更新到本地数据库并刷新UI
			}

			@Override
			public void onProgress(String msgId, int totalByte, int progressByte) {
				// 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
			}
		});

		RingtoneManager.getRingtone(
				CSApplication.getInstance(),
				Uri.parse("android.resource://" + AppManager.getPackageName()
						+ "/" + R.raw.sound_send)).play();
	}


	public void destroy() {
		mChatManager = null;
		mInstance = null;
	}

	@Override
	public void OnReceivedMessage(ECMessage ecMessage) {
		if (ecMessage == null) {
			return;
		}
		postReceiveMessage(ecMessage);
	}

	/**
	 * 处理接收消息
	 * @param msg
	 */
	private synchronized void postReceiveMessage(ECMessage msg) {
		if (!msg.getTo().equals(AppManager.getClientUser().userId)) {
			return;
		}
		long conversationId = ConversationSqlManager.getInstance(mContext)
				.insertConversation(msg);
		IMessage message = new IMessage();
		message.msgId = AppManager.getUUID();
		String userData = msg.getUserData();
		if (!TextUtils.isEmpty(userData)) {
			String[] data = userData.split(";");
			if (data.length > 0) {
				message.talker = data[0];
				message.sender = data[0];
				message.sender_name = data[1];
			}
		}
		message.isRead = false;
		message.isSend = IMessage.MessageIsSend.RECEIVING;
		message.create_time = msg.getMsgTime();
		message.send_time = message.create_time;
		message.status = IMessage.MessageStatus.RECEIVED;
		message.conversationId = conversationId;

		if (msg.isMultimediaBody()) {
			ECFileMessageBody body = (ECFileMessageBody) msg.getBody();
			if (!TextUtils.isEmpty(body.getRemoteUrl())) {
				if (msg.getType() == ECMessage.Type.VOICE) {
					ECVoiceMessageBody voiceBody = (ECVoiceMessageBody) body;
				} else if (msg.getType() == ECMessage.Type.IMAGE) {
					ECImageMessageBody imageBody = (ECImageMessageBody) body;
					message.msgType = IMessage.MessageType.IMG;
					message.content = mContext.getResources().getString(R.string.image_symbol);
					message.fileUrl = imageBody.getRemoteUrl();
				} else if (msg.getType() == ECMessage.Type.VIDEO){
					ECVideoMessageBody videoBody = (ECVideoMessageBody) body;
				}
			} else {
				Log.e("test", "ECMessage fileUrl: null");
			}
		} else if(msg.getType() == ECMessage.Type.TXT) {
			ECTextMessageBody body = (ECTextMessageBody)msg.getBody();
			message.msgType = IMessage.MessageType.TEXT;
			message.content = body.getMessage();
		} else if (msg.getType() == ECMessage.Type.LOCATION){
			ECLocationMessageBody locationBody = (ECLocationMessageBody) msg.getBody();
			message.msgType = IMessage.MessageType.LOCATION;
			message.latitude = locationBody.getLatitude();
			message.longitude = locationBody.getLongitude();
			message.content = locationBody.getTitle();
			message.fileUrl = locationBody.getRemoteUrl();
		} else if (msg.getType() == ECMessage.Type.STATE){
			ECUserStateMessageBody stateBody = (ECUserStateMessageBody) msg.getBody();
			message.msgType = IMessage.MessageType.RED_PKT;
			message.content = stateBody.getMessage();
		}

		MessageCallbackListener.getInstance().notifyPushMessage(message);//刷新UI
		if(!isSyncOffline) { //不是离线消息，直接插入
			IMessageDaoManager.getInstance(mContext).insertIMessage(message);
			AppManager.showNotification(message);
		} else {
			offlineMsg.add(message);
			if (offlineMsg.size() == mHistoryMsgCount) {
				IMessageDaoManager.getInstance(mContext).insertIMessageList(offlineMsg);
				AppManager.showNotification(message);
				isSyncOffline = false;
				offlineMsg.clear();
			}
		}
	}

	@Override
	public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {
	}

	@Override
	public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage ecGroupNoticeMessage) {

	}

	@Override
	public void onOfflineMessageCount(int count) {
	}

	@Override
	public int onGetOfflineMessage() {
		// 获取全部的离线历史消息
		return ECDevice.SYNC_OFFLINE_MSG_ALL;
	}

	@Override
	public void onReceiveOfflineMessage(List<ECMessage> msgs) {
		// 离线消息的处理可以参考 void OnReceivedMessage(ECMessage msg)方法
		// 处理逻辑完全一样
		if (msgs != null && !msgs.isEmpty()) {
			mHistoryMsgCount = msgs.size();
			isSyncOffline = true;
		}
		for (ECMessage msg : msgs) {
			postReceiveMessage(msg);
		}
	}

	@Override
	public void onReceiveOfflineMessageCompletion() {
		isSyncOffline = false;
	}

	@Override
	public void onServicePersonVersion(int i) {

	}

	/**
	 * 客服消息
	 * @param ecMessage
	 */
	@Override
	public void onReceiveDeskMessage(ECMessage ecMessage) {

	}

	public static boolean isSyncOffline() {
		return getInstance().isSyncOffline;
	}

	@Override
	public void onSoftVersion(String s, int i) {

	}

	/**
	 * 发送初始登录信息
	 */
	public void sendInitLoginMsg() {
		// 获取是不是第一次登录
		boolean isFirstLogin = PreferencesUtils.getFirstLogin(CSApplication.getInstance());
		if (isFirstLogin) {
			Conversation convs = new Conversation();
			convs.talker = String.valueOf(-1);
			convs.talkerName = mContext.getResources().getString(R.string.app_name) + "团队";
			convs.localPortrait = "res:///" + R.mipmap.ic_launcher;
			convs.faceUrl = "http://real-love-server.oss-cn-shenzhen.aliyuncs.com/tan_love/img/tl_168.png";
			convs.content = CSApplication.getInstance().getResources()
					.getString(R.string.init_official_message);
			convs.createTime = System.currentTimeMillis();
			convs.unreadCount++;
			long id = ConversationSqlManager.getInstance(mContext).inserConversation(convs);
			convs.id = id;

			IMessage message = new IMessage();
			message.msgId = AppManager.getUUID();
			message.sender = String.valueOf(-1);
			message.sender_name = mContext.getResources().getString(R.string.app_name) + "团队";
			message.talker = AppManager.getClientUser().userId;
			message.isRead = false;
			message.create_time = convs.createTime;
			message.send_time = message.create_time;
			message.content = CSApplication.getInstance().getResources()
					.getString(R.string.init_official_message);
			message.isSend = IMessage.MessageIsSend.RECEIVING;
			message.status = IMessage.MessageStatus.RECEIVED;
			message.msgType = IMessage.MessageType.TEXT;
			message.conversationId = id;
			IMessageDaoManager.getInstance(mContext).insertIMessage(message);
			MessageCallbackListener.getInstance().notifyPushMessage(message);
			MessageChangedListener.getInstance()
					.notifyMessageChanged(String.valueOf(message.conversationId));
			try {
				PreferencesUtils.setFirstLogin(CSApplication.getInstance(), false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ECChatManager getChatManager() {
		return mChatManager;
	}
}
