package com.cyanbirds.wwjy.entity;


import java.io.Serializable;
import java.util.List;

/**
 * 缘分model
 * @author Administrator
 *
 */
public class YuanFenModel implements Serializable{
	/** 用户id */
	public Integer uid;
	/** 用户性别 */
	public Integer sex;
	/** 用户昵称 */
	public String nickname;
	/** 用户生日 */
	public int age;
	/** 城市 */
	public String city;
	/** 签名 */
	public String signature;
	/** 头像 */
	public String faceUrl;
	/** 情感状态 */
	public String emotionStatus;
	/** 星座 */
	public String constellation;
	/**距离**/
	public Double distance;

	public List<String> pictures;

}
