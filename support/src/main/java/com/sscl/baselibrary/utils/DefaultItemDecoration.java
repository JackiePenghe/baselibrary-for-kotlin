package com.sscl.baselibrary.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * RecyclerView的装饰（分割线）
 *
 * @author jacke
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class DefaultItemDecoration extends RecyclerView.ItemDecoration {

    /*--------------------------------静态常量--------------------------------*/

    /**
     * 垂直列表
     */
    public static final int ORIENTATION_VERTICAL = 1;
    /**
     * 水平列表
     */
    public static final int ORIENTATION_HORIZONTAL = 2;

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 颜色图像
     */
    private Drawable mDivider;
    /**
     * 装饰物（分割线）的宽度
     */
    private int mDividerWidth;
    /**
     * 装饰物（分割线）的高度
     */
    private int mDividerHeight;
    /**
     * 保存ViewType集合
     */
    private List<Integer> mViewTypeList = new ArrayList<>();

    /*--------------------------------构造方法--------------------------------*/

    /**
     * @param color decoration line color.
     */
    public DefaultItemDecoration(@ColorInt int color) {
        this(color, 2, 2, -1);
    }

    /**
     * @param color           line color.
     * @param dividerWidth    line width.
     * @param dividerHeight   line height.
     * @param excludeViewType don't need to draw the ViewType of the item of the split line.
     */
    public DefaultItemDecoration(@ColorInt int color, int dividerWidth, int dividerHeight, int... excludeViewType) {
        mDivider = new ColorDrawable(color);
        mDividerWidth = dividerWidth;
        mDividerHeight = dividerHeight;
        for (int i : excludeViewType) {
            mViewTypeList.add(i);
        }
    }

    /*--------------------------------重写父类方法--------------------------------*/

    /**
     * Retrieve any offsets for the given item. Each field of <code>outRect</code> specifies
     * the number of pixels that the item view should be inset by, similar to padding or margin.
     * The default implementation sets the bounds of outRect to 0 and returns.
     * <p>
     * <p>
     * If this ItemDecoration does not affect the positioning of item views, it should set
     * all four fields of <code>outRect</code> (left, top, right, bottom) to zero
     * before returning.
     * <p>
     * <p>
     * If you need to access Adapter for additional data, you can call
     * {@link RecyclerView#getChildAdapterPosition(View)} to get the adapter position of the
     * View.
     *
     * @param outRect Rect to receive the output.
     * @param view    The child view to decorate
     * @param parent  RecyclerView this ItemDecoration is decorating
     * @param state   The current state of RecyclerView.
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position < 0) {
            return;
        }
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }
        if (mViewTypeList.contains(adapter.getItemViewType(position))) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        int columnCount = getSpanCount(parent);
        int childCount = adapter.getItemCount();

        boolean firstRaw = isFirstRaw(position, columnCount);
        boolean lastRaw = isLastRaw(position, columnCount, childCount);
        boolean firstColumn = isFirstColumn(position, columnCount);
        boolean lastColumn = isLastColumn(position, columnCount);

        if (columnCount == 1) {
            if (firstRaw) {
                outRect.set(0, 0, 0, mDividerHeight / 2);
            } else if (lastRaw) {
                outRect.set(0, mDividerHeight / 2, 0, 0);
            } else {
                outRect.set(0, mDividerHeight / 2, 0, mDividerHeight / 2);
            }
        } else {
            // right, bottom
            if (firstRaw && firstColumn) {
                outRect.set(0, 0, mDividerWidth / 2, mDividerHeight / 2);
            }
            // left, right
            else if (firstRaw && lastColumn) {
                outRect.set(mDividerWidth / 2, 0, 0, mDividerHeight / 2);
            }
            // left, right, bottom
            else if (firstRaw) {
                outRect.set(mDividerWidth / 2, 0, mDividerWidth / 2, mDividerHeight / 2);
            }
            // top, right
            else if (lastRaw && firstColumn) {
                outRect.set(0, mDividerHeight / 2, mDividerWidth / 2, 0);
            }
            // left, top
            else if (lastRaw && lastColumn) {
                outRect.set(mDividerWidth / 2, mDividerHeight / 2, 0, 0);
            }
            // left, top, right
            else if (lastRaw) {
                outRect.set(mDividerWidth / 2, mDividerHeight / 2, mDividerWidth / 2, 0);
            }
            // top, right, bottom
            else if (firstColumn) {
                outRect.set(0, mDividerHeight / 2, mDividerWidth / 2, mDividerHeight / 2);
            }
            // left, top, bottom
            else if (lastColumn) {
                outRect.set(mDividerWidth / 2, mDividerHeight / 2, 0, mDividerHeight / 2);
            }
            // left, bottom.
            else {
                outRect.set(mDividerWidth / 2, mDividerHeight / 2, mDividerWidth / 2, mDividerHeight / 2);
            }
        }
    }

    /**
     * Draw any appropriate decorations into the Canvas supplied to the RecyclerView.
     * Any content drawn by this method will be drawn before the item views are drawn,
     * and will thus appear underneath the views.
     *
     * @param c      Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state  The current state of RecyclerView
     */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    /*--------------------------------静态公开方法--------------------------------*/

    /**
     * 创建新的分割线
     *
     * @param color 分割线颜色
     * @return 分割线
     */
    public static DefaultItemDecoration newLine(@ColorInt int color) {
        return newLine(color, ORIENTATION_VERTICAL);
    }

    /**
     * 创建新的分割线
     *
     * @param color       分割线颜色
     * @param orientation 列表的排列方式
     * @return 分割线
     */
    public static DefaultItemDecoration newLine(@ColorInt int color, int orientation) {
        switch (orientation) {
            case ORIENTATION_HORIZONTAL:
                return new DefaultItemDecoration(color, 2, ViewGroup.LayoutParams.MATCH_PARENT, -1);
            case ORIENTATION_VERTICAL:
                return new DefaultItemDecoration(color, ViewGroup.LayoutParams.MATCH_PARENT, 2, -1);
            default:
                return new DefaultItemDecoration(color);
        }
    }

    /*--------------------------------私有方法--------------------------------*/

    /**
     * 根据不同的LayoutManager获取SpanCount
     *
     * @param parent RecyclerView
     * @return SpanCount
     */
    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return 1;
    }

    /**
     * 检查是不是第一行
     *
     * @param position    位置
     * @param columnCount 列数
     * @return true代表该位置在第一行
     */
    private boolean isFirstRaw(int position, int columnCount) {
        return position < columnCount;
    }

    /**
     * 检查是不是最后一行
     *
     * @param position    位置
     * @param columnCount 列数
     * @param childCount  子项数量
     * @return true代表该位置在最后一行
     */
    private boolean isLastRaw(int position, int columnCount, int childCount) {
        if (columnCount == 1) {
            return position + 1 == childCount;
        } else {
            int lastRawItemCount = childCount % columnCount;
            int rawCount = (childCount - lastRawItemCount) / columnCount + (lastRawItemCount > 0 ? 1 : 0);

            int rawPositionJudge = (position + 1) % columnCount;
            if (rawPositionJudge == 0) {
                int rawPosition = (position + 1) / columnCount;
                return rawCount == rawPosition;
            } else {
                int rawPosition = (position + 1 - rawPositionJudge) / columnCount + 1;
                return rawCount == rawPosition;
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
    private boolean isFirstColumn(int position, int columnCount) {
        return columnCount == 1 || position % columnCount == 0;
    }

    /**
     * 检查是不是最后一列
     *
     * @param position    位置
     * @param columnCount 列数
     * @return true代表该位置在最后一列
     */
    private boolean isLastColumn(int position, int columnCount) {
        return columnCount == 1 || (position + 1) % columnCount == 0;
    }

    /**
     * 画横线
     *
     * @param c      Canvas
     * @param parent RecyclerView
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        c.save();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);
            if (childPosition < 0) {
                continue;
            }
            RecyclerView.Adapter adapter = parent.getAdapter();
            if (adapter == null) {
                return;
            }
            if (mViewTypeList.contains(adapter.getItemViewType(childPosition))) {
                continue;
            }
            final int left = child.getLeft();
            final int top = child.getBottom();
            final int right = child.getRight();
            final int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
        c.restore();
    }

    /**
     * 画竖线
     *
     * @param c      Canvas
     * @param parent RecyclerView
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        c.save();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);
            if (childPosition < 0) {
                continue;
            }
            RecyclerView.Adapter adapter = parent.getAdapter();
            if (adapter == null) {
                return;
            }
            if (mViewTypeList.contains(adapter.getItemViewType(childPosition))) {
                continue;
            }
//            if (child instanceof SwipeMenuRecyclerView.LoadMoreView) continue;
            final int left = child.getRight();
            final int top = child.getTop();
            final int right = left + mDividerWidth;
            final int bottom = child.getBottom();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
        c.restore();
    }

}
