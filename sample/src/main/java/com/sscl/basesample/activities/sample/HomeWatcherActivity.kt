package com.sscl.basesample.activities.sample

import android.view.Menu
import android.view.MenuItem
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.receiver.HomeWatcherReceiver
import com.sscl.baselibrary.receiver.HomeWatcherReceiver.OnHomePressedListener
import com.sscl.baselibrary.utils.HomeWatcher
import com.sscl.baselibrary.utils.ToastUtil
import com.sscl.basesample.R

/**
 * @author jackie
 */
class HomeWatcherActivity : BaseAppCompatActivity() {
    private var homeWatcher: HomeWatcher? = null
    private val onHomePressedListener: OnHomePressedListener = object : OnHomePressedListener {
        override fun onHomePressed() {
            ToastUtil.toastLong(this@HomeWatcherActivity, "home button pressed")
        }

        override fun onHomeLongPressed() {
            ToastUtil.toastLong(this@HomeWatcherActivity, "home button long pressed")
        }
    }

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
        homeWatcher = HomeWatcher(this)
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_home_watcher
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {}

    /**
     * 初始化布局控件
     */
    override fun initViews() {}

    /**
     * 初始化控件数据
     */
    override fun initViewData() {}

    /**
     * 初始化其他数据
     */
    override fun initOtherData() {}

    /**
     * 初始化事件
     */
    override fun initEvents() {
        homeWatcher?.setOnHomePressedListener(onHomePressedListener)
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {
        homeWatcher?.startWatch()
    }

    override fun onDestroy() {
        super.onDestroy()
        homeWatcher?.stopWatch()
        homeWatcher = null
    }
}