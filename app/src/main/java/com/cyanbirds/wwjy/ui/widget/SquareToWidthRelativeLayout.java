package com.cyanbirds.wwjy.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 
 * @Description:根据宽度设置正方形布局
 * @author wangyb
 * @Date:2015年7月26日下午1:51:44
 */
public class SquareToWidthRelativeLayout extends RelativeLayout {

	public SquareToWidthRelativeLayout(Context context) {
		super(context, null);
	}

	public SquareToWidthRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public SquareToWidthRelativeLayout(Context context, AttributeSet attrs,
									   int defStyleAttr) {
		super(context, attrs, defStyleAttr, 0);
	}

	public SquareToWidthRelativeLayout(Context context, AttributeSet attrs,
									   int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
	

}
