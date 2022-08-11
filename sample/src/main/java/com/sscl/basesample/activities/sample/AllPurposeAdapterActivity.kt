package com.sscl.basesample.activities.sample

import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.utils.SystemUtil
import com.sscl.baselibrary.utils.Tool
import com.sscl.basesample.R
import com.sscl.basesample.adapter.SampleAdapter

/**
 * @author jacke
 */
class AllPurposeAdapterActivity : BaseAppCompatActivity() {
    private lateinit var listView: ListView
    private val dataList = ArrayList<String>()

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
        for (i in 0..3) {
            dataList.add("test$i")
        }
        SystemUtil.hideNavigationBar(this)
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_all_purpose_adapter
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {}

    /**
     * 初始化布局控件
     */
    override fun initViews() {
        listView = findViewById(R.id.list_view)
    }

    /**
     * 初始化控件数据
     */
    override fun initViewData() {
        val adapter = SampleAdapter(dataList)
        listView.adapter = adapter
    }

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
}