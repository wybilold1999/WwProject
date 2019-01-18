package com.cyanbirds.wwjy.listener;


import java.util.ArrayList;

/**
 * 
 * @ClassName:FileProgressListener
 * @Description:文件上传和下载进度监听
 * @author wangyb
 * @Date:2015年6月9日下午4:37:31
 */
public class ImgProgressListener {

	private static ImgProgressListener mNoticeCenter;
	private ArrayList<OnImgProgressChangedListener> mOnImgProgressChangedListener;

	private ImgProgressListener() {
	}

	public interface OnImgProgressChangedListener {
		public void onImgProgressChanged(int progress);
	}

	public static ImgProgressListener getInstance() {
		if (null == mNoticeCenter) {
			mNoticeCenter = new ImgProgressListener();
			mNoticeCenter.init();
		}
		return mNoticeCenter;
	}

	private void init() {
		mOnImgProgressChangedListener = new ArrayList<OnImgProgressChangedListener>();
	}

	public void addOnImgProgressChangedListener(OnImgProgressChangedListener listener) {
		mOnImgProgressChangedListener.add(listener);
	}

	public void removeOnImgProgressChangedListener(OnImgProgressChangedListener listener) {
		mOnImgProgressChangedListener.remove(listener);
	}

	public void notifyImgProgressChanged(int progress) {
		for (OnImgProgressChangedListener listener : mOnImgProgressChangedListener) {
			if (listener != null) {
				listener.onImgProgressChanged(progress);
			}
		}
	}

}
