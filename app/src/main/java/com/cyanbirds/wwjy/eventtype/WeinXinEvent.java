package com.cyanbirds.wwjy.eventtype;

/**
 * 作者：wangyb
 * 时间：2016/11/1 14:58
 * 描述：微信授权登陆的事件
 */
public class WeinXinEvent {
	public String code;//授权登陆之后获取的code，用于上传服务器

	public WeinXinEvent(String code) {
		this.code = code;
	}
}
