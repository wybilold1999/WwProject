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
import com.cyanbirds.wwjy.adapter.LoveFormeAdapter;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.LoveModel;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserLoveApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.ui.widget.CircularProgress;
import com.cyanbirds.wwjy.ui.widget.DividerItemDecoration;
import com.cyanbirds.wwjy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.wwjy.utils.DensityUtil;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-01-13 22:17 GMT+8
 * @email 395044952@qq.com
 * 谁赞了我
 */
public class LoveFormeActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    CircularProgress mCircularProgress;
    @BindView(R.id.info)
    TextView mNoUserinfo;
    private LoveFormeAdapter mAdapter;

    private List<LoveModel> mLoveModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_me);
        ButterKnife.bind(this);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView() {
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

    private void setupEvent() {

    }

    private void setupData() {
        mAdapter = new LoveFormeAdapter(LoveFormeActivity.this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mCircularProgress.setVisibility(View.VISIBLE);
        requestLoveForme(1, 100);
    }

    private LoveFormeAdapter.OnItemClickListener mOnItemClickListener = new LoveFormeAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            LoveModel loveModel = mLoveModels.get(position);
            Intent intent = new Intent(LoveFormeActivity.this, PersonalInfoActivity.class);
            intent.putExtra(ValueKey.USER_ID, String.valueOf(loveModel.userId));
            startActivity(intent);
        }
    };

    private void requestLoveForme(final int pageNo, final int pageSize){
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("uid", AppManager.getClientUser().userId);
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        RetrofitFactory.getRetrofit().create(IUserLoveApi.class)
                .getLoveFormeList(AppManager.getClientUser().sessionId, params)
                .subscribeOn(Schedulers.io())
                .map(responseBody -> JsonUtils.parseJsonLovers(responseBody.string()))
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(loveModels -> {
                    mCircularProgress.setVisibility(View.GONE);
                    if(loveModels != null && loveModels.size() > 0){
                        mNoUserinfo.setVisibility(View.GONE);
                        mLoveModels = loveModels;
                        mAdapter.setLoveModels(loveModels);
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
