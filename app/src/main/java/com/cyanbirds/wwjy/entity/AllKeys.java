package com.cyanbirds.wwjy.entity;

import java.io.Serializable;

/**
 * 作者：wangyb
 * 时间：2016/9/27 15:01
 * 描述：
 */
public class AllKeys implements Serializable {
	
	public int id;
	/**微信登录ID**/
	public String weChatId;
	/**微信支付ID**/
	public String weChatPayId;
	/**qqid**/
	public String qqId;
	/**小米id**/
	public String xmId;
	/**小米key**/
	public String xmKey;
	/**云通讯id**/
	public String ytxId;
	/**云通讯id**/
	public String ytxKey;
	/**聊天次数限制**/
	public int chatLimit;
}
