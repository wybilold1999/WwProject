package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.utils.PreferencesUtils;
import com.cyanbirds.wwjy.utils.StringUtil;
import com.dl7.tag.TagLayout;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 */
public class FindLoveAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<ClientUser> mClientUsers;
    private Context mContext;
    private boolean mShowFooter = false;

    private OnItemClickListener mOnItemClickListener;
    private DecimalFormat mFormat;
    private String mCurCity;
    private int mCurIndex;

    public FindLoveAdapter(List<ClientUser> clientUsers, Context mContext, int index) {
        this.mClientUsers = clientUsers;
        this.mContext = mContext;
        mFormat = new DecimalFormat("#.00");
        mCurCity = PreferencesUtils.getCurrentCity(mContext);
        mCurIndex = index;
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
                    .inflate(R.layout.item_find_love, parent, false);
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < 0) {
            return;
        }
        if(holder instanceof ItemViewHolder){
            ClientUser clientUser = mClientUsers.get(position);
            if(clientUser == null){
                return;
            }
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.userName.setText(clientUser.user_name);
            itemViewHolder.age.setText(String.valueOf(clientUser.age));
            if ("1".equals(clientUser.sex)) {
                itemViewHolder.mSexImg.setImageResource(R.mipmap.list_male);
            } else {
                itemViewHolder.mSexImg.setImageResource(R.mipmap.list_female);
            }
            itemViewHolder.marrayState.setText(clientUser.state_marry);
            itemViewHolder.constellation.setText(clientUser.constellation);
            if (!TextUtils.isEmpty(mCurCity) && mCurIndex == 1) {
                itemViewHolder.distance.setText("来自" + mCurCity);
            } else if (null == clientUser.distance || Double.parseDouble(clientUser.distance) == 0.0) {
                itemViewHolder.distance.setText("来自" + clientUser.city);
            } else {
                itemViewHolder.distance.setText(mFormat.format(Double.parseDouble(clientUser.distance)) + " km");
            }
            if (mCurIndex == 2 && !TextUtils.isEmpty(mCurCity) && !TextUtils.isEmpty(clientUser.distance)
                    && Double.parseDouble(clientUser.distance) > 0.0) {
                itemViewHolder.distance.setText("来自" + mCurCity);
            }
            itemViewHolder.signature.setText(clientUser.signature);
            if(clientUser.is_vip && AppManager.getClientUser().isShowVip){
                itemViewHolder.isVip.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.isVip.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(clientUser.face_url)) {
                itemViewHolder.portrait.setImageURI(Uri.parse(clientUser.face_url));
            } else {
                itemViewHolder.portrait.setImageURI(Uri.parse("res:///" + R.mipmap.default_head));
            }
            if (!TextUtils.isEmpty(clientUser.personality_tag)) {
                List<String> tags = StringUtil.stringToIntList(clientUser.personality_tag);
                List<String> pTags = new ArrayList<>(3);
                for (int i = 0; i < tags.size(); i++) {
                    if ("".equals(tags.get(i)) || " ".equals(tags.get(i))) {
                        tags.remove(i);
                    } else {
                        pTags.add(tags.get(i));
                        if (pTags.size() == 3) {
                            break;
                        }
                    }
                }
                itemViewHolder.tag_layout.setTags(pTags);
            }
        }
    }

    @Override
    public int getItemCount() {
        int begin = mShowFooter?1:0;
        if(mClientUsers == null) {
            return begin;
        }
        return mClientUsers.size() + begin;
    }

    public ClientUser getItem(int position){
        if (mClientUsers == null || mClientUsers.size() < 1 || position < 0) {
            return null;
        }
        return mClientUsers == null ? null : mClientUsers.get(position);
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        SimpleDraweeView portrait;
        TextView userName;
        ImageView isVip;
        TextView age;
        TextView marrayState;
        TextView constellation;
        TextView distance;
        TextView signature;
        ImageView mSexImg;
        TagLayout tag_layout;
        public ItemViewHolder(View itemView) {
            super(itemView);
            portrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            isVip = (ImageView) itemView.findViewById(R.id.is_vip);
            age = (TextView) itemView.findViewById(R.id.age);
            marrayState = (TextView) itemView.findViewById(R.id.marray_state);
            constellation = (TextView) itemView.findViewById(R.id.constellation);
            distance = (TextView) itemView.findViewById(R.id.distance);
            signature = (TextView) itemView.findViewById(R.id.signature);
            mSexImg = (ImageView) itemView.findViewById(R.id.sex_img);
            tag_layout = (TagLayout) itemView.findViewById(R.id.tag_layout);
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

    public void setIsShowFooter(boolean showFooter) {
        this.mShowFooter = showFooter;
    }

    public boolean isShowFooter() {
        return this.mShowFooter;
    }

    public void setClientUsers(List<ClientUser> users){
        this.mClientUsers = users;
        this.notifyDataSetChanged();
    }
}
