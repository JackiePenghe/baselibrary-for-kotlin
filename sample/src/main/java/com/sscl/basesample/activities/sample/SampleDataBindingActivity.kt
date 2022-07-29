package com.sscl.basesample.activities.sample

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.sscl.baselibrary.activity.BaseDataBindingAppCompatActivity
import com.sscl.basesample.R
import com.sscl.basesample.databinding.ComSsclBasesampleActivitySampleDataBindingBinding
import com.sscl.basesample.viewbinding.SampleDataBindingActivityViewModel

class SampleDataBindingActivity :
    BaseDataBindingAppCompatActivity<ComSsclBasesampleActivitySampleDataBindingBinding>() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * viewModel
     */
    private val sampleDataBindingActivityViewModel: SampleDataBindingActivityViewModel by viewModels {
        SampleDataBindingActivityViewModel.SampleDataBindingActivityViewModelFactory
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
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
        return R.layout.com_sscl_basesample_activity_sample_data_binding
    }

    /**
     * 初始化数据绑定
     */
    override fun initDataBinding(view: View): ComSsclBasesampleActivitySampleDataBindingBinding {
        val bind = ComSsclBasesampleActivitySampleDataBindingBinding.bind(view)
        bind.lifecycleOwner = this
        bind.viewModel = sampleDataBindingActivityViewModel
        return bind
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {
        setTitleText("这是测试DataBinding的示例")
    }

    /**
     * 初始化布局控件
     */
    override fun initViews() {

    }

    /**
     * 初始化控件数据
     */
    override fun initViewData() {
        sampleDataBindingActivityViewModel.text.value = "这是用于显示的文本"

    }

    /**
     * 初始化其他数据
     */
    override fun initOtherData() {

    }

    /**
     * 初始化事件
     */
    override fun initEvents() {
        binding?.changeTextBtn?.setOnClickListener{
            val text = binding?.sampleEt?.text?.toString()?:"这是默认的文本"
            sampleDataBindingActivityViewModel.text.value = text
        }
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {

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
}