package com.sscl.baselibrary.widget.bannerold;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public abstract class BaseBannerAdapter<T> extends PagerAdapter {

    protected ArrayList<T> mData = new ArrayList<>();

    /*--------------------------------接口定义--------------------------------*/

    protected Context mContext;

    /*--------------------------------成员变量--------------------------------*/

    private final int layoutRes;
    private OnItemClickListener<T> mOnItemClickListener;

    public BaseBannerAdapter(@LayoutRes int layoutRes) {
        this.layoutRes = layoutRes;
    }

    /*--------------------------------构造方法--------------------------------*/

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mData == null ? 0 : (mData.size() == 1) ? 1 : mData.size() + 2;
    }

    /*--------------------------------实现父类方法--------------------------------*/

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with <code>object</code>
     * @param object Object to check for association with <code>view</code>
     * @return true if <code>view</code> is associated with the key object <code>object</code>
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (mContext == null) {
            mContext = container.getContext();
        }
        final int realPosition = getRealPosition(position);
        View view = View.inflate(mContext, layoutRes, null);
        BannerHolder holder = new BannerHolder(view);
        bindView(holder, mData.get(realPosition), realPosition);
        final View finalView = view;
        view.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(finalView, mData.get(realPosition), realPosition);
            }
        });
        container.addView(view);
        return view;
    }

    /*--------------------------------重写父类方法--------------------------------*/

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position  The page position to be removed.
     * @param object    The same object that was returned by
     *                  {@link #instantiateItem(View, int)}.
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    /*--------------------------------抽象方法--------------------------------*/

    /**
     * 当绑定某一个位置的布局时，进行的操作
     *
     * @param holder   ViewHolder
     * @param itemData itemData
     * @param position 位置
     */
    public abstract void bindView(BannerHolder holder, T itemData, int position);

    /**
     * 有某个位置的布局被选中时，进行的操作
     *
     * @param position 位置
     */
    public abstract void onPageSelected(int position);

    /**
     * 获取当前位置需要附加的延时的时间
     *
     * @param position         位置
     * @param defaultDelayTime 默认已有的延时时间
     * @return 延时时间(单位 : 毫秒)
     */
    public abstract long getDelayTime(int position, long defaultDelayTime);

    /*--------------------------------setter--------------------------------*/

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /*--------------------------------自定义私有方法--------------------------------*/


    /**
     * 返回真实的位置
     *
     * @param position 当前位置
     * @return 真实的位置
     */
    private int getRealPosition(int position) {
        int realPosition = (position - 1) % mData.size();
        if (realPosition < 0) {
            realPosition += mData.size();
        }
        return realPosition;
    }

    public interface OnItemClickListener<T> {

        void onItemClick(View itemView, T itemData, int position);
    }
}
