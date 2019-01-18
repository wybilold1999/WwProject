package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.ViewPagerPhotoViewActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.DynamicContent;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.ui.widget.MultiImageView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;


/**
 * @author: wangyb
 * @datetime: 2016-01-02 16:51 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class TabDynamicAdapter extends
        RecyclerView.Adapter<TabDynamicAdapter.ViewHolder>{

    private List<DynamicContent.DataBean> data;
    private Context mContext;

    public TabDynamicAdapter(Context mContext, List<DynamicContent.DataBean> mContents) {
        this.mContext = mContext;
        this.data = mContents;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DynamicContent.DataBean content = data.get(position);
        if(content != null){
            holder.dynamic_text_content.setText(content.getContent());
//            holder.mPublishDate.setText(DateUtil.getDateString(Long.parseLong(content.getCreateTime()), DateUtil.SHOW_TYPE_CALL_LOG));
            Uri uri = null;
            if (String.valueOf(content.getUsersId()).equals(AppManager.getClientUser().userId)) {
                uri = Uri.parse("file://" + AppManager.getClientUser().face_local);
            } else {
                if(!TextUtils.isEmpty(content.getFaceUrl())){
                    uri = Uri.parse(content.getFaceUrl());
                } else {
                    uri = Uri.parse("res:///" + R.mipmap.default_head);
                }
            }
            holder.mPortrait.setImageURI(uri);
            if(!TextUtils.isEmpty(content.getNickname())){
                holder.mUserName.setText(content.getNickname());
            }
            if (content.getPictures() != null && !content.getPictures().isEmpty()) {
                final List<String> urls = new ArrayList<>();
                for (DynamicContent.DataBean.PicturesBean picturesBean : content.getPictures()) {
                    urls.add(picturesBean.getPath());
                }
                holder.mDynamicImg.setList(urls);
                holder.mDynamicImg.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(mContext, ViewPagerPhotoViewActivity.class);
                        intent.putStringArrayListExtra(ValueKey.IMAGE_URL,
                                (ArrayList<String>) urls);
                        intent.putExtra(ValueKey.POSITION, position);
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tab_dynamic, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView mPortrait;
        TextView mUserName;
        TextView mPublishDate;
        TextView dynamic_text_content;
        MultiImageView mDynamicImg;
        public ViewHolder(View itemView) {
            super(itemView);
            mPortrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            mPublishDate = (TextView) itemView.findViewById(R.id.publish_date);
            dynamic_text_content = (TextView) itemView.findViewById(R.id.dynamic_text_content);
            mDynamicImg = (MultiImageView) itemView.findViewById(R.id.dynamic_image_content);
        }
    }

    public void setData(List<DynamicContent.DataBean> dataBeen) {
        data = dataBeen;
    }
}
