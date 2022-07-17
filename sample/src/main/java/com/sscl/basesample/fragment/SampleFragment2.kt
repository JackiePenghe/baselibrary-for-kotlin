package com.sscl.basesample.fragment

import com.sscl.baselibrary.fragment.BaseFragment
import com.sscl.basesample.R

/**
 *
 * @author pengh
 * @date 2017/10/13
 *
 * 用起来很简单
 */
class SampleFragment2 : BaseFragment() {
    /**
     * 在设置布局之前进行的操作
     */
   override fun doBeforeSetLayout() {}

    /**
     * 设置fragment的布局
     *
     * @return 布局id
     */
   override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_fragment_sample_2
    }

    /**
     * 初始化数据
     */
   override fun initData() {}

    /**
     * 初始化控件
     */
   override fun initViews() {}

    /**
     * 初始化控件数据
     */
   override fun initViewData() {}

    /**
     * 初始化事件
     */
   override fun initEvents() {}

    /**
     * 在最后执行的操作
     */
   override fun doAfterAll() {}
}