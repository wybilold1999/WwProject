package com.cyanbirds.wwjy.entity;

import android.content.ContentValues;

/**
 * 
 * @ClassName:Expression.java
 * @Description:在线表情
 * @author wangyb
 * @Date:2015年6月12日下午1:51:42
 */
public class Expression {

	/** 存取在数据库中的id*/
	public int id;
	/** 表情文字描述 */
	public String note;
	/** 表情图片id */
	public String pic_id;
	/** 表情主题 */
	public String theme;
	/** 表情主题id */
	public int theme_id;
	/** 表情的图片类型后缀 */
	public String type;
	/** 表情服务器路径（无前缀） */
	public String url;

	public ContentValues buildContentValues() {
		ContentValues values = new ContentValues();
		/*values.put(ExpressionColumn.NOTE, note);
		values.put(ExpressionColumn.PIC_ID, pic_id);
		values.put(ExpressionColumn.THEME, theme);
		values.put(ExpressionColumn.THEME_ID, theme_id);
		values.put(ExpressionColumn.TYPE, type);
		values.put(ExpressionColumn.URL, url);*/
		return values;
	}
}
