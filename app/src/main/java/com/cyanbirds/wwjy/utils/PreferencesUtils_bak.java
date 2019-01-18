package com.cyanbirds.wwjy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cyanbirds.wwjy.manager.AppManager;


/**
 * 
 * @ClassName:PrefUtils.java
 * @Description:
 * @author wangyb
 * @Date:2015年7月6日下午2:12:22
 */
public class PreferencesUtils_bak {

	/** 保存登陆账号 */
	public static final String SETTINGS_RL_ACCOUNT = "com.cyanbird.tanlove_account";
	/** 保存登陆密码 */
	public static final String SETTINGS_RL_PASSWORD = "com.cyanbird.tanlove_p";
	/** 手机号 */
	public static final String SETTINGS_RL_USER_MOBILE = "com.cyanbird.tanlove_mobile";
	/** 用户名 */
	public static final String SETTINGS_RL_USER_USER_NAME = "com.cyanbird.tanlove_user_name";
	/** 某用户是否第一次登录 */
	public static final String SETTINGS_RL_FIRST_LOGIN = "com.cyanbird.tanlove_first_login";
	/** 是否登录 */
	public static final String SETTINGS_RL_IS_LOGIN = "com.cyanbird.tanlove_is_login";
	/** 本地头像地址 */
	public static final String SETTINGS_RL_FACE_LOCAL = "com.cyanbird.tanlove_face_local";
	/**sessionId */
	public static final String SETTINGS_RL_SESSIONID = "com.cyanbird.tanlove_sessionid";
	/** 新消息提醒 */
	public static final String SETTINGS_RL_NEW_MESSAGE_NOTICE = "com.cyanbird.tanlove_new_message_notice";
	/** 通知显示消息详情 */
	public static final String SETTINGS_RL_NOTICE_MESSAGE_INFO = "com.cyanbird.tanlove_notice_message_info";
	/** 通知声音*/
	public static final String SETTINGS_RL_NOTICE_VOICE = "com.cyanbird.tanlove_notice_voice";
	/** 通知震动*/
	public static final String SETTINGS_RL_NOTICE_SHOCK = "com.cyanbird.tanlove_notice_shock";
	/** 听筒播放*/
	public static final String SETTINGS_RL_EARPIECE_PLAY_VOICE = "com.cyanbird.tanlove_earpiece_play_voice";
	/** 定位到的城市*/
	public static final String SETTINGS_CURRENT_CITY = "com.cyanbird.tanlove_current_city";
	/** 最近喜欢我的userid*/
	public static final String SETTINGS_LOVE_ME_USER_ID = "com.cyanbird.tanlove_love_me_user_id";
	/** 最近关注我的userid*/
	public static final String SETTINGS_ATTENTION_ME_USER_ID = "com.cyanbird.tanlove_attention_me_user_id";
	/** 最近送我礼物的userid*/
	public static final String SETTINGS_GIFT_ME_USER_ID = "com.cyanbird.tanlove_gift_me_user_id";
	/** 登录时间*/
	public static final String SETTINGS_LOGIN_TIME = "com.cyanbird.tanlove_login_time";
	/** 省份*/
	public static final String SETTINGS_CURRENT_PROVINCE = "com.cyanbird.tanlove_current_province";
	/** 经度*/
	public static final String SETTINGS_LATITUDE = "com.cyanbird.tanlove_latitude";
	/** 纬度*/
	public static final String SETTINGS_LONGITUDE = "com.cyanbird.tanlove_longitude";
	/** 是否上传好评截图*/
	public static final String SETTINGS_APP_COMMENT = "com.cyanbirds.wwjy_app_comment";
	/** 高德定位是否成功 **/
	public static final String SETTINGS_LOCATION_SUCCESS = "com.cyanbirds.wwjy_location_success";
	/** 性别 0：女生 1：男生 **/
	public static final String SETTINGS_SEX = "com.cyanbirds.wwjy_sex";

	/**
	 * 获取RL账号
	 *
	 * @return
	 */
	public static String getAccount(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_ACCOUNT, "");
	}

	/**
	 * 保存RL账号
	 *
	 * @param context
	 * @param account
	 */
	public static void setAccount(final Context context, final String account) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_ACCOUNT, account).commit();
	}

	/**
	 * 获取保存的密码
	 *
	 * @param context
	 * @return
	 */
	public static String getPassword(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_PASSWORD, "");
	}

	/**
	 * 保存密码
	 *
	 * @param context
	 */
	public static void setPassword(final Context context, final String password) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_PASSWORD, password).commit();
	}

	/**
	 * 获取用户手机号码
	 *
	 * @param context
	 * @return
	 */
	public static String getUserMobile(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_USER_MOBILE, "");
	}

	/**
	 * 保存用户手机号码
	 *
	 * @param context
	 */
	public static void setUserMobile(final Context context, final String faceUrl) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_USER_MOBILE, faceUrl).commit();
	}

	/**
	 * 获取用户名称
	 *
	 * @param context
	 * @return
	 */
	public static String getUserName(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_USER_USER_NAME, "");
	}

	/**
	 * 保存用户名称
	 *
	 * @param context
	 */
	public static void setUserName(final Context context, final String username) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_USER_USER_NAME, username).commit();
	}

	/**
	 * 获取用户是否第一次登录
	 *
	 * @param context
	 * @return
	 */
	public static boolean getFirstLogin(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(
				SETTINGS_RL_FIRST_LOGIN + "_"
						+ AppManager.getClientUser().userId, true);
	}

	/**
	 * 保存用户是否第一次登录
	 *
	 * @param context
	 */
	public static void setFirstLogin(final Context context,
									 final Boolean firstLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit()
				.putBoolean(
						SETTINGS_RL_FIRST_LOGIN + "_"
								+ AppManager.getClientUser().userId, firstLogin)
				.commit();
	}

	/**
	 * 获取用户是否登录
	 *
	 * @param context
	 * @return
	 */
	public static boolean getIsLogin(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_IS_LOGIN, false);
	}

	/**
	 * 保存用户是否登录
	 *
	 * @param context
	 */
	public static void setIsLogin(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_IS_LOGIN, isLogin).commit();
	}

	/**
	 * 获取本地头像地址
	 *
	 * @param context
	 * @return
	 */
	public static String getFaceLocal(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_FACE_LOCAL, "");
	}

	/**
	 * 保存本地头像地址
	 *
	 * @param context
	 * @return
	 */
	public static void setFaceLocal(final Context context, final String face_local) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_FACE_LOCAL, face_local).commit();
	}

	/**
	 *
	 * 获取sessionId
	 * @param context
	 * @return
	 */
	public static String getSessionid(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_SESSIONID, "");
	}

	/**
	 * 保存sessionId
	 *
	 * @param context
	 */
	public static void setSessionId(final Context context,
							 final String sessionId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_SESSIONID, sessionId).commit();
	}

	/**
	 *
	 * 获取新消息是否通知
	 * @param context
	 * @return
	 */
	public static boolean getNewMessageNotice(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_NEW_MESSAGE_NOTICE, true);
	}

	/**
	 * 保存新消息是否通知
	 *
	 * @param context
	 */
	public static void setNewMessageNotice(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_NEW_MESSAGE_NOTICE, isLogin).commit();
	}

	/**
	 *
	 * 获取消息通知是否显示详情
	 * @param context
	 * @return
	 */
	public static boolean getShowMessageInfo(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_NOTICE_MESSAGE_INFO, true);
	}

	/**
	 * 保存消息通知是否显示详情
	 *
	 * @param context
	 */
	public static void setShowMessageInfo(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_NOTICE_MESSAGE_INFO, isLogin).commit();
	}

	/**
	 *
	 * 获取消息通知是否有声音
	 * @param context
	 * @return
	 */
	public static boolean getNoticeVoice(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_NOTICE_VOICE, true);
	}

	/**
	 * 保存消息通知是否有声音
	 *
	 * @param context
	 */
	public static void setNoticeVoice(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_NOTICE_VOICE, isLogin).commit();
	}

	/**
	 *
	 * 获取消息通知是否震动
	 * @param context
	 * @return
	 */
	public static boolean getNoticeShock(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_NOTICE_SHOCK, true);
	}

	/**
	 * 保存消息通知是否震动
	 *
	 * @param context
	 */
	public static void setNoticeShock(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_NOTICE_SHOCK, isLogin).commit();
	}

	/**
	 *
	 * 获取是否听筒播放语音
	 * @param context
	 * @return
	 */
	public static boolean getEarpiecePlayVoice(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_EARPIECE_PLAY_VOICE, false);
	}

	/**
	 * 保存是否听筒播放语音
	 *
	 * @param context
	 */
	public static void setEarpiecePlayVoice(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_EARPIECE_PLAY_VOICE, isLogin).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getCurrentCity(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_CURRENT_CITY, "");
	}

	/**
	 * @param context
	 */
	public static void setCurrentCity(final Context context, final String city) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_CURRENT_CITY, city).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getLoveMeUserId(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_LOVE_ME_USER_ID, "");
	}

	/**
	 * @param context
	 */
	public static void setLoveMeUserId(final Context context, final String userId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_LOVE_ME_USER_ID, userId).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getAttentionMeUserId(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_ATTENTION_ME_USER_ID, "");
	}

	/**
	 * @param context
	 */
	public static void setAttentionMeUserId(final Context context, final String userId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_ATTENTION_ME_USER_ID, userId).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getGiftMeUserId(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_GIFT_ME_USER_ID, "");
	}

	/**
	 * @param context
	 */
	public static void setGiftMeUserId(final Context context, final String userId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_GIFT_ME_USER_ID, userId).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static long getLoginTime(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getLong(SETTINGS_LOGIN_TIME, -1);
	}

	/**
	 * @param context
	 */
	public static void setLoginTime(final Context context, final long loginTime) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putLong(SETTINGS_LOGIN_TIME, loginTime).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getCurrentProvince(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_CURRENT_PROVINCE, "");
	}

	/**
	 * @param context
	 */
	public static void setCurrentProvince(final Context context, final String province) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_CURRENT_PROVINCE, province).commit();
	}

	public static void setLatitude(final Context context, final String lat) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_LATITUDE, lat).commit();
	}

	public static String getLatitude(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_LATITUDE, "");
	}

	public static void setLongitude(final Context context, final String longitude) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_LONGITUDE, longitude).commit();
	}

	public static String getLongitude(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_LONGITUDE, "");
	}

	public static boolean getIsUploadCommentImg(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_APP_COMMENT, false);
	}

	public static void setIsUploadCommentImg(final Context context,
									   final Boolean isUpload) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_APP_COMMENT, isUpload)
				.commit();
	}

	public static boolean getIsLocationSuccess(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_LOCATION_SUCCESS, false);
	}

	public static void setIsLocationSuccess(final Context context,
											 final Boolean isSuccess) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_LOCATION_SUCCESS, isSuccess)
				.commit();
	}

	public static String getSettingsSex(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_SEX, "");
	}

	public static void setSettingsSex(final Context context, final String sex) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_SEX, sex).commit();
	}

}
