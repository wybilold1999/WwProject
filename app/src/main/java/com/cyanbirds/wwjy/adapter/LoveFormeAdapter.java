package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.entity.LoveModel;
import com.cyanbirds.wwjy.manager.AppManager;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-01-14 18:47 GMT+8
 * @email 395044952@qq.com
 */
public class LoveFormeAdapter extends RecyclerView.Adapter<LoveFormeAdapter.ViewHolder> {

    private Context mContext;
    private List<LoveModel> mLoveModels;

    private OnItemClickListener mOnItemClickListener;
    public LoveFormeAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_love_forme, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LoveModel loveModel = mLoveModels.get(position);
        if(loveModel == null){
            return;
        }
        holder.portrait.setImageURI(Uri.parse(loveModel.faceUrl));
        holder.age.setText(loveModel.age);
        if ("男".equals(loveModel.sex)) {
            holder.mSexImg.setImageResource(R.mipmap.list_male);
        } else {
            holder.mSexImg.setImageResource(R.mipmap.list_female);
        }
        holder.user_name.setText(loveModel.nickname);
        holder.constellation.setText(loveModel.constellation);
        holder.signature.setText(loveModel.signature);
        if(loveModel.isVip && AppManager.getClientUser().isShowVip){
            holder.mIsVip.setVisibility(View.VISIBLE);
        } else {
            holder.mIsVip.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mLoveModels == null ? 0 : mLoveModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SimpleDraweeView portrait;
        TextView user_name;
        TextView age;
        TextView constellation;
        TextView signature;
        ImageView mIsVip;
        ImageView mSexImg;
        public ViewHolder(View itemView) {
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

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setLoveModels(List<LoveModel> mLoveModels){
        this.mLoveModels = mLoveModels;
        this.notifyDataSetChanged();
    }
}
