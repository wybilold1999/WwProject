package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.ChatActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.db.ConversationSqlManager;
import com.cyanbirds.wwjy.db.IMessageDaoManager;
import com.cyanbirds.wwjy.entity.ClientUser;
import com.cyanbirds.wwjy.entity.Conversation;
import com.cyanbirds.wwjy.listener.MessageUnReadListener;
import com.cyanbirds.wwjy.manager.NotificationManagerUtils;
import com.cyanbirds.wwjy.utils.DateUtil;
import com.cyanbirds.wwjy.utils.EmoticonUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 15:24 GMT+8
 * @email 395044952@qq.com
 */
public class MessageAdapter extends
        RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Conversation> mConversations;
    private RecyclerView mRecyclerView;
    private Context mContext;

    public MessageAdapter(Context context, List<Conversation> mConversations) {
        this.mConversations = mConversations;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return mConversations == null ? 0 : mConversations.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Conversation conversation = mConversations.get(position);
        if(!TextUtils.isEmpty(conversation.localPortrait)){
            if (conversation.localPortrait.startsWith("res://")) {//官方头像
                holder.mPortrait.setImageURI(Uri.parse(conversation.localPortrait));
            } else {
                holder.mPortrait.setImageURI(Uri.parse("file://" + conversation.localPortrait));
            }
        } else {
            holder.mPortrait.setImageURI(Uri.parse(conversation.faceUrl));
        }
        holder.mTitle.setText(conversation.talkerName);
        holder.mContent.setText(Html.fromHtml(
                EmoticonUtil.convertExpression(conversation.content==null ? "" : conversation.content),
                EmoticonUtil.conversation_imageGetter_resource, null));

        holder.mUpdateTime.setText(DateUtil.longToString(conversation.createTime));
        holder.mUnreadCount.setVisibility(View.GONE);
        if (conversation.unreadCount != 0) {
            holder.mUnreadCount.setVisibility(View.VISIBLE);
            if (conversation.unreadCount >= 100) {
                holder.mUnreadCount.setText(String.valueOf("99+"));
            } else {
                holder.mUnreadCount.setText(String
                        .valueOf(conversation.unreadCount));
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_message, parent, false));
    }


    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener{
        SimpleDraweeView mPortrait;
        TextView mUnreadCount;
        ImageView mRedPoint;
        TextView mTitle;
        TextView mUpdateTime;
        TextView mContent;
        LinearLayout mItemMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            mPortrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
            mUnreadCount = (TextView) itemView.findViewById(R.id.un_read_number);
            mRedPoint = (ImageView) itemView.findViewById(R.id.red_point);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mUpdateTime = (TextView) itemView.findViewById(R.id.update_time);
            mContent = (TextView) itemView.findViewById(R.id.content);
            mItemMsg = (LinearLayout) itemView.findViewById(R.id.item_msg);
            mItemMsg.setOnClickListener(this);
            mItemMsg.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (mConversations.size() > position && position > -1) {
                Conversation conversation = mConversations.get(position);
                conversation.unreadCount = 0;
                mConversations.set(position, conversation);
                ConversationSqlManager.getInstance(mContext).updateConversation(conversation);
                MessageUnReadListener.getInstance().notifyDataSetChanged(0);

                Intent intent = new Intent(mContext, ChatActivity.class);
                ClientUser clientUser = new ClientUser();
                clientUser.face_local = conversation.localPortrait;
                clientUser.user_name = conversation.talkerName;
                clientUser.userId = conversation.talker;
                clientUser.face_url = conversation.faceUrl;
                intent.putExtra(ValueKey.USER, clientUser);
                mContext.startActivity(intent);
                mUnreadCount.setVisibility(View.GONE);
                mRedPoint.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            final int position = getAdapterPosition();
            new AlertDialog.Builder(mContext)
                    .setItems(
                            new String[] {
                                    mContext.getResources().getString(
                                            R.string.delete_conversation),
                                    mContext.getResources().getString(
                                            R.string.delete_all_conversation)},
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            ConversationSqlManager.getInstance(mContext).deleteConversationById(mConversations.get(position));
                                            IMessageDaoManager.getInstance(mContext).deleteIMessageByConversationId(mConversations.get(position).id);
                                            mConversations.remove(position);
                                            notifyDataSetChanged();
                                            MessageUnReadListener.getInstance().notifyDataSetChanged(0);
                                            break;
                                        case 1:
                                            ConversationSqlManager.getInstance(mContext).deleteAllConversation();
                                            IMessageDaoManager.getInstance(mContext).deleteAllIMessage();
                                            mConversations.clear();
                                            notifyDataSetChanged();
                                            MessageUnReadListener.getInstance().notifyDataSetChanged(0);
                                            NotificationManagerUtils.getInstance().cancelNotification();
                                            break;
                                    }
                                    dialog.dismiss();

                                }
                            }).setTitle("操作").show();
            return true;
        }
    }

    public void setConversations(List<Conversation> conversations){
        this.mConversations = conversations;
    }
}
