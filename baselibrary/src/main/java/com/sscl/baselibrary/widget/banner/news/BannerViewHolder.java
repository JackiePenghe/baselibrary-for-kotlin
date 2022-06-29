package com.sscl.baselibrary.widget.banner.news;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Banner ViewHolder
 *
 * @author pengh
 */
public class BannerViewHolder extends RecyclerView.ViewHolder {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 获取View
     *
     * @param id  view id
     * @param <T> 泛型
     * @return View
     */
    public <T extends View> T getView(@IdRes int id) {
        return itemView.findViewById(id);
    }
}