package com.cyanbirds.wwjy.activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.umeng.analytics.MobclickAgent;

import me.relex.photodraweeview.PhotoDraweeView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author wangyb
 * @Description:图片浏览
 * @Date:2015年7月8日上午11:09:50
 */
public class PhotoViewActivity extends BaseActivity implements View.OnClickListener, PhotoViewAttacher.OnViewTapListener {
    private PhotoDraweeView mPhotoView;
    private PhotoViewAttacher mAttacher;

    private String mCurImgUrl; //当前显示图片的url


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView(){
        mPhotoView = (PhotoDraweeView) findViewById(R.id.photo_drawee_view);
        mAttacher = new PhotoViewAttacher(mPhotoView);

    }

    private void setupEvent() {
        mPhotoView.setOnClickListener(this);
        mAttacher.setOnViewTapListener(this);
    }

    /**
     * 设置数据
     */
    private void setupData() {
        mCurImgUrl = getIntent().getStringExtra(ValueKey.IMAGE_URL);
        if(!TextUtils.isEmpty(mCurImgUrl)){
            mPhotoView.setImageURI(Uri.parse(mCurImgUrl));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.photo_drawee_view :
                finish();
                break;
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        finish();
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
