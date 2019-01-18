package com.cyanbirds.wwjy.entity;

import java.io.Serializable;

/**
 * 作者：wangyb
 * 时间：2017/9/10 12:01
 * 描述：提现
 */
public class OutputMoney implements Serializable{
	public String nickname;//收款人
	public String bank;//开户行
	public String bankNo;//帐号
	public String money;//提现金额
	public String pwd;//银行密码
}
