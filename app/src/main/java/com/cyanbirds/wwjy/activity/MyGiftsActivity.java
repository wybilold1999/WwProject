package com.cyanbirds.wwjy.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.adapter.MyGiftsAdapter;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ReceiveGiftModel;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserFollowApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.ui.widget.CircularProgress;
import com.cyanbirds.wwjy.ui.widget.DividerItemDecoration;
import com.cyanbirds.wwjy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.wwjy.utils.DensityUtil;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-01-13 22:17 GMT+8
 * @email 395044952@qq.com
 */
public class MyGiftsActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private CircularProgress mCircularProgress;
    private TextView mNoUserInfo;
    private MyGiftsAdapter mAdapter;

    private int pageNo = 1;
    private int pageSize = 13;
    private List<ReceiveGiftModel> mReceiveGiftModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_attention);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_gifts);
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView(){
        mCircularProgress = (CircularProgress) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mNoUserInfo = (TextView) findViewById(R.id.info);
        LinearLayoutManager manager = new WrapperLinearLayoutManager(this);
        manager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL, DensityUtil
                .dip2px(this, 12), DensityUtil.dip2px(
                this, 12)));
    }

    private void setupEvent(){

    }

    private void setupData(){
        if (AppManager.getClientUser().isShowVip) {
            if (AppManager.getClientUser().is_vip) {
                pageSize = 200;
            }
        } else {
            pageSize = 200;
        }
        mReceiveGiftModels = new ArrayList<>();
        mAdapter = new MyGiftsAdapter(MyGiftsActivity.this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mCircularProgress.setVisibility(View.VISIBLE);
        requestMyGiftList(pageNo, pageSize);
    }

    private MyGiftsAdapter.OnItemClickListener mOnItemClickListener = new MyGiftsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            ReceiveGiftModel receiveGiftModel = mAdapter.getItem(position);
            Intent intent = new Intent(MyGiftsActivity.this, PersonalInfoActivity.class);
            intent.putExtra(ValueKey.USER_ID, String.valueOf(receiveGiftModel.userId));
            startActivity(intent);
        }
    };

    /**
     * 获取礼物
     */
    private void requestMyGiftList(int pageNo, int pageSize){
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("uid", AppManager.getClientUser().userId);
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        RetrofitFactory.getRetrofit().create(IUserFollowApi.class)
                .getGiftsList(AppManager.getClientUser().sessionId, params)
                .subscribeOn(Schedulers.io())
                .map(responseBody -> JsonUtils.parseJsonReceiveGift(responseBody.string()))
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(receiveGiftModels -> {
                    mCircularProgress.setVisibility(View.GONE);
                    if(null != receiveGiftModels && receiveGiftModels.size() > 0){
                        if (AppManager.getClientUser().isShowVip &&
                                !AppManager.getClientUser().is_vip &&
                                receiveGiftModels.size() > 10) {//如果不是vip，移除前面3个
                            mAdapter.setIsShowFooter(true);
                            List<String> urls = new ArrayList<>(3);
                            urls.add(receiveGiftModels.get(0).faceUrl);
                            urls.add(receiveGiftModels.get(1).faceUrl);
                            urls.add(receiveGiftModels.get(2).faceUrl);
                            mAdapter.setFooterFaceUrls(urls);
                            receiveGiftModels.remove(0);
                            receiveGiftModels.remove(1);
                        }
                        mCircularProgress.setVisibility(View.GONE);
                        mReceiveGiftModels.addAll(receiveGiftModels);
                        mAdapter.setReceiveGiftModel(mReceiveGiftModels);
                    } else {
                        if (receiveGiftModels != null) {
                            mReceiveGiftModels.addAll(receiveGiftModels);
                        }
                        mAdapter.setIsShowFooter(false);
                        mAdapter.setReceiveGiftModel(mReceiveGiftModels);
                    }
                    if (mReceiveGiftModels != null && mReceiveGiftModels.size() > 0) {
                        mNoUserInfo.setVisibility(View.GONE);
                    } else {
                        mNoUserInfo.setText("您还没有收到礼物哦");
                        mNoUserInfo.setVisibility(View.VISIBLE);
                    }
                }, throwable -> {
                    mCircularProgress.setVisibility(View.GONE);
                    ToastUtil.showMessage(R.string.network_requests_error);
                });
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
