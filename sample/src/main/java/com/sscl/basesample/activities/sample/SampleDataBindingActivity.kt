package com.sscl.basesample.activities.sample

import android.view.LayoutInflater
import androidx.activity.viewModels
import com.sscl.baselibrary.activity.BaseDataBindingAppCompatActivity
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
     * 初始化数据绑定
     */
    override fun inflateLayout(layoutInflater: LayoutInflater): ComSsclBasesampleActivitySampleDataBindingBinding {
        val binding = ComSsclBasesampleActivitySampleDataBindingBinding.inflate(layoutInflater)
        binding.viewModel = sampleDataBindingActivityViewModel
        return binding
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {
        setTitleText("这是测试DataBinding的示例")
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
        binding.changeTextBtn.setOnClickListener{
            val text = binding.sampleEt.text?.toString()?:"这是默认的文本"
            sampleDataBindingActivityViewModel.text.value = text
        }
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {

    }
}