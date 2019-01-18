package com.cyanbirds.wwjy.entity;

/**
 * 
 * @Description:收到的礼物
 * @author wangyb
 * @Date:2015年8月12日下午2:09:07
 */
public class ReceiveGiftModel {

	/** 关注id */
	public Integer id;
	/** 用户id */
	public Integer userId;
	/** 赠送时间 */
	public String sendTime;
	/** 用户头像 */
	public String faceUrl;
	/** 性别 */
	public int sex;
	/** 昵称 */
	public String nickname;
	/** 年龄 */
	public Integer age;
	/** 最后登录时间 */
	public String lastLoginTime;
	/** 用户类型 */
	public Integer UType;
	/** 签名 */
	public String signature;
	/** 距离 */
	public Double distance;
	/** 情感状态 */
	public String emotionStatus;
	/** 星座 */
	public String constellation;
	/** vip */
	public Boolean isVip;
	/** 礼物名称 **/
	public String giftName;
	/** 礼物url **/
	public String giftUrl;
}
