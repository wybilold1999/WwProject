package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.entity.Emoticon;

import java.util.List;


/**
 * 
 * @ClassName:ChatEmoticonsAdapter
 * @Description:表情
 * @author wangyb
 * @Date:2015年5月27日上午9:52:22
 *
 */
public class ChatEmoticonsAdapter extends ArrayAdapter<Emoticon> implements
		OnItemClickListener {

	private OnEmojiItemClickListener mItemClickListener;

	public ChatEmoticonsAdapter(Context context, List<Emoticon> objects,
								GridView emojiGrid) {
		super(context, 0, objects);
		emojiGrid.setOnItemClickListener(this);
	}

	@Override
	public int getCount() {
		return super.getCount() + 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_chat_emotion, null);
			holder = new ViewHolder();
			holder.emoticons = (ImageView) convertView
					.findViewById(R.id.emoticons);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (getCount() - 1 == position) {
			holder.emoticons.setImageResource(R.mipmap.delete_emoticon_btn);
		} else {
			Emoticon emoticon = getItem(position);
			holder.emoticons.setImageResource(emoticon.reId);
		}

		return convertView;
	}

	private class ViewHolder {
		ImageView emoticons;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (mItemClickListener != null) {
			if (position == getCount() - 1) {
				mItemClickListener.onEmojiDelClick();
				return;
			}
			Emoticon emoticon = getItem(position);
			mItemClickListener.onEmojiItemClick(emoticon);
		}
	}

	/**
	 * 回调监听
	 */
	public interface OnEmojiItemClickListener {

		/**
		 * 
		 * 选择表情回调
		 * 
		 */
		void onEmojiItemClick(Emoticon emoticon);

		/**
		 * 删除回调
		 */
		void onEmojiDelClick();
	}

	/**
	 * 设置监听
	 * 
	 * @param listener
	 */
	public void setOnEmojiItemClickListener(OnEmojiItemClickListener listener) {
		mItemClickListener = listener;
	}
}
