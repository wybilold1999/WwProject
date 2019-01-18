package com.cyanbirds.wwjy.activity;

import android.Manifest;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.adapter.ViewPagerAdapter;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.db.ConversationSqlManager;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.entity.FollowModel;
import com.cyanbirds.wwjy.entity.ReceiveGiftModel;
import com.cyanbirds.wwjy.fragment.FoundFragment;
import com.cyanbirds.wwjy.fragment.HomeLoveFragment;
import com.cyanbirds.wwjy.fragment.MessageFragment;
import com.cyanbirds.wwjy.fragment.PersonalFragment;
import com.cyanbirds.wwjy.helper.BottomNavigationViewHelper;
import com.cyanbirds.wwjy.helper.SDKCoreHelper;
import com.cyanbirds.wwjy.listener.MessageUnReadListener;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.manager.NotificationManagerUtils;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.IUserFollowApi;
import com.cyanbirds.wwjy.net.IUserLoveApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.service.MyIntentService;
import com.cyanbirds.wwjy.service.MyPushService;
import com.cyanbirds.wwjy.ui.widget.CustomViewPager;
import com.cyanbirds.wwjy.utils.CheckUtil;
import com.cyanbirds.wwjy.utils.DensityUtil;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.MsgUtil;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.cyanbirds.wwjy.utils.PushMsgUtil;
import com.cyanbirds.wwjy.utils.Utils;
import com.igexin.sdk.PushManager;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yuntongxun.ecsdk.ECInitParams;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends BaseActivity implements MessageUnReadListener.OnMessageUnReadListener, AMapLocationListener {

	private CustomViewPager viewPager;
	private BottomNavigationView bottomNavigationView;
	private ClientConfiguration mOSSConf;

	private final int REQUEST_LOCATION_PERMISSION = 1000;

	private AMapLocationClientOption mLocationOption;
	private AMapLocationClient mlocationClient;
	private boolean isSecondAccess = false;

	private String curLat;
	private String curLon;

	/**
	 * oss鉴权获取失败重试次数
	 */
	public int mOSSTokenRetryCount = 0;

	private Badge mBadgeView;
	private QBadgeView mQBadgeView;
	private RxPermissions rxPermissions;

	private static Handler mHandler = new Handler();

	private ViewPagerAdapter mViewPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		setupViews();
		setupEvent();
		initOSS();
		if (AppManager.getClientUser().is_vip) {
			SDKCoreHelper.init(CSApplication.getInstance(), ECInitParams.LoginMode.FORCE_LOGIN);
		}
		updateConversationUnRead();

		locationSuccess();

		AppManager.getExecutorService().execute(() -> {

			if (AppManager.getClientUser().isShowVip) {
				/**
				 * 注册小米推送
				 */
				MiPushClient.registerPush(MainActivity.this, AppConstants.MI_PUSH_APP_ID, AppConstants.MI_PUSH_APP_KEY);

				//个推
				initGeTuiPush();
			}
		});

		if (AppManager.getClientUser().isShowVip) {
			mHandler.postDelayed(() -> requestLoveForme(1, 1), 9000 * 10);
			mHandler.postDelayed(() -> requestMyGiftList(1, 1), 5000 * 10);
			mHandler.postDelayed(() -> requestFollowList("followFormeList", 1, 1), 1500 * 10);
		} else {
			SDKCoreHelper.init(CSApplication.getInstance(), ECInitParams.LoginMode.FORCE_LOGIN);
		}

		loadData();
	}

	/**
	 * 判断是否定位成功。成功就不定位了，直接上传城市
	 */
	private void locationSuccess() {
		String currentCity = AppManager.getClientUser().currentCity;
		curLat = AppManager.getClientUser().latitude;
		curLon = AppManager.getClientUser().longitude;
		boolean isLocSuc = PreferencesUtils.getIsLocationSuccess(this);
		if (isLocSuc && !TextUtils.isEmpty(currentCity) && !TextUtils.isEmpty(curLat) && !TextUtils.isEmpty(curLon)) {
			uploadCityInfoRequest(currentCity, curLat, curLon);
		} else {
			initLocationClient();
			requestLocationPermission();
		}
	}


	/**
	 * 初始化定位
	 */
	private void initLocationClient() {
		mlocationClient = new AMapLocationClient(this);
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位监听
		mlocationClient.setLocationListener(this);
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//获取最近3s内精度最高的一次定位结果：
		mLocationOption.setOnceLocationLatest(true);
	}

	/**
	 * 开始定位
	 */
	private void startLocation() {
		//设置定位参数
		mlocationClient.setLocationOption(mLocationOption);
		//启动定位
		mlocationClient.startLocation();
	}

	/**
	 * 停止定位
	 */
	private void stopLocation(){
		// 停止定位
		mlocationClient.stopLocation();
	}

	/**
	 * 销毁定位
	 */
	private void destroyLocation(){
		if (null != mlocationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			mlocationClient.onDestroy();
			mlocationClient = null;
			mLocationOption = null;
		}
	}

	/**
	 * 点击通知栏的消息，将消息入库
	 */
	private void loadData() {
		String msg = getIntent().getStringExtra(ValueKey.DATA);
		if (!TextUtils.isEmpty(msg)) {
			PushMsgUtil.getInstance().handlePushMsg(false, msg);
			NotificationManagerUtils.getInstance().cancelNotification();
			AppManager.isMsgClick = true;
		}
	}

	/**
	 * 初始化oss
	 */
	private void initOSS() {
		mOSSConf = new ClientConfiguration();
		mOSSConf.setConnectionTimeout(30 * 1000); // 连接超时，默认15秒
		mOSSConf.setSocketTimeout(30 * 1000); // socket超时，默认15秒
		mOSSConf.setMaxConcurrentRequest(50); // 最大并发请求书，默认5个
		mOSSConf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
		OSSLog.enableLog();

		final Handler handler = new Handler();
		// 每30分钟请求一次鉴权
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				getFederationToken();
				handler.postDelayed(this, 60 * 30 * 1000);
			}
		};

		handler.postDelayed(runnable, 0);
	}

	private void getFederationToken() {
		RetrofitFactory.getRetrofit().create(IUserApi.class)
				.getOSSToken()
				.subscribeOn(Schedulers.io())
				.map(responseBody -> JsonUtils.parseOSSToken(responseBody.string()))
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(result -> {
					if (result != null) {
						AppManager.setFederationToken(result);
						OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(result.accessKeyId, result.accessKeySecret, result.securityToken);
						OSS oss = new OSSClient(getApplicationContext(), result.endpoint, credentialProvider, mOSSConf);
						AppManager.setOSS(oss);
						mOSSTokenRetryCount = 0;
					} else {
						if (mOSSTokenRetryCount < 5) {
							getFederationToken();
							mOSSTokenRetryCount++;
						}
					}
				}, throwable -> {});

	}

	/**
	 * 个推注册
	 */
	private void initGeTuiPush() {
		// SDK初始化，第三方程序启动时，都要进行SDK初始化工作
		PushManager.getInstance().initialize(this.getApplicationContext(), MyPushService.class);
		PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), MyIntentService.class);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);// 必须要调用这句(信鸽推送)
	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if (aMapLocation != null && !TextUtils.isEmpty(aMapLocation.getCity())) {
			ClientUser clientUser = AppManager.getClientUser();
			clientUser.latitude = String.valueOf(aMapLocation.getLatitude());
			clientUser.longitude = String.valueOf(aMapLocation.getLongitude());
			AppManager.setClientUser(clientUser);
			curLat = clientUser.latitude;
			curLon = clientUser.longitude;

			uploadCityInfoRequest(aMapLocation.getCity(), String.valueOf(aMapLocation.getLatitude()),
					String.valueOf(aMapLocation.getLongitude()));

			PreferencesUtils.setCurrentCity(this, aMapLocation.getCity());
			PreferencesUtils.setCurrentProvince(this, aMapLocation.getProvince());
			PreferencesUtils.setLatitude(this, curLat);
			PreferencesUtils.setLongitude(this, curLon);
			PreferencesUtils.setIsLocationSuccess(this, true);

			if (!TextUtils.isEmpty(aMapLocation.getCity())) {
				stopLocation();
			}
		}

	}

	private void uploadCityInfoRequest(String city, String lat, String lon) {
		ArrayMap<String, String> params = new ArrayMap<>();
		params.put("channel", CheckUtil.getAppMetaData(CSApplication.getInstance(), "UMENG_CHANNEL"));
		params.put("currentCity", city);
		params.put("latitude", lat);
		params.put("longitude", lon);
		RetrofitFactory.getRetrofit().create(IUserApi.class)
				.uploadCityInfo(params, AppManager.getClientUser().sessionId)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(responseBody -> {} , throwable -> {});
	}

	/**
	 * 获取最近喜欢我的那个人
	 */
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
					if(loveModels != null && loveModels.size() > 0) {
						String lastUserId = PreferencesUtils.getLoveMeUserId(MainActivity.this);
						if (!lastUserId.equals(String.valueOf(loveModels.get(0).userId))) {

							PreferencesUtils.setLoveMeUserId(
									MainActivity.this, String.valueOf(loveModels.get(0).userId));
							Intent intent = new Intent(MainActivity.this, PopupLoveActivity.class);
							intent.putExtra(ValueKey.DATA, loveModels.get(0));
							startActivity(intent);
						}
					}
				}, throwable -> {});
	}

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
					if(null != receiveGiftModels && receiveGiftModels.size() > 0){
						ReceiveGiftModel model = receiveGiftModels.get(0);
						String lastUserId = PreferencesUtils.getGiftMeUserId(MainActivity.this);
						if (!lastUserId.equals(String.valueOf(model.userId))) {
							PreferencesUtils.setGiftMeUserId(
									MainActivity.this, String.valueOf(model.userId));
							MsgUtil.sendAttentionOrGiftMsg(String.valueOf(model.userId), model.nickname, model.faceUrl,
									model.nickname + "给您送了一件礼物");
						}
					}
				}, throwable -> {});
	}

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
					if(followModels != null && followModels.size() > 0){
						FollowModel followModel = followModels.get(0);
						String lastUserId = PreferencesUtils.getAttentionMeUserId(MainActivity.this);
						if (!lastUserId.equals(String.valueOf(followModel.userId))) {
							PreferencesUtils.setAttentionMeUserId(
									MainActivity.this, String.valueOf(followModel.userId));
							MsgUtil.sendAttentionOrGiftMsg(String.valueOf(followModel.userId),
									followModel.nickname, followModel.faceUrl,
									followModel.nickname + "关注了您");
						}
					}
				}, throwable -> {});
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		viewPager = findViewById(R.id.viewpager);
		viewPager.setNoScroll(true);
		bottomNavigationView = findViewById(R.id.bottom_navigation);
		//默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
		BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
		bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
			switch (item.getItemId()) {
				case R.id.item_news:
					viewPager.setCurrentItem(0);
					break;
				case R.id.item_lib:
					viewPager.setCurrentItem(1);
					break;
				case R.id.item_find:
					viewPager.setCurrentItem(2);
					break;
				case R.id.item_more:
					viewPager.setCurrentItem(3);
					break;
			}
			return false;
		});

		setupViewPager(viewPager);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if(bottomNavigationView.getMenu().getItem(position).isChecked()){
					bottomNavigationView.getMenu().getItem(position).setChecked(false);
				}
				String title = getResources().getString(R.string.tab_find_love);
				if (position == 1) {
					title = getResources().getString(R.string.tab_found);
				} else if (position == 2) {
					title = getResources().getString(R.string.tab_message);
				} else if (position == 3) {
					title = getResources().getString(R.string.tab_personal);
				}
				if (mViewPagerAdapter != null && mViewPagerAdapter.getItem(position) !=null &&
						mViewPagerAdapter.getItem(position).getActivity() != null) {
					mViewPagerAdapter.getItem(position).getActivity().setTitle(title);
				}
			}

			@Override
			public void onPageSelected(int position) {
				bottomNavigationView.getMenu().getItem(position).setChecked(true);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
		if (menuView != null) {
			mQBadgeView = new QBadgeView(this);
			mBadgeView = mQBadgeView.setGravityOffset((float) (DensityUtil.getWidthInPx(this) / 3.2), 2, false)
					.bindTarget(menuView);
		}
	}

	private void setupViewPager(ViewPager viewPager) {
		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		mViewPagerAdapter.addFragment(new HomeLoveFragment());
		mViewPagerAdapter.addFragment(new FoundFragment());
		mViewPagerAdapter.addFragment(new MessageFragment());
		mViewPagerAdapter.addFragment(new PersonalFragment());
		viewPager.setAdapter(mViewPagerAdapter);
	}


	private void setupEvent(){
		MessageUnReadListener.getInstance().setMessageUnReadListener(this);
	}


	@Override
	public void notifyUnReadChanged(int type) {
		updateConversationUnRead();
	}

	/**
	 * 更新会话未读消息总数
	 */
	private void updateConversationUnRead() {
		if (mBadgeView != null) {
			int total = ConversationSqlManager.getInstance(this)
					.getAnalyticsUnReadConversation();
			if (total > 0) {
				mQBadgeView.setVisibility(View.VISIBLE);
				if (total >= 100) {
					mBadgeView.setBadgeText("99+");
				} else {
					mBadgeView.setBadgeText(String.valueOf(total));
				}
			} else {
				mQBadgeView.setVisibility(View.GONE);
			}
		}
	}

	private void requestLocationPermission() {
		if (!CheckUtil.isGetPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
				!CheckUtil.isGetPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
			if (rxPermissions == null) {
				rxPermissions = new RxPermissions(this);
			}
			rxPermissions.requestEachCombined(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
					.subscribe(permission -> {// will emit 1 Permission object
						if (permission.granted) {
							// All permissions are granted !
							startLocation();
						} else if (permission.shouldShowRequestPermissionRationale) {
							// At least one denied permission without ask never again
							if (!isSecondAccess) {
								showAccessLocationDialog();
							}
						} else {
							// At least one denied permission with ask never again
							// Need to go to the settings
							if (!isSecondAccess) {
								showAccessLocationDialog();
							}
						}
					}, throwable -> {

					});
		} else {
			startLocation();
		}
	}

	private void showAccessLocationDialog() {
		isSecondAccess = true;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.permission_request);
		builder.setMessage(R.string.access_location);
		builder.setPositiveButton(R.string.ok, (dialog, i) -> {
			dialog.dismiss();
			Utils.goToSetting(MainActivity.this, REQUEST_LOCATION_PERMISSION);
		});
		builder.show();
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
	protected void onDestroy() {
		super.onDestroy();
		destroyLocation();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			showQuitDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showQuitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.quit_app_name);
		builder.setMessage(R.string.quit_are_you_sure);
		builder.setNegativeButton(R.string.cancel, (dialog, i) -> {
			dialog.dismiss();
		});
		builder.setPositiveButton(R.string.ok, (dialog, i) -> {
			dialog.dismiss();
			exitApp();
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_LOCATION_PERMISSION) {
			isSecondAccess = false;
			requestLocationPermission();
		}
	}

}
