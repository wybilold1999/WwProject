package com.cyanbirds.wwjy.entity;

import java.io.Serializable;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-31 19:29 GMT+8
 * @email 395044952@qq.com
 */
public class ClientUser implements Serializable{
    public long id;
    /** 用户id */
    public String userId;
    /** 用户名 */
    public String user_name;
    /** 密码 */
    public String userPwd;
    /** 本地头像地址*/
    public String face_local;
    /** 头像URL **/
    public String face_url;
    /** 个性签名 **/
    public String signature;
    /** 电话号码 **/
    public String mobile;
    /** 微信号 **/
    public String weixin_no;
    /** QQ号 **/
    public String qq_no;
    /** 性别 **/
    public String sex;
    /**年龄**/
    public int age;
    /** 城市 **/
    public String city;
    /**身高**/
    public String tall;
    /**体重**/
    public String weight;
    /**距离**/
    public String distance;
    /**情感状态**/
    public String state_marry;
    /**星座**/
    public String constellation;
    /**个性标签**/
    public String personality_tag;
    /**魅力部位**/
    public String part_tag;
    /** 兴趣爱好 **/
    public String intrest_tag;
    /** 职业 **/
    public String occupation;
    /** 学历 **/
    public String education;
    public String purpose; //交友目的
    public String love_where; //喜欢爱爱地点
    public String do_what_first; //首次见面希望
    public String conception; //恋爱观念
    /**是否vip**/
    public boolean is_vip;
    /**是否赚钱会员**/
    public boolean is_download_vip;
    /**金币数量**/
    public int gold_num;
    /**是否公开社交帐号**/
    public boolean publicSocialNumber;
    /**是否验证手机**/
    public boolean isCheckPhone;
    /**登录的sessiondid**/
    public String sessionId;
    public boolean isFollow;
    /**用户上传的图片**/
    public String imgUrls; //用json字符串保存上传的图片
    /**
     * 最新的版本号
     */
    public int versionCode;
    /**
     * 最新的apk的url
     */
    public String apkUrl;
    /**
     * 版本更新的信息
     */
    public String versionUpdateInfo;
    /**
     * 是否强制升级
     */
    public boolean isForceUpdate;
	/**
     * 用户获得的礼物
     */
    public String gifts;
    /**
     * 0:不显示  1：显示
     */
    public boolean isShowVip; //vip
    /**
     * 0:不显示  1：显示
     */
    public boolean isShowDownloadVip; //下载赚钱
    /**
     * 0:不显示  1：显示
     */
    public boolean isShowGold; //金币
    /**
     * 0:不显示  1：显示
     */
    public boolean isShowLovers; //红娘服务
    /**
     * 0:不显示  1：显示
     */
    public boolean isShowVideo; //视频
    /**
     * 0:不显示  1：显示
     */
    public boolean isShowMap; //地图
    /**
     * 0:不显示  1：显示
     */
    public boolean isShowRpt; //红包
    /**
     * 0:不显示  1：显示
     */
    public boolean isShowTd; //跳转到vip还是downloadvip

    /**
     * 0:不显示  1：显示
     */
    public boolean isShowAppointment; //是否显示约会

    /**
     * 0:不显示  1：显示
     */
    public boolean isShowGiveVip; //是否显示赠送会员

    public String latitude = "";
    public String longitude = "";

    public String currentCity;//定位的当前城市

    public long loginTime;//登录时间
    
    public boolean isLocalMsg;//是否是本地创建的消息(礼物、关注、喜欢)

}
