package com.cyanbirds.wwjy.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.config.AppConstants;
import com.cyanbirds.wwjy.eventtype.PayEvent;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.utils.RxBus;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * 作者：wangyb
 * 时间：2016/11/3 18:06
 * 描述：微信支付后的回调页面
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handleIntent(getIntent());
	}

	private void handleIntent(Intent paramIntent) {
		if (null != AppManager.getIWX_PAY_API()) {
			AppManager.getIWX_PAY_API().handleIntent(paramIntent, this);
		} else if (CSApplication.api != null) {
			CSApplication.api.handleIntent(paramIntent, this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	@Override
	public void onReq(BaseReq baseReq) {
		switch (baseReq.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				break;
			default:
				break;
		}
	}

	@Override
	public void onResp(BaseResp baseResp) {
		if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (baseResp.errCode == 0) {
				RxBus.getInstance().post(AppConstants.PAY_SUCCESS, new PayEvent());
				ToastUtil.showMessage(R.string.pay_success);
			} else if (baseResp.errCode == -1){
				ToastUtil.showMessage(R.string.pay_failure);
			} else {
				ToastUtil.showMessage("已取消");
			}
		}
		finish();
	}
}
