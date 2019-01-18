package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.cyanbirds.wwjy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName:ShareMorePagerAdapter.java
 * @Description:
 * @author wangyb
 * @Date:2015年5月27日上午11:11:42
 *
 */
public class PagerGridAdapter extends PagerAdapter implements
		OnPageChangeListener {

	private List<GridView> mGridViews;
	private Context mContext;
	private LinearLayout mGuideDotLay;
	private List<ImageView> mDotImage;

	@SuppressWarnings("deprecation")
	public PagerGridAdapter(Context context, List<GridView> grids,
							ViewPager viewPage, LinearLayout guideDotLay) {
		this.mGridViews = grids;
		this.mContext = context;
		this.mGuideDotLay = guideDotLay;
		viewPage.setOnPageChangeListener(this);
		// viewPage.addOnPageChangeListener(this);
		mDotImage = new ArrayList<ImageView>();
	}

	@Override
	public int getCount() {
		return mGridViews == null ? 0 : mGridViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(mGridViews.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(mGridViews.get(position));
		return mGridViews.get(position);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (null != observer) {
			super.unregisterDataSetObserver(observer);
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mDotImage.clear();
		mGuideDotLay.removeAllViews();
		for (int i = 0; i < mGridViews.size(); i++) {
			ImageView iv = new ImageView(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					40);
			lp.setMargins(20, 0, 0, 10);
			int ic = i == 0 ? R.mipmap.ic_page_indicator_enabled
					: R.mipmap.ic_page_indicator;
			iv.setImageResource(ic);
			iv.setLayoutParams(lp);
			mGuideDotLay.addView(iv);
			mDotImage.add(iv);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		for (int i = 0; i < mDotImage.size(); i++) {
			mDotImage.get(i).setImageResource(R.mipmap.ic_page_indicator);
		}
		mDotImage.get(arg0).setImageResource(
				R.mipmap.ic_page_indicator_enabled);
	}
}
