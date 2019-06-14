package com.sscl.baselibrary.adapter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author jackie
 */
public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

    /*--------------------------------成员变量--------------------------------*/

    /**
     * Fragment数组
     */
    private Fragment[] fragments;
    /**
     * Fragment数组对应的标题
     */
    private String[] pageTitles;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     * @param fm Fragment管理器
     * @param fragments Fragment数组
     * @param pageTitles Fragment数组对应的标题
     */
    public FragmentViewPagerAdapter(FragmentManager fm, @NonNull Fragment[] fragments, @NonNull String[] pageTitles) {
        super(fm);
        if (fragments.length > pageTitles.length){
            throw new IllegalStateException("The length of array parameter \"pageTitles\" must be equals or more than array parameter \"fragments\"");
        }
        this.fragments = fragments;
        this.pageTitles = pageTitles;
    }

    /*--------------------------------重写方法--------------------------------*/

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position 当前的位置
     */
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return fragments.length;
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return a title for the requested page
     */
    @Override
    @Nullable
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}
