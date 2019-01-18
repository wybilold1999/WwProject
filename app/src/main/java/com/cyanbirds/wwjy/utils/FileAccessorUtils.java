package com.cyanbirds.wwjy.utils;

import android.os.Environment;

import com.cyanbirds.wwjy.R;

import java.io.File;

/**
 * 
 * @ClassName:FileAccessorUtils
 * @Description:文件存取工具类
 * @author Administrator
 * @Date:2015年6月5日上午10:06:20
 */
public class FileAccessorUtils {
	
	/** 默认路径 */
	public static final String DEFAULT_PATH = getExternalStorePath()
			+ "/tanlove";
	/** 文件存储路径 */
	public static final String FILE_PATH = getExternalStorePath()
			+ "/tanlove/file";
	/** 图像的存储路径 */
	public static final String IMESSAGE_IMAGE = getExternalStorePath()
			+ "/tanlove/image";
	/** 头像存储路径 */
	public static final String FACE_IMAGE = getExternalStorePath()
			+ "/tanlove/face";
	/** 语音存储路径 */
	public static final String VOICE_PATH = getExternalStorePath()
			+ "/tanlove/voice";
	/** 视频存储路径 */
	public static final String VIDEO_PATH = getExternalStorePath()
			+ "/tanlove/video";
	/** crash路径 */
	public static final String CRASH_PATH = getExternalStorePath()
			+ "/tanlove/crash";

	/** APK文件暂时存放的路径 */
	public static final String APK_PATH = getExternalStorePath()
			+ "/tanlove/apk";

	/**
	 * 外置存储卡的路径
	 * 
	 * @return
	 */
	public static String getExternalStorePath() {
		if (isExistExternalStore()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}

	/**
	 * 是否有外存卡
	 * 
	 * @return
	 */
	public static boolean isExistExternalStore() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * 返回图片存放目录
	 *
	 * @return
	 */
	public static File getFilePathName() {
		if (!isExistExternalStore()) {
			ToastUtil.showMessage(R.string.media_ejected);
			return null;
		}

		File directory = new File(FILE_PATH);
		if (!directory.exists() && !directory.mkdirs()) {
			ToastUtil.showMessage("Path to file could not be created");
			return null;
		}
		return directory;
	}


	/**
	 * 返回图片存放目录
	 * 
	 * @return
	 */
	public static File getImagePathName() {
		if (!isExistExternalStore()) {
			ToastUtil.showMessage(R.string.media_ejected);
			return null;
		}

		File directory = new File(IMESSAGE_IMAGE);
		if (!directory.exists() && !directory.mkdirs()) {
			ToastUtil.showMessage("Path to file could not be created");
			return null;
		}
		return directory;
	}
	
	/**
	 * 返回默认存放目录
	 * 
	 * @return
	 */
	public static File getDefaultPathName() {
		if (!isExistExternalStore()) {
			ToastUtil.showMessage(R.string.media_ejected);
			return null;
		}

		File directory = new File(DEFAULT_PATH);
		if (!directory.exists() && !directory.mkdirs()) {
			ToastUtil.showMessage("Path to file could not be created");
			return null;
		}
		return directory;
	}
	
	/**
	 * 返回头像存放目录
	 * 
	 * @return
	 */
	public static File getFacePathName() {
		if (!isExistExternalStore()) {
			ToastUtil.showMessage(R.string.media_ejected);
			return null;
		}

		File directory = new File(FACE_IMAGE);
		if (!directory.exists() && !directory.mkdirs()) {
			ToastUtil.showMessage("Path to file could not be created");
			return null;
		}
		return directory;
	}

	/**
	 * 返回语音存放目录
	 *
	 * @return
	 */
	public static File getVoicePathName() {
		if (!isExistExternalStore()) {
			ToastUtil.showMessage(R.string.media_ejected);
			return null;
		}

		File directory = new File(VOICE_PATH);
		if (!directory.exists() && !directory.mkdirs()) {
			ToastUtil.showMessage("Path to file could not be created");
			return null;
		}
		return directory;
	}

	/**
	 * 返回视频存放目录
	 *
	 * @return
	 */
	public static File getVideoPathName() {
		if (!isExistExternalStore()) {
			ToastUtil.showMessage(R.string.media_ejected);
			return null;
		}

		File directory = new File(VIDEO_PATH);
		if (!directory.exists() && !directory.mkdirs()) {
			ToastUtil.showMessage("Path to file could not be created");
			return null;
		}
		return directory;
	}

	/**
	 * 返回Crash目录
	 *
	 * @return
	 */
	public static File getCrashPathName() {
		if (!isExistExternalStore()) {
			ToastUtil.showMessage(R.string.media_ejected);
			return null;
		}

		File directory = new File(CRASH_PATH);
		if (!directory.exists() && !directory.mkdirs()) {
			ToastUtil.showMessage("Path to file could not be created");
			return null;
		}
		return directory;
	}

	/**
	 * 返回APK目录
	 *
	 * @return
	 */
	public static File getAPKPathName() {
		if (!isExistExternalStore()) {
			ToastUtil.showMessage(R.string.media_ejected);
			return null;
		}

		File directory = new File(APK_PATH);
		if (!directory.exists() && !directory.mkdirs()) {
			return null;
		}
		return directory;
	}

}
