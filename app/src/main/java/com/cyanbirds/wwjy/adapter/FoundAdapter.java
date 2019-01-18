package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.PersonalInfoActivity;
import com.cyanbirds.wwjy.activity.PhotoViewActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.entity.PictureModel;
import com.cyanbirds.wwjy.utils.PreferencesUtils;

import java.text.DecimalFormat;
import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 17:44 GMT+8
 * @email 395044952@qq.com
 */
public class FoundAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private boolean mShowFooter = false;

    private List<PictureModel> pictureModels;
    private Context mContext;
    private DecimalFormat mFormat;
    private String mCurCity;

    public FoundAdapter(List<PictureModel> pics, Context context) {
        this.pictureModels = pics;
        mContext = context;
        mFormat = new DecimalFormat("#.00");
        mCurCity = PreferencesUtils.getCurrentCity(context);
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
    public int getItemCount() {
        int begin = mShowFooter?1:0;
        if(pictureModels == null) {
            return begin;
        }
        return pictureModels.size() + begin;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            PictureModel model = pictureModels.get(position);
            if(model == null){
                return;
            }
            viewHolder.portrait.setImageURI(Uri.parse(model.faceUrl));
            viewHolder.mUserName.setText(model.nickname);
            if (!TextUtils.isEmpty(mCurCity) && null != model.distance && model.distance != 0.00) {
                viewHolder.mFromCity.setVisibility(View.VISIBLE);
                viewHolder.mDistanceLayout.setVisibility(View.GONE);
                viewHolder.mFromCity.setText("来自" + mCurCity);
            } else if (null == model.distance || model.distance == 0.00) {
                viewHolder.mFromCity.setVisibility(View.VISIBLE);
                viewHolder.mDistanceLayout.setVisibility(View.GONE);
                viewHolder.mFromCity.setText("来自" + model.city);
            } else {
                viewHolder.mDistanceLayout.setVisibility(View.VISIBLE);
                viewHolder.mFromCity.setVisibility(View.GONE);
                viewHolder.mDistance.setText(mFormat.format(model.distance) + " km");
            }
            viewHolder.imgQueue.setImageURI(Uri.parse(model.path));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_found, parent, false);
            ItemViewHolder vh = new ItemViewHolder(v);
            return vh;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imgQueue;
        ImageView portrait;
        TextView mDistance;
        TextView mUserName;
        TextView mFromCity;
        LinearLayout cardView;
        RelativeLayout mDistanceLayout;
        public ItemViewHolder(View itemView) {
            super(itemView);
            imgQueue = (ImageView) itemView.findViewById(R.id.img_queue);
            portrait = (ImageView) itemView.findViewById(R.id.portrait);
            mDistance = (TextView) itemView.findViewById(R.id.distance);
            mUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            mFromCity = (TextView) itemView.findViewById(R.id.from_city);
            cardView = (LinearLayout) itemView.findViewById(R.id.cardview);
            mDistanceLayout = (RelativeLayout) itemView.findViewById(R.id.distance_layout);
            imgQueue.setOnClickListener(this);
            portrait.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position < 0) {
                return;
            }
            Intent intent = new Intent();
            PictureModel model = pictureModels.get(position);
            switch (v.getId()){
                case R.id.portrait :
                    intent.setClass(mContext, PersonalInfoActivity.class);
                    intent.putExtra(ValueKey.USER_ID, String.valueOf(model.usersId));
                    mContext.startActivity(intent);
                    break;
                case R.id.img_queue :
                    intent.setClass(mContext, PhotoViewActivity.class);
                    intent.putExtra(ValueKey.IMAGE_URL, model.path);
                    intent.putExtra(ValueKey.FROM_ACTIVITY, this.getClass().getSimpleName());
                    mContext.startActivity(intent);
                    break;
            }
        }
    }

    public void setIsShowFooter(boolean showFooter) {
        this.mShowFooter = showFooter;
    }

    public boolean isShowFooter() {
        return this.mShowFooter;
    }

    public void setPictureModels(List<PictureModel> pictureModels){
        this.pictureModels = pictureModels;
        this.notifyDataSetChanged();
    }
}
