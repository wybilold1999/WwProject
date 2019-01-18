package com.cyanbirds.wwjy.listener;

import com.cyanbirds.wwjy.entity.IMessage;

/**
 * 
 * @Description:消息回调监听
 * @author wangyb
 * @Date:2015年8月25日下午9:36:45
 */
public class MessageCallbackListener {

	private static OnMessageReportCallback mOnMessageReportCallback;

	private MessageCallbackListener() {
	}

	public static MessageCallbackListener getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		private static final MessageCallbackListener INSTANCE = new MessageCallbackListener();
	}

	public void setOnMessageReportCallback(OnMessageReportCallback callback) {
		mOnMessageReportCallback = callback;
	}

	public interface OnMessageReportCallback {

		void onPushMessage(IMessage msg);

		void onIMessageItemChange(String msgId);

		void onNotifyDataSetChanged(int conversationId);
	}

	public void notifyPushMessage(IMessage msg) {
		if (mOnMessageReportCallback != null) {
			mOnMessageReportCallback.onPushMessage(msg);
		}
	}

	public void notifyMessageItemChange(String msgId) {
		if (mOnMessageReportCallback != null) {
			mOnMessageReportCallback.onIMessageItemChange(msgId);
		}
	}

	public void notifyDataSetChanged(int conversationId) {
		if (mOnMessageReportCallback != null) {
			mOnMessageReportCallback.onNotifyDataSetChanged(conversationId);
		}
	}

}
