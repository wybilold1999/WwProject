package com.cyanbirds.wwjy.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
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
import com.cyanbirds.wwjy.activity.GiveVipActivity;
import com.cyanbirds.wwjy.activity.VipCenterActivity;
import com.cyanbirds.wwjy.adapter.TabPersonalPhotosAdapter;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.eventtype.UserEvent;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.wwjy.utils.RxBus;
import com.cyanbirds.wwjy.utils.StringUtil;
import com.dl7.tag.TagLayout;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * @author: wangyb
 * @datetime: 2016-01-02 16:32 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class TabPersonalFragment extends Fragment implements GeocodeSearch.OnGeocodeSearchListener,
		AMap.OnMapScreenShotListener{
	@BindView(R.id.occupation)
	TextView mOccupation;
	@BindView(R.id.colleage)
	TextView mColleage;
	@BindView(R.id.constellation)
	TextView mConstellation;
	@BindView(R.id.tall)
	TextView mTall;
	@BindView(R.id.weight)
	TextView mWeight;
	@BindView(R.id.married)
	TextView mMarried;
	@BindView(R.id.signature)
	TextView mSignature;
	@BindView(R.id.plable_flowlayout)
	TagLayout mPlableFlowlayout;
	@BindView(R.id.part_flowlayout)
	TagLayout mPartFlowlayout;
	@BindView(R.id.intrest_flowlayout)
	TagLayout mIntrestFlowlayout;
	@BindView(R.id.purpose)
	TextView mPurpose;
	@BindView(R.id.loveWhere)
	TextView mLoveWhere;
	@BindView(R.id.do_what_first)
	TextView mDoWhatFirst;
	@BindView(R.id.conception)
	TextView mConception;
	@BindView(R.id.occupation_lay)
	RelativeLayout mOccupationLay;
	@BindView(R.id.colleage_text)
	TextView mColleageText;
	@BindView(R.id.colleage_lay)
	RelativeLayout mColleageLay;
	@BindView(R.id.constellation_text)
	TextView mConstellationText;
	@BindView(R.id.constellation_lay)
	RelativeLayout mConstellationLay;
	@BindView(R.id.tall_text)
	TextView mTallText;
	@BindView(R.id.tall_lay)
	RelativeLayout mTallLay;
	@BindView(R.id.weight_text)
	TextView mWeightText;
	@BindView(R.id.weight_lay)
	RelativeLayout mWeightLay;
	@BindView(R.id.married_text)
	TextView mMarriedText;
	@BindView(R.id.married_lay)
	RelativeLayout mMarriedLay;
	@BindView(R.id.signature_text)
	TextView mSignatureText;
	@BindView(R.id.signature_lay)
	RelativeLayout mSignatureLay;
	@BindView(R.id.my_info)
	CardView mMyInfo;
	@BindView(R.id.qq_id)
	TextView mQqId;
	@BindView(R.id.social_text)
	TextView mSocialText;
	@BindView(R.id.social_card)
	CardView mSocialCard;
	@BindView(R.id.plable_icon)
	ImageView mPlableIcon;
	@BindView(R.id.plable_lay)
	RelativeLayout mPlableLay;
	@BindView(R.id.part_icon)
	ImageView mPartIcon;
	@BindView(R.id.part_lay)
	RelativeLayout mPartLay;
	@BindView(R.id.intrest_icon)
	ImageView mIntrestIcon;
	@BindView(R.id.intrest_lay)
	RelativeLayout mIntrestLay;
	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerview;
	@BindView(R.id.photo_card)
	CardView mPhotoCard;
	@BindView(R.id.gift_text)
	TextView mGiftText;
	@BindView(R.id.gift_recyclerview)
	RecyclerView mGiftRecyclerview;
	@BindView(R.id.gift_card)
	CardView mGiftCard;
	@BindView(R.id.wechat_id)
	TextView mWechatId;
	@BindView(R.id.check_view_wechat)
	Button mCheckViewWechat;
	@BindView(R.id.check_view_qq)
	Button mCheckViewQq;
	@BindView(R.id.map)
	MapView mapView;
	@BindView(R.id.address)
	TextView mAdress;
	@BindView(R.id.map_card)
	CardView mMapCard;
	@BindView(R.id.my_location)
	TextView mMyLocation;
	@BindView(R.id.nickname)
	TextView mNickName;
	@BindView(R.id.age)
	TextView mAge;
	@BindView(R.id.city_text)
	TextView mCityText;
	@BindView(R.id.city)
	TextView mCity;
	@BindView(R.id.is_vip)
	ImageView mIsVip;
	@BindView(R.id.tv_friend)
	TextView mTvFriend;
	@BindView(R.id.card_friend)
	CardView mCardFriend;
	@BindView(R.id.city_lay)
	RelativeLayout mCityLay;

	private AMap aMap;
	private UiSettings mUiSettings;
	private GeocodeSearch geocoderSearch;

	private LatLonPoint mLatLonPoint;
	private String mAddress;// 选中的地址
	private double latitude;
	private double longitude;

	private View rootView;

	private ClientUser clientUser;
	private List<String> mVals = null;
	private DecimalFormat mFormat = new DecimalFormat("#.00");

	private TabPersonalPhotosAdapter mAdapter;
	private LinearLayoutManager layoutManager;
	private LinearLayoutManager mGiftLayoutManager;

	private DPoint mStartPoint;
	private DPoint mEndPoint;

	private Observable<UserEvent> observable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.tab_item_personal, null);
			ButterKnife.bind(this, rootView);
			initMap();
			setupViews();
			rxBusSub();
			setupData();
			setHasOptionsMenu(true);
			mapView.onCreate(savedInstanceState);// 此方法必须重写
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setZoomControlsEnabled(false);// 不显示缩放按钮
			mUiSettings.setLogoPosition(-50);
			mUiSettings.setZoomGesturesEnabled(false);
			aMap.moveCamera(CameraUpdateFactory.zoomTo(16));// 设置缩放比例
		}


		// 地理编码
		geocoderSearch = new GeocodeSearch(getActivity());
		geocoderSearch.setOnGeocodeSearchListener(this);

	}

	/**
	 * rx订阅
	 */
	private void rxBusSub() {
		observable = RxBus.getInstance().register(AppConstants.UPDATE_USER_INFO);
		observable.subscribe(userEvent -> setUserInfo(AppManager.getClientUser()));
	}

	private void setupViews() {
		layoutManager = new WrapperLinearLayoutManager(
				getActivity(), LinearLayoutManager.HORIZONTAL, false);
		layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mRecyclerview.setLayoutManager(layoutManager);
		mRecyclerview.setItemAnimator(new DefaultItemAnimator());

		mGiftLayoutManager = new WrapperLinearLayoutManager(
				getActivity(), LinearLayoutManager.HORIZONTAL, false);
		mGiftLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mGiftRecyclerview.setLayoutManager(mGiftLayoutManager);
		mGiftRecyclerview.setItemAnimator(new DefaultItemAnimator());
	}

	private void setupData() {
		mVals = new ArrayList<>();
		if (getArguments() != null) {
			clientUser = (ClientUser) getArguments().getSerializable(ValueKey.ACCOUNT);
			String lat = getArguments().getString(ValueKey.LATITUDE);
			String lon = getArguments().getString(ValueKey.LONGITUDE);
			if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)) {
				latitude = Double.parseDouble(lat);
				longitude = Double.parseDouble(lon);
			}
			getLocation();
			if (clientUser != null) {
				setUserInfo(clientUser);
				/**
				 * 用户收到的礼物
				 */
				if (!TextUtils.isEmpty(clientUser.gifts)) {
					mGiftText.setVisibility(View.VISIBLE);
					mGiftCard.setVisibility(View.VISIBLE);
					mAdapter = new TabPersonalPhotosAdapter(getActivity(),
							StringUtil.stringToIntList(clientUser.gifts));
					mGiftRecyclerview.setAdapter(mAdapter);
				} else {
					mGiftText.setVisibility(View.GONE);
					mGiftCard.setVisibility(View.GONE);
				}
				if (AppManager.getClientUser().isShowVip) {
					mTvFriend.setVisibility(View.VISIBLE);
					mCardFriend.setVisibility(View.VISIBLE);
				} else {
					mTvFriend.setVisibility(View.GONE);
					mCardFriend.setVisibility(View.GONE);
				}
			}
		}
	}

	/**
	 * 展示用户地图
	 */
	private void getLocation() {
		try {
			String myLatitude = AppManager.getClientUser().latitude;
			String myLongitude = AppManager.getClientUser().longitude;
			if (!TextUtils.isEmpty(myLatitude) &&
					!TextUtils.isEmpty(myLongitude)) {
				LatLonPoint latLonPoint = null;
				if ("-1".equals(AppManager.getClientUser().userId)) {
					latLonPoint = new LatLonPoint(latitude, longitude);
				} else {
					latLonPoint = new LatLonPoint(Double.parseDouble(myLatitude) + latitude,
							Double.parseDouble(myLongitude) + longitude);
				}
				mLatLonPoint = latLonPoint;
				LatLng latLng = null;
				if ("-1".equals(AppManager.getClientUser().userId)) {
					latLng = new LatLng(latitude, longitude);
				} else {
					latLng = new LatLng(Double.parseDouble(myLatitude) + latitude,
							Double.parseDouble(myLongitude) + longitude);
				}
				aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
				RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 1000,
						GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
				geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求

				mStartPoint = new DPoint(Double.parseDouble(myLatitude), Double.parseDouble(myLongitude));
				mEndPoint = new DPoint(latLonPoint.getLatitude(), latLonPoint.getLongitude());
			}
		} catch (Exception e) {

		}
	}

	private void setUserInfo(ClientUser clientUser) {
		if (AppManager.getClientUser().isShowGold) {
			mSocialCard.setVisibility(View.VISIBLE);
			mSocialText.setVisibility(View.VISIBLE);
		} else {
			mSocialCard.setVisibility(View.GONE);
			mSocialText.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(clientUser.purpose)) {
			mPurpose.setText(clientUser.purpose);
		}
		if (!TextUtils.isEmpty(clientUser.love_where)) {
			mLoveWhere.setText(clientUser.love_where);
		}
		if (!TextUtils.isEmpty(clientUser.do_what_first)) {
			mDoWhatFirst.setText(clientUser.do_what_first);
		}
		if (!TextUtils.isEmpty(clientUser.user_name)) {
			mNickName.setText(clientUser.user_name);
		}
		if (AppManager.getClientUser().isShowVip && clientUser.is_vip) {
			mIsVip.setVisibility(View.VISIBLE);
		}
		mAge.setText(String.valueOf(clientUser.age) + "岁");
		if (clientUser.userId.equals(AppManager.getClientUser().userId)) {
			if (!TextUtils.isEmpty(clientUser.city)) {
				mCityLay.setVisibility(View.VISIBLE);
				mCityText.setText("城市");
				mCity.setText(clientUser.city);
			} else {
				mCityLay.setVisibility(View.GONE);
			}
		} else {
			mCityLay.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(clientUser.distance) && Double.parseDouble(clientUser.distance) != 0) {
				mCityText.setText("距离");
				if (mStartPoint != null && mEndPoint != null) {
					mCity.setText(mFormat.format((CoordinateConverter.calculateLineDistance(mStartPoint, mEndPoint) / 1000)) + "km");
				} else {
					mCity.setText(mFormat.format(Double.parseDouble(clientUser.distance)) + "km");
				}
			} else if (!TextUtils.isEmpty(clientUser.city)) {
				mCityText.setText("城市");
				mCity.setText(clientUser.city);
			}
		}
		if (!TextUtils.isEmpty(clientUser.conception)) {
			mConception.setText(clientUser.conception);
		}
		if (!TextUtils.isEmpty(clientUser.tall)) {
			mTall.setText(clientUser.tall);
		}
		if (!TextUtils.isEmpty(clientUser.state_marry)) {
			mMarried.setText(clientUser.state_marry);
		}
		if (!TextUtils.isEmpty(clientUser.weight)) {
			int index = clientUser.weight.indexOf("k");
			String weight = clientUser.weight.substring(0, index);
			int w = 0;
			if (Integer.parseInt(weight) > 70) {
				w = Integer.parseInt(weight) / 2;
			} else {
				w = Integer.parseInt(weight);
			}
			mWeight.setText(w + "kg");
		}
		if (!TextUtils.isEmpty(clientUser.signature)) {
			mSignature.setText(clientUser.signature);
		}
		if (!TextUtils.isEmpty(clientUser.constellation)) {
			mConstellation.setText(clientUser.constellation);
		}
		if (!TextUtils.isEmpty(clientUser.occupation)) {
			mOccupation.setText(clientUser.occupation);
		}
		if (!TextUtils.isEmpty(clientUser.education)) {
			mColleage.setText(clientUser.education);
		}
		if (clientUser.userId.equals(AppManager.getClientUser().userId)) {
			if (!TextUtils.isEmpty(clientUser.weixin_no)) {
				mWechatId.setText(clientUser.weixin_no);
			}
			if (!TextUtils.isEmpty(clientUser.qq_no)) {
				mQqId.setText(clientUser.qq_no);
			}
			mCheckViewWechat.setVisibility(View.GONE);
			mCheckViewQq.setVisibility(View.GONE);
		} else {
			if (!TextUtils.isEmpty(clientUser.weixin_no)) {
				String weChat = clientUser.weixin_no;
				String subUrl = clientUser.weixin_no.substring(2, clientUser.weixin_no.length() - 3);
				weChat = weChat.replaceAll(subUrl, "****");
				mWechatId.setText(weChat);
			}
			if (!TextUtils.isEmpty(clientUser.qq_no)) {
				String qq = clientUser.qq_no;
				String subUrl = clientUser.qq_no.substring(2, clientUser.qq_no.length() - 3);
				qq = qq.replaceAll(subUrl, "****");
				mQqId.setText(qq);
			}
		}

		if (!TextUtils.isEmpty(clientUser.part_tag)) {
			mPartFlowlayout.setVisibility(View.VISIBLE);
			mVals.clear();
			mVals = StringUtil.stringToIntList(clientUser.part_tag);
			for (int i = 0; i < mVals.size(); i++) {
				if ("".equals(mVals.get(i)) || " ".equals(mVals.get(i))) {
					mVals.remove(i);
				}
			}
			mPartFlowlayout.setTags(mVals);
		}
		if (!TextUtils.isEmpty(clientUser.personality_tag)) {
			mPlableFlowlayout.setVisibility(View.VISIBLE);
			mVals.clear();
			mVals = StringUtil.stringToIntList(clientUser.personality_tag);
			for (int i = 0; i < mVals.size(); i++) {
				if ("".equals(mVals.get(i)) || " ".equals(mVals.get(i))) {
					mVals.remove(i);
				}
			}
			mPlableFlowlayout.setTags(mVals);
		}
		if (!TextUtils.isEmpty(clientUser.intrest_tag)) {
			mIntrestFlowlayout.setVisibility(View.VISIBLE);
			mVals.clear();
			mVals = StringUtil.stringToIntList(clientUser.intrest_tag);
			for (int i = 0; i < mVals.size(); i++) {
				if ("".equals(mVals.get(i)) || " ".equals(mVals.get(i))) {
					mVals.remove(i);
				}
			}
			mIntrestFlowlayout.setTags(mVals);
		}
	}

	@OnClick({R.id.check_view_wechat, R.id.check_view_qq})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.check_view_wechat:
				if (AppManager.getClientUser().is_vip) {
					showNoCheckDialog();
				} else {
					showTurnOnVipDialog("微信");
				}
				break;
			case R.id.check_view_qq:
				if (AppManager.getClientUser().is_vip) {
					showNoCheckDialog();
				} else {
					showTurnOnVipDialog("QQ");
				}
				break;
		}
	}


	private void showTurnOnVipDialog(String socialTpe) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(String.format(getResources().getString(R.string.social_id_need_vip), socialTpe));
		builder.setPositiveButton(R.string.ok, ((dialog, i) -> {
			dialog.dismiss();
			Intent intent = new Intent(getActivity(), VipCenterActivity.class);
			startActivity(intent);
		}));
		if (AppManager.getClientUser().isShowGiveVip) {
			builder.setNegativeButton(R.string.free_give_vip, ((dialog, i) -> {
				dialog.dismiss();
				Intent intent = new Intent(getActivity(), GiveVipActivity.class);
				startActivity(intent);
			}));
		} else {
			builder.setNegativeButton(R.string.until_single, ((dialog, i) -> dialog.dismiss()));
		}
		builder.show();
	}

	/**
	 * 禁止查看
	 */
	private void showNoCheckDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.no_check_tips);
		builder.setPositiveButton(getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		builder.show();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mapView != null) {
			mapView.onDestroy();
		}
		RxBus.getInstance().unregister(AppConstants.UPDATE_USER_INFO, observable);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mapView != null) {
			mapView.onResume();
		}
		MobclickAgent.onPageStart(this.getClass().getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mapView != null) {
			mapView.onPause();
		}
		MobclickAgent.onPageEnd(this.getClass().getName());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onMapScreenShot(Bitmap bitmap) {

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 1000) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				PoiItem poiItem = new PoiItem("", mLatLonPoint, "", result
						.getRegeocodeAddress().getFormatAddress());
				mAddress = poiItem.getSnippet();
				mAdress.setText(mAddress);
				if (AppManager.getClientUser().isShowMap &&
						!TextUtils.isEmpty(clientUser.distance) &&
						!"0.0".equals(clientUser.distance) &&
						!TextUtils.isEmpty(mAddress)) {
					mMyLocation.setVisibility(View.VISIBLE);
					mMapCard.setVisibility(View.VISIBLE);
				} else {
					mMapCard.setVisibility(View.GONE);
					mMyLocation.setVisibility(View.GONE);
				}
				if (clientUser.userId.equals(AppManager.getClientUser().userId)) {
					mMyLocation.setVisibility(View.GONE);
					mMapCard.setVisibility(View.GONE);
				}
			} else {
				mMyLocation.setVisibility(View.GONE);
				mMapCard.setVisibility(View.GONE);
			}
		} else {
			mMyLocation.setVisibility(View.GONE);
			mMapCard.setVisibility(View.GONE);
		}
	}

	@Override
	public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

	}
}
