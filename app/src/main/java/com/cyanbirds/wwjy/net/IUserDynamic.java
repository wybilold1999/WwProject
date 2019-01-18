package com.cyanbirds.wwjy.net;

import android.support.v4.util.ArrayMap;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by wangyb on 2018/9/14
 */
public interface IUserDynamic {

    @FormUrlEncoded
    @POST("dynamic/publishDynamic")
    Observable<ResponseBody> publishDynamic(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("dynamic/dynamicList")
    Observable<ResponseBody> getDynamicList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);
}
