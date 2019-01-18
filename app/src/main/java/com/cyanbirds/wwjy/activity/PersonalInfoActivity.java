package com.cyanbirds.wwjy.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.adapter.TabFragmentAdapter;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.eventtype.UserEvent;
import com.cyanbirds.wwjy.fragment.TabDynamicFragment;
import com.cyanbirds.wwjy.fragment.TabPersonalFragment;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.IUserFollowApi;
import com.cyanbirds.wwjy.net.IUserLoveApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.ProgressDialogUtils;
import com.cyanbirds.wwjy.utils.RxBus;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: wangyb
 * @datetime: 2016-01-02 16:26 GMT+8
 * @email: 395044952@qq.com
 * @description: 个人信息界面
 */
public class PersonalInfoActivity extends BaseActivity {

	@BindView(R.id.portrait)
	SimpleDraweeView mPortrait;
	@BindView(R.id.toolbar)
	Toolbar mToolbar;
	@BindView(R.id.collapsingToolbarLayout)
	CollapsingToolbarLayout mCollapsingToolbarLayout;
	@BindView(R.id.tabs)
	TabLayout mTabLayout;
	@BindView(R.id.viewpager)
	ViewPager mViewpager;
	@BindView(R.id.fab)
	FloatingActionButton mFab;
	@BindView(R.id.attention)
	TextView mAttention;
	@BindView(R.id.love)
	TextView mLove;
	@BindView(R.id.message)
	TextView mMessage;
	@BindView(R.id.bottom_layout)
	LinearLayout mBottomLayout;
	@BindView(R.id.gift)
	TextView mGift;

	private List<String> tabList;
	private List<Fragment> fragmentList;
	private Fragment personalFragment;//个人信息tab
	private Fragment dynamicFragment; //动态tab

	private ClientUser mClientUser; //当前用户
	private String curUserId; //当前用户id

	private Observable<UserEvent> observable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		ButterKnife.bind(this);
		setupView();
		setupData();
	}

	private void setupView() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mCollapsingToolbarLayout.setTitle(" ");

		tabList = new ArrayList<>();
		tabList.add("简介");
		tabList.add("动态");
		mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(0)));//添加tab选项卡
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(1)));

		fragmentList = new ArrayList<>();

		personalFragment = new TabPersonalFragment();
		dynamicFragment = new TabDynamicFragment();
		fragmentList.add(personalFragment);
		fragmentList.add(dynamicFragment);

		if (AppManager.getClientUser().isShowAppointment) {
			mLove.setText(R.string.tv_appointment);
		} else {
			mLove.setText(R.string.like);
		}

		rxBusSub();

	}


	/**
	 * rx订阅
	 */
	private void rxBusSub() {
		observable = RxBus.getInstance().register(AppConstants.UPDATE_USER_INFO);
		observable.subscribe(this::updateUserInfo);
	}

	private void setupData() {
		curUserId = getIntent().getStringExtra(ValueKey.USER_ID);
		if (!TextUtils.isEmpty(curUserId)) {
			if (AppManager.getClientUser().userId.equals(curUserId)) {
				mFab.setVisibility(View.VISIBLE);
				mBottomLayout.setVisibility(View.GONE);
				mClientUser = AppManager.getClientUser();
				setUserInfoAndValue(mClientUser);
			} else {
				mFab.setVisibility(View.GONE);
				mBottomLayout.setVisibility(View.VISIBLE);
				ProgressDialogUtils.getInstance(PersonalInfoActivity.this).show(R.string.dialog_request_data);
				getUserInfo(curUserId);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (AppManager.getClientUser().userId.equals(curUserId)) {
			getMenuInflater().inflate(R.menu.personal_menu, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.modify_info) {
			Intent intent = new Intent(this, ModifyUserInfoActivity.class);
			startActivity(intent);
		} else {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@OnClick({R.id.gift, R.id.portrait, R.id.fab, R.id.attention, R.id.love, R.id.message})
	public void onClick(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
			case R.id.portrait:
				intent.setClass(this, PhotoViewActivity.class);
				if (null != mClientUser) {
					if (!TextUtils.isEmpty(mClientUser.face_local) && new File(mClientUser.face_local).exists()) {
						intent.putExtra(ValueKey.IMAGE_URL, "file://" + mClientUser.face_local);
					} else {
						intent.putExtra(ValueKey.IMAGE_URL, mClientUser.face_url);
					}
				}
				intent.putExtra(ValueKey.FROM_ACTIVITY, "PersonalInfoActivity");
				startActivity(intent);
				break;
			case R.id.fab:
				intent.setClass(this, PublishDynamicActivity.class);
				startActivity(intent);
				break;
			case R.id.attention:
				if (null != mClientUser) {
					if (mAttention.getText().toString().equals("关注")) {
						addCancelFollow(mClientUser.userId, true);
					} else {
						addCancelFollow(mClientUser.userId, false);
					}
				}
				break;
			case R.id.gift:
				intent.setClass(this, GiftMarketActivity.class);
				intent.putExtra(ValueKey.USER, mClientUser);
				startActivity(intent);
				break;
			case R.id.love:
				if (AppManager.getClientUser().isShowAppointment && !TextUtils.isEmpty(curUserId)) {
					if (mClientUser != null) {
						/*intent.setClass(this, AppointmentActivity.class);
						intent.putExtra(ValueKey.USER_ID, curUserId);
						intent.putExtra(ValueKey.USER_NAME, mClientUser.user_name);
						intent.putExtra(ValueKey.IMAGE_URL, mClientUser.face_url);
						startActivity(intent);*/
					}
				} else {
					if (null != mClientUser) {
						sendGreet(mClientUser.userId);
						addLove(mClientUser.userId);
					}
				}
				break;
			case R.id.message:
				if (null != mClientUser) {
					intent.setClass(this, ChatActivity.class);
					intent.putExtra(ValueKey.USER, mClientUser);
					startActivity(intent);
				}
				break;
		}
	}

	/**
	 * 关注，取消关注
	 * @param userId
	 * @param isAdd true:关注 false:取消关注
	 */
	private void addCancelFollow(String userId, boolean isAdd) {
		RetrofitFactory.getRetrofit().create(IUserFollowApi.class)
				.addFollow(AppManager.getClientUser().sessionId, userId)
				.subscribeOn(Schedulers.io())
				.map(responseBody -> {
					JsonObject obj = new JsonParser().parse(responseBody.string()).getAsJsonObject();
					int code = obj.get("code").getAsInt();
					if (code == 0) {//关注成功
						return obj.get("data").getAsString();
					} else {
						return CSApplication.getInstance().getResources()
								.getString(R.string.attention_faiure);
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(s -> {
					if (isAdd) {
						if (s.equals("已关注")) {
							mAttention.setText("已关注");
							mAttention.setTextColor(getResources().getColor(R.color.colorPrimary));
							ToastUtil.showMessage(R.string.attention_success);
						} else {
							ToastUtil.showMessage(R.string.attention_faiure);
						}
					} else {
						mAttention.setText("关注");
						ToastUtil.showMessage(R.string.cancle_attention);
					}
				}, throwable -> {
					if (isAdd) {
						ToastUtil.showMessage(R.string.attention_faiure);
					} else {
						ToastUtil.showMessage(R.string.cancel_follow_faiure);
					}
				});
	}

	private void sendGreet(String userId) {
		RetrofitFactory.getRetrofit().create(IUserLoveApi.class)
				.sendGreet(AppManager.getClientUser().sessionId, userId)
				.subscribeOn(Schedulers.io())
				.map(responseBody -> {
					JsonObject obj = new JsonParser().parse(responseBody.string()).getAsJsonObject();
					int code = obj.get("code").getAsInt();
					if (code == 0) {//喜欢成功
						return CSApplication.getInstance().getResources()
								.getString(R.string.like_success);
					} else {
						return CSApplication.getInstance().getResources()
								.getString(R.string.like_faiure);
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(s -> ToastUtil.showMessage(s), throwable -> ToastUtil.showMessage(R.string.like_faiure));
	}

	private void addLove(String userId) {
		RetrofitFactory.getRetrofit().create(IUserLoveApi.class)
				.addLove(AppManager.getClientUser().sessionId, userId)
				.subscribeOn(Schedulers.io())
				.map(responseBody -> {
					JsonObject obj = new JsonParser().parse(responseBody.string()).getAsJsonObject();
					int code = obj.get("code").getAsInt();
					if (code == 0) {//喜欢成功
						return obj.get("data").getAsBoolean();
					} else {
						return false;
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(aBoolean -> {
					if (aBoolean) {
						mLove.setText("已喜欢");
					} else {
						mLove.setText("喜欢");
					}
				}, throwable -> {});
	}

	/**
	 * 获取用户信息
	 */
	private void getUserInfo(String userId) {
		RetrofitFactory.getRetrofit().create(IUserApi.class)
				.getUserInfo(AppManager.getClientUser().sessionId, userId)
				.subscribeOn(Schedulers.io())
				.map(responseBody -> JsonUtils.parserUserInfo(responseBody.string()))
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(clientUser -> {
					ProgressDialogUtils.getInstance(PersonalInfoActivity.this).dismiss();
					mClientUser = clientUser;
					if (null != mClientUser) {
						setUserInfoAndValue(clientUser);
					}
				}, throwable -> {
					ToastUtil.showMessage(R.string.network_requests_error);
					ProgressDialogUtils.getInstance(PersonalInfoActivity.this).dismiss();
				});
	}

	/**
	 * 将TabLayout和ViewPager关联起来。
	 *
	 * @param clientUser
	 */
	private void setUserInfoAndValue(ClientUser clientUser) {
		Bundle personBundle = new Bundle();
		personBundle.putSerializable(ValueKey.ACCOUNT, clientUser);
		personBundle.putString(ValueKey.LATITUDE, clientUser.latitude);
		personBundle.putString(ValueKey.LONGITUDE, clientUser.longitude);
		personalFragment.setArguments(personBundle);

		Bundle dynamicBundle = new Bundle();
		dynamicBundle.putString(ValueKey.USER_ID, clientUser.userId);
		dynamicFragment.setArguments(dynamicBundle);

		//如果是本人信息，先查找本地有没有头像，有就加载，没有就用face_url;如果是其他用户信息，直接使用face_url
		String imagePath = "";
		if (clientUser.userId.equals(AppManager.getClientUser().userId)) {
			if (!TextUtils.isEmpty(clientUser.face_url)) {
				imagePath = clientUser.face_url;
			} else if (!TextUtils.isEmpty(clientUser.face_local) && new File(clientUser.face_local).exists()) {
				imagePath = "file://" + clientUser.face_local;
			} else {
				imagePath = "res:///" + R.mipmap.default_head;
			}
		} else {
			imagePath = clientUser.face_url;
		}
		if (!TextUtils.isEmpty(imagePath)) {
			mPortrait.setImageURI(Uri.parse(imagePath));
		}
		mCollapsingToolbarLayout.setTitle(clientUser.user_name);

		if (mClientUser.isFollow) {
			mAttention.setText("已关注");
		} else {
			mAttention.setText("关注");
		}

		TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(
				getSupportFragmentManager(), fragmentList, tabList);
		mViewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
		mTabLayout.setupWithViewPager(mViewpager);//将TabLayout和ViewPager关联起来。
		mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				mViewpager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	public void updateUserInfo(UserEvent event) {
		//如果是本人信息，先查找本地有没有头像，有就加载，没有就用face_url;如果是其他用户信息，直接使用face_url
		ClientUser clientUser = AppManager.getClientUser();
		String imagePath = "";
		if (!TextUtils.isEmpty(clientUser.face_url)) {
			imagePath = clientUser.face_url;
		} else if (!TextUtils.isEmpty(clientUser.face_local) && new File(clientUser.face_local).exists()) {
			imagePath = "file://" + clientUser.face_local;
		} else {
			imagePath = "res:///" + R.mipmap.default_head;
		}
		mPortrait.setImageURI(Uri.parse(imagePath));
		mCollapsingToolbarLayout.setTitle(AppManager.getClientUser().user_name);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		RxBus.getInstance().unregister(AppConstants.UPDATE_USER_INFO, observable);
	}
}
