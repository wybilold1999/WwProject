package com.cyanbirds.wwjy.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import com.cyanbirds.wwjy.R;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 */
public class PhotosAdapter extends ArrayAdapter<String> implements
        OnItemClickListener {

    private OnImgItemClickListener mItemClickListener;
    public PhotosAdapter(Context context, List<String> objects,
                         GridView imgGrid) {
        super(context, 0, objects);
        imgGrid.setOnItemClickListener(this);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_personal_photo, null);
            holder = new ViewHolder();
            holder.photo = (ImageView) convertView
                    .findViewById(R.id.photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.photo.setImageURI(Uri.parse(getItem(position)));
        return convertView;
    }

    private class ViewHolder {
        ImageView photo;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        if (mItemClickListener != null) {
            mItemClickListener.onImgItemClick(position);
        }
    }

    /**
     * 回调监听
     */
    public interface OnImgItemClickListener {
        void onImgItemClick(int position);
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnImgItemClickListener(OnImgItemClickListener listener) {
        mItemClickListener = listener;
    }
}
