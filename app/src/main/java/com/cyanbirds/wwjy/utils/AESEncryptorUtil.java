package com.cyanbirds.wwjy.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 
 * @ClassName:AESEncryptorUtil.java
 * @Description:AES加密器
 * @author zxj
 * @Date:2015年5月22日上午8:53:57
 *
 */
public class AESEncryptorUtil {

	/**
	 * 加密
	 * 
	 * @param content
	 * @param key
	 * @return
	 */
	public static String crypt(String content, String key) {
		String result = null;
		try {
			Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
			Key k = new SecretKeySpec(key.getBytes(), 0, 16, "AES");
			c.init(Cipher.ENCRYPT_MODE, k);
			BASE64Encoder encoder = new BASE64Encoder();
			result = encoder.encode(c.doFinal(content.getBytes()));
		} catch (Exception ex) {
			result = null;
		}

		return result;
	}
	
	/**
	 * 解密
	 * @param code
	 * @param key
	 * @return
	 */
	public static String decrypt(String code, String key) {
		String result = null;
		try {
			Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
			Key k = new SecretKeySpec(key.getBytes(), 0, 16, "AES");
			c.init(Cipher.DECRYPT_MODE, k);
			BASE64Decoder decoder=new BASE64Decoder();
			result = new String(c.doFinal(decoder.decodeBuffer(code)));
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

}
