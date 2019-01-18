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
public interface IUserPictureApi {
    @FormUrlEncoded
    @POST("picture/discoverPictures")
    Observable<ResponseBody> getDiscoverPictures(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("picture/uploadCommentImg")
    Observable<ResponseBody> uploadCommentImg(@Header("token") String token, @Field("imgUrl") String imgUrl);
}
