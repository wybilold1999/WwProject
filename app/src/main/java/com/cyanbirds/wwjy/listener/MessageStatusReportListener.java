package com.cyanbirds.wwjy.listener;


import com.cyanbirds.wwjy.entity.IMessage;

/**
 * 
 * @Description:消息发送状态监听
 * @author wangyb
 * @Date:2015年8月25日下午9:36:45
 */
public class MessageStatusReportListener {

	private static OnMessageStatusReport mOnMessageReport;

	private MessageStatusReportListener() {
	}

	public static MessageStatusReportListener getInstance() {
		return MessageStatusReportListener.SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		private static final MessageStatusReportListener INSTANCE = new MessageStatusReportListener();
	}

	public void setOnMessageReportCallback(OnMessageStatusReport callback) {
		mOnMessageReport = callback;
	}

	public interface OnMessageStatusReport {
		void onNotifyMessageStatusReport(IMessage msg);
	}

	public void notifyMessageStatus(IMessage msg) {
		if (mOnMessageReport != null) {
			mOnMessageReport.onNotifyMessageStatusReport(msg);
		}
	}

}
