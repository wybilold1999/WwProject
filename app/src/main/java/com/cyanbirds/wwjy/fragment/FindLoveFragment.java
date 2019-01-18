package com.cyanbirds.wwjy.fragment;

import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.CardActivity;
import com.cyanbirds.wwjy.activity.PersonalInfoActivity;
import com.cyanbirds.wwjy.adapter.FindLoveAdapter;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserApi;
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
 * @author: wangyb
 * @datetime: 2015-12-20 11:33 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class FindLoveFragment extends Fragment implements OnRefreshListener, View.OnClickListener{
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private FloatingActionButton mFab;
    private CircularProgress mProgress;
    private View rootView;
    private View searchView;
    private RadioButton sex_male;
    private RadioButton sex_female;
    private RadioGroup mSexGroup;

    private FindLoveAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private List<ClientUser> mClientUsers;
    private final long freshSpan = 3 * 60 * 1000;//3分钟之后才运行下拉刷新
    private long freshTime = 0;
    private int pageIndex = 1;
    private int pageSize = 150;
    private String GENDER = ""; //空表示查询和自己性别相反的用户
	/**
     * 0:同城 1：缘分 2：颜值  -1:就是全国
     */
    private String mUserScopeType = "";

    private final String SAME_CITY = "0";
    private final String BEAUTIFUL = "2";
    private final String ALL_COUNTRY = "-1";

    private static final String FRAGMENT_INDEX = "fragment_index";
    private int mCurIndex = -1;
    private boolean mIsRefreshing = false;

    public static FindLoveFragment newInstance(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_INDEX, index);
        FindLoveFragment fragment = new FindLoveFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_find_love, null);
            setupViews();
            setupEvent();
            setupData();
            setHasOptionsMenu(true);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void setupViews() {
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mProgress = (CircularProgress) rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        layoutManager = new WrapperLinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getActivity(), LinearLayoutManager.VERTICAL, DensityUtil
                .dip2px(getActivity(), 12), DensityUtil.dip2px(
                getActivity(), 12)));
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsRefreshing) {
                    return true;
                }
                return false;
            }
        });

    }

    private void setupEvent() {
        mSwipeRefresh.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mFab.setOnClickListener(this);
    }

    private void setupData() {
        //获得索引值
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurIndex = bundle.getInt(FRAGMENT_INDEX);
        }
        switch (mCurIndex) {
            case 0 :
                mUserScopeType = BEAUTIFUL;
                break;
            case 1 :
                mUserScopeType = SAME_CITY;
                break;
            case 2 :
                mUserScopeType = ALL_COUNTRY;
                break;
        }

        mClientUsers = new ArrayList<>();
        mAdapter = new FindLoveAdapter(mClientUsers, getActivity(), mCurIndex);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mProgress.setVisibility(View.VISIBLE);
        if("1".equals(AppManager.getClientUser().sex)){
            GENDER = "FeMale";
        } else {
            GENDER = "Male";
        }
        getFindLove(pageIndex, pageSize, GENDER, mUserScopeType);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSearchDialog();
        return super.onOptionsItemSelected(item);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        private int lastVisibleItem;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == mAdapter.getItemCount()
                    && mAdapter.isShowFooter()) {
                //加载更多
                //请求数据
                getFindLove(++pageIndex, pageSize, GENDER, mUserScopeType);
            }
        }
    };

    private FindLoveAdapter.OnItemClickListener mOnItemClickListener = new FindLoveAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            ClientUser clientUser = mAdapter.getItem(position);
            if (clientUser != null) {
                Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
                intent.putExtra(ValueKey.USER_ID, clientUser.userId);
                intent.putExtra(ValueKey.FROM_ACTIVITY, "FindLoveFragment");
                startActivity(intent);
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), CardActivity.class);
        startActivity(intent);
    }

    private void getFindLove(int pageNo, int pageSize, String gender, String mUserScopeType) {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("userId", AppManager.getClientUser().userId);
        params.put("gender", gender);
        params.put("user_scope_type", mUserScopeType);
        RetrofitFactory.getRetrofit().create(IUserApi.class)
                .getHomeLoveList(AppManager.getClientUser().sessionId, params)
                .subscribeOn(Schedulers.io())
                .map(responseBody -> JsonUtils.parseUsertList(responseBody.string()))
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(userList -> {
                    freshTime = System.currentTimeMillis();
                    mProgress.setVisibility(View.GONE);
                    mIsRefreshing = false;
                    mSwipeRefresh.setRefreshing(false);
                    if (pageIndex == 1) {//进行筛选的时候，滑动到顶部
                        layoutManager.scrollToPositionWithOffset(0, 0);
                    }
                    if(userList == null || userList.size() == 0){
                        mAdapter.setIsShowFooter(false);
                        mAdapter.notifyDataSetChanged();
                        ToastUtil.showMessage(R.string.no_more_data);
                    } else {
                        mClientUsers.addAll(userList);
                        mAdapter.setIsShowFooter(true);
                        mAdapter.setClientUsers(mClientUsers);
                    }
                }, throwable -> {
                    ToastUtil.showMessage(R.string.network_requests_error);
                    mProgress.setVisibility(View.GONE);
                    mIsRefreshing = false;
                    mSwipeRefresh.setRefreshing(false);
                    mAdapter.setIsShowFooter(false);
                    mAdapter.notifyDataSetChanged();
                });
    }

    /**
     * 筛选dialog
     */
    private void showSearchDialog(){
        initSearchDialogView();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.search_option));
        builder.setView(searchView);
        builder.setPositiveButton(getResources().getString(R.string.ok),
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        pageIndex = 1;
                        mClientUsers.clear();
                        getFindLove(pageIndex, pageSize, GENDER, mUserScopeType);
                        mProgress.setVisibility(View.VISIBLE);
                        mIsRefreshing = true;
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    /**
     * 初始化筛选对话框
     */
    private void initSearchDialogView(){
        searchView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search, null);
        mSexGroup = (RadioGroup) searchView.findViewById(R.id.rg_sex);
        sex_male = (RadioButton) searchView.findViewById(R.id.sex_male);
        sex_female = (RadioButton) searchView.findViewById(R.id.sex_female);
        if("Male".equals(GENDER)){
            sex_male.setChecked(true);
        } else if("FeMale".equals(GENDER)){
            sex_female.setChecked(true);
        }
        mSexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == sex_male.getId()){
                    GENDER = "Male";
                } else if(checkedId == sex_female.getId()){
                    GENDER = "FeMale";
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefresh.setRefreshing(true);
        if (System.currentTimeMillis() > freshTime + freshSpan) {
            getFreshData();
        } else {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void getFreshData() {
        getFreshFindLove(++pageIndex, pageSize, GENDER, mUserScopeType);
    }

    private void getFreshFindLove(int pageNo, int pageSize, String gender, String mUserScopeType) {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("userId", AppManager.getClientUser().userId);
        params.put("gender", gender);
        params.put("user_scope_type", mUserScopeType);
        RetrofitFactory.getRetrofit().create(IUserApi.class)
                .getHomeLoveList(AppManager.getClientUser().sessionId, params)
                .subscribeOn(Schedulers.io())
                .map(responseBody -> JsonUtils.parseUsertList(responseBody.string()))
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(userList -> {
                    mProgress.setVisibility(View.GONE);
                    mSwipeRefresh.setRefreshing(false);
                    if(userList == null || userList.size() == 0){//没有数据了就又从第一页开始查找
                        pageIndex = 1;
                    } else {
                        freshTime = System.currentTimeMillis();
                        mClientUsers.clear();
                        mClientUsers.addAll(userList);
                        mAdapter.setIsShowFooter(false);
                        mAdapter.setClientUsers(mClientUsers);
                    }
                }, throwable -> {
                    ToastUtil.showMessage(R.string.network_requests_error);
                    mProgress.setVisibility(View.GONE);
                    mSwipeRefresh.setRefreshing(false);
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mSwipeRefresh) {
            mSwipeRefresh.setOnRefreshListener(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
    }
}
