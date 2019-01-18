package com.cyanbirds.wwjy.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @ClassName:CheckUtil
 * @Description:验证工具类
 * @Author: wangyb
 * @Date:2015年3月26日上午11:23:16
 *
 */
public class CheckUtil {

	private static final Pattern URL_WITH_PROTOCOL_PATTERN = Pattern
			.compile("[a-zA-Z][a-zA-Z0-9+-.]+:");
	private static final Pattern URL_WITHOUT_PROTOCOL_PATTERN = Pattern
			.compile("([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,}" + // host name elements
					"(:\\d{1,5})?" + // maybe port
					"(/|\\?|$)");

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		String regex = "^0?(13[0-9]|17[0-9]|15[012356789]|18[0123456789]|14[57])[0-9]{8}$";
		Pattern p = Pattern
				.compile(regex);
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 验证url
	 * @param uri
	 * @return
	 */
	public static boolean isBasicallyValidURI(String uri) {
		if (uri.contains(" ")) {
			// Quick hack check for a common case
			return false;
		}
		Matcher m = URL_WITH_PROTOCOL_PATTERN.matcher(uri);
		if (m.find() && m.start() == 0) { // match at start only
			return true;
		}
		m = URL_WITHOUT_PROTOCOL_PATTERN.matcher(uri);
		return m.find() && m.start() == 0;
	}

	/**
	 * 获取application中指定的meta-data
	 * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
	 */
	public static String getAppMetaData(Context ctx, String key) {
		if (ctx == null || TextUtils.isEmpty(key)) {
			return null;
		}
		String resultData = null;
		try {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
//						resultData = String.valueOf(applicationInfo.metaData.getInt(key));
						resultData = applicationInfo.metaData.getString(key);
					}
				}

			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return resultData;
	}

	public static boolean isGetPermission(Context context, String permissionName) {
		if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context, permissionName) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}
}
