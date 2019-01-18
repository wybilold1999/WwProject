package com.cyanbirds.wwjy.utils;

import android.text.TextUtils;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.entity.AllKeys;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.entity.NoResponsibilityModel;
import com.cyanbirds.wwjy.entity.FederationToken;
import com.cyanbirds.wwjy.entity.FollowLoveModel;
import com.cyanbirds.wwjy.entity.FollowModel;
import com.cyanbirds.wwjy.entity.Gift;
import com.cyanbirds.wwjy.entity.LoveModel;
import com.cyanbirds.wwjy.entity.MemberBuy;
import com.cyanbirds.wwjy.entity.PictureModel;
import com.cyanbirds.wwjy.entity.ReceiveGiftModel;
import com.cyanbirds.wwjy.entity.WeChatPay;
import com.cyanbirds.wwjy.entity.YuanFenModel;
import com.cyanbirds.wwjy.manager.AppManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    /**
     * 登录的clientuser
     * @param json
     * @return
     */
    public static ClientUser parseClientUser(String json){
        try {
            String decrptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decrptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code == 1) {
                return null;
            }
            JsonObject data = obj.get("data").getAsJsonObject();
            ClientUser clientUser = AppManager.getClientUser();
            clientUser.userId = data.get("uid").getAsString();
            clientUser.userPwd = data.get("upwd").getAsString();
            clientUser.sex = data.get("sex").getAsInt() == 1 ? "1" : (data.get("sex").getAsInt() == 0 ? "0" : "all");
            clientUser.mobile = data.get("phone") == null ? "" : data.get("phone").getAsString();
            clientUser.qq_no = data.get("qq").getAsString();
            clientUser.weixin_no = data.get("wechat").getAsString();
            clientUser.face_url = data.get("faceUrl").getAsString();
            clientUser.user_name = data.get("nickname").getAsString();
            clientUser.occupation = data.get("occupation").getAsString();
            clientUser.education = data.get("education").getAsString();
            clientUser.tall = data.get("heigth").getAsString();
            clientUser.weight = data.get("weight").getAsString();
            clientUser.isCheckPhone = data.get("isCheckPhone").getAsBoolean();
            clientUser.is_vip = data.get("isVip").getAsBoolean();
            clientUser.is_download_vip = data.get("isDownloadVip").getAsBoolean();
            JsonObject jsonObject = data.get("showClient").getAsJsonObject();
            clientUser.isShowVip = jsonObject.get("isShowVip").getAsBoolean();
            clientUser.isShowDownloadVip = jsonObject.get("isShowDownloadVip").getAsBoolean();
            clientUser.isShowGold = jsonObject.get("isShowGold").getAsBoolean();
            clientUser.isShowLovers = jsonObject.get("isShowLovers").getAsBoolean();
            clientUser.isShowVideo = jsonObject.get("isShowVideo").getAsBoolean();
            clientUser.isShowMap = jsonObject.get("isShowMap").getAsBoolean();
            clientUser.isShowRpt = jsonObject.get("isShowRpt").getAsBoolean();
            clientUser.isShowTd = jsonObject.get("isShowTd").getAsBoolean();
            clientUser.isShowAppointment = jsonObject.get("isShowAppointment").getAsBoolean();
            clientUser.isShowGiveVip = jsonObject.get("isShowGiveVip").getAsBoolean();
            clientUser.gold_num = data.get("goldNum").getAsInt();
            clientUser.state_marry = data.get("emotionStatus").getAsString();
            clientUser.city = data.get("city").getAsString();
            clientUser.age = data.get("age").getAsInt();
            clientUser.signature = data.get("signature").getAsString();
            clientUser.constellation = data.get("constellation").getAsString();
            clientUser.distance = data.get("distance").getAsString();
            clientUser.intrest_tag = data.get("intrestTag").getAsString();
            clientUser.personality_tag = data.get("personalityTag").getAsString();
            clientUser.part_tag = data.get("partTag").getAsString();
            clientUser.purpose = data.get("purpose").getAsString();
            clientUser.love_where = data.get("loveWhere").getAsString();
            clientUser.do_what_first = data.get("doWhatFirst").getAsString();
            clientUser.conception = data.get("conception").getAsString();
            clientUser.sessionId = data.get("sessionId").getAsString();
            clientUser.imgUrls = data.get("pictures") == null ? "" : data.get("pictures").getAsString();
            clientUser.gifts = data.get("gifts").getAsString();
            clientUser.versionCode = data.get("versionCode").getAsInt();
            clientUser.apkUrl = data.get("apkUrl").getAsString();
            clientUser.versionUpdateInfo = data.get("versionUpdateInfo").getAsString();
            clientUser.isForceUpdate = data.get("isForceUpdate").getAsBoolean();
            return clientUser;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 微信ID等
     * @param json
     * @return
     */
    public static AllKeys parseJsonIdKeys(String json){
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            if (!TextUtils.isEmpty(decryptData)) {
                Gson gson = new Gson();
                AllKeys keys = gson.fromJson(decryptData, AllKeys.class);
                return keys;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 手机是否注册
     * @param json
     * @return
     */
    public static boolean parseCheckIsRegister(String json) {
        try {
            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return false;
            }
            boolean isRegister = obj.get("data").getAsBoolean();
            return isRegister;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * OSS鉴权
     * @param result
     * @return
     */
    public static FederationToken parseOSSToken(String result) {
        try {
            if (!TextUtils.isEmpty(result)) {
                String data = AESOperator.getInstance().decrypt(result);
                Gson gson = new Gson();
                FederationToken token = gson.fromJson(data, FederationToken.class);
                if (token != null && !TextUtils.isEmpty(token.accessKeySecret) && !TextUtils.isEmpty(token.accessKeyId) && !TextUtils.isEmpty(token.bucketName) && !TextUtils.isEmpty(token.imagesEndpoint)) {
                    return token;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 关注者
     * @param json
     * @return
     */
    public static ArrayList<FollowModel> parseJsonFollows(String json){
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            String result = obj.get("data").getAsString();
            Type listType = new TypeToken<ArrayList<FollowModel>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<FollowModel> followModels = gson.fromJson(result, listType);
            return followModels;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 接收到的礼物
     * @param json
     * @return
     */
    public static ArrayList<ReceiveGiftModel> parseJsonReceiveGift(String json){
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            String result = obj.get("data").getAsString();
            Type listType = new TypeToken<ArrayList<ReceiveGiftModel>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<ReceiveGiftModel> receiveGiftModels = gson.fromJson(result, listType);
            return receiveGiftModels;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 喜欢的人
     * @param json
     * @return
     */
    public static ArrayList<LoveModel> parseJsonLovers(String json){
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            String result = obj.get("data").getAsString();
            Type listType = new TypeToken<ArrayList<LoveModel>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<LoveModel> loveModels = gson.fromJson(result, listType);
            return loveModels;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取用户信息
     * @param json
     */
    public static ClientUser parserUserInfo(String json){
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            String dataString = obj.get("data").getAsString();
            JsonObject data = new JsonParser().parse(dataString).getAsJsonObject();
            ClientUser clientUser = new ClientUser();
            clientUser.userId = data.get("uid").getAsString();
            clientUser.sex = data.get("sex").getAsInt() == 1 ? "1" : "0";
            clientUser.user_name = data.get("nickname").getAsString();
            clientUser.city = data.get("city").getAsString();
            clientUser.distance = data.get("distance").getAsString();
            clientUser.tall = data.get("heigth").getAsString();
            clientUser.weight = data.get("weight").getAsString();
            clientUser.is_vip = data.get("isVip").getAsBoolean();
            clientUser.isFollow = data.get("isFollow").getAsBoolean();
            clientUser.state_marry = data.get("emotionStatus").getAsString();
            clientUser.face_url = data.get("faceUrl").getAsString();
            clientUser.age = data.get("age").getAsInt();
            clientUser.signature = data.get("signature").getAsString();
            clientUser.constellation = data.get("constellation").getAsString();
            clientUser.qq_no = data.get("qq").getAsString();
            clientUser.weixin_no = data.get("wechat").getAsString();
            clientUser.occupation = data.get("occupation").getAsString();
            clientUser.education = data.get("education").getAsString();
            clientUser.intrest_tag = data.get("intrestTag").getAsString();
            clientUser.personality_tag = data.get("personalityTag").getAsString();
            clientUser.part_tag = data.get("partTag").getAsString();
            clientUser.purpose = data.get("purpose").getAsString();
            clientUser.love_where = data.get("loveWhere").getAsString();
            clientUser.do_what_first = data.get("doWhatFirst").getAsString();
            clientUser.conception = data.get("conception").getAsString();
            clientUser.imgUrls = data.get("picturesUrls").getAsString();
            clientUser.gifts = data.get("gifts").getAsString();
            clientUser.latitude = data.get("latitude").getAsString();
            clientUser.longitude = data.get("longitude").getAsString();
            return clientUser;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 缘分用户
     * @param json
     * @return
     */
    public static List<YuanFenModel> parseYuanFenUsers(String json){
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            String dataString = obj.get("data").getAsString();
            Type listType = new TypeToken<ArrayList<YuanFenModel>>() {
            }.getType();
            Gson gson = new Gson();
            List<YuanFenModel> models = gson.fromJson(dataString, listType);
            return models;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取关注，喜欢我的用户数
     * @param json
     * @return
     */
    public static FollowLoveModel parseFollowLove(String json) {
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code == 1) {
                return null;
            }
            Gson gson = new Gson();
            FollowLoveModel model = gson.fromJson(obj.get("data").getAsJsonObject(), FollowLoveModel.class);
            return model;
        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<PictureModel> parseDiscoverInfo(String json) {
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            String result = obj.get("data").getAsString();
            Type listType = new TypeToken<ArrayList<PictureModel>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<PictureModel> pictureModels = gson.fromJson(result, listType);
            return pictureModels;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 开通会员的名称
     * @param json
     * @return
     */
    public static ArrayList<String> parseUserName(String json) {
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            String result = obj.get("data").getAsString();
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<String> mNameList = gson.fromJson(result, listType);
            return mNameList;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 商品列表
     * @param json
     * @return
     */
    public static ArrayList<MemberBuy> parseMemberBuy(String json) {
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            String result = obj.get("data").getAsString();
            Type listType = new TypeToken<ArrayList<MemberBuy>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<MemberBuy> memberBuys = gson.fromJson(result, listType);
            return memberBuys;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 微信支付信息
      * @param json
     * @return
     */
    public static WeChatPay parseWeChatPay(String json) {
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            JsonObject data = obj.get("data").getAsJsonObject();
            String payInfo = data.get("payInfo").getAsString();
            Gson gson = new Gson();
            WeChatPay weChatPay = gson.fromJson(payInfo, WeChatPay.class);
            return weChatPay;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 礼物列表
     * @param json
     * @return
     */
    public static List<Gift> parseGiftList(String json) {
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            String data = obj.get("data").getAsString();
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Gift>>() {
            }.getType();
            List<Gift> gifts = gson.fromJson(data, listType);
            return gifts;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取用户列表
     * @param json
     * @return
     */
    public static List<ClientUser> parseUsertList(String json) {
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code == 1) {
                return null;
            } else if(code == 3){
                return null;
            }
            String strData = obj.get("data").getAsString();
            JsonArray data = new JsonParser().parse(strData).getAsJsonArray();
            List<ClientUser> userList = new ArrayList<>();
            for(int i = 0; i < data.size(); i++){
                ClientUser clientUser = new ClientUser();
                JsonObject jsonObject = data.get(i).getAsJsonObject();
                clientUser.userId = jsonObject.get("uid").getAsString();
                clientUser.sex = jsonObject.get("sex").getAsInt() == 1 ? "1" : "0";
                clientUser.user_name = jsonObject.get("nickname").getAsString();
                clientUser.is_vip = jsonObject.get("isVip").getAsBoolean();
                clientUser.state_marry = jsonObject.get("emotionStatus").getAsString();
                clientUser.face_url = jsonObject.get("faceUrl").getAsString();
                clientUser.age = jsonObject.get("age").getAsInt();
                clientUser.signature = jsonObject.get("signature").getAsString();
                clientUser.constellation = jsonObject.get("constellation").getAsString();
                clientUser.city = jsonObject.get("city").getAsString();
                Object o = jsonObject.get("distance");
                if (!(o instanceof JsonNull)) {
                    clientUser.distance = jsonObject.get("distance").getAsString();
                }
                clientUser.personality_tag = jsonObject.get("personalityTag").getAsString();
                userList.add(clientUser);
            }
            return userList;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 根据api解析返回的ip地址
     * @param result
     * @return
     */
    public static String parseIPJson(String result) {
        int start = result.indexOf("{");
        int end = result.indexOf("}");
        String json = result.substring(start, end + 1);
        String ipAddress = "";
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                ipAddress = jsonObject.optString("cip");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ipAddress;
    }

    /**
     * 解析根据ip地址返回的地址
     * @param result
     * @return
     */
    public static String parseCityJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject content = jsonObject.optJSONObject("content");
            JSONObject addressDetail = content.optJSONObject("address_detail");
            String city = addressDetail.optString("city");
            String province = addressDetail.optString("province");
            JSONObject point = content.optJSONObject("point");
            String lat = point.optString("y");
            String lon = point.optString("x");
            PreferencesUtils.setCurrentCity(CSApplication.getInstance(), city);
            PreferencesUtils.setCurrentProvince(CSApplication.getInstance(), province);
            PreferencesUtils.setLatitude(CSApplication.getInstance(), lat);
            PreferencesUtils.setLongitude(CSApplication.getInstance(), lon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static NoResponsibilityModel parseNoResponsibilityModel(String json) {
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                return null;
            }
            Gson gson = new Gson();
            NoResponsibilityModel model = gson.fromJson(obj.getAsJsonObject("data"), NoResponsibilityModel.class);
            return model;
        } catch (Exception e) {
        }
        return null;
    }

}
