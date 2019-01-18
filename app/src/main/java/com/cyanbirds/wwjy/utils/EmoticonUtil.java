package com.cyanbirds.wwjy.utils;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.entity.Emoticon;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName:EmoticonUtil
 * @Description:表情工具类
 * @author Administrator
 * @Date:2015年5月29日下午2:20:46
 */
public class EmoticonUtil {

	private static List<Emoticon> mEmoticons = new ArrayList<Emoticon>();

	private static EmoticonUtil mEmoticonUtil;

	public static EmoticonUtil getInstace() {
		if (mEmoticonUtil == null) {
			mEmoticonUtil = new EmoticonUtil();
		}
		return mEmoticonUtil;
	}

	/**
	 * 获取表情数据
	 */
	public List<Emoticon> getEmoticonList() {
		return mEmoticons;
	}

	/**
	 * 初始化表情
	 */
	public void initEmoticon() {
		String[] emoticonName = CSApplication.getInstance().getResources()
				.getStringArray(R.array.emoji_name);
		String[] emoticonCode = CSApplication.getInstance().getResources()
				.getStringArray(R.array.emoji_code);
		if (emoticonName != null && emoticonCode != null) {
			initEmoticonIcon(emoticonName, emoticonCode);
		}
	}

	/**
	 * 初始化表情头像
	 */
	private void initEmoticonIcon(String[] emoticonName, String[] emoticonCode) {
		mEmoticons.clear();
		Emoticon emoticon = null;
		for (int i = 0; i < emoticonCode.length; i++) {
			emoticon = new Emoticon();
			emoticon.reId = CSApplication
					.getInstance()
					.getResources()
					.getIdentifier("e_" + (i + 1), "mipmap",
							CSApplication.getInstance().getPackageName());
			emoticon.emojiName = emoticonName[i];
			emoticon.emojiCode = emoticonCode[i];
			mEmoticons.add(emoticon);
		}
	}

	/**
	 * 将代码转换为表情，可直接在TextView中显示
	 * 
	 * @param content
	 * @return
	 */
	public static String convertExpression(String content) {
		if (!TextUtils.isEmpty(content)) {
			for (int i = 0; i < mEmoticons.size(); i++) {
				content = content.replace(mEmoticons.get(i).emojiCode,
						"<img src=\"" + mEmoticons.get(i).reId + "\" />");
			}
		}
		return content;
	}

	/***
	 * 表情转换
	 */
	public static Html.ImageGetter chat_imageGetter_resource = new Html.ImageGetter() {
		@SuppressWarnings("deprecation")
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			int rId = Integer.parseInt(source);
			if (Build.VERSION.SDK_INT >= 22) {
				drawable = CSApplication.getInstance().getResources()
						.getDrawable(rId, null);
			} else {
				drawable = CSApplication.getInstance().getResources()
						.getDrawable(rId);
			}
			int wh = (int) CSApplication.getInstance().getResources()
					.getDimension(R.dimen.chat_emoji_wh);
			drawable.setBounds(0, 0, wh, wh);
			return drawable;
		}
	};

	/***
	 * 表情转换
	 */
	public static Html.ImageGetter conversation_imageGetter_resource = new Html.ImageGetter() {
		@SuppressWarnings("deprecation")
		public Drawable getDrawable(String source) {
			int wh = (int) CSApplication.getInstance().getResources()
					.getDimension(R.dimen.conversation_enoticon_wh);
			Drawable drawable = null;
			int rId = Integer.parseInt(source);
			if (Build.VERSION.SDK_INT >= 22) {
				drawable = CSApplication.getInstance().getResources()
						.getDrawable(rId, null);
			} else {
				drawable = CSApplication.getInstance().getResources()
						.getDrawable(rId);
			}
			drawable.setBounds(0, 0, wh, wh);
			return drawable;
		}
	};

}
