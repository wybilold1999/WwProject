package com.cyanbirds.wwjy.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.request.UploadTokenRequest;
import com.cyanbirds.wwjy.utils.PushMsgUtil;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class MyIntentService extends GTIntentService {

	private static final String TAG = "GetuiSdkDemo";

	private Handler mHandler = new Handler(Looper.getMainLooper());

	private boolean isAlreadyUpload = false;

	public MyIntentService() {

	}

	@Override
	public void onReceiveServicePid(Context context, int pid) {
	}

	@Override
	public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
		byte[] payload = msg.getPayload();
		if (payload != null) {
			final String data = new String(payload);
			if (AppManager.getClientUser().isShowVip) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						PushMsgUtil.getInstance().handlePushMsg(true, data);
					}
				});
			}
		}
	}

	@Override
	public void onReceiveClientId(Context context, String clientid) {
		if (!TextUtils.isEmpty(clientid) && !isAlreadyUpload) {
			isAlreadyUpload = true;
			PushManager.getInstance().bindAlias(context, AppManager.getClientUser().userId);
			new UploadTokenRequest().request(clientid, "", "");
		}
	}

	@Override
	public void onReceiveOnlineState(Context context, boolean online) {
	}

	@Override
	public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
		int action = cmdMessage.getAction();

		if (action == PushConsts.SET_TAG_RESULT) {
			setTagResult((SetTagCmdMessage) cmdMessage);
		} else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
		}
	}

	@Override
	public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {
	}

	@Override
	public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {
	}

	private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
		String sn = setTagCmdMsg.getSn();
		String code = setTagCmdMsg.getCode();

		String text = "设置标签失败, 未知异常";
		switch (Integer.valueOf(code)) {
			case PushConsts.SETTAG_SUCCESS:
				text = "设置标签成功";
				break;

			case PushConsts.SETTAG_ERROR_COUNT:
				text = "设置标签失败, tag数量过大, 最大不能超过200个";
				break;

			case PushConsts.SETTAG_ERROR_FREQUENCY:
				text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
				break;

			case PushConsts.SETTAG_ERROR_REPEAT:
				text = "设置标签失败, 标签重复";
				break;

			case PushConsts.SETTAG_ERROR_UNBIND:
				text = "设置标签失败, 服务未初始化成功";
				break;

			case PushConsts.SETTAG_ERROR_EXCEPTION:
				text = "设置标签失败, 未知异常";
				break;

			case PushConsts.SETTAG_ERROR_NULL:
				text = "设置标签失败, tag 为空";
				break;

			case PushConsts.SETTAG_NOTONLINE:
				text = "还未登陆成功";
				break;

			case PushConsts.SETTAG_IN_BLACKLIST:
				text = "该应用已经在黑名单中,请联系售后支持!";
				break;

			case PushConsts.SETTAG_NUM_EXCEED:
				text = "已存 tag 超过限制";
				break;

			default:
				break;
		}
	}

}
