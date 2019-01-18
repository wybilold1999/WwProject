package com.cyanbirds.wwjy.net;

import android.support.v4.util.ArrayMap;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by wangyb on 2018/9/14
 */
public interface IUserLoveApi {
    
    @FormUrlEncoded
    @POST("love/addLove")
    Observable<ResponseBody> addLove(@Header("token") String token, @Field("loveId") String loveId);

    @FormUrlEncoded
    @POST("love/loveFormeList")
    Observable<ResponseBody> getLoveFormeList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("greet/sendGreet")
    Observable<ResponseBody> sendGreet(@Header("token") String token, @Field("greetId") String greetId);
}
