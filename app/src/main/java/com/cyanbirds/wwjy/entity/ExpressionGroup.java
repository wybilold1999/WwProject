package com.cyanbirds.wwjy.entity;

import java.util.List;

/**
 * 
 * @ClassName:ExpressionGroup.java
 * @Description:在线表情分组
 * @author zxj
 * @Date:2015年6月12日下午1:46:49
 */
public class ExpressionGroup {

	/** 存储数据库的id */
	public int id;
	/** 表情组的代表图片路径（无前缀） */
	public String cover;
	/** 表情组id */
	public int id_pic_themes;
	/** 表情组名字 */
	public String name;
	/** 压缩包路径（无前缀） */
	public String zip;
	/** 单组表情信息 */
	public List<Expression> expressions;
	/** 是否已经下载 */
	public int status = ExpressionGroupStatus.NO_DOWNLOAD;
	/**下载进度*/
	public int progress;

	/**
	 * 表情状态
	 */
	public class ExpressionGroupStatus {
		/** 未下载 */
		public static final int NO_DOWNLOAD = 0;
		/** 正在下载 */
		public static final int DOWNLOAD = 1;
		/** 已下载 */
		public static final int ALREADY_DOWNLOAD = 2;
	}

}
