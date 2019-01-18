package com.cyanbirds.wwjy.entity;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;

import java.io.Serializable;

import static com.cyanbirds.wwjy.entity.AppointmentModel.AppointStatus.ACCEPT;
import static com.cyanbirds.wwjy.entity.AppointmentModel.AppointStatus.DECLINE;
import static com.cyanbirds.wwjy.entity.AppointmentModel.AppointStatus.MY_ACCEPT;
import static com.cyanbirds.wwjy.entity.AppointmentModel.AppointStatus.MY_DECLINE;
import static com.cyanbirds.wwjy.entity.AppointmentModel.AppointStatus.MY_WAIT_CALL_BACK;
import static com.cyanbirds.wwjy.entity.AppointmentModel.AppointStatus.OUT_TIME;
import static com.cyanbirds.wwjy.entity.AppointmentModel.AppointStatus.WAIT_CALL_BACK;

/**
 * Created by wangyb on 2018/1/22.
 * 约会model
 */

public class AppointmentModel implements Serializable{

    public int id;
    public String userId;     //约会者id
    public String userById;   //被约会者id
    public String userName;   //约会者名字
    public String userByName;   //被约会者名字
    public String faceUrl;    //被约会者头像
    public int status;     //约会状态
    public String theme;      //约会主题
    public String appointTime;       //约会时间
    public String appointTimeLong;       //约会时长
    public double latitude;   //经度
    public double longitude;  //纬度
    public String remark;     //留言
    public String imgUrl;     //地图图片url
    public String address;     //地址


    public static class AppointStatus {

        public static final int MY_WAIT_CALL_BACK = 0;//等待自己回应
        public static final int ACCEPT = 1;//已接受
        public static final int DECLINE = 2;//已拒绝
        public static final int OUT_TIME = 3;//超时已取消
        public static final int MY_ACCEPT = 4;//自己接受
        public static final int MY_DECLINE = 5;//自己拒绝
        public static final int WAIT_CALL_BACK = 6;//等待对方回应
    }

    public static String getStatus(int status) {
        String AppointStatus = CSApplication.getInstance().getResources().getString(R.string.appointment_status);
        switch (status) {
            case MY_WAIT_CALL_BACK:
                AppointStatus = String.format(AppointStatus, "等待同意");
                break;
            case ACCEPT:
                AppointStatus = String.format(AppointStatus, "对方已接受");
                break;
            case DECLINE:
                AppointStatus = String.format(AppointStatus, "对方已拒绝");
                break;
            case OUT_TIME:
                AppointStatus = String.format(AppointStatus, "超时已取消");
                break;
            case MY_ACCEPT:
                AppointStatus = String.format(AppointStatus, "已同意");
                break;
            case MY_DECLINE:
                AppointStatus = String.format(AppointStatus, "已拒绝");
                break;
            case WAIT_CALL_BACK:
                AppointStatus = String.format(AppointStatus, "等待对方同意");
                break;
        }
        return AppointStatus;
    }

}
