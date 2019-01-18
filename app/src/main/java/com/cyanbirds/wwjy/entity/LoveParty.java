package com.cyanbirds.wwjy.entity;

import java.io.Serializable;

/**
 * 作者：wangyb
 * 时间：2016/9/27 15:01
 * 描述：相亲活动
 */
public class LoveParty implements Serializable {
	
	public Integer id;
	public String title;
	public String time;
	public String partyWhere;
	public String limitCount;
	public String partyDetail;
	public String ImgUrl;
	public double price;
	public String priceInfo;
	public String banner;
	public boolean status;
}
