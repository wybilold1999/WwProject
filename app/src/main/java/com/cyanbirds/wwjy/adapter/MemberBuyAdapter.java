package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.entity.MemberBuy;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 */
public class MemberBuyAdapter extends
		RecyclerView.Adapter<MemberBuyAdapter.ViewHolder> {

	private List<MemberBuy> mMemberBuyList;
	private OnItemClickListener mOnItemClickListener;

	public MemberBuyAdapter(Context context, List<MemberBuy> memberBuys) {
		this.mMemberBuyList = memberBuys;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_member_buy, parent, false);
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		MemberBuy memberBuy = mMemberBuyList.get(position);
		if (memberBuy == null) {
			return;
		}
		holder.mDateLimit.setText(memberBuy.months);
		holder.mPrice.setText(memberBuy.price + "元");
		holder.mDesciption.setText(memberBuy.descreption);
	}

	@Override
	public int getItemCount() {
		return mMemberBuyList == null ? 0 : mMemberBuyList.size();
	}


	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView mDateLimit;
		TextView mPrice;
		TextView mDesciption;
		Button mBuy;
		public ViewHolder(View itemView) {
			super(itemView);
			mDateLimit = (TextView) itemView.findViewById(R.id.date_limit);
			mPrice = (TextView) itemView.findViewById(R.id.price);
			mDesciption = (TextView) itemView.findViewById(R.id.desciption);
			mBuy = (Button) itemView.findViewById(R.id.buy);
			mBuy.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.buy:
					int position = getAdapterPosition();
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(v, position);
					}
					break;
			}
		}
	}

	public MemberBuy getItem(int position){
		return mMemberBuyList == null ? null : mMemberBuyList.get(position);
	}

	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}
}
