package com.cyanbirds.wwjy.eventtype;

/**
 * 作者：wangyb
 * 时间：2016/10/18 21:50
 * 描述：下载app之后，点击snackbar
 */
public class SnackBarEvent {
    public String content;

    public SnackBarEvent() {

    }

    public SnackBarEvent(String msg) {
        this.content = msg;
    }
}
