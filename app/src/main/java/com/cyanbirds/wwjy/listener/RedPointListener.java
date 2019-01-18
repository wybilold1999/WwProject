package com.cyanbirds.wwjy.listener;

/**
 * 
 * @Description:红点提示监听
 * @author wangyb
 * @Date:2015年8月13日下午5:22:43
 */
public class RedPointListener {

	private OnRedPointListener readListener;

	private RedPointListener() {
	}

	public static RedPointListener getInstance() {
		return RedPointListener.SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		private static final RedPointListener INSTANCE = new RedPointListener();
	}

	public void setRedPointListener(OnRedPointListener listener) {
		readListener = listener;
	}

	public OnRedPointListener getRedPointListener() {
		return readListener;
	}

	public interface OnRedPointListener {
		void notifyPoint(int type);
	}

	public void notifyDataSetChanged(int type) {
		if (readListener != null) {
			readListener.notifyPoint(type);
		}
	}
	

}
