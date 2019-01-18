package com.cyanbirds.wwjy.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @Description:位置信息详情
 * @author wangyb
 * @Date:2015年7月13日下午3:01:46
 */
public class LocationDetailActivity extends BaseActivity implements
		AMapLocationListener, LocationSource,
		OnInfoWindowClickListener, InfoWindowAdapter {

	private MapView mMapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private AMapLocationClientOption mLocationOption;
	private AMapLocationClient mlocationClient;
	private OnLocationChangedListener mListener;

	private LatLng mMessageLatLng;
	private String mAddress;
	private String from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_detail);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupViews();
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		setupData();
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mMapView = (MapView) findViewById(R.id.map);
	}

	/**
	 * 设置数据
	 */
	private void setupData() {
	}

	/**
	 * 初始化地图
	 */
	private void init() {
		if (aMap == null) {
			aMap = mMapView.getMap();
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setZoomControlsEnabled(false);// 不显示缩放按钮
			aMap.moveCamera(CameraUpdateFactory.zoomTo(16));// 设置缩放比例
		}
		// 定位
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

		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.mipmap.location_marker));// 设置小蓝点的图标
		aMap.setMyLocationStyle(myLocationStyle);

		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器

		double latitude = getIntent().getDoubleExtra("latitude", 0);
		double longitude = getIntent().getDoubleExtra("longitude", 0);
		mAddress = getIntent().getStringExtra(ValueKey.ADDRESS);
		mMessageLatLng = new LatLng(latitude, longitude);
		aMap.animateCamera(CameraUpdateFactory.changeLatLng(mMessageLatLng));
		Marker marker = aMap
				.addMarker(new MarkerOptions()
						.position(mMessageLatLng)
						.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
								.decodeResource(getResources(),
										R.mipmap.icon_marked)))
						.draggable(true));
		from = getIntent().getStringExtra(ValueKey.FROM_ACTIVITY);
		if (TextUtils.isEmpty(from)) {
			marker.setTitle("");

			marker.showInfoWindow();// 设置默认显示一个infowinfow
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}


	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}


	@Override
	public void onLocationChanged(AMapLocation location) {
		if (mListener != null && location != null) {
			mListener.onLocationChanged(location);// 显示系统小蓝点
			aMap.animateCamera(CameraUpdateFactory.changeLatLng(mMessageLatLng));
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		//启动定位
		mlocationClient.startLocation();

	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent intent = new Intent(
				"android.intent.action.VIEW",
				android.net.Uri
						.parse("androidamap://showTraffic?sourceApplication=softname&poiid=BGVIS1&lat=36.2&lon=116.1&level=10&dev=0"));
		intent.setPackage("com.autonavi.minimap");
		startActivity(intent);
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = getLayoutInflater().inflate(
				R.layout.popup_location_info_overlay, null);
		render(marker, infoWindow);
		return infoWindow;
	}

	public void render(Marker marker, View view) {
		String[] s = mAddress.split("#");
		if (s != null && s.length > 0) {
			/*TextView title = (TextView) view.findViewById(R.id.title);
			title.setText(s[0]);*/
			TextView content = (TextView) view.findViewById(R.id.content);
			content.setText(s.length == 2 ? s[1] : s[0]);
		}
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
