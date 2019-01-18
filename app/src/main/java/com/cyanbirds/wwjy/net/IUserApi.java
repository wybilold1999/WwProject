package com.cyanbirds.wwjy.net;

import android.support.v4.util.ArrayMap;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface IUserApi {

    @FormUrlEncoded
    @POST("user/login")
    Observable<ResponseBody> userLogin(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/qq_login")
    Observable<ResponseBody> qqLogin(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/wechat_login")
    Observable<ResponseBody> wxLogin(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/register")
    Observable<ResponseBody> userRegister(@FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/logoutLogin")
    Observable<ResponseBody> userLogout(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    /**
     * 获取微信登录和支付id
     * @return
     */
    @GET("user/getIdKeys")
    Observable<ResponseBody> getIdKeys();

    @FormUrlEncoded
    @POST("user/checkIsRegister")
    Observable<ResponseBody> checkIsRegister(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("captch/smsCode")
    Observable<ResponseBody> checkSmsCode(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/newPasswordSetting")
    Observable<ResponseBody> findPwd(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/updatePassword")
    Observable<ResponseBody> modifyPwd(@Header("token") String token, @Field("newPassword") String newPassword);

    @FormUrlEncoded
    @POST("user/updatePerson")
    Observable<ResponseBody> updateUserInfo(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @GET("oss/distribute-token")
    Observable<ResponseBody> getOSSToken();

    @FormUrlEncoded
    @POST("user/userInfo")
    Observable<ResponseBody> getUserInfo(@Header("token") String token, @Field("uid") String uid);

    /**
     * 缘分卡片
     * @param token
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("user/yuanFenUser")
    Observable<ResponseBody> getYuanFenUser(@Header("token") String token, @FieldMap ArrayMap<String, Integer> params);

    @FormUrlEncoded
    @POST("user/getUserName")
    Observable<ResponseBody> getUserName(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    /**
     * 获取礼物信息
     * @param token
     * @return
     */
    @GET("user/getGift")
    Observable<ResponseBody> getGift(@Header("token") String token);

    @FormUrlEncoded
    @POST("user/homeLoveList")
    Observable<ResponseBody> getHomeLoveList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    /**
     * 上传城市信息
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("user/uploadCityInfo")
    Observable<ResponseBody> uploadCityInfo(@FieldMap ArrayMap<String, String> params, @Header("token") String token);

    /**
     * 上传crash信息
     * @return
     */
    @FormUrlEncoded
    @POST("user/uploadCrash")
    Call<ResponseBody> uploadCrash(@Field("crashInfo") String crashInfo);

    /**
     * 上传token
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("user/uploadToken")
    Call<ResponseBody> uploadToken(@FieldMap ArrayMap<String, String> params, @Header("token") String token);

    /**
     * 根据api查询ip地址
     * @return
     */
    @GET("http://pv.sohu.com/cityjson?ie=utf-8")
    Observable<ResponseBody> getIPAddress();

    /**
     * 根据ip获取城市
     * @param url
     * @return
     */
    @GET
    Observable<ResponseBody> getCityByIP(@Url String url);

    /**
     * 免责申明
     * @param token
     * @return
     */
    @GET("buy/getFareActivityInfo")
    Observable<ResponseBody> getFareActivityInfo(@Header("token") String token);

}
