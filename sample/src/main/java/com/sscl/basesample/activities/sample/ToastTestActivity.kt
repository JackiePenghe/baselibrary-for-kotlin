package com.sscl.basesample.activities.sample

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.utils.ToastUtil
import com.sscl.basesample.R

/**
 * Toast测试
 *
 * @author jackie
 */
class ToastTestActivity : BaseAppCompatActivity() {
    private lateinit var longTimeBtn: Button
    private lateinit var shortTimeBtn: Button
    private lateinit var time100Btn: Button
    private lateinit var time500Btn: Button
    private lateinit var viewTestBtn: Button
    private lateinit var reuseRadioGroup: RadioGroup

    private val onClickListener = View.OnClickListener { v: View ->
        when (v.id) {
            R.id.long_time -> ToastUtil.toastLong(
                this@ToastTestActivity,
                R.string.com_sscl_basesample_long_time
            )
            R.id.short_time -> ToastUtil.toastShort(
                this@ToastTestActivity,
                R.string.com_sscl_basesample_short_time
            )
            R.id.time_100 -> ToastUtil.toast(
                this@ToastTestActivity,
                R.string.com_sscl_basesample_time_100,
                100
            )
            R.id.time_500 -> ToastUtil.toast(
                this@ToastTestActivity,
                R.string.com_sscl_basesample_time_500,
                500
            )
            R.id.view_test -> {
                val view = View.inflate(
                    this@ToastTestActivity,
                    R.layout.com_sscl_basesample_toast_view,
                    null
                )
                ToastUtil.toast(this@ToastTestActivity, view, 2000)
            }
            else -> {}
        }
    }
    private val onCheckedChangeListener: RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { group: RadioGroup, checkedId: Int ->
            when (group.id) {
                R.id.reuse_toast_group -> onReuseGroupButtonCheckedChanged(checkedId)
                else -> {}
            }
        }

    private fun onReuseGroupButtonCheckedChanged(checkedId: Int) {
        when (checkedId) {
            R.id.open_reuse -> ToastUtil.isToastReuse = true
            R.id.close_reuse -> ToastUtil.isToastReuse = false
            else -> {}
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
   override fun doBeforeSetLayout() {}

    /**
     * 设置布局
     *
     * @return 布局id
     */
   override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_toast_test
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
   override fun doBeforeInitOthers() {}

    /**
     * 初始化布局控件
     */
   override fun initViews() {
        longTimeBtn = findViewById(R.id.long_time)
        shortTimeBtn = findViewById(R.id.short_time)
        time100Btn = findViewById(R.id.time_100)
        time500Btn = findViewById(R.id.time_500)
        viewTestBtn = findViewById(R.id.view_test)
        reuseRadioGroup = findViewById(R.id.reuse_toast_group)
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
   override fun initEvents() {
        reuseRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener)
        longTimeBtn.setOnClickListener(onClickListener)
        shortTimeBtn.setOnClickListener(onClickListener)
        time100Btn.setOnClickListener(onClickListener)
        time500Btn.setOnClickListener(onClickListener)
        viewTestBtn.setOnClickListener(onClickListener)
    }

    /**
     * 在最后进行的操作
     */
   override fun doAfterAll() {
        reuseRadioGroup.check(R.id.open_reuse)
    }
}