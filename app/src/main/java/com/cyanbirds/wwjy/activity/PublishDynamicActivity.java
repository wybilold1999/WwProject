package com.cyanbirds.wwjy.activity;

import android.Manifest;
import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.adapter.PublishImageAdapter;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.Picture;
import com.cyanbirds.wwjy.eventtype.PubDycEvent;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.IUserDynamic;
import com.cyanbirds.wwjy.net.base.RetrofitFactory;
import com.cyanbirds.wwjy.net.request.OSSImagUploadRequest;
import com.cyanbirds.wwjy.utils.AESOperator;
import com.cyanbirds.wwjy.utils.CheckUtil;
import com.cyanbirds.wwjy.utils.FileAccessorUtils;
import com.cyanbirds.wwjy.utils.ImageUtil;
import com.cyanbirds.wwjy.utils.ProgressDialogUtils;
import com.cyanbirds.wwjy.utils.RxBus;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.cyanbirds.wwjy.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.cyanbirds.wwjy.activity.ModifyUserInfoActivity.REQUEST_PERMISSION_CAMERA_WRITE_EXTERNAL;

/**
 * 作者：wangyb
 * 时间：2016/9/13 22:49
 * 描述：
 */
public class PublishDynamicActivity extends BaseActivity {
	@BindView(R.id.dynamic_text_content)
	EditText mDynamicTextContent;
	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerview;

	private PublishImageAdapter mAdapter;
	private List<String> photoList;
	private List<Picture> ossImgUrls;
	private int count = 0;//OSS上传的时候修改piclist中的url值

	public static final int CHOOSE_IMG_RESULT = 0;

	private RxPermissions rxPermissions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_dynamic);
		Toolbar mToolbar = getActionBarToolbar();
		if (mToolbar != null) {
			mToolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		ButterKnife.bind(this);
		rxPermissions = new RxPermissions(this);
		setupView();
		setupData();
	}

	private void setupView() {
		mRecyclerview.setLayoutManager(new GridLayoutManager(this, 3));
	}

	private void setupData() {
		photoList = new ArrayList<>();
		mAdapter = new PublishImageAdapter(photoList){
			@Override
			public void openGallery() {
				requestPermission();
			}
		};
		mRecyclerview.setAdapter(mAdapter);
	}

	private void requestPermission() {
	    if (!CheckUtil.isGetPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                !CheckUtil.isGetPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            rxPermissions.requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(permission -> {// will emit 1 Permission object
                        if (permission.granted) {
                            // All permissions are granted !
                            toIntent();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // At least one denied permission without ask never again
                            showPermissionDialog(R.string.open_camera_write_external_permission, REQUEST_PERMISSION_CAMERA_WRITE_EXTERNAL);
                        } else {
                            // At least one denied permission with ask never again
                            // Need to go to the settings
                            showPermissionDialog(R.string.open_camera_write_external_permission, REQUEST_PERMISSION_CAMERA_WRITE_EXTERNAL);
                        }
                    }, throwable -> {

                    });
        } else {
	    	toIntent();
		}
	}

	private void toIntent() {
		Intent intent = new Intent(PublishDynamicActivity.this, PhotoChoserActivity.class);
		intent.putStringArrayListExtra(ValueKey.IMAGE_URL, (ArrayList<String>) photoList);
		startActivityForResult(intent, CHOOSE_IMG_RESULT);
	}

	private void showPermissionDialog(int textResId, int requestCode) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.permission_request);
		builder.setMessage(textResId);
		builder.setPositiveButton(R.string.ok, (dialog, i) -> {
			dialog.dismiss();
			Utils.goToSetting(this, requestCode);
		});
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.publish_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.publish) {
			if (TextUtils.isEmpty(mDynamicTextContent.getText().toString())) {
				ToastUtil.showMessage("还是说点什么吧");
				return false;
			}
			ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_uploda);
			if (photoList != null && photoList.size() > 0) {
				ossImgUrls = new ArrayList<>();
				for (String path : photoList) {
					ossImgUrls.add(ImageUtil.getPicInfoForPath(path));
					String imgUrl = ImageUtil.compressImage(path, FileAccessorUtils.IMESSAGE_IMAGE);
					new OSSUploadImgTask().request(AppManager.getFederationToken().bucketName,
							AppManager.getOSSFacePath(), imgUrl);
				}
			} else {
				publishDynamic("", mDynamicTextContent.getText().toString());
			}
			return true;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("您还未发表动态，确定退出？");
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
		return true;
	}

	class OSSUploadImgTask extends OSSImagUploadRequest {
		@Override
		public void onPostExecute(String s) {
			count++;
			if (count <= ossImgUrls.size()) {
				ossImgUrls.get(count - 1).path = AppConstants.OSS_IMG_ENDPOINT + s;
				if (count == photoList.size()) {
					Gson gson = new Gson();
					String picUrls = gson.toJson(ossImgUrls);
					publishDynamic(picUrls, mDynamicTextContent.getText().toString());
				}
			}
		}

		@Override
		public void onErrorExecute(String error) {
			ProgressDialogUtils.getInstance(PublishDynamicActivity.this).dismiss();
		}
	}

	private void publishDynamic(String pictures, String content) {
		ArrayMap<String, String> params = new ArrayMap<>(2);
		params.put("pictures", pictures);
		params.put("content", content);
		RetrofitFactory.getRetrofit().create(IUserDynamic.class)
				.publishDynamic(AppManager.getClientUser().sessionId, params)
				.subscribeOn(Schedulers.io())
				.map(responseBody -> {
					String decryptData = AESOperator.getInstance().decrypt(responseBody.string());
					JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
					int code = obj.get("code").getAsInt();
					if (code != 0) {
						return null;
					}
					return decryptData;
				})
				.observeOn(AndroidSchedulers.mainThread())
				.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))
				.subscribe(s -> {
					ProgressDialogUtils.getInstance(PublishDynamicActivity.this).dismiss();
					ToastUtil.showMessage(R.string.publish_success);
					RxBus.getInstance().post(AppConstants.PUB_DYNAMIC, new PubDycEvent(s));
					finish();
				}, throwable -> {
					ProgressDialogUtils.getInstance(PublishDynamicActivity.this).dismiss();
					if (throwable instanceof NullPointerException) {
						ToastUtil.showMessage(R.string.publish_dynamic_fail);
					} else {
						ToastUtil.showMessage(R.string.network_requests_error);
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == CHOOSE_IMG_RESULT) {
			List<String> imgUrls = data.getStringArrayListExtra(ValueKey.IMAGE_URL);
			if (imgUrls != null && !imgUrls.isEmpty()) {
				photoList.clear();
				photoList.addAll(imgUrls);
				mAdapter.notifyDataSetChanged();
			}
		} else if (requestCode == REQUEST_PERMISSION_CAMERA_WRITE_EXTERNAL) {
			requestPermission();
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
