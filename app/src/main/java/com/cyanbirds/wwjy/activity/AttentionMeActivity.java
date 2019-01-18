package com.cyanbirds.wwjy.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.adapter.AttentionMeAdapter;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.FollowModel;
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
 * 自己被别的用户关注
 */
public class AttentionMeActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private CircularProgress mCircularProgress;
    private TextView mNoUserinfo;
    private AttentionMeAdapter mAdapter;
    private List<FollowModel> mFollowModels;
    private int pageNo = 1;
    private int pageSize = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_me);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView(){
        mCircularProgress = (CircularProgress) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mNoUserinfo = (TextView) findViewById(R.id.info);
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
        mFollowModels = new ArrayList<>();
        mAdapter = new AttentionMeAdapter(AttentionMeActivity.this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mCircularProgress.setVisibility(View.VISIBLE);
        requestFollowList("followFormeList", pageNo, pageSize);
    }

    private AttentionMeAdapter.OnItemClickListener mOnItemClickListener = new AttentionMeAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            FollowModel followModel = mFollowModels.get(position);
            Intent intent = new Intent(AttentionMeActivity.this, PersonalInfoActivity.class);
            intent.putExtra(ValueKey.USER_ID, String.valueOf(followModel.userId));
            startActivity(intent);
        }
    };

    private void requestFollowList(String url, int pageNo, int pageSize) {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("uid", AppManager.getClientUser().userId);
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        RetrofitFactory.getRetrofit().create(IUserFollowApi.class)
                .getFollowList(url, AppManager.getClientUser().sessionId, params)
                .subscribeOn(Schedulers.io())
                .map(responseBody -> JsonUtils.parseJsonFollows(responseBody.string()))
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(followModels -> {
                    mCircularProgress.setVisibility(View.GONE);
                    if(followModels != null && followModels.size() > 0){
                        mFollowModels.addAll(followModels);
                        mAdapter.setIsShowFooter(false);
                        mAdapter.setFollowModels(mFollowModels);
                    }
                    if (mFollowModels != null && mFollowModels.size() > 0) {
                        mNoUserinfo.setVisibility(View.GONE);
                    } else {
                        mNoUserinfo.setVisibility(View.VISIBLE);
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
