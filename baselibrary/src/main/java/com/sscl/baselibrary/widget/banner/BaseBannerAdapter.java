package com.sscl.baselibrary.widget.banner;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public abstract class BaseBannerAdapter<T> extends PagerAdapter {

    private static final String TAG = BaseBannerAdapter.class.getSimpleName();
    protected ArrayList<T> mData = new ArrayList<>();

    /*--------------------------------接口定义--------------------------------*/
    protected Context mContext;

    /*--------------------------------成员变量--------------------------------*/
    private ViewPager viewPager;
    private SparseArray<View> views = new SparseArray<>();
    private int layoutRes;
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
        return mData == null ? 0 : mData.size();
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
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        if (mContext == null) {
            mContext = container.getContext();
        }
        View view = views.get(position);
        if (view != null) {
//            container.addView(view);
            return views;
        } else {
            view = View.inflate(mContext, layoutRes, null);
            BannerHolder holder = new BannerHolder(view);
            bindView(holder, mData.get(position), position);
            final View finalView = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(finalView, mData.get(position), position);
                    }
                }
            });
            container.addView(view);
            views.put(position, view);
            return view;
        }
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
        View view = views.get(position);
        if (view.getParent() == null) {
            container.removeView(view);
        }
    }

    public abstract void bindView(BannerHolder holder, T itemData, int position);

    /*--------------------------------抽象方法--------------------------------*/

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /*--------------------------------setter--------------------------------*/

    public void boundToViewPater(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.setAdapter(this);
    }

    /*--------------------------------自定义公开方法--------------------------------*/

    public void setData(ArrayList<T> data) {
        if (data.size() > 0) {
            mData.add(data.get(data.size() - 1));
            mData.addAll(data);
            mData.add(data.get(0));
        } else {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener<T> {

        void onItemClick(View itemView, T itemData, int position);
    }
}
