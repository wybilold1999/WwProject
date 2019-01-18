package com.cyanbirds.wwjy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.adapter.PlaceListAdapter;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.ui.widget.CircularProgress;
import com.cyanbirds.wwjy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.wwjy.utils.FileAccessorUtils;
import com.cyanbirds.wwjy.utils.Md5Util;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @ClassName:ShareLocationActivity
 * @Description:分享位置信息
 * @author wangyb
 * @Date:2015年6月7日上午10:24:01
 *
 */
public class ShareLocationActivity extends BaseActivity implements
		AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener,
		AMap.OnMapTouchListener, OnClickListener, AMap.OnMapScreenShotListener {

	private MapView mapView;
	private UiSettings mUiSettings;
	private AMapLocationClientOption mLocationOption;
	private AMapLocationClient mlocationClient;
	private AMap aMap;
	private GeocodeSearch geocoderSearch;
	private CircularProgress mProgressBar;

	private RecyclerView mRecyclerView;
	private PlaceListAdapter mAdapter;
	private List<PoiItem> mPoiLists;
	private ImageButton mCurrentLocation;

	private LatLonPoint mLatLonPoint;
	private LatLng mSelLoactionLatLng;// 选择的经纬度
	private int mSelId = 0;
	private String mAddress;// 选中的地址

	private String from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_location);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.share_location);
		setupViews();
		setupData();
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		initMap();
		setupEvent();
	}

	private void setupEvent() {
		aMap.setOnMapTouchListener(this);
		mCurrentLocation.setOnClickListener(this);
	}

	private void setupData() {
		from = getIntent().getStringExtra(ValueKey.FROM_ACTIVITY);
		mPoiLists = new ArrayList<PoiItem>();
		mAdapter = new PlaceListAdapter(mPoiLists, mSelId, mRecyclerView) {
			@Override
			public void onClick(int position) {
				if (position < 0) {
					return;
				}
				mAdapter.notifyItemChanged(mSelId, position);
				mSelId = position;
				PoiItem poiItem = mPoiLists.get(position);
				mAddress = poiItem.getTitle()  + poiItem.getSnippet();
				LatLonPoint latLonPoint = poiItem.getLatLonPoint();
				if (latLonPoint != null) {
					LatLng latLng = new LatLng(latLonPoint.getLatitude(),
							latLonPoint.getLongitude());
					mSelLoactionLatLng = latLng;
					aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
				}
			}
		};
		mRecyclerView.setAdapter(mAdapter);
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mapView = (MapView) findViewById(R.id.map);
		mCurrentLocation = (ImageButton) findViewById(R.id.current_location);
		mRecyclerView = (RecyclerView) findViewById(R.id.place_list);
		mProgressBar = (CircularProgress) findViewById(R.id.progress_bar);
		mRecyclerView.setLayoutManager(new WrapperLinearLayoutManager(this));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
	}

	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		aMap = mapView.getMap();
		mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
		mUiSettings.setZoomControlsEnabled(false);
		mUiSettings.setAllGesturesEnabled(false);
		aMap.animateCamera(CameraUpdateFactory.zoomTo(16));

		mlocationClient = new AMapLocationClient(this);
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位监听
		mlocationClient.setLocationListener(this);
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//获取最近3s内精度最高的一次定位结果：
		mLocationOption.setOnceLocationLatest(true);
		//设置定位参数
		mlocationClient.setLocationOption(mLocationOption);
		//启动定位
		mlocationClient.startLocation();

		// 地理编码
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
		MobclickAgent.onResume(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}


	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}


	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(),
					location.getLongitude());
			mLatLonPoint = latLonPoint;
			LatLng latLng = new LatLng(location.getLatitude(),
					location.getLongitude());
			mSelLoactionLatLng = latLng;
			aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
			RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 1000,
					GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
			geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
		}
	}


	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 1000) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				List<PoiItem> poiList = result.getRegeocodeAddress().getPois();
				mPoiLists.clear();
				PoiItem poiItem = new PoiItem("", mLatLonPoint, getResources()
						.getString(R.string.location_symbol), result
						.getRegeocodeAddress().getFormatAddress());
				mPoiLists.add(poiItem);
				if (!TextUtils.isEmpty(from)) {
					mAddress = poiItem.getSnippet();
				} else {
					mAddress = poiItem.getTitle()  + poiItem.getSnippet();
				}
				for (PoiItem item : poiList) {
					if (!TextUtils.isEmpty(item.getSnippet())) {
						mPoiLists.add(item);
					}
				}
				mProgressBar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged(mSelId);
				mRecyclerView.scrollToPosition(0);
			} else {
				ToastUtil.showMessage(R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.showMessage(R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.showMessage(R.string.error_key);
		} else {
			ToastUtil.showMessage(getString(R.string.error_other) + rCode);
		}
	}

	/** -------------------地图触摸事件监听---------------------- */
	@Override
	public void onTouch(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {

			// 获取当前中心屏幕坐标对应的地理坐标
			LatLng currentLatLng = aMap.getCameraPosition().target;
			mSelLoactionLatLng = currentLatLng;
			RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(
					currentLatLng.latitude, currentLatLng.longitude), 1000,
					GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
			geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.current_location:
			if (null != mLatLonPoint) {
				LatLng latLng = new LatLng(mLatLonPoint.getLatitude(),
						mLatLonPoint.getLongitude());
				aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (AppManager.getClientUser().isShowVip && !TextUtils.isEmpty(from)) {
			getMenuInflater().inflate(R.menu.define_menu, menu);
		} else {
			getMenuInflater().inflate(R.menu.send_menu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.send) {
			if (AppManager.getClientUser().isShowVip) {
				if (AppManager.getClientUser().is_vip) {
					sendLocation();
				} else {
					showTurnOnVipDialog();
				}
			} else {
				sendLocation();
			}
		} else if (id == R.id.ok) {
			sendLocation();
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean sendLocation() {
		if (mSelLoactionLatLng == null || mSelLoactionLatLng.latitude == 0
				|| mSelLoactionLatLng.longitude == 0
				|| TextUtils.isEmpty(mAddress)) {
			if (!TextUtils.isEmpty(from)) {
				ToastUtil.showMessage(R.string.select_appointment_location_tips);
			} else {
				ToastUtil.showMessage(R.string.select_send_location_tips);
			}
			return true;
		}
		getMapScreenShot();
		return true;
	}

	private void showTurnOnVipDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.un_send_msg);
		builder.setPositiveButton(R.string.ok, ((dialog, i) -> {
			dialog.dismiss();
			Intent intent = new Intent(ShareLocationActivity.this, VipCenterActivity.class);
			startActivity(intent);
		}));
		if (AppManager.getClientUser().isShowGiveVip) {
			builder.setNegativeButton(R.string.free_give_vip, ((dialog, i) -> {
				dialog.dismiss();
				Intent intent = new Intent(ShareLocationActivity.this, GiveVipActivity.class);
				startActivity(intent);
			}));
		} else {
			builder.setNegativeButton(R.string.until_single, ((dialog, i) -> dialog.dismiss()));
		}
		builder.show();
	}

	/**
	 * 对地图进行截屏
	 */
	private void getMapScreenShot() {
		aMap.getMapScreenShot(this);
		aMap.invalidate();// 刷新地图
	}

	@Override
	public void onMapScreenShot(Bitmap bitmap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		if (null == bitmap) {
			return;
		}
		try {
			String path = FileAccessorUtils.getImagePathName() + "/"
					+ Md5Util.md5(sdf.format(new Date())) + ".jpg";
			FileOutputStream fos = new FileOutputStream(path);
			boolean b = bitmap.compress(CompressFormat.JPEG, 100, fos);
			try {
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (b) {
				Intent intent = new Intent();
				
				intent.putExtra(ValueKey.LATITUDE, mSelLoactionLatLng.latitude);
				intent.putExtra(ValueKey.LONGITUDE, mSelLoactionLatLng.longitude);
				
				intent.putExtra(ValueKey.ADDRESS, mAddress);
				intent.putExtra(ValueKey.IMAGE_URL, path);
				setResult(RESULT_OK, intent);
				finish();
			} else {
				ToastUtil.showMessage(R.string.send_failure);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	/**
	 * 
	 * 火星坐标系 (国测局标准)(GCJ-02) 转换为百度坐标系 (BD-09) 的转换算法
	 * 
	 * @param gg_lon
	 * 
	 * @param gg_lat
	 * 
	 * @return
	 */

	public static double[] bd_encrypt(double gg_lon, double gg_lat) {
		double[] d = new double[2];
		double x = gg_lon, y = gg_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bd_lon = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		d[0] = bd_lon;
		d[1] = bd_lat;
		return d;
	}




}
