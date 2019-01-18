package com.cyanbirds.wwjy.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cyanbirds.wwjy.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-02-19 10:18 GMT+8
 * @email 395044952@qq.com
 */
public class PublishImageAdapter extends RecyclerView.Adapter<PublishImageAdapter.ViewHolder> {
    private List<String> mPhotoPathList;
    private int width = 150;
    private int height = 150;

    public PublishImageAdapter(List<String> photoPathList) {
        mPhotoPathList = photoPathList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_dynamic_img_content, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == mPhotoPathList.size()) {
            holder.img.setImageURI(Uri.parse("res:///" + R.mipmap.add_pot));
            holder.delete.setVisibility(View.GONE);
            holder.img.setEnabled(true);
            holder.img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });
        } else {
            holder.img.setEnabled(false);//图片不可点击
            holder.delete.setVisibility(View.VISIBLE);

            Uri uri = Uri.parse("file://" + mPhotoPathList.get(position));
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.img.getController())
                    .setImageRequest(request)
                    .build();
            holder.img.setController(controller);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoPathList == null ? 0 : (mPhotoPathList.size() < 6 ? mPhotoPathList.size() + 1 : mPhotoPathList.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        SimpleDraweeView img;
        ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (SimpleDraweeView) itemView.findViewById(R.id.dynamic_img);
            delete = (ImageView) itemView.findViewById(R.id.cancel_img);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position > -1) {
                notifyItemRemoved(position);
                mPhotoPathList.remove(position);
            }
        }
    }

    public void openGallery(){

    }
}
