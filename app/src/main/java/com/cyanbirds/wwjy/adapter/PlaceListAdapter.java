package com.cyanbirds.wwjy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.cyanbirds.wwjy.R;

import java.util.List;

public class PlaceListAdapter extends
		RecyclerView.Adapter<PlaceListAdapter.ViewHolder> implements
		OnClickListener {

	private int mSelId;
	private List<PoiItem> mPoiItems;
	private RecyclerView mRecyclerView;

	public PlaceListAdapter(List<PoiItem> poiItems, int selId,
							RecyclerView recyclerView) {
		this.mPoiItems = poiItems;
		this.mSelId = selId;
		this.mRecyclerView = recyclerView;
	}

	@Override
	public int getItemCount() {
		return mPoiItems == null ? 0 : mPoiItems.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PoiItem poiItem = mPoiItems.get(position);
		holder.place_name.setText(poiItem.getTitle());
		holder.location.setText(poiItem.getSnippet());
		holder.album_checkmark.setVisibility(View.GONE);
		if (position == mSelId) {
			holder.album_checkmark.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_place, parent, false);
		view.setOnClickListener(this);
		return new ViewHolder(view);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		ImageView album_checkmark;
		TextView place_name;
		TextView location;

		public ViewHolder(View itemView) {
			super(itemView);
			album_checkmark = (ImageView) itemView
					.findViewById(R.id.album_checkmark);
			place_name = (TextView) itemView.findViewById(R.id.place_name);
			location = (TextView) itemView.findViewById(R.id.location);
		}
	}

	@Override
	public void onClick(View v) {
		int position = mRecyclerView.getChildAdapterPosition(v);
		onClick(position);
	}

	public void onClick(int position) {

	}

	public void notifyDataSetChanged(int selId) {
		this.mSelId = selId;
		notifyDataSetChanged();
	}

	public void notifyItemChanged(int oldId, int position) {
		this.mSelId = position;
		notifyDataSetChanged(oldId);
		notifyDataSetChanged(position);
	}

}
