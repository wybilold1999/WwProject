package com.cyanbirds.wwjy.entity;


/**
 * 会员商品
 */
public class MemberBuy implements java.io.Serializable {

	public Integer id;
	public Integer type; // 0:普通会员  1：赚钱会员
	public String months; //期限
	public double aliPrice;
	public double price;  //价格
	public String preferential;//优惠
	public String descreption;  //描述 低至1元/天
	public boolean isShowAliPay;
	public boolean isShowWePay;

	public boolean isShowAli;
	public boolean isShowWe;
	public boolean isSelected = false;

}
