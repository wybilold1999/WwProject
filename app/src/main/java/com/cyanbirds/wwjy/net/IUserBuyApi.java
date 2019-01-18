package com.cyanbirds.wwjy.net;

import android.support.v4.util.ArrayMap;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by wangyb on 2018/9/15
 */
public interface IUserBuyApi {

    /**
     * 会员商品信息
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("buy/buyList")
    Observable<ResponseBody> getBuyList(@Header("token") String token, @Field("type") int type);

    /**
     * 获取支付成功之后用户的vip信息
     * @param token
     * @return
     */
    @GET("user/getPayResult")
    Observable<ResponseBody> getPayResult(@Header("token") String token);

    /**
     * 创建订单
     * @param token
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("memberOrders/createOrder")
    Observable<ResponseBody> createOrder(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    /**
     * 获取返话费活动条件
     * @param token
     * @return
     */
    @GET("buy/getFareActivityInfo")
    Observable<ResponseBody> getFareActivityInfo(@Header("token") String token);
}
