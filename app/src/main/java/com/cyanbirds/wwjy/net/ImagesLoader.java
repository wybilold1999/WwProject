package com.cyanbirds.wwjy.net;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;

import com.cyanbirds.wwjy.entity.ImageBean;
import com.cyanbirds.wwjy.utils.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @Description:获取本地图片
 * @author wangyb
 * @Date:2015年7月26日上午11:34:19
 */
public class ImagesLoader extends AsyncTaskLoader<List<ImageBean>> {

	private List<ImageBean> mImages = null;

	/**
	 * @param context
	 */
	public ImagesLoader(Context context) {
		super(context);
	}

	@Override
	public List<ImageBean> loadInBackground() {
		List<ImageBean> imageList = new ArrayList<ImageBean>();

		Cursor imageCursor = getContext().getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media.DATA,
						MediaStore.Images.Media._ID }, null, null,
				MediaStore.Images.Media._ID);

		if (imageCursor != null && imageCursor.getCount() > 0) {

			while (imageCursor.moveToNext()) {
				String imgPath = imageCursor.getString(imageCursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				//图片文件需要大于1kb
				if (FileUtils.decodeFileLength(imgPath) > 1024) {
					ImageBean item = new ImageBean(imgPath, false);
					imageList.add(item);
				}
			}
		}

		if (imageCursor != null) {
			imageCursor.close();
		}

		// show newest photo at beginning of the list
		Collections.reverse(imageList);
		return imageList;
	}

	/* Runs on the UI thread */
	@Override
	public void deliverResult(List<ImageBean> images) {
		if (isReset()) {
			// An async query came in while the loader is stopped
			if (images != null) {
				images.clear();
				images = null;
			}
			return;
		}
		List<ImageBean> oldImages = mImages;
		mImages = images;

		if (isStarted()) {
			super.deliverResult(images);
		}

		if (oldImages != null && oldImages != mImages) {
			oldImages.clear();
			oldImages = null;
		}
	}

	@Override
	protected void onStartLoading() {
		if (mImages != null && mImages.size() > 0) {
			deliverResult(mImages);
		}

		if (takeContentChanged() || mImages == null) {
			forceLoad();
		}
	}

	/**
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(List<ImageBean> images) {
		if (images != null) {
			images.clear();
			images = null;
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		if (mImages != null) {
			mImages.clear();
			mImages = null;
		}
	}
}
