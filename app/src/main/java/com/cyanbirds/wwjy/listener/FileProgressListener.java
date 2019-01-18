package com.cyanbirds.wwjy.listener;



import com.cyanbirds.wwjy.entity.IMessage;

import java.util.ArrayList;

/**
 * 
 * @ClassName:FileProgressListener
 * @Description:文件上传和下载进度监听
 * @author wangyb
 * @Date:2015年6月9日下午4:37:31
 */
public class FileProgressListener {

	private static FileProgressListener mNoticeCenter;
	private ArrayList<OnFileProgressChangedListener> mOnFileProgressChangedListener;

	private FileProgressListener() {
	}

	public interface OnFileProgressChangedListener {
		public void onFileProgressChanged(IMessage message, int progress);
	}

	public static FileProgressListener getInstance() {
		if (null == mNoticeCenter) {
			mNoticeCenter = new FileProgressListener();
			mNoticeCenter.init();
		}
		return mNoticeCenter;
	}

	private void init() {
		mOnFileProgressChangedListener = new ArrayList<OnFileProgressChangedListener>();
	}

	public void addOnFileProgressChangedListener(OnFileProgressChangedListener listener) {
		mOnFileProgressChangedListener.add(listener);
	}

	public void removeOnFileProgressChangedListener(OnFileProgressChangedListener listener) {
		mOnFileProgressChangedListener.remove(listener);
	}

	public void notifyFileProgressChanged(IMessage message,int progress) {
		for (OnFileProgressChangedListener listener : mOnFileProgressChangedListener) {
			if (listener != null) {
				listener.onFileProgressChanged(message,progress);
			}
		}
	}

}
