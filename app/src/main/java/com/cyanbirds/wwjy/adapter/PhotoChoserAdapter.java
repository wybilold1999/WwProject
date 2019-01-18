package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.PhotoViewActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.entity.ImageBean;
import com.cyanbirds.wwjy.listener.ChoseImageListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;


/**
 * 
 * @Description:图片选择
 * @author wangyb
 * @Date:2015年7月26日上午11:53:17
 */
public class PhotoChoserAdapter extends
		RecyclerView.Adapter<PhotoChoserAdapter.ViewHolder> {

	private List<ImageBean> mBeans;
	private ChoseImageListener mChoseImageListener;
	private OnClickCameraListener mCameraListener;
	private Context mContext;

	public PhotoChoserAdapter(Context context, List<ImageBean> beans) {
		mContext = context;
		mBeans = beans;
	}

	@Override
	public int getItemCount() {
		return mBeans == null ? 0 : mBeans.size();
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final ImageBean imageBean = mBeans.get(position);
		Uri uri = Uri.parse("file://" + imageBean.getPath());
		int width = 200, height = 200;
		ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
				.setResizeOptions(new ResizeOptions(width, height))
				.build();
		PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
				.setOldController(holder.img_queue.getController())
				.setImageRequest(request)
				.build();
		holder.img_queue.setController(controller);

		holder.v_selected_frame
				.setBackgroundResource(imageBean.isSeleted() ? R.color.image_selected_color
						: android.R.color.transparent);

		holder.select_photo.setChecked(imageBean.isSeleted());
		holder.select_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mChoseImageListener == null) {
					return;
				}
				if(imageBean.isSeleted()){
					mChoseImageListener.onCancelSelect(imageBean);
				}else{
					mChoseImageListener.onSelected(imageBean);
				}
				holder.select_photo.setChecked(imageBean.isSeleted());
				holder.v_selected_frame
						.setBackgroundResource(imageBean.isSeleted() ? R.color.image_selected_color
								: android.R.color.transparent);

			}
		});
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_photo_choser, parent, false));
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements
			OnClickListener {

		SimpleDraweeView img_queue;
		CheckBox select_photo;
		View v_selected_frame;

		public ViewHolder(View itemView) {
			super(itemView);
			img_queue = (SimpleDraweeView) itemView.findViewById(R.id.img_queue);
			select_photo = (CheckBox) itemView.findViewById(R.id.select_photo);
			v_selected_frame = itemView.findViewById(R.id.v_selected_frame);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			int position = getAdapterPosition();
			ImageBean ib=mBeans.get(position);
			Intent intent=new Intent(v.getContext(),  PhotoViewActivity.class);
			intent.putExtra(ValueKey.IMAGE_URL, "file://"+ib.getPath());
			intent.putExtra(ValueKey.FROM_ACTIVITY, "PhotoChoserActivity");
			v.getContext().startActivity(intent);
		}
	}

	/**
	 * 设置图片监听
	 * @param listener
	 */
	public void setChoseImageListener(ChoseImageListener listener) {
		this.mChoseImageListener = listener;
	}
	
	public void setCameraListener(OnClickCameraListener listener){
		this.mCameraListener=listener;
	}
	
	/**
	 * 打开相机监听
	 */
	public interface OnClickCameraListener{
		 public void openCamera();
	}

}
