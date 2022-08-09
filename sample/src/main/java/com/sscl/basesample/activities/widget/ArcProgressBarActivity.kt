package com.sscl.basesample.activities.widget

import android.view.Menu
import android.view.MenuItem
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.widget.ArcProgressBar
import com.sscl.basesample.R

/**
 * 自定义弧形进度条
 *
 * @author pengh
 */
class ArcProgressBarActivity : BaseAppCompatActivity() {

    private lateinit var arcProgressBar: ArcProgressBar

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
   override fun titleBackClicked(): Boolean {
        return false
    }

    /**
     * 在设置布局之前需要进行的操作
     */
   override fun doBeforeSetLayout() {}

    /**
     * 设置布局
     *
     * @return 布局id
     */
   override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_arc_progress_bar
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
   override fun doBeforeInitOthers() {}

    /**
     * 初始化布局控件
     */
   override fun initViews() {
        arcProgressBar = findViewById(R.id.arc_progress_bar)
    }

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
   override fun initEvents() {}

    /**
     * 在最后进行的操作
     */
   override fun doAfterAll() {}

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */
   override fun onResume() {
        super.onResume()
        arcProgressBar.setValues(0, 100, 50, "test")
    }
}