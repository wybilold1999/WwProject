package com.cyanbirds.wwjy.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.adapter.GiftMarketAdapter;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.entity.Gift;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserApi;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.utils.JsonUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：wangyb
 * 时间：2016/11/25 19:27
 * 描述：
 */
public class GiftMarketActivity extends BaseActivity implements View.OnClickListener{
	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerview;

	TextView mGiftName;
	SimpleDraweeView mGiftUrl;
	SimpleDraweeView mMyPortrait;
	SimpleDraweeView mOtherPortrait;
	TextView mSendGift;

	private View mGiftDialogView;
	private AlertDialog mGiftDialog;
	private Gift gift;

	private GridLayoutManager mGridLayoutManager;
	private GiftMarketAdapter mAdapter;
	private ClientUser giftUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gift_market);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		initView();
		getGiftList();
	}

	private void initView() {
		mGridLayoutManager = new GridLayoutManager(this, 3);
		mRecyclerview.setLayoutManager(mGridLayoutManager);

		giftUser = (ClientUser) getIntent().getSerializableExtra(ValueKey.USER);
	}

	private void getGiftList() {
		RetrofitFactory.getRetrofit().create(IUserApi.class)
				.getGift(AppManager.getClientUser().sessionId)
				.subscribeOn(Schedulers.io())
				.map(responseBody -> JsonUtils.parseGiftList(responseBody.string()))
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(gifts -> {
					mAdapter = new GiftMarketAdapter(GiftMarketActivity.this, gifts);
					mAdapter.setOnItemClickListener(mOnItemClickListener);
					mRecyclerview.setAdapter(mAdapter);
				}, throwable -> ToastUtil.showMessage(R.string.network_requests_error));
	}

	private GiftMarketAdapter.OnItemClickListener mOnItemClickListener = new GiftMarketAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			gift = mAdapter.getItem(position);
			initSendGiftDialogView();
			AlertDialog.Builder builder = new AlertDialog.Builder(GiftMarketActivity.this);
			builder.setView(mGiftDialogView);
			mGiftDialog = builder.show();

			mGiftName.setText(gift.name);
			mGiftUrl.setImageURI(Uri.parse(gift.dynamic_image_url));
			if (!TextUtils.isEmpty(AppManager.getClientUser().face_url)) {
				mMyPortrait.setImageURI(Uri.parse(AppManager.getClientUser().face_url));
			}
			if (giftUser != null) {
				mOtherPortrait.setImageURI(Uri.parse(giftUser.face_url));
			}
		}
	};

	private void initSendGiftDialogView() {
		mGiftDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_send_gift, null);
		mGiftName = (TextView) mGiftDialogView.findViewById(R.id.gift_name);
		mGiftUrl = (SimpleDraweeView) mGiftDialogView.findViewById(R.id.gift_url);
		mMyPortrait = (SimpleDraweeView) mGiftDialogView.findViewById(R.id.my_portrait);
		mOtherPortrait = (SimpleDraweeView) mGiftDialogView.findViewById(R.id.other_portrait);
		mSendGift = (TextView) mGiftDialogView.findViewById(R.id.send_gift);
		mSendGift.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		mGiftDialog.dismiss();
		if (AppManager.getClientUser().isShowVip) {
			if (AppManager.getClientUser().is_vip) {
				Snackbar.make(findViewById(R.id.recyclerview),
						getResources().getString(R.string.send_gift_success),
						Snackbar.LENGTH_LONG).show();
			} else {
				showVipDialog();
			}
		} else {
			Snackbar.make(findViewById(R.id.recyclerview),
					getResources().getString(R.string.send_gift_success),
					Snackbar.LENGTH_LONG).show();
		}
	}

	private void showVipDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.un_send_msg);
		builder.setPositiveButton(R.string.ok, ((dialog, i) -> {
			dialog.dismiss();
			Intent intent = new Intent(GiftMarketActivity.this, VipCenterActivity.class);
			startActivity(intent);
		}));
		if (AppManager.getClientUser().isShowGiveVip) {
			builder.setNegativeButton(R.string.free_give_vip, ((dialog, i) -> {
				dialog.dismiss();
				Intent intent = new Intent(GiftMarketActivity.this, GiveVipActivity.class);
				startActivity(intent);
			}));
		} else {
			builder.setNegativeButton(R.string.until_single, ((dialog, i) -> dialog.dismiss()));
		}
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
}
