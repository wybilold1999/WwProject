package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.GiveVipActivity;
import com.cyanbirds.wwjy.activity.PhotoViewActivity;
import com.cyanbirds.wwjy.activity.VipCenterActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.FollowModel;
import com.cyanbirds.wwjy.manager.AppManager;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-01-14 18:47 GMT+8
 * @email 395044952@qq.com
 */
public class AttentionMeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;
    private List<FollowModel> mFollowModels;
    private List<String> mFaceUrls;//foot里面的头像url
    private boolean mShowFooter = false;

    private OnItemClickListener mOnItemClickListener;
    public AttentionMeAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if(!mShowFooter) {
            return TYPE_ITEM;
        }
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_attention, parent, false);
            ItemViewHolder vh = new ItemViewHolder(v);
            return vh;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer_view_more, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            FollowModel followModel = mFollowModels.get(position);
            if(followModel == null){
                return;
            }
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.portrait.setImageURI(Uri.parse(followModel.faceUrl));
            itemViewHolder.age.setText(followModel.age);
            if ("男".equals(followModel.sex)) {
                itemViewHolder.mSexImg.setImageResource(R.mipmap.list_male);
            } else {
                itemViewHolder.mSexImg.setImageResource(R.mipmap.list_female);
            }
            itemViewHolder.user_name.setText(followModel.nickname);
            itemViewHolder.constellation.setText(followModel.constellation);
            itemViewHolder.signature.setText(followModel.signature);
            if(followModel.isVip && AppManager.getClientUser().isShowVip){
                itemViewHolder.mIsVip.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.mIsVip.setVisibility(View.GONE);
            }
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            if (mFaceUrls != null && mFaceUrls.size() >=3) {
                footerViewHolder.mPortrait1.setImageURI(Uri.parse(mFaceUrls.get(0)));
                footerViewHolder.mPortrait2.setImageURI(Uri.parse(mFaceUrls.get(1)));
                footerViewHolder.mPortrait3.setImageURI(Uri.parse(mFaceUrls.get(2)));
            }
        }
    }

    public FollowModel getItem(int position){
        return mFollowModels == null ? null : mFollowModels.get(position);
    }

    @Override
    public int getItemCount() {
        return mFollowModels == null ? 0 : mFollowModels.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SimpleDraweeView portrait;
        TextView user_name;
        TextView age;
        TextView constellation;
        TextView signature;
        ImageView mIsVip;
        ImageView mSexImg;
        public ItemViewHolder(View itemView) {
            super(itemView);
            portrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            age = (TextView) itemView.findViewById(R.id.age);
            constellation = (TextView) itemView.findViewById(R.id.constellation);
            signature = (TextView) itemView.findViewById(R.id.signature);
            mIsVip = (ImageView) itemView.findViewById(R.id.is_vip);
            mSexImg = (ImageView) itemView.findViewById(R.id.sex_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SimpleDraweeView mPortrait1;
        SimpleDraweeView mPortrait2;
        SimpleDraweeView mPortrait3;
        LinearLayout mFooterLay;
        public FooterViewHolder(View view) {
            super(view);
            mPortrait1 = (SimpleDraweeView) view.findViewById(R.id.portrait_1);
            mPortrait2 = (SimpleDraweeView) view.findViewById(R.id.portrait_2);
            mPortrait3 = (SimpleDraweeView) view.findViewById(R.id.portrait_3);
            mFooterLay = (LinearLayout) view.findViewById(R.id.footer);
            mFooterLay.setOnClickListener(this);
            mPortrait1.setOnClickListener(this);
            mPortrait2.setOnClickListener(this);
            mPortrait3.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, PhotoViewActivity.class);
            intent.putExtra(ValueKey.FROM_ACTIVITY, this.getClass().getSimpleName());
            switch (v.getId()) {
                case R.id.footer :
                    showVipDialog();
                    break;
                case R.id.portrait_1:
                    if (mFaceUrls != null) {
                        intent.putExtra(ValueKey.IMAGE_URL,mFaceUrls.get(0));
                        mContext.startActivity(intent);
                    }
                    break;
                case R.id.portrait_2:
                    if (mFaceUrls != null) {
                        intent.putExtra(ValueKey.IMAGE_URL,mFaceUrls.get(1));
                        mContext.startActivity(intent);
                    }
                    break;
                case R.id.portrait_3:
                    if (mFaceUrls != null) {
                        intent.putExtra(ValueKey.IMAGE_URL,mFaceUrls.get(2));
                        mContext.startActivity(intent);
                    }
                    break;
            }
        }
    }

    private void showVipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.see_more_data);
        builder.setPositiveButton(R.string.ok, ((dialog, i) -> {
            dialog.dismiss();
            Intent intent = new Intent(mContext, VipCenterActivity.class);
            mContext.startActivity(intent);
        }));
        if (AppManager.getClientUser().isShowGiveVip) {
            builder.setNegativeButton(R.string.free_give_vip, ((dialog, i) -> {
                dialog.dismiss();
                Intent intent = new Intent(mContext, GiveVipActivity.class);
                mContext.startActivity(intent);
            }));
        } else {
            builder.setNegativeButton(R.string.until_single, ((dialog, i) -> dialog.dismiss()));
        }
        builder.show();
    }

    public void setIsShowFooter(boolean showFooter) {
        this.mShowFooter = showFooter;
    }

    public boolean isShowFooter() {
        return this.mShowFooter;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setFollowModels(List<FollowModel> mFollowModels){
        this.mFollowModels = mFollowModels;
        this.notifyDataSetChanged();
    }

    public void setFooterFaceUrls(List<String> urls) {
        mFaceUrls = urls;
    }
}
