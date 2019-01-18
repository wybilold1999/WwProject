package com.cyanbirds.wwjy.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;


/**
 * 
 * @ClassName:Conversation
 * @Description:聊天会话
 * @author wangyb
 * @Date:2015年5月23日下午10:27:09
 *
 */
@Entity
public class Conversation implements Serializable{
	public static final long serialVersionUID = 1L;

	/** id */
	@Id(autoincrement = true)
	public Long id;
	/** 聊天对象 */
	@Property
	@NotNull
	@Unique
	public String talker;

	/** 聊天对象名称 */
	@Property
	@NotNull
	public String talkerName;
	/** 消息内容 */
	@Property
	@NotNull
	public String content;
	/** 消息未读数 */
	@Property
	@NotNull
	public int unreadCount;
	/** 最后一条消息时间 */
	@Property
	@NotNull
	public long createTime;
	/** 最后一条消息类型 */
	@Property
	@NotNull
	public int type;
	/** 本地头像地址 */
	@Property
	public String localPortrait;
	/** 头像地址 */
	@Property
	public String faceUrl;
	@Generated(hash = 1491216450)
	public Conversation(Long id, @NotNull String talker,
			@NotNull String talkerName, @NotNull String content, int unreadCount,
			long createTime, int type, String localPortrait, String faceUrl) {
		this.id = id;
		this.talker = talker;
		this.talkerName = talkerName;
		this.content = content;
		this.unreadCount = unreadCount;
		this.createTime = createTime;
		this.type = type;
		this.localPortrait = localPortrait;
		this.faceUrl = faceUrl;
	}
	@Generated(hash = 1893991898)
	public Conversation() {
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTalker() {
		return this.talker;
	}
	public void setTalker(String talker) {
		this.talker = talker;
	}
	public String getTalkerName() {
		return this.talkerName;
	}
	public void setTalkerName(String talkerName) {
		this.talkerName = talkerName;
	}
	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getUnreadCount() {
		return this.unreadCount;
	}
	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}
	public long getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getType() {
		return this.type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getLocalPortrait() {
		return this.localPortrait;
	}
	public void setLocalPortrait(String localPortrait) {
		this.localPortrait = localPortrait;
	}
	public String getFaceUrl() {
		return this.faceUrl;
	}
	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

}
