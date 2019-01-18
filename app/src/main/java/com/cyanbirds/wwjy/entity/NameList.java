package com.cyanbirds.wwjy.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

/**
 * 作者：wangyb
 * 时间：2016/12/29 11:40
 * 描述：
 */
@Entity
public class NameList {
	@Id(autoincrement = true)
	public Long id;
	@Property
	@NotNull
	public long seeTime;
	@Property
	@NotNull
	public String namelist;

	@Generated(hash = 909718871)
	public NameList(Long id, long seeTime, @NotNull String namelist) {
		this.id = id;
		this.seeTime = seeTime;
		this.namelist = namelist;
	}

	@Generated(hash = 1906063924)
	public NameList() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNamelist() {
		return namelist;
	}

	public void setNamelist(String namelist) {
		this.namelist = namelist;
	}

	public long getSeeTime() {
		return seeTime;
	}

	public void setSeeTime(long seeTime) {
		this.seeTime = seeTime;
	}
}
