package com.cyanbirds.wwjy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * 
 * @Description:网络工具类
 * @author zxj
 * @Date:2015年12月15日下午4:00:59
 */
public class NetworkUtils {

	/**
	 * 获取当前网络类型
	 * 
	 * @param context
	 * @return null=获取网络信息失败or未知网络
	 */
	public static String getNetType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return null;
		}
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			WifiManager wifiMgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			String name = wifiInfo.getSSID();
			//wifi
			return name;
		}
		if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int sub = info.getSubtype();
			switch (sub) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_CDMA:// 电信的2G
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_IDEN:
				return "2G";
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_EHRPD:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				return "3G";
			case TelephonyManager.NETWORK_TYPE_LTE:
				return "4G";
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				return null;
			default:
				return null;
			}
		}
		return null;
	}

}
