package com.cyanbirds.wwjy.entity;

/**
 * 作者：wangyb
 * 时间：2016/11/3 18:30
 * 描述：服务端返回的信息
 */
public class WeChatPay {
	public String mch_id;
	public String prepay_id;
	public String nonce_str;
	public String timeStamp;
	public String sign;
	public String appSign;
}
