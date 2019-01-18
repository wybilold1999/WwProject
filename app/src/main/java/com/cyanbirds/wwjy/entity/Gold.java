package com.cyanbirds.wwjy.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

/**
 * 作者：wangyb
 * 时间：2016/9/16 15:47
 * 描述：我的账户余额
 */
@Entity
public class Gold {
	@Id(autoincrement = true)
	public Long id;
	@Property
	@NotNull
	public long downloadTime;
	@Property
	@NotNull
	public double banlance;
	@Property
	public int clickCount;//每天可以点击的次数，15或者25
	@Property
	public int vipFlag; //1: 15  2：25
	
	@Generated(hash = 950170009)
	public Gold(Long id, long downloadTime, double banlance, int clickCount,
			int vipFlag) {
		this.id = id;
		this.downloadTime = downloadTime;
		this.banlance = banlance;
		this.clickCount = clickCount;
		this.vipFlag = vipFlag;
	}
	@Generated(hash = 2123471570)
	public Gold() {
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public double getBanlance() {
		return this.banlance;
	}
	public void setBanlance(double banlance) {
		this.banlance = banlance;
	}
	public long getDownloadTime() {
		return this.downloadTime;
	}
	public void setDownloadTime(long downloadTime) {
		this.downloadTime = downloadTime;
	}
	public int getClickCount() {
		return this.clickCount;
	}
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}
	public int getVipFlag() {
		return this.vipFlag;
	}
	public void setVipFlag(int vipFlag) {
		this.vipFlag = vipFlag;
	}
}
