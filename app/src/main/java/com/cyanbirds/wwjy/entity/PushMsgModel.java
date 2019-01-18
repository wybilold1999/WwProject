package com.cyanbirds.wwjy.entity;

import java.io.Serializable;

/**
 * 作者：wangyb
 * 时间：2016/10/11 23:09
 * 描述：
 */
public class PushMsgModel implements Serializable {

	/**
	 * msgId : bcdaacc8-7f23-4828-ac8c-c9391dbe2bef
	 * msgType : 0
	 * serverTime : 1476195787489
	 * content : 222222222
	 * sender : 1
	 * senderName : miss?英?
	 * faceUrl : http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_c965d150-dd50-47fd-82ba-ce668f6fe9c9.jpg
	 * duration : 0
	 * boxType : 1
	 * latitude : 0.0
	 * longitude : 0.0
	 * imgW : 0
	 * imgH : 0
	 * fileSize : 0
	 * isGif : false
	 * stickerId : 0
	 * stickerPackId : 0
	 */

	public String msgId;
	public int msgType;
	public long serverTime;
	public String content;
	public String sender;
	public String senderName;
	public String faceUrl;
	public String fileUrl;
	public int duration;
	public int boxType;
	public double latitude;
	public double longitude;
	public int imgW;
	public int imgH;
	public int fileSize;
	public boolean isGif;
	public int stickerId;
	public int stickerPackId;

	/** 消息类型 **/
	public class MessageType {
		/** 文字消息 **/
		public static final int TEXT = 0;
		/** 图片 **/
		public static final int IMG = 1;
		/** 音频 **/
		public static final int VOICE = 2;
		/** 位置 **/
		public static final int LOCATION = 3;
		/** 贴纸 */
		public static final int STICKER = 4;
		/** 电话 */
		public static final int VOIP = 5;
	}
}
