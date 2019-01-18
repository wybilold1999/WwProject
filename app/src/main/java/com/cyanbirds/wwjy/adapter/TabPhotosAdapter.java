package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.ViewPagerPhotoViewActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangyb
 * @datetime: 2016-01-02 16:51 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class TabPhotosAdapter extends
        RecyclerView.Adapter<TabPhotosAdapter.ViewHolder>{
    private Context mContext;
    private List<String> imgResourceUrl;
    private int width = 200;
    private int height = 200;

    public TabPhotosAdapter(Context context, List<String> url) {
        mContext = context;
        this.imgResourceUrl = url;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri uri = Uri.parse(imgResourceUrl.get(position));
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(holder.imageView.getController())
                .setImageRequest(request)
                .build();
        holder.imageView.setController(controller);
    }

    @Override
    public int getItemCount() {
        return imgResourceUrl == null ? 0 : imgResourceUrl.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_tab_photos, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        SimpleDraweeView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (SimpleDraweeView)itemView.findViewById(R.id.img);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(mContext, ViewPagerPhotoViewActivity.class);
            intent.putExtra(ValueKey.POSITION, position);
            intent.putStringArrayListExtra(ValueKey.IMAGE_URL, (ArrayList<String>) imgResourceUrl);
            mContext.startActivity(intent);
        }
    }
}
