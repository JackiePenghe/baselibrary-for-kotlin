package com.sscl.baselibrary.widget.banner

import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.IdRes
import android.view.*

/**
 * Banner ViewHolder
 *
 * @author pengh
 */
class BannerViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
    </T> */
    fun <T : View?> getView(@IdRes id: Int): T {
        return itemView.findViewById(id)
    }
}