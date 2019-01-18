package com.cyanbirds.wwjy.utils;

import android.app.Service;
import android.os.Vibrator;

import com.cyanbirds.wwjy.CSApplication;

/**
 * Created by wangyb on 2017/6/12.
 * 描述：
 */

public class VibratorUtil {
    private static Vibrator vibrator;
    /**
     * final Activity activity  ：调用该方法的Activity实例
     * long milliseconds ：震动的时长，单位是毫秒
     * long[] pattern  ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */

    public static void Vibrate(long milliseconds) {
        vibrator = (Vibrator) CSApplication.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }
    public static void Vibrate(long[] pattern,boolean isRepeat) {
        vibrator = (Vibrator) CSApplication.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, isRepeat ? 1 : -1);
    }

    public static void start() {
        vibrator = (Vibrator) CSApplication.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
        long [] pattern = {600, 1500, 600, 1500};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, 2);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

    public static void cancel() {
        vibrator.cancel();
    }


}
