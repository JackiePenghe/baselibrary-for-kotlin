package com.sscl.basesample.activities.sample

import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.utils.StatusBarUtil
import com.sscl.baselibrary.utils.SystemUtil
import com.sscl.baselibrary.utils.Tool
import com.sscl.baselibrary.widget.GifView
import com.sscl.basesample.R

/**
 * @author jacke
 */
class SampleBaseAppcompatActivity : BaseAppCompatActivity() {
    private var gifView: GifView? = null

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
        //在Activity中，有时候会设置一些属性，这些属性在setContentView()之前，可在这里实现
        if (SystemUtil.isFlyme) {
            SystemUtil.flymeSetStatusBarLightMode(window, true)
        } else if (SystemUtil.isMiui) {
            SystemUtil.miuiSetStatusBarLightMode(window, true)
        } else {
            Log.w(TAG, "非MIUI 非 FlyMe 使用通用方法,请通过style设置状态栏lightMode")
        }
        StatusBarUtil.setColor(this, Tool.getStatusBarColor(this), 0)
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
        setTitleTextColor(Color.BLACK)
    }

    /**
     * 初始化布局控件
     */
    override fun initViews() {
        //通常在这里findViewById()
        gifView = findViewById(R.id.gif_view)
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
        //在这里设置监听事件
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {}

    override fun onPostResume() {
        super.onPostResume()
        gifView?.isPaused = false
    }

    /**
     * Dispatch onPause() to fragments.
     */
    override fun onPause() {
        super.onPause()
        gifView?.isPaused = true
    }

    override fun onDestroy() {
        super.onDestroy()
        gifView = null
    }

    companion object {
        private val TAG = SampleBaseAppcompatActivity::class.java.simpleName
    }
}