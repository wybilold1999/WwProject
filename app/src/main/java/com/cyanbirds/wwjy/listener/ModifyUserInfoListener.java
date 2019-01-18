package com.cyanbirds.wwjy.listener;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-02-01 19:34 GMT+8
 * @email 395044952@qq.com
 */
public class ModifyUserInfoListener {
    private static OnModifyUserSignatureListener signatureListener = null;


    private ModifyUserInfoListener() {
    }

    public static ModifyUserInfoListener getInstance() {
        return ModifyUserInfoListener.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ModifyUserInfoListener INSTANCE = new ModifyUserInfoListener();
    }

    /**------------修改个性签名监听器-----------------**/
    public void setModifyUserSignatureListener(OnModifyUserSignatureListener listener) {
        signatureListener = listener;
    }

    public interface OnModifyUserSignatureListener {
        void notifyUserSignatureChanged(String signature);
    }

    public void notifyModifyUserSignatureListener(String signature){
        if(signatureListener != null){
            signatureListener.notifyUserSignatureChanged(signature);
        }
    }

}
