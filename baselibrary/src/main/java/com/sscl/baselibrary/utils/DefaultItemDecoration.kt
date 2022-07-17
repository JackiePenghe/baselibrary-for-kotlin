package com.sscl.baselibrary.utils

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * RecyclerView的装饰（分割线）
 *
 * @author jacke
 */
class DefaultItemDecoration  : ItemDecoration {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    companion object {

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 属性声明
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /* * * * * * * * * * * * * * * * * * * 静态常量 * * * * * * * * * * * * * * * * * * */

        /**
         * 垂直列表
         */
        const val ORIENTATION_VERTICAL: Int = 1

        /**
         * 水平列表
         */
        const val ORIENTATION_HORIZONTAL: Int = 2

        /*--------------------------------静态公开方法--------------------------------*/
        /**
         * 创建新的分割线
         *
         * @param color 分割线颜色
         * @return 分割线
         */
        @JvmOverloads
        fun newLine(
            @ColorInt color: Int,
            orientation: Int = ORIENTATION_VERTICAL
        ): DefaultItemDecoration {
            return when (orientation) {
                ORIENTATION_HORIZONTAL -> DefaultItemDecoration(
                    color,
                    2,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    -1
                )
                ORIENTATION_VERTICAL -> DefaultItemDecoration(
                    color,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    2,
                    -1
                )
                else -> DefaultItemDecoration(color)
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 颜色图像
     */
    private val mDivider: Drawable

    /**
     * 装饰物（分割线）的宽度
     */
    private val mDividerWidth: Int

    /**
     * 装饰物（分割线）的高度
     */
    private val mDividerHeight: Int

    /**
     * 保存ViewType集合
     */
    private val mViewTypeList: MutableList<Int> = ArrayList()

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * @param color decoration line color.
     */
    constructor(@ColorInt color: Int) : this(color, 2, 2, -1)

    /**
     * @param color           line color.
     * @param dividerWidth    line width.
     * @param dividerHeight   line height.
     * @param excludeViewType don't need to draw the ViewType of the item of the split line.
     */
    constructor(@ColorInt color: Int,
                 dividerWidth: Int,
                 dividerHeight: Int,
                 vararg excludeViewType: Int){
        mDivider = ColorDrawable(color)
        mDividerWidth = dividerWidth
        mDividerHeight = dividerHeight
        for (i: Int in excludeViewType) {
            mViewTypeList.add(i)
        }
    }



   /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    *
    * 重写方法
    *
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position: Int = parent.getChildAdapterPosition(view)
        if (position < 0) {
            return
        }
        val adapter: RecyclerView.Adapter<*> = parent.adapter ?: return
        if (mViewTypeList.contains(adapter.getItemViewType(position))) {
            outRect.set(0, 0, 0, 0)
            return
        }
        val columnCount: Int = getSpanCount(parent)
        val childCount: Int = adapter.itemCount
        val firstRaw: Boolean = isFirstRaw(position, columnCount)
        val lastRaw: Boolean = isLastRaw(position, columnCount, childCount)
        val firstColumn: Boolean = isFirstColumn(position, columnCount)
        val lastColumn: Boolean = isLastColumn(position, columnCount)
        if (columnCount == 1) {
            if (firstRaw) {
                outRect.set(0, 0, 0, mDividerHeight / 2)
            } else if (lastRaw) {
                outRect.set(0, mDividerHeight / 2, 0, 0)
            } else {
                outRect.set(0, mDividerHeight / 2, 0, mDividerHeight / 2)
            }
        } else {
            // right, bottom
            if (firstRaw && firstColumn) {
                outRect.set(0, 0, mDividerWidth / 2, mDividerHeight / 2)
            } else if (firstRaw && lastColumn) {
                outRect.set(mDividerWidth / 2, 0, 0, mDividerHeight / 2)
            } else if (firstRaw) {
                outRect.set(mDividerWidth / 2, 0, mDividerWidth / 2, mDividerHeight / 2)
            } else if (lastRaw && firstColumn) {
                outRect.set(0, mDividerHeight / 2, mDividerWidth / 2, 0)
            } else if (lastRaw && lastColumn) {
                outRect.set(mDividerWidth / 2, mDividerHeight / 2, 0, 0)
            } else if (lastRaw) {
                outRect.set(mDividerWidth / 2, mDividerHeight / 2, mDividerWidth / 2, 0)
            } else if (firstColumn) {
                outRect.set(0, mDividerHeight / 2, mDividerWidth / 2, mDividerHeight / 2)
            } else if (lastColumn) {
                outRect.set(mDividerWidth / 2, mDividerHeight / 2, 0, mDividerHeight / 2)
            } else {
                outRect.set(
                    mDividerWidth / 2,
                    mDividerHeight / 2,
                    mDividerWidth / 2,
                    mDividerHeight / 2
                )
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 根据不同的LayoutManager获取SpanCount
     *
     * @param parent RecyclerView
     * @return SpanCount
     */
    private fun getSpanCount(parent: RecyclerView): Int {
        val layoutManager: RecyclerView.LayoutManager? = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            return layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            return layoutManager.spanCount
        }
        return 1
    }

    /**
     * 检查是不是第一行
     *
     * @param position    位置
     * @param columnCount 列数
     * @return true代表该位置在第一行
     */
    private fun isFirstRaw(position: Int, columnCount: Int): Boolean {
        return position < columnCount
    }

    /**
     * 检查是不是最后一行
     *
     * @param position    位置
     * @param columnCount 列数
     * @param childCount  子项数量
     * @return true代表该位置在最后一行
     */
    private fun isLastRaw(position: Int, columnCount: Int, childCount: Int): Boolean {
        return if (columnCount == 1) {
            position + 1 == childCount
        } else {
            val lastRawItemCount: Int = childCount % columnCount
            val rawCount: Int =
                (childCount - lastRawItemCount) / columnCount + (if (lastRawItemCount > 0) 1 else 0)
            val rawPositionJudge: Int = (position + 1) % columnCount
            if (rawPositionJudge == 0) {
                val rawPosition: Int = (position + 1) / columnCount
                rawCount == rawPosition
            } else {
                val rawPosition: Int = (position + 1 - rawPositionJudge) / columnCount + 1
                rawCount == rawPosition
            }
        }
    }

    /**
     * 检查是不是第一列
     *
     * @param position    位置
     * @param columnCount 列数
     * @return true代表该位置在第一列
     */
    private fun isFirstColumn(position: Int, columnCount: Int): Boolean {
        return columnCount == 1 || position % columnCount == 0
    }

    /**
     * 检查是不是最后一列
     *
     * @param position    位置
     * @param columnCount 列数
     * @return true代表该位置在最后一列
     */
    private fun isLastColumn(position: Int, columnCount: Int): Boolean {
        return columnCount == 1 || (position + 1) % columnCount == 0
    }

    /**
     * 画横线
     *
     * @param c      Canvas
     * @param parent RecyclerView
     */
    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        c.save()
        val childCount: Int = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val childPosition: Int = parent.getChildAdapterPosition(child)
            if (childPosition < 0) {
                continue
            }
            val adapter: RecyclerView.Adapter<*> = parent.adapter ?: return
            if (mViewTypeList.contains(adapter.getItemViewType(childPosition))) {
                continue
            }
            val left: Int = child.left
            val top: Int = child.bottom
            val right: Int = child.right
            val bottom: Int = top + mDividerHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
        c.restore()
    }

    /**
     * 画竖线
     *
     * @param c      Canvas
     * @param parent RecyclerView
     */
    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        c.save()
        val childCount: Int = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val childPosition: Int = parent.getChildAdapterPosition(child)
            if (childPosition < 0) {
                continue
            }
            val adapter: RecyclerView.Adapter<*> = parent.adapter ?: return
            if (mViewTypeList.contains(adapter.getItemViewType(childPosition))) {
                continue
            }
            val left: Int = child.right
            val top: Int = child.top
            val right: Int = left + mDividerWidth
            val bottom: Int = child.bottom
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
        c.restore()
    }
}