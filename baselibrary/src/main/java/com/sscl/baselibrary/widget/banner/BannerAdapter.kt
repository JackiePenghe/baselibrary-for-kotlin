package com.sscl.baselibrary.widget.banner

import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.viewpager.widget.PagerAdapter

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * 私有静态内部类
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */ /**
 * Banner适配器
 *
 * @author pengh
 */
internal class BannerAdapter : PagerAdapter() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    interface OnItemClickListener {
        fun onItemClick()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    val views: ArrayList<View> = ArrayList()

    /**
     * banner选项点击监听器
     */
    private var onItemClickListener: OnItemClickListener? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    *
    * 实现方法
    *
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun getCount(): Int {
        return views.size
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = views[position]
        view.setOnClickListener {
            onItemClickListener?.onItemClick()
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val view: View = any as View
        container.removeView(view)
    }

    override fun getItemPosition(any: Any): Int {
        return POSITION_NONE
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 清空数据
     */
    fun clear() {
        for (i in views.indices) {
            val view: View = views[i]
            if (view is VideoView) {
                view.stopPlayback()
            }
        }
        views.clear()
    }

    /**
     * 设置banner选项点击监听器
     */
    internal fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }
}