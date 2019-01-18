package com.cyanbirds.wwjy.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.adapter.CardAdapter;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.CardModel;
import com.cyanbirds.wwjy.entity.YuanFenModel;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.IUserLoveApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：wangyb
 * 时间：2016/9/18 20:07
 * 描述：卡片
 */
public class CardActivity extends BaseActivity implements SwipeFlingAdapterView.onFlingListener,
		SwipeFlingAdapterView.OnItemClickListener {

	@BindView(R.id.left)
	ImageView mLeft;
	@BindView(R.id.info)
	ImageView mInfo;
	@BindView(R.id.right)
	ImageView mRight;
	@BindView(R.id.frame)
	SwipeFlingAdapterView mFrame;
	@BindView(R.id.loading_lay)
	RelativeLayout mLoadingLay;
	@BindView(R.id.data_lay)
	FrameLayout mDataLay;
	@BindView(R.id.radar_img)
	ImageView mRadarImg;
	@BindView(R.id.radar_bttom_img)
	ImageView mRadarBttomImg;
	@BindView(R.id.radar_top_img)
	ImageView mRadarTopImg;
	@BindView(R.id.portrait)
	SimpleDraweeView mPortrait;

	private CardModel curModel;
	private int pageNo = 1;
	private int pageSize = 200;
	private Handler mHandler = new Handler();
	private AnimationSet grayAnimal;
	private List<CardModel> dataList = new ArrayList<CardModel>();
	private CardAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card);
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
		mLoadingLay.setVisibility(View.VISIBLE);
		mDataLay.setVisibility(View.GONE);
	}

	private void setupEvent() {
		mFrame.setOnItemClickListener(this);
		mFrame.setFlingListener(this);
	}

	private void setupData() {
		dataList = new ArrayList<>();
		mAdapter = new CardAdapter(this, dataList);
		mFrame.setAdapter(mAdapter);

		if (!TextUtils.isEmpty(AppManager.getClientUser().face_local)) {
			mPortrait.setImageURI(Uri.parse("file://" + AppManager.getClientUser().face_local));
		}

		startcircularAnima();
		mHandler.postDelayed(() -> getUsers(), 3500);
	}

	@OnClick({R.id.left, R.id.info, R.id.right})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.left:
				mFrame.getTopCardListener().selectLeft();
				break;
			case R.id.info:
				if (curModel != null) {
					Intent intent = new Intent(this, PersonalInfoActivity.class);
					intent.putExtra(ValueKey.USER_ID, String.valueOf(curModel.userId));
					startActivity(intent);
				}
				break;
			case R.id.right:
				mFrame.getTopCardListener().selectRight();
				break;
		}
	}

	@Override
	public void onItemClicked(int itemPosition, Object dataObject) {
		CardModel model = (CardModel) dataObject;
		Intent intent = new Intent(this, PersonalInfoActivity.class);
		intent.putExtra(ValueKey.USER_ID, String.valueOf(model.userId));
		startActivity(intent);
	}

	@Override
	public void removeFirstObjectInAdapter() {
		if (dataList != null && dataList.size() > 0) {
			dataList.remove(0);
			mAdapter.notifyDataSetChanged();
			if (dataList.size() > 0) {
				curModel = dataList.get(0);
			}
		}
	}

	@Override
	public void onLeftCardExit(Object dataObject) {

	}

	@Override
	public void onRightCardExit(Object dataObject) {
		CardModel cardModel = (CardModel) dataObject;
		addLove(String.valueOf(cardModel.userId));
		sendGreet(String.valueOf(cardModel.userId));
	}

	@Override
	public void onAdapterAboutToEmpty(int itemsInAdapter) {
		if (itemsInAdapter == 0) {
			mLoadingLay.setVisibility(View.VISIBLE);
			mDataLay.setVisibility(View.GONE);
			mHandler.postDelayed(() -> getUsers(), 3500);
		}
	}

	@Override
	public void onScroll(float scrollProgressPercent) {

	}

	private void getUsers() {
		ArrayMap<String, Integer> map = new ArrayMap<>(2);
		map.put("pageNo", pageNo);
		map.put("pageSize", pageSize);
		RetrofitFactory.getRetrofit().create(IUserApi.class)
				.getYuanFenUser(AppManager.getClientUser().sessionId, map)
				.subscribeOn(Schedulers.io())
				.map(responseBody -> JsonUtils.parseYuanFenUsers(responseBody.string()))
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(yuanFenModels -> {
					mLoadingLay.setVisibility(View.GONE);
					mDataLay.setVisibility(View.VISIBLE);
					if (yuanFenModels != null && yuanFenModels.size() > 0) {
						dataList.clear();
						for (YuanFenModel model : yuanFenModels) {
							CardModel dataItem = new CardModel();
							dataItem.userId = model.uid;
							dataItem.userName = model.nickname;
							dataItem.imagePath = model.faceUrl;
							dataItem.city = model.city;
							dataItem.age = model.age;
							dataItem.constellation = model.constellation;
							dataItem.distance = model.distance == null ? 0.00 : model.distance;
							dataItem.signature = model.signature;
							dataItem.pictures = model.pictures;
							dataList.add(dataItem);
						}
						curModel = dataList.get(0);
						mAdapter.notifyDataSetChanged();
					}
				}, throwable -> ToastUtil.showMessage(R.string.network_requests_error));
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
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(responseBody -> {}, throwable -> {});
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

	private void startcircularAnima() {
		grayAnimal = playHeartbeatAnimation();
		mRadarBttomImg.startAnimation(grayAnimal);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startwhiteAnimal();
			}
		}, 500);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startannularAnimat();
			}
		}, 700);
	}

	private AnimationSet playHeartbeatAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(800);
		sa.setFillAfter(true);
		sa.setRepeatCount(0);
		sa.setInterpolator(new LinearInterpolator());
		animationSet.addAnimation(sa);
		return animationSet;
	}

	private void startannularAnimat() {
		mRadarImg.setVisibility(View.VISIBLE);
		AnimationSet annularAnimat = getAnimAnnular();
		annularAnimat.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mRadarImg.setVisibility(View.GONE);
			}
		});
		mRadarImg.startAnimation(annularAnimat);
	}

	private void startwhiteAnimal() {
		AnimationSet whiteAnimal = playHeartbeatAnimation();
		whiteAnimal.setRepeatCount(0);
		whiteAnimal.setDuration(600);
		mRadarTopImg.setVisibility(View.VISIBLE);
		mRadarTopImg.startAnimation(whiteAnimal);
		whiteAnimal.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mRadarImg.setVisibility(View.GONE);
				mRadarTopImg.setVisibility(View.GONE);
				startcircularAnima();
			}
		});

	}

	private AnimationSet getAnimAnnular() {
		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation sa = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		animationSet.addAnimation(new AlphaAnimation(1.0f, 0.1f));
		animationSet.setDuration(400);
		sa.setDuration(500);
		sa.setFillAfter(true);
		sa.setRepeatCount(0);
		sa.setInterpolator(new LinearInterpolator());
		animationSet.addAnimation(sa);
		return animationSet;
	}
}
