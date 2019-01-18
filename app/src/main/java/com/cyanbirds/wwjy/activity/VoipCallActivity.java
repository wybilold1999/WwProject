package com.cyanbirds.wwjy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.cyanbirds.wwjy.utils.VibratorUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangyb on 2017/6/12.
 * 描述：拨打电话
 */

public class VoipCallActivity extends BaseActivity {

    @BindView(R.id.portrait)
    SimpleDraweeView mPortrait;
    @BindView(R.id.nick_name)
    TextView mNickName;
    @BindView(R.id.answer)
    ImageView mAnswer;
    @BindView(R.id.decline)
    ImageView mDecline;
    @BindView(R.id.receive_call_lay)
    RelativeLayout mCallLay;
    @BindView(R.id.calling_lay)
    RelativeLayout mCallingLay;
    @BindView(R.id.decline_call)
    ImageView mDeclineCall;
    @BindView(R.id.speaker)
    ImageView mSpeaker;

    private CountDownTimer timer;
    private String from;
    private long time;
    private boolean changeFlag = false;//切换图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voip_call);
        ButterKnife.bind(this);
        setupData();
    }

    private void setupData() {
        String faceUrl = getIntent().getStringExtra(ValueKey.IMAGE_URL);
        String nickName = getIntent().getStringExtra(ValueKey.USER_NAME);
        from = getIntent().getStringExtra(ValueKey.FROM_ACTIVITY);
        if (!TextUtils.isEmpty(faceUrl)) {
            mPortrait.setImageURI(Uri.parse(faceUrl));
        }
        if (!TextUtils.isEmpty(nickName)) {
            mNickName.setVisibility(View.VISIBLE);
            mNickName.setText(nickName);
        }
        if (!TextUtils.isEmpty(from)) {//主动拨打电话
            mCallLay.setVisibility(View.GONE);
            mCallingLay.setVisibility(View.VISIBLE);
            time = 1000;
        } else {
            mCallLay.setVisibility(View.VISIBLE);
            mCallingLay.setVisibility(View.GONE);
            time = 50000;
        }
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                timer.cancel();
                if (!TextUtils.isEmpty(from)) {
                    VibratorUtil.cancel();
                    if (!AppManager.getClientUser().is_vip) {
                        showTurnOnVipDialog(getResources().getString(R.string.no_vip_calling));
                    }
                } else {
                    finish();
                }
            }

        };
        timer.start();
        VibratorUtil.start();
    }

    @OnClick({R.id.answer, R.id.decline, R.id.decline_call, R.id.speaker})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.answer:
                if (!AppManager.getClientUser().is_vip) {
                    showTurnOnVipDialog(getResources().getString(R.string.no_vip_receive_calling));
                }
                break;
            case R.id.decline:
                ToastUtil.showMessage(R.string.decline_call);
                timer.cancel();
                VibratorUtil.cancel();
                finish();
                break;
            case R.id.decline_call:
                timer.cancel();
                VibratorUtil.cancel();
                finish();
                break;
            case R.id.speaker:
                if (changeFlag) {
                    mSpeaker.setImageResource(R.mipmap.voip_speaker_off);
                } else {
                    mSpeaker.setImageResource(R.mipmap.voip_speaker_on);
                }
                changeFlag = !changeFlag;
                break;
        }
    }

    private void showTurnOnVipDialog(String tips) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(tips);
        builder.setPositiveButton(R.string.ok, ((dialog, i) -> {
            dialog.dismiss();
            Intent intent = new Intent(VoipCallActivity.this, VipCenterActivity.class);
            startActivity(intent);
            finish();
        }));
        if (AppManager.getClientUser().isShowGiveVip) {
            builder.setNegativeButton(R.string.free_give_vip, ((dialog, i) -> {
                dialog.dismiss();
                Intent intent = new Intent(VoipCallActivity.this, GiveVipActivity.class);
                startActivity(intent);
                finish();
            }));
        } else {
            builder.setNegativeButton(R.string.until_single, ((dialog, i) -> {
                dialog.dismiss();
                if (!TextUtils.isEmpty(from)) {
                    finish();
                }
            }));
        }
        if (!TextUtils.isEmpty(from)) {
            builder.setCancelable(false);
        }
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VibratorUtil.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;//不执行父类点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }
}
