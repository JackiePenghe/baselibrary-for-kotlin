package com.sscl.basesample.activities.widget

import android.view.Menu
import android.view.MenuItem
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.utils.BaseManager
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.widget.CircleProgressBar
import com.sscl.basesample.R

/**
 * @author pengh
 */
class CircleProgressBarActivity : BaseAppCompatActivity() {

    private lateinit var circleProgressBar: CircleProgressBar

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
   override fun titleBackClicked(): Boolean {
        onBackPressed()
        return true
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
        return R.layout.com_sscl_basesample_activity_circle_progress_bar
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
   override fun doBeforeInitOthers() {}

    /**
     * 初始化布局控件
     */
   override fun initViews() {
        circleProgressBar = findViewById(R.id.circle_progress_bar)
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
   override fun doAfterAll() {
        circleProgressBar.setAnimateProgressDelay(1)
        circleProgressBar.setAnimateTimeMillis(1000)
        val maxProgress: Double = circleProgressBar.maxProgress
        DebugUtil.warnOut(TAG, "maxProgress = $maxProgress")
        circleProgressBar.setProgress(1000.0)
        BaseManager.handler.postDelayed(
            Runnable { circleProgressBar.setProgressWithAnimate(3000.0) },
            2000
        )
    }

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
   override fun createOptionsMenu(menu: Menu): Boolean {
        return false
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
   override fun optionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    companion object {
        private val TAG = CircleProgressBarActivity::class.java.simpleName
    }
}