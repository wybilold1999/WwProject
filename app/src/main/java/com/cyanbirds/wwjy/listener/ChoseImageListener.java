package com.cyanbirds.wwjy.listener;


import com.cyanbirds.wwjy.entity.ImageBean;

/**
 * 
 * @Description:选择图片监听
 * @author wangyb
 * @Date:2015年7月26日上午11:12:51
 */
public interface ChoseImageListener {

    public boolean onSelected(ImageBean image);

    public boolean onCancelSelect(ImageBean image);
}
