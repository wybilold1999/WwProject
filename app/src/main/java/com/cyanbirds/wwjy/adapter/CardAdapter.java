package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.entity.CardModel;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

/**
 * Created by Shall on 2015-06-23.
 */
public class CardAdapter extends BaseAdapter {
    private Context mContext;
    private List<CardModel> mCardList;
    private ResizeOptions mResizeOptions;

    public CardAdapter(Context mContext, List<CardModel> mCardList) {
        this.mContext = mContext;
        this.mCardList = mCardList;
        mResizeOptions = new ResizeOptions(80, 80);
    }

    @Override
    public int getCount() {
        return mCardList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.card_item, parent, false);
            holder = new ViewHolder();
            holder.mPortrait = (SimpleDraweeView) convertView.findViewById(R.id.card_image_portrait);
            holder.mImgOne = (SimpleDraweeView) convertView.findViewById(R.id.card_image_one);
            holder.mImgTwo = (SimpleDraweeView) convertView.findViewById(R.id.card_image_two);
            holder.mImgThree = (SimpleDraweeView) convertView.findViewById(R.id.card_image_three);
            holder.mImgFour = (SimpleDraweeView) convertView.findViewById(R.id.card_image_four);
            holder.mNickName = (TextView) convertView.findViewById(R.id.card_user_name);
            holder.mAge = (TextView) convertView.findViewById(R.id.age);
            holder.mCon = (TextView) convertView.findViewById(R.id.constellation);
            holder.mSignature = (TextView) convertView.findViewById(R.id.signature);
            holder.mDistance = (TextView) convertView.findViewById(R.id.distance);
            holder.mCity = (TextView) convertView.findViewById(R.id.city);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CardModel model = mCardList.get(position);
        if (model == null) {
            return convertView;
        }
        holder.mPortrait.setImageURI(Uri.parse(model.imagePath));
        ImageRequest requestOne = ImageRequestBuilder.newBuilderWithSource(Uri.parse(model.pictures.get(0)))
                .setResizeOptions(mResizeOptions)
                .setProgressiveRenderingEnabled(true)
                .build();
        PipelineDraweeController controllerOne = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(holder.mImgOne.getController())
                .setImageRequest(requestOne)
                .build();
        holder.mImgOne.setController(controllerOne);

        ImageRequest requestTwo = ImageRequestBuilder.newBuilderWithSource(Uri.parse(model.pictures.get(1)))
                .setResizeOptions(mResizeOptions)
                .setProgressiveRenderingEnabled(true)
                .build();
        PipelineDraweeController controllerTwo = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(holder.mImgTwo.getController())
                .setImageRequest(requestTwo)
                .build();
        holder.mImgTwo.setController(controllerTwo);

        ImageRequest requestThree = ImageRequestBuilder.newBuilderWithSource(Uri.parse(model.pictures.get(2)))
                .setResizeOptions(mResizeOptions)
                .setProgressiveRenderingEnabled(true)
                .build();
        PipelineDraweeController controllerThree = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(holder.mImgThree.getController())
                .setImageRequest(requestThree)
                .build();
        holder.mImgThree.setController(controllerThree);

        ImageRequest requestFour = ImageRequestBuilder.newBuilderWithSource(Uri.parse(model.pictures.get(3)))
                .setResizeOptions(mResizeOptions)
                .setProgressiveRenderingEnabled(true)
                .build();
        PipelineDraweeController controllerFour = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(holder.mImgFour.getController())
                .setImageRequest(requestFour)
                .build();
        holder.mImgFour.setController(controllerFour);

        holder.mNickName.setText(model.userName);
        holder.mAge.setText(String.valueOf(model.age));
        holder.mCon.setText(model.constellation);
        if (model.distance == 0.00) {
            holder.mCity.setVisibility(View.VISIBLE);
            holder.mDistance.setVisibility(View.GONE);
            holder.mCity.setText("来自" + model.city);
        } else {
            holder.mDistance.setVisibility(View.VISIBLE);
            holder.mCity.setVisibility(View.GONE);
            holder.mDistance.setText(model.distance + "km");
        }
        holder.mSignature.setText("个性签名：" + model.signature);

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView mPortrait;
        SimpleDraweeView mImgOne;
        SimpleDraweeView mImgTwo;
        SimpleDraweeView mImgThree;
        SimpleDraweeView mImgFour;
        TextView mNickName;
        TextView mAge;
        TextView mCon;
        TextView mSignature;
        TextView mDistance;
        TextView mCity;
    }
}
