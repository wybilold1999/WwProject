package com.cyanbirds.wwjy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.adapter.PhotoChoserAdapter;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ImageBean;
import com.cyanbirds.wwjy.listener.ChoseImageListener;
import com.cyanbirds.wwjy.net.ImagesLoader;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Description:图片选择
 * @author wangyb
 * @Date:2015年7月27日下午3:28:06
 */
public class PhotoChoserActivity extends BaseActivity implements
		LoaderCallbacks<List<ImageBean>>, ChoseImageListener{

	private TextView mSelectNumber;
	private RecyclerView mRecyclerView;
	private PhotoChoserAdapter mAdapter;

	private List<ImageBean> mImages;
	private int mSelectedCount = 0;

	private static int MAX_SELECT_NUMBER = 6;

	/**
	 * 已经选中的url
	 */
	private List<String> imgUrls = null;
	/**
	 * 选中本地图片的url
	 */
	List<String> list = null;
	/**
	 * oss上面的url
	 */
	List<String> ossUrl = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_choser);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupViews();
		setupEvent();
		setupData();
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mSelectNumber = (TextView) findViewById(R.id.select_number);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
	}

	private void setupEvent(){
	}

	/**
	 * 设置数据
	 */
	private void setupData() {
		MAX_SELECT_NUMBER = getIntent().getIntExtra(ValueKey.DATA, 6);
		ossUrl = new ArrayList<>();
		mImages = new ArrayList<ImageBean>();
		mAdapter = new PhotoChoserAdapter(this, mImages);
		mAdapter.setChoseImageListener(this);
		mRecyclerView.setAdapter(mAdapter);
		getSupportLoaderManager().initLoader(0, null, this);
		imgUrls = getIntent().getStringArrayListExtra(ValueKey.IMAGE_URL);
		if (imgUrls != null && imgUrls.size() > 0) {
			mSelectedCount = imgUrls.size();
			refreshPreviewTextView();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.define_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.ok) {
			list = getSelectedImagePaths();
			if(list != null && list.size() > 0){
				Intent intent = new Intent();
				intent.putStringArrayListExtra(ValueKey.IMAGE_URL, (ArrayList<String>) list);
				setResult(RESULT_OK, intent);
				finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 刷新预览
	 */
	private void refreshPreviewTextView() {
		if (mSelectedCount > 0) {
			mSelectNumber.setText(String.valueOf(mSelectedCount));
			mSelectNumber.setEnabled(true);
		} else {
			mSelectNumber.setText(String.valueOf(0));
			mSelectNumber.setEnabled(false);
		}
	}

	/**
	 * 获取选择的图片路径
	 * 
	 * @return
	 */
	private List<String> getSelectedImagePaths() {
		List<String> selectedImages = new ArrayList<String>();
		for (ImageBean image : mImages) {
			if (image.isSeleted()) {
				selectedImages.add(image.getPath());
			}
		}
		return selectedImages;
	}

	@Override
	public Loader<List<ImageBean>> onCreateLoader(int arg0, Bundle arg1) {
		return new ImagesLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<ImageBean>> loader,
			List<ImageBean> imageBeans) {
		this.mImages.clear();
		this.mImages.addAll(imageBeans);
		if (imgUrls != null && imgUrls.size() > 0) {
			for (int i = 0; i < imageBeans.size(); i++) {
				if (imgUrls.contains(imageBeans.get(i).getPath())) {
					imageBeans.get(i).setSeleted(true);
				}
			}
		}
		mAdapter.notifyDataSetChanged();

	}

	@Override
	public void onLoaderReset(Loader<List<ImageBean>> arg0) {

	}

	@Override
	public boolean onSelected(ImageBean image) {
		if (mSelectedCount >= MAX_SELECT_NUMBER) {
			ToastUtil.showMessage(R.string.arrive_limit_count);
			return false;
		}
		image.setSeleted(true);
		mSelectedCount++;
		refreshPreviewTextView();
		return true;
	}

	@Override
	public boolean onCancelSelect(ImageBean image) {
		image.setSeleted(false);
		mSelectedCount--;
		refreshPreviewTextView();
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
		MobclickAgent.onPause(this);
	}
}
