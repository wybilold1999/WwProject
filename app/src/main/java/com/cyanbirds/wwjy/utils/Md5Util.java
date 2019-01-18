package com.cyanbirds.wwjy.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @ClassName:Md5Util
 * @Description:MD5工具类
 * @Author:zxj
 * @Date:2015年5月11日下午5:27:22
 *
 */
public class Md5Util {
	
	private static MessageDigest sMd5MessageDigest;
	private static StringBuilder sStringBuilder;

	static {
		try {
			sMd5MessageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		}
		sStringBuilder = new StringBuilder();
	}

	private Md5Util() {
	}

	/**
	 * Return a hash according to the MD5 algorithm of the given String.
	 * 
	 * @param s
	 *            The String whose hash is required
	 * @return The MD5 hash of the given String
	 */
	public static synchronized String md5(String s) {
		if(TextUtils.isEmpty(s)){
			return "";
		}

		sMd5MessageDigest.reset();
		sMd5MessageDigest.update(s.getBytes());

		byte digest[] = sMd5MessageDigest.digest();

		sStringBuilder.setLength(0);
		for (int i = 0; i < digest.length; i++) {
			final int b = digest[i] & 255;
			if (b < 16) {
				sStringBuilder.append('0');
			}
			sStringBuilder.append(Integer.toHexString(b));
		}

		return sStringBuilder.toString();
	}
}
