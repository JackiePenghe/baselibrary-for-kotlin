package com.sscl.baselibrary.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @author jackie
 */
public class FragmentViewPagerAdapter extends FragmentStatePagerAdapter {

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
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        if (fragments.length > pageTitles.length){
            throw new IllegalStateException("The length of array parameter \"pageTitles\" must be equals or more than array parameter \"fragments\"");
        }
        this.fragments = fragments;
        this.pageTitles = pageTitles;
    }

    /*--------------------------------公开方法--------------------------------*/

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position 当前的位置
     */
    @Override
    @NonNull
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
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}
