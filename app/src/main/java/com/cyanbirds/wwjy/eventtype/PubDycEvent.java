package com.cyanbirds.wwjy.eventtype;

/**
 * 作者：wangyb
 * 时间：2016/9/13 21:18
 * 描述发表动态之后更新自己动态的event
 */
public class PubDycEvent {
	public String dynamicContent;

	public PubDycEvent(String dynamicContent) {
		this.dynamicContent = dynamicContent;
	}
}
