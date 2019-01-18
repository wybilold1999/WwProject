package com.cyanbirds.wwjy.listener;

/**
 * @author WANGYB
 * @Description:消息变更监听
 * @Date:2015年8月26日下午9:39:15
 */
public class MessageChangedListener {

    private static OnMessageChangedListener changedListener;

    private MessageChangedListener (){

    }

    public static MessageChangedListener getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final MessageChangedListener INSTANCE = new MessageChangedListener();
    }

    public void setMessageChangedListener(OnMessageChangedListener listener) {
        changedListener = listener;
    }

    public void clearAllMessageChangedListener() {
        changedListener = null;
    }

    public void notifyMessageChanged(String conversationId) {
        if (changedListener != null) {
            changedListener.onMessageChanged(conversationId);
        }
    }

    public interface OnMessageChangedListener {
        void onMessageChanged(String conversationId);
    }
}
