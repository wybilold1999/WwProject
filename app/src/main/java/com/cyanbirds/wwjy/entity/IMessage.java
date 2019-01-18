package com.cyanbirds.wwjy.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:21 GMT+8
 * @email 395044952@qq.com
 */
@Entity
public class IMessage implements Serializable{
    public static final long serialVersionUID = 1L;
    /** 消息id */
    @Id(autoincrement = true)
    public Long id;
    /** 消息id */
    @Property
    @NotNull
    @Unique
    public String msgId;
    /** 会话id */
    @Property
    @NotNull
    public Long conversationId;
    /** 消息内容 */
    @Property
    @NotNull
    public String content;
    /** 聊天对象 */
    @Property
    @NotNull
    public String talker;
    /**聊天对象的url**/
    @Transient
    public String face_url;
    /** 消息创建者 */
    @Property
    @NotNull
    public String sender;
    /** 消息创建者 */
    @Property
    @NotNull
    public String sender_name;
    /** 消息类型 */
    @Property
    @NotNull
    public int msgType;
    /** 发送类型 0:表示接收 1:表示发送 */
    @Property
    @NotNull
    public int isSend;
    /** 消息状态 */
    @Property
    @NotNull
    public int status;
    /** 创建时间 */
    @Property
    @NotNull
    public long create_time;
    /** 发送时间 */
    @Property
    @NotNull
    public long send_time;
    /** 是否读取 */
    @Property
    @NotNull
    public boolean isRead;
    /** 文件路径 */
    @Property
    public String fileUrl;
    /** 文件本地路径 */
    @Property
    public String localPath;
    /** 文件名称 **/
    @Property
    public String fileName;
    /** 语音时间 */
    @Property
    public int duration;
    /** 经度 */
    @Property
    public double latitude;
    /** 维度 */
    @Property
    public double longitude;
    /** 图片宽 */
    @Property
    public int imgWidth;
    /** 图片高 */
    @Property
    public int imgHigh;
    // 图片接收状态
    @Property
    public int imageStatus;
    @Transient
    public int imageProgress;

    @Generated(hash = 2109885016)
    public IMessage(Long id, @NotNull String msgId, @NotNull Long conversationId,
            @NotNull String content, @NotNull String talker,
            @NotNull String sender, @NotNull String sender_name, int msgType,
            int isSend, int status, long create_time, long send_time,
            boolean isRead, String fileUrl, String localPath, String fileName,
            int duration, double latitude, double longitude, int imgWidth,
            int imgHigh, int imageStatus) {
        this.id = id;
        this.msgId = msgId;
        this.conversationId = conversationId;
        this.content = content;
        this.talker = talker;
        this.sender = sender;
        this.sender_name = sender_name;
        this.msgType = msgType;
        this.isSend = isSend;
        this.status = status;
        this.create_time = create_time;
        this.send_time = send_time;
        this.isRead = isRead;
        this.fileUrl = fileUrl;
        this.localPath = localPath;
        this.fileName = fileName;
        this.duration = duration;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgWidth = imgWidth;
        this.imgHigh = imgHigh;
        this.imageStatus = imageStatus;
    }

    @Generated(hash = 26802981)
    public IMessage() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Long getConversationId() {
        return this.conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTalker() {
        return this.talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender_name() {
        return this.sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getIsSend() {
        return this.isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getSend_time() {
        return this.send_time;
    }

    public void setSend_time(long send_time) {
        this.send_time = send_time;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getImgWidth() {
        return this.imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHigh() {
        return this.imgHigh;
    }

    public void setImgHigh(int imgHigh) {
        this.imgHigh = imgHigh;
    }

    public int getImageStatus() {
        return this.imageStatus;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }

    public int getImageProgress() {
        return this.imageProgress;
    }

    public void setImageProgress(int imageProgress) {
        this.imageProgress = imageProgress;
    }


    



    /** 消息类型 **/
    public class MessageType {
        /** 文字消息 **/
        public static final int TEXT = 0;
        /** 图片 **/
        public static final int IMG = 1;
        /** 文件 **/
        public static final int FILE = 2;
        /** 音频 **/
        public static final int VOICE = 3;
        /** 位置 **/
        public static final int LOCATION = 4;
        /** 电话 **/
        public static final int VOIP = 5;
        /** 红包 **/
        public static final int RED_PKT = 6;
    }

    public class MessageIsSend {
        /** 接收到的消息 **/
        public static final int RECEIVING = 0;
        /** 发送的消息 **/
        public static final int SEND = 1;
    }

    public class MessageStatus {
        /** 等待发送 **/
        public static final int WAITING_FOR_SEND = 1;
        /** 正在发送 **/
        public static final int SENDING = 2;
        /** 等待接收 **/
        public static final int WAITING_FOR_RECEIVE = 3;
        /** 正在接收 **/
        public static final int RECEIVING = 4;
        /** 已取消 **/
        public static final int CANCELED = 5;
        /** 已接收 **/
        public static final int RECEIVED = 6;
        /** 未下载 **/
        public static final int NO_RECEIVED = 7;
        /** 已发送 **/
        public static final int SENT = 8;
        /** 已失败 **/
        public static final int FAILED = 9;
    }

    /**
     * 图片接收状态
     */
    public class ImageStatus {
        /** 接收成功 **/
        public static final int RECEIVING_SUCCESS = 0;
        /** 正在接收 */
        public static final int RECEIVING = 1;
        /** 正在发送 */
        public static final int SEND = 2;
        /** 发送失败 */
        public static final int SEND_FAILED = 3;
        /** 接收失败 */
        public static final int RECEIVING_FAILED = 4;
        /** 发送成功 **/
        public static final int SEND_SUCCESS = 5;
    }
}
