package com.cyanbirds.wwjy.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 
 * @ClassName: DensityUtil
 * @Description: 屏幕密度转换
 * @author: wangyb
 * @date: 2014年6月17日下午2:14:44
 */
public class DensityUtil {

	/**
	 * 获取手机高度px
	 * 
	 * @param context
	 * @return
	 */
	public static final int getHeightInPx(Context context) {
		final int height = context.getResources().getDisplayMetrics().heightPixels;
		return height;
	}

	/**
	 * 获取宽度px
	 * 
	 * @param context
	 * @return
	 */
	public static final int getWidthInPx(Context context) {
		final int width = context.getResources().getDisplayMetrics().widthPixels;
		return width;
	}

	/**
	 * 获取手机高度dp
	 * 
	 * @param context
	 * @return
	 */
	public static final int getHeightInDp(Context context) {
		final float height = context.getResources().getDisplayMetrics().heightPixels;
		int heightInDp = px2dip(context, height);
		return heightInDp;
	}

	/**
	 * 获取手机宽度dp
	 * 
	 * @param context
	 * @return
	 */
	public static final int getWidthInDp(Context context) {
		final float height = context.getResources().getDisplayMetrics().heightPixels;
		int widthInDp = px2dip(context, height);
		return widthInDp;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (spValue * scale + 0.5f);
	}

	/**
	 * 获取手机密度
	 * @param context
	 * @param height
	 * @return
	 */
	public static int getMetricsDensity(Context context, float height) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(localDisplayMetrics);
		return Math.round(height * localDisplayMetrics.densityDpi / 160.0F);
	}
}