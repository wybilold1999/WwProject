package com.cyanbirds.wwjy.listener;

/**
 * 
 * @Description:消息未读数监听
 * @author wangyb
 * @Date:2015年8月13日下午5:22:43
 */
public class MessageUnReadListener {

	private OnMessageUnReadListener readListener;

	private MessageUnReadListener() {
	}

	public static MessageUnReadListener getInstance() {
		return MessageUnReadListener.SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		private static final MessageUnReadListener INSTANCE = new MessageUnReadListener();
	}

	public void setMessageUnReadListener(OnMessageUnReadListener listener) {
		readListener = listener;
	}

	public interface OnMessageUnReadListener {
		void notifyUnReadChanged(int type);
	}

	//按照FragmentTabHost下标
	public void notifyDataSetChanged(int type) {
		if (readListener != null) {
			readListener.notifyUnReadChanged(type);
		}
	}
	

}
