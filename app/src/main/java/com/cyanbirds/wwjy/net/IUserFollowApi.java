package com.cyanbirds.wwjy.net;

import android.support.v4.util.ArrayMap;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by wangyb on 2018/9/14
 */
public interface IUserFollowApi {
    @FormUrlEncoded
    @POST("follow/addFollow")
    Observable<ResponseBody> addFollow(@Header("token") String token, @Field("followId") String followId);

    @FormUrlEncoded
    @POST("follow/{listUrl}")
    Observable<ResponseBody> getFollowList(@Path("listUrl") String listUrl, @Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("follow/getFollowAndLoveInfo")
    Observable<ResponseBody> getFollowAndLoveInfo(@Header("token") String token, @Field("uid") String uid);

    @FormUrlEncoded
    @POST("follow/giftsList")
    Observable<ResponseBody> getGiftsList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);
}
