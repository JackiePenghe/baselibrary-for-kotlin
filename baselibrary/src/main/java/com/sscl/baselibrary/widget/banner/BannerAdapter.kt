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
     * 私有成员常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    val views: ArrayList<View> = ArrayList()

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    *
    * 实现方法
    *
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int {
        return views.size
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by [.instantiateItem]. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with any
     * @param object Object to check for association with `view`
     * @return true if `view` is associated with the key object any
     */
    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * [PagerAdapter.finishUpdate].
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = views[position]
        container.addView(view)
        return view
    }

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from [.finishUpdate].
     *
     * @param container The containing View from which the page will be removed.
     * @param position  The page position to be removed.
     * @param object    The same object that was returned by
     * [.instantiateItem].
     */
    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val view: View = any as View
        container.removeView(view)
    }

    /**
     * Called when the host view is attempting to determine if an item's position
     * has changed. Returns [.POSITION_UNCHANGED] if the position of the given
     * item has not changed or [.POSITION_NONE] if the item is no longer present
     * in the adapter.
     *
     *
     * The default implementation assumes that items will never
     * change position and always returns [.POSITION_UNCHANGED].
     *
     * @param object Object representing an item, previously returned by a call to
     * [.instantiateItem].
     * @return object's new position index from [0, [.getCount]),
     * [.POSITION_UNCHANGED] if the object's position has not changed,
     * or [.POSITION_NONE] if the item is no longer present.
     */
    override fun getItemPosition(any: Any): Int {
        return POSITION_NONE
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 包内方法
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
}