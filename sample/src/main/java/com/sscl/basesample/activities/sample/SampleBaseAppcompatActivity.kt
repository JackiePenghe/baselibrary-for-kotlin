package com.sscl.basesample.activities.sample

import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.basesample.R

/**
 * @author jacke
 */
class SampleBaseAppcompatActivity : BaseAppCompatActivity() {

    /**
     * 标题栏的返回按钮被按下的时候回调此函数
     */
    override fun titleBackClicked(): Boolean {
        return false
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    override fun doBeforeSetLayout() {

    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_sample_base_appcompat
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {
        /*
         * 在这里可以进行一些常见的操作
         * 下方的操作必须在setLayout()之后执行
         * 最佳调用位置就是此处 doBeforeInitOthers()
         */
        //隐藏标题中的返回按钮
//        hideTitleBackButton()
        //显示标题栏的返回按钮
//        showTitleBackButton()
        //设置根布局（整个Activity）的背景色
//        setRootBackGroundResource()
//        setRootBackGroundColor()
        //隐藏整个标题栏
//        hideTitleBar()
        //显示标题栏
//        showTitleBar()
        // 设置标题栏的文本
        setTitleText("BaseAppCompatActivity的示例")
        //设置标题栏文本颜色
//        setTitleTextColor()
//        setTitleTextColorRes()
        //设置标题栏的背景色
//        setTitleBackgroundColor()
//        setTitleBackgroundDrawable()
//        setTitleBackgroundResource()
        //设置标题栏左边的图片，当某些情况下返回的按钮不再是返回时，可以设置对应功能的图片替换返回按钮，并调用setTitleBackOnClickListener()来监听这个按钮的点击事件
//        setTitleBackIcon()
    }

    /**
     * 初始化布局控件
     */
    override fun initViews() {
        //通常在这里findViewById()
    }

    /**
     * 初始化控件数据
     */
    override fun initViewData() {
        //在这里设置View的数据。如：ListView.setAdapter()
    }

    /**
     * 初始化其他数据
     */
    override fun initOtherData() {}

    /**
     * 初始化事件
     */
    override fun initEvents() {
        /* * * * * * * * * * * * * * * * * * * 在这里设置监听事件 * * * * * * * * * * * * * * * * * * */
        //设置标题栏的返回按钮的点击事件（点击返回不一定是返回的情况下）
//        setTitleBackOnClickListener()
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {}

}