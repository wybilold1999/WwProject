package com.cyanbirds.wwjy.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyanbirds.wwjy.CSApplication;
import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.LocationDetailActivity;
import com.cyanbirds.wwjy.activity.PersonalInfoActivity;
import com.cyanbirds.wwjy.activity.PhotoViewActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.db.IMessageDaoManager;
import com.cyanbirds.wwjy.entity.Conversation;
import com.cyanbirds.wwjy.entity.IMessage;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.net.request.DownloadImageRequest;
import com.cyanbirds.wwjy.ui.widget.CircularProgress;
import com.cyanbirds.wwjy.utils.DensityUtil;
import com.cyanbirds.wwjy.utils.EmoticonUtil;
import com.cyanbirds.wwjy.utils.FileAccessorUtils;
import com.cyanbirds.wwjy.utils.ImageUtil;
import com.cyanbirds.wwjy.utils.LinkUtil;
import com.cyanbirds.wwjy.utils.Md5Util;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wangyb
 * @ClassName:ChatMessageAdapter
 * @Description:聊天消息
 * @Date:2015年5月27日下午9:41:17
 */
public class ChatMessageAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static List<IMessage> mIMessages;
    private static Conversation mConversation;
    /**
     * 需要显示时间的Item position
     */
    private ArrayList<String> mShowTimePosition;
    private static Context mContext;
    private String redPkt[] = null;//红包数据结构:祝福语;金额

    public ChatMessageAdapter(Context context, List<IMessage> messages, Conversation mConversation) {
        mContext = context;
        mIMessages = messages;
        mShowTimePosition = new ArrayList<String>();
        this.mConversation = mConversation;
    }

    @Override
    public int getItemCount() {
        return mIMessages == null ? 0 : mIMessages.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            final IMessage message = mIMessages.get(position);
            // 判断是否显示时间
            boolean showTimer = isShowTime(position, message);

            if (message.msgType == IMessage.MessageType.TEXT) {
                // 普通消息
                TextViewHolder textHolder = (TextViewHolder) holder;
                if (!TextUtils.isEmpty(message.content)) {
                    String content = message.content
                            .replaceAll("<", "&lt;")
                            .replaceAll(">", "&gt;").replace("\n", "<br/>");
                    Set<String> urls = LinkUtil.getTextUrls(content);
                    content = EmoticonUtil.convertExpression(content);
                    content = LinkUtil.generateLink(content, urls);
                    textHolder.message_text.setText(Html.fromHtml(content,
                            EmoticonUtil.chat_imageGetter_resource, null));
                }

                textHolder.nickname.setVisibility(View.GONE);
                textHolder.message_send_fail.setVisibility(View.GONE);
                textHolder.progress_bar.setVisibility(View.GONE);
                if (message.isSend == IMessage.MessageIsSend.RECEIVING) {
                    textHolder.message_text
                            .setBackgroundResource(R.drawable.left_bubble_selector);
                    textHolder.message_text.setTextColor(Color.BLACK);

                    if(null != mConversation && !TextUtils.isEmpty(mConversation.localPortrait)){
                        if (mConversation.localPortrait.startsWith("res")) {
                            textHolder.portrait.setImageURI(Uri.parse(mConversation.localPortrait));
                        } else {
                            textHolder.portrait.setImageURI(Uri.parse("file://" + mConversation.localPortrait));
                        }
                    } else if (null != mConversation){
                        textHolder.portrait.setImageURI(Uri.parse(mConversation.faceUrl));
                    }

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textHolder.portrait
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                            RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    textHolder.portrait.setLayoutParams(lp);

                    lp = (RelativeLayout.LayoutParams) textHolder.message_content
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.RIGHT_OF, R.id.portrait);
                    lp.addRule(RelativeLayout.LEFT_OF, 0);
                    textHolder.message_content.setLayoutParams(lp);
                } else if (message.isSend == IMessage.MessageIsSend.SEND) {
                    if (message.status == IMessage.MessageStatus.FAILED) {
                        textHolder.message_send_fail
                                .setVisibility(View.VISIBLE);
                    } else if (message.status == IMessage.MessageStatus.SENDING) {
                        textHolder.progress_bar.setVisibility(View.VISIBLE);
                    } else if(message.status == IMessage.MessageStatus.SENT){
                        textHolder.progress_bar.setVisibility(View.GONE);
                    }
                    textHolder.message_text
                            .setBackgroundResource(R.drawable.right_bubble_selector);
                    textHolder.message_text.setTextColor(Color.WHITE);

                    if(!TextUtils.isEmpty(AppManager.getClientUser().face_local)){
                        textHolder.portrait.setImageURI(Uri.parse("file://"
                                + AppManager.getClientUser().face_local));
                    } else {
                        textHolder.portrait.setImageURI(Uri.parse(
                                AppManager.getClientUser().face_url));
                    }
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textHolder.portrait
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
                            RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    textHolder.portrait.setLayoutParams(lp);

                    lp = (RelativeLayout.LayoutParams) textHolder.message_content
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.LEFT_OF, R.id.portrait);
                    lp.addRule(RelativeLayout.RIGHT_OF, 0);
                    textHolder.message_content.setLayoutParams(lp);
                }
                // 设置显示消息时间
                setChatTime(textHolder.chat_time, message.send_time,
                        showTimer);
            } else if (message.msgType == IMessage.MessageType.IMG) {
                final ImageViewHolder imageHolder = (ImageViewHolder) holder;
                imageHolder.message_send_fail.setVisibility(View.GONE);
                imageHolder.nickname.setVisibility(View.GONE);
                imageHolder.progress_value.setVisibility(View.GONE);
                imageHolder.message_receiving_fail.setVisibility(View.GONE);
                if (message.imageStatus == IMessage.ImageStatus.RECEIVING && message.imageProgress < 100) {
                    imageHolder.progress_value.setText(message.imageProgress
                            + "%");
                    imageHolder.progress_value.setVisibility(View.VISIBLE);
                }

                if (message.isSend == IMessage.MessageIsSend.RECEIVING) {
                    if (message.status == IMessage.MessageStatus.FAILED) {
                        imageHolder.message_receiving_fail
                                .setVisibility(View.VISIBLE);
                    }
                    if (message.imgWidth > 0 && message.imgHigh > 0) {
                        setImageShowWH(message.imgWidth, message.imgHigh,
                                imageHolder.message_img);
                    } else {
                        setImageShowWH(DensityUtil.dip2px(mContext, 120),
                                DensityUtil.dip2px(mContext, 120),
                                imageHolder.message_img);
                    }
                    imageHolder.message_img
                            .setImageResource(R.drawable.default_photo_background);
                    if (!TextUtils.isEmpty(message.localPath)
                            && new File(message.localPath).exists()){
                        if (message.imageStatus != IMessage.ImageStatus.RECEIVING) {
                            BitmapFactory.Options options = ImageUtil
                                    .getBitmapOptions(new File(
                                            message.localPath)
                                            .getAbsolutePath());
                            if (message.imgWidth <= 0 || message.imgHigh <= 0) {
                                message.imgWidth = options.outWidth;
                                message.imgHigh = options.outHeight;
                                IMessageDaoManager.getInstance(mContext).updateIMessage(message);
                            }
                            setImageShowWH(message.imgWidth, message.imgHigh, imageHolder.message_img);
                            imageHolder.message_img.setImageURI(Uri.parse("file://" + message.localPath));
                        }
                    } else {
                        if (!TextUtils.isEmpty(message.fileUrl)) {
                            String savePath = FileAccessorUtils
                                    .getImagePathName().getAbsolutePath();
                            String fileName = Md5Util.md5(message.fileUrl) + ".jpg";
                            if (message.imageStatus != IMessage.ImageStatus.RECEIVING) {
                                message.imageStatus = IMessage.ImageStatus.RECEIVING;
                                imageHolder.progress_value.setVisibility(View.VISIBLE);
                                imageHolder.progress_value.setText("0%");
                                new DownloadImageRequest().request(message.fileUrl, savePath, fileName, message);
                            }
                        }
                    }
                    if(null != mConversation && !TextUtils.isEmpty(mConversation.localPortrait)){
                        if (mConversation.localPortrait.startsWith("res")) {
                            imageHolder.portrait.setImageURI(Uri.parse(mConversation.localPortrait));
                        } else {
                            imageHolder.portrait.setImageURI(Uri.parse("file://" + mConversation.localPortrait));
                        }
                    }  else if (null != mConversation){
                        imageHolder.portrait.setImageURI(Uri.parse(mConversation.faceUrl));
                    }

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageHolder.portrait
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                            RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    imageHolder.portrait.setLayoutParams(lp);

                    lp = (RelativeLayout.LayoutParams) imageHolder.message_content
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.RIGHT_OF, R.id.portrait);
                    lp.addRule(RelativeLayout.LEFT_OF, 0);
                    imageHolder.message_content.setLayoutParams(lp);
                } else if (message.isSend == IMessage.MessageIsSend.SEND) {
                    if (message.status == IMessage.MessageStatus.FAILED) {
                        imageHolder.message_send_fail
                                .setVisibility(View.VISIBLE);
                    }
                        setImageShowWH(message.imgWidth, message.imgHigh,
                                imageHolder.message_img);
                    if (!TextUtils.isEmpty(message.localPath)) {
                        imageHolder.message_img.setImageURI(Uri.parse("file://" + message.localPath));
                    }
                    if (message.imageProgress < 100) {
                        imageHolder.progress_value.setVisibility(View.VISIBLE);
                        imageHolder.progress_value.setText(String.valueOf(message.imageProgress) + "%");
                    } else {
                        imageHolder.progress_value.setVisibility(View.GONE);
                    }

                    if(!TextUtils.isEmpty(AppManager.getClientUser().face_local)){
                        imageHolder.portrait.setImageURI(Uri.parse("file://"
                                + AppManager.getClientUser().face_local));
                    } else {
                        imageHolder.portrait.setImageURI(Uri.parse(
                                AppManager.getClientUser().face_url));
                    }
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageHolder.portrait
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
                            RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    imageHolder.portrait.setLayoutParams(lp);

                    lp = (RelativeLayout.LayoutParams) imageHolder.message_content
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.LEFT_OF, R.id.portrait);
                    lp.addRule(RelativeLayout.RIGHT_OF, 0);
                    imageHolder.message_content.setLayoutParams(lp);
                }
                // 设置显示消息时间
                setChatTime(imageHolder.chat_time, message.send_time, showTimer);
            } else if (message.msgType == IMessage.MessageType.LOCATION) {
                LocationViewHolder locationHolder = (LocationViewHolder) holder;
                locationHolder.nickname.setVisibility(View.GONE);
                locationHolder.message_send_fail.setVisibility(View.GONE);
                if (message.isSend == IMessage.MessageIsSend.RECEIVING) {

                    if(null != mConversation && !TextUtils.isEmpty(mConversation.localPortrait)){
                        if (mConversation.localPortrait.startsWith("res")) {
                            locationHolder.portrait.setImageURI(Uri.parse(mConversation.localPortrait));
                        } else {
                            locationHolder.portrait.setImageURI(Uri.parse("file://" + mConversation.localPortrait));
                        }
                    } else if (null != mConversation){
                        locationHolder.portrait.setImageURI(Uri.parse(mConversation.faceUrl));
                    }

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) locationHolder.portrait
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                            RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    locationHolder.portrait.setLayoutParams(lp);

                    lp = (RelativeLayout.LayoutParams) locationHolder.message_content
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.RIGHT_OF, R.id.portrait);
                    lp.addRule(RelativeLayout.LEFT_OF, 0);
                    locationHolder.message_content.setLayoutParams(lp);
                } else if (message.isSend == IMessage.MessageIsSend.SEND) {
                    if (message.status == IMessage.MessageStatus.FAILED) {
                        locationHolder.message_send_fail
                                .setVisibility(View.VISIBLE);
                    }

                    if(!TextUtils.isEmpty(AppManager.getClientUser().face_local)){
                        locationHolder.portrait.setImageURI(Uri.parse("file://"
                                + AppManager.getClientUser().face_local));
                    } else {
                        locationHolder.portrait.setImageURI(Uri.parse(
                                AppManager.getClientUser().face_url));
                    }
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) locationHolder.portrait
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
                            RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    locationHolder.portrait.setLayoutParams(lp);

                    lp = (RelativeLayout.LayoutParams) locationHolder.message_content
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.LEFT_OF, R.id.portrait);
                    lp.addRule(RelativeLayout.RIGHT_OF, 0);
                    locationHolder.message_content.setLayoutParams(lp);
                }

                int w = (int) holder.itemView.getResources().getDimension(
                        R.dimen.location_img_w);
                int h = (int) holder.itemView.getResources().getDimension(
                        R.dimen.location_img_h);
                String url = "http://restapi.amap.com/v3/staticmap?location="
                        + message.longitude + "," + message.latitude
                        + "&zoom=17&size=" + w + "*" + h
                        + "&key=c80cd60ac718f6953c32ed0d31bd9283";
                locationHolder.location_img.setImageURI(Uri.parse(url));
                locationHolder.location_info.setText(message.content);
                setChatTime(locationHolder.chat_time, message.send_time,
                        showTimer);
            } else if (message.msgType == IMessage.MessageType.VOIP) {
                // 普通消息
                VoipViewHolder voipViewHolder = (VoipViewHolder) holder;
                if (!TextUtils.isEmpty(message.content)) {
                    voipViewHolder.message_text.setText(message.content);
                }

                voipViewHolder.nickname.setVisibility(View.GONE);
                voipViewHolder.message_send_fail.setVisibility(View.GONE);
                voipViewHolder.progress_bar.setVisibility(View.GONE);
                if (message.isSend == IMessage.MessageIsSend.RECEIVING) {
                    voipViewHolder.message_text
                            .setBackgroundResource(R.drawable.left_bubble_selector);
                    voipViewHolder.message_text.setTextColor(Color.BLACK);

                    if(null != mConversation && !TextUtils.isEmpty(mConversation.localPortrait)){
                        if (mConversation.localPortrait.startsWith("res")) {
                            voipViewHolder.portrait.setImageURI(Uri.parse(mConversation.localPortrait));
                        } else {
                            voipViewHolder.portrait.setImageURI(Uri.parse("file://" + mConversation.localPortrait));
                        }
                    } else if (null != mConversation){
                        voipViewHolder.portrait.setImageURI(Uri.parse(mConversation.faceUrl));
                    }

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) voipViewHolder.portrait
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
                            RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    voipViewHolder.portrait.setLayoutParams(lp);

                    lp = (RelativeLayout.LayoutParams) voipViewHolder.message_content
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.RIGHT_OF, R.id.portrait);
                    lp.addRule(RelativeLayout.LEFT_OF, 0);
                    voipViewHolder.message_content.setLayoutParams(lp);
                } else if (message.isSend == IMessage.MessageIsSend.SEND) {
                    if (message.status == IMessage.MessageStatus.FAILED) {
                        voipViewHolder.message_send_fail
                                .setVisibility(View.VISIBLE);
                    } else if (message.status == IMessage.MessageStatus.SENDING) {
                        voipViewHolder.progress_bar.setVisibility(View.VISIBLE);
                    } else if(message.status == IMessage.MessageStatus.SENT){
                        voipViewHolder.progress_bar.setVisibility(View.GONE);
                    }
                    voipViewHolder.message_text
                            .setBackgroundResource(R.drawable.right_bubble_selector);
                    voipViewHolder.message_text.setTextColor(Color.WHITE);

                    if(!TextUtils.isEmpty(AppManager.getClientUser().face_local)){
                        voipViewHolder.portrait.setImageURI(Uri.parse("file://"
                                + AppManager.getClientUser().face_local));
                    } else {
                        voipViewHolder.portrait.setImageURI(Uri.parse(
                                AppManager.getClientUser().face_url));
                    }
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) voipViewHolder.portrait
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
                            RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    voipViewHolder.portrait.setLayoutParams(lp);

                    lp = (RelativeLayout.LayoutParams) voipViewHolder.message_content
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.LEFT_OF, R.id.portrait);
                    lp.addRule(RelativeLayout.RIGHT_OF, 0);
                    voipViewHolder.message_content.setLayoutParams(lp);
                }
                // 设置显示消息时间
                setChatTime(voipViewHolder.chat_time, message.send_time,
                        showTimer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算设置图片显示的宽高
     *
     * @return
     */
    private void setImageShowWH(int img_w, int img_h, View view) {

        int max_img_w = (int) (DensityUtil
                .getWidthInPx(mContext) * 0.35);
        int max_img_h = (int) (DensityUtil.getHeightInPx(mContext) * 0.35);

        int w = img_w - max_img_w;
        int h = img_h - max_img_h;

        int width = 0;
        int height = 0;
        if (w > h) {
            if (img_w > max_img_w) {
                width = max_img_w;
            } else {
                width = img_w;
            }
            height = (int) ((double) width / img_w * img_h);
        } else {
            if (img_h > max_img_h) {
                height = max_img_h;
            } else {
                height = img_h;
            }
            width = (int) ((double) height / img_h * img_w);
        }

        if (max_img_w > 240 && max_img_h > 360) {
            // 图片最小宽高
            float min_width_height = 100;
            if (width <= 100) {
                min_width_height = 200;
            } else if (width <= 200) {
                min_width_height = 300;
            }
            if (min_width_height > width /* || min_width_height > height */) {
                if (width > height) {
                    width = (int) ((double) min_width_height / height * width);
                    height = (int) min_width_height;
                } else {
                    height = (int) ((double) min_width_height / width * height);
                    width = (int) min_width_height;
                }
            }
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
    /**
     * 判断是否显示时间
     *
     * @return
     */
    private boolean isShowTime(int position, IMessage message) {
        boolean showTimer = false;
        if (position == 0) {
            showTimer = true;
        }
        if (position != 0) {
            IMessage messageTime = mIMessages.get(position - 1);
            if (mShowTimePosition.contains(message.id)
                    || (message.create_time - messageTime.create_time >= 180000L)) {
                showTimer = true;
            }
        }
        return showTimer;
    }

    /**
     * 设置显示时间
     */
    private void setChatTime(TextView chat_time, long time, boolean showTimer) {
        /*chat_time.setVisibility(View.GONE);
        if (showTimer) {
            chat_time.setVisibility(View.VISIBLE);
            chat_time.setText(DateUtil.getDateString(time,
                    DateUtil.SHOW_TYPE_CALL_LOG).trim());
        }*/
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case IMessage.MessageType.TEXT:
                return new TextViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_message_text, parent, false));
            case IMessage.MessageType.IMG:
                return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_message_image, parent, false));
            case IMessage.MessageType.LOCATION:
                return new LocationViewHolder(LayoutInflater.from(
                        parent.getContext()).inflate(
                    R.layout.item_chat_message_location, parent, false));
            case IMessage.MessageType.VOIP:
                return new VoipViewHolder(LayoutInflater.from(
                        parent.getContext()).inflate(
                        R.layout.item_chat_message_voip, parent, false));
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mIMessages.get(position).msgType;
    }


    /**
     * 复制聊天
     */
    private static void copyChat(String content) {
        ClipboardManager clipboardManager = (ClipboardManager) CSApplication
                .getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(CSApplication.getInstance()
                .getResources().getString(R.string.chat_message), content);
        clipboardManager.setPrimaryClip(clip);
        ToastUtil.showMessage(R.string.success_copy);
    }


    public class TextViewHolder extends RecyclerView.ViewHolder implements
            OnLongClickListener, OnClickListener {

        RelativeLayout message_lay;
        SimpleDraweeView portrait;
        TextView message_text;
        TextView chat_time;
        LinearLayout message_content;
        TextView nickname;
        ImageView message_send_fail;
        CircularProgress progress_bar;

        public TextViewHolder(View itemView) {
            super(itemView);
            message_lay = (RelativeLayout) itemView
                    .findViewById(R.id.message_lay);
            portrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
            message_text = (TextView) itemView.findViewById(R.id.message_text);
            chat_time = (TextView) itemView.findViewById(R.id.chat_time);
            message_content = (LinearLayout) itemView
                    .findViewById(R.id.message_content);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            message_send_fail = (ImageView) itemView
                    .findViewById(R.id.message_send_fail);
            progress_bar = (CircularProgress) itemView
                    .findViewById(R.id.progress_bar);
            portrait.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            IMessage message = mIMessages.get(position);
            if (message == null) {
                return true;
            }
            switch (v.getId()) {
                case R.id.message_text:
                    break;
            }
            return true;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.message_send_fail:
                    break;
                case R.id.portrait:
                    if(mConversation != null && !"-1".equals(mConversation.talker)){
                        Intent intent = new Intent(mContext, PersonalInfoActivity.class);
                        intent.putExtra(ValueKey.USER_ID, mConversation.talker);
                        mContext.startActivity(intent);
                    }
                    break;
            }
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements
            OnClickListener{

        SimpleDraweeView portrait;
        SimpleDraweeView message_img;
        TextView chat_time;
        LinearLayout message_content;
        TextView nickname;
        TextView progress_value;
        ImageView message_send_fail;
        ImageView message_receiving_fail;

        public ImageViewHolder(View itemView) {
            super(itemView);
            portrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
            message_img = (SimpleDraweeView) itemView
                    .findViewById(R.id.message_img);
            chat_time = (TextView) itemView.findViewById(R.id.chat_time);
            message_content = (LinearLayout) itemView
                    .findViewById(R.id.message_content);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            progress_value = (TextView) itemView
                    .findViewById(R.id.progress_value);
            message_send_fail = (ImageView) itemView
                    .findViewById(R.id.message_send_fail);
            message_receiving_fail = (ImageView) itemView
                    .findViewById(R.id.message_receiving_fail);
            message_img.setOnClickListener(this);
            portrait.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            IMessage message = mIMessages.get(position);
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.message_img:
                    String path = "";
                    if (!TextUtils.isEmpty(message.localPath)
                            && new File(message.localPath).exists()) {
                        path = "file://" + message.localPath;
                    } else {
                        path = message.fileUrl;
                    }
                    intent.setClass(itemView.getContext(), PhotoViewActivity.class);
                    intent.putExtra(ValueKey.IMAGE_URL, path);
                    intent.putExtra(ValueKey.FROM_ACTIVITY, "ChatActivity");
                    itemView.getContext().startActivity(intent);
                    break;
                case R.id.portrait :
                    if(null != mConversation && !"-1".equals(mConversation.talker)){
                        intent.setClass(itemView.getContext(), PersonalInfoActivity.class);
                        intent.putExtra(ValueKey.USER_ID, mConversation.talker);
                        mContext.startActivity(intent);
                    }
                    break;
            }
        }

    }

    public class LocationViewHolder extends RecyclerView.ViewHolder implements
            OnClickListener{

        ImageView location_img;
        TextView location_info;
        ImageView portrait;
        TextView chat_time;
        LinearLayout message_content;
        TextView nickname;
        ImageView message_send_fail;

        public LocationViewHolder(View itemView) {
            super(itemView);
            location_img = (ImageView) itemView.findViewById(R.id.location_img);
            location_info = (TextView) itemView
                    .findViewById(R.id.location_info);
            portrait = (ImageView) itemView.findViewById(R.id.portrait);
            chat_time = (TextView) itemView.findViewById(R.id.chat_time);
            message_content = (LinearLayout) itemView
                    .findViewById(R.id.message_content);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            message_send_fail = (ImageView) itemView
                    .findViewById(R.id.message_send_fail);

            portrait.setOnClickListener(this);
            location_img.setOnClickListener(this);
//            message_send_fail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            IMessage message = mIMessages.get(position);
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.portrait:
                    if(null != mConversation && !"-1".equals(mConversation.talker)){
                        intent.setClass(itemView.getContext(), PersonalInfoActivity.class);
                        intent.putExtra(ValueKey.USER_ID, mConversation.talker);
                        mContext.startActivity(intent);
                    }
                    break;
                case R.id.location_img:
                    intent.setClass(mContext, LocationDetailActivity.class);
                    intent.putExtra(ValueKey.LATITUDE, message.latitude);
                    intent.putExtra(ValueKey.LONGITUDE, message.longitude);
                    intent.putExtra(ValueKey.ADDRESS, message.content);
                    mContext.startActivity(intent);
                    break;
                case R.id.message_send_fail:
                    break;
            }
        }
    }

    public class VoipViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        RelativeLayout message_lay;
        SimpleDraweeView portrait;
        TextView message_text;
        TextView chat_time;
        LinearLayout message_content;
        TextView nickname;
        ImageView message_send_fail;
        CircularProgress progress_bar;

        public VoipViewHolder(View itemView) {
            super(itemView);
            message_lay = (RelativeLayout) itemView
                    .findViewById(R.id.message_lay);
            portrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
            message_text = (TextView) itemView.findViewById(R.id.message_text);
            chat_time = (TextView) itemView.findViewById(R.id.chat_time);
            message_content = (LinearLayout) itemView
                    .findViewById(R.id.message_content);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            message_send_fail = (ImageView) itemView
                    .findViewById(R.id.message_send_fail);
            progress_bar = (CircularProgress) itemView
                    .findViewById(R.id.progress_bar);
            portrait.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.message_send_fail:
                    break;
                case R.id.portrait:
                    if(null != mConversation && !"-1".equals(mConversation.talker)){
                        Intent intent = new Intent(mContext, PersonalInfoActivity.class);
                        intent.putExtra(ValueKey.USER_ID, mConversation.talker);
                        mContext.startActivity(intent);
                    }
                    break;
            }
        }
    }

    public void onDestroy() {
        if (mIMessages != null) {
            mIMessages.clear();
            mIMessages = null;
        }
        if (mShowTimePosition != null) {
            mShowTimePosition.clear();
            mShowTimePosition = null;
        }
    }
}
