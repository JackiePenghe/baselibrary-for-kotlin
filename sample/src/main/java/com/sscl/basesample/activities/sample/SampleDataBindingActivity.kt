package com.sscl.basesample.activities.sample

import androidx.activity.viewModels
import com.sscl.baselibrary.activity.BaseDataBindingAppCompatActivity
import com.sscl.basesample.R
import com.sscl.basesample.databinding.ComSsclBasesampleActivitySampleDataBindingBinding
import com.sscl.basesample.viewmodel.SampleDataBindingActivityViewModel

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
     */
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_sample_data_binding
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {
        setTitleText("这是测试DataBinding的示例")
        binding.viewModel = sampleDataBindingActivityViewModel
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
        binding.changeTextBtn.setOnClickListener {
            val text = binding.sampleEt.text?.toString() ?: "这是默认的文本"
            sampleDataBindingActivityViewModel.text.value = text
        }
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {

    }
}