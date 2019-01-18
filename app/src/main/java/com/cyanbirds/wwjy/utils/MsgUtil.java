package com.cyanbirds.wwjy.utils;

import com.cyanbirds.wwjy.entity.IMessage;
import com.cyanbirds.wwjy.manager.AppManager;

/**
 * 作者：wangyb
 * 时间：2017/7/3 22:00
 * 描述：有关注或者礼物的时候，构造消息
 */
public class MsgUtil {

	/**
	 *
	 * @param nickname 关注或者送礼物的人
	 * @param faceUrl
	 * @param content
	 */
	public static void sendAttentionOrGiftMsg(String userId, String nickname, String faceUrl, String content) {

		IMessage message = new IMessage();
		message.msgId = AppManager.getUUID();
		message.sender = userId;
		message.sender_name = nickname;
		message.talker = AppManager.getClientUser().userId;
		message.face_url = faceUrl;
		message.isRead = false;
		message.create_time = System.currentTimeMillis();
		message.send_time = message.create_time;
		message.content = content;
		message.isSend = IMessage.MessageIsSend.RECEIVING;
		message.status = IMessage.MessageStatus.RECEIVED;
		message.msgType = IMessage.MessageType.TEXT;
		AppManager.showNotification(message);
	}
}
