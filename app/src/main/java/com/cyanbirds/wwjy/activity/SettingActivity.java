package com.cyanbirds.wwjy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.db.ConversationSqlManager;
import com.cyanbirds.wwjy.db.IMessageDaoManager;
import com.cyanbirds.wwjy.db.MyGoldDaoManager;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.manager.NotificationManagerUtils;
import com.cyanbirds.wwjy.presenter.UserLoginPresenterImpl;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.cyanbirds.wwjy.utils.ProgressDialogUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.cyanbirds.wwjy.view.IUserLoginLogOut;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author: wangyb
 * @datetime: 2016-01-23 16:58 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class SettingActivity extends BaseActivity<IUserLoginLogOut.Presenter> implements IUserLoginLogOut.View {

    @BindView(R.id.card_no_responsibility)
    CardView mNoRespCard;
    @BindView(R.id.no_responsibility_lay)
    RelativeLayout mNoRespLay;
    @BindView(R.id.switch_msg)
    SwitchCompat mSwitchMsg;
    @BindView(R.id.switch_msg_content)
    SwitchCompat mSwitchMsgContent;
    @BindView(R.id.switch_voice)
    SwitchCompat mSwitchVoice;
    @BindView(R.id.switch_vibrate)
    SwitchCompat mSwitchVibrate;
    @BindView(R.id.is_bangding_phone)
    TextView mIsBangdingPhone;
    @BindView(R.id.banding_phone_lay)
    RelativeLayout mBandingPhoneLay;
    @BindView(R.id.modify_pwd_lay)
    RelativeLayout mModifyPwdLay;
    @BindView(R.id.quit)
    RelativeLayout mQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting);
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView() {
    }

    private void setupEvent() {
    }

    private void setupData() {
        if (AppManager.getClientUser().isShowVip) {
            mNoRespCard.setVisibility(View.VISIBLE);
        } else {
            mNoRespCard.setVisibility(View.GONE);
        }
        if (AppManager.getClientUser().isCheckPhone) {
            mIsBangdingPhone.setText(R.string.already_bangding);
        } else {
            mIsBangdingPhone.setText(R.string.un_bangding);
        }
        if (PreferencesUtils.getNewMessageNotice(this)) {
            mSwitchMsg.setChecked(true);
        } else {
            mSwitchMsg.setChecked(false);
        }
        if (PreferencesUtils.getShowMessageInfo(this)) {
            mSwitchMsgContent.setChecked(true);
        } else {
            mSwitchMsgContent.setChecked(false);
        }
        if (PreferencesUtils.getNoticeVoice(this)) {
            mSwitchVoice.setChecked(true);
        } else {
            mSwitchVoice.setChecked(false);
        }
        if (PreferencesUtils.getNoticeShock(this)) {
            mSwitchVibrate.setChecked(true);
        } else {
            mSwitchVibrate.setChecked(false);
        }

    }

    @OnClick({R.id.no_responsibility_lay, R.id.switch_msg, R.id.switch_msg_content, R.id.switch_voice, R.id.switch_vibrate,
            R.id.banding_phone_lay, R.id.modify_pwd_lay, R.id.quit})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.no_responsibility_lay:
                intent.setClass(this, NoResponsibilityActivity.class);
                startActivity(intent);
                break;
            case R.id.switch_msg:
                if (PreferencesUtils.getNewMessageNotice(this)) {
                    mSwitchMsg.setChecked(false);
                    PreferencesUtils.setNewMessageNotice(this, false);
                } else {
                    mSwitchMsg.setChecked(true);
                    PreferencesUtils.setNewMessageNotice(this, true);
                }
                break;
            case R.id.switch_msg_content:
                if (PreferencesUtils.getShowMessageInfo(this)) {
                    mSwitchMsgContent.setChecked(false);
                    PreferencesUtils.setShowMessageInfo(this, false);
                } else {
                    mSwitchMsgContent.setChecked(true);
                    PreferencesUtils.setShowMessageInfo(this, true);
                }
                break;
            case R.id.switch_voice:
                if (PreferencesUtils.getNoticeVoice(this)) {
                    mSwitchVoice.setChecked(false);
                    PreferencesUtils.setNoticeVoice(this, false);
                } else {
                    mSwitchVoice.setChecked(true);
                    PreferencesUtils.setNoticeVoice(this, true);
                }
                break;
            case R.id.switch_vibrate:
                if (PreferencesUtils.getNoticeShock(this)) {
                    mSwitchVibrate.setChecked(false);
                    PreferencesUtils.setNoticeShock(this, false);
                } else {
                    mSwitchVibrate.setChecked(true);
                    PreferencesUtils.setNoticeShock(this, true);
                }
                break;
            case R.id.banding_phone_lay:
                intent.setClass(this, BandPhoneActivity.class);
                startActivity(intent);
                break;
            case R.id.modify_pwd_lay:
                intent.setClass(this, ModifyPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.quit:
                showQuitDialog();
                break;
        }
    }

    @Override
    public void loginLogOutSuccess(ClientUser clientUser) {
        ProgressDialogUtils.getInstance(SettingActivity.this).dismiss();
        if (clientUser.age == 1) {//用age代表是否退出登录成功的返回码.1表示不成功
            ToastUtil.showMessage(R.string.quite_faiure);
        } else {
            MobclickAgent.onProfileSignOff();
            release();
            NotificationManagerUtils.getInstance().cancelNotification();
            finishAll();
            PreferencesUtils.setIsLogin(SettingActivity.this, false);
            Intent intent = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(
                            getBaseContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void setPresenter(IUserLoginLogOut.Presenter presenter) {
        if (presenter == null) {
            this.presenter = new UserLoginPresenterImpl(this);
        }
    }

    /**
     * 显示退出dialog
     */
    private void showQuitDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.quit_items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            ProgressDialogUtils.getInstance(SettingActivity.this).show(R.string.dialog_logout_tips);
                            presenter.onLogOut();
                            break;
                        case 1:
                            exitApp();
                            break;
                    }
                }).setTitle(R.string.quit).show();
    }


    /**
     * 释放数据库
     */
    private static void release() {
        IMessageDaoManager.reset();
        ConversationSqlManager.reset();
        MyGoldDaoManager.reset();
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
