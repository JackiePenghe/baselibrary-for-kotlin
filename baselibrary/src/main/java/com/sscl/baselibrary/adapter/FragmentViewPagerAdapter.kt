@file:Suppress("DEPRECATION")

package com.sscl.baselibrary.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.lang.IllegalStateException

import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * @author jackie
 */
@Deprecated("Deprecated", ReplaceWith("androidx.viewpager2.adapter.FragmentStateAdapter"), DeprecationLevel.WARNING)
class FragmentViewPagerAdapter constructor(
    fm: FragmentManager,
    fragments: Array<Fragment>,
    pageTitles: Array<String>
) : FragmentStatePagerAdapter(
    fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    /*--------------------------------静态常量--------------------------------*/

    @Suppress("PropertyName", "ProtectedInFinal")
    protected val TAG: String = javaClass.simpleName

    /*--------------------------------成员变量--------------------------------*/
    /**
     * Fragment数组
     */
    private val fragments: Array<Fragment>

    /**
     * Fragment数组对应的标题
     */
    private val pageTitles: Array<String>
    /*--------------------------------公开方法--------------------------------*/
    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position 当前的位置
     */
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int {
        return fragments.size
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
    override fun getPageTitle(position: Int): CharSequence? {
        return pageTitles.get(position)
    }
    /*--------------------------------构造方法--------------------------------*/

    init {
        if (fragments.size > pageTitles.size) {
            throw IllegalStateException("The length of array parameter \"pageTitles\" must be equals or more than array parameter \"fragments\"")
        }
        this.fragments = fragments
        this.pageTitles = pageTitles
    }
}