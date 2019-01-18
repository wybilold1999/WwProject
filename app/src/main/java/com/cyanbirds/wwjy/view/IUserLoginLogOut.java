package com.cyanbirds.wwjy.view;

import com.cyanbirds.wwjy.activity.base.IBasePresenter;
import com.cyanbirds.wwjy.activity.base.IBaseView;
import com.cyanbirds.wwjy.entity.ClientUser;

public interface IUserLoginLogOut {

    interface View extends IBaseView<Presenter> {
        void loginLogOutSuccess(ClientUser clientUser);
    }

    interface Presenter extends IBasePresenter {
        void onUserLogin(String account, String pwd);
        void onWXLogin(String code);
        void onQQLogin(String token, String openId);
        void onRegist(ClientUser clientUser, String channel);
        void onLogOut();
    }

    interface CheckSmsCodeView extends IBaseView<CheckSmsCodePresenter> {
        void checkSmsCode(int checkCode);
    }

    interface CheckSmsCodePresenter extends IBasePresenter {
        void checkSmsCode(String code, String phoneNum, int mPhoneType);
    }
}
