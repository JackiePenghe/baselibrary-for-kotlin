package com.sscl.baselibrary.fragment

import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

import com.sscl.baselibrary.R
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 使用ViewBiding的fragment的基类
 *
 * @author alm
 */
abstract class BaseDataBindingFragment<T : ViewDataBinding> : Fragment() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    @Suppress("PropertyName")
    protected val TAG: String = javaClass.simpleName

    /* * * * * * * * * * * * * * * * * * * 延时初始化属性 * * * * * * * * * * * * * * * * * * */

    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var binding: T

    /**
     * 标题
     */
    private lateinit var toolBar: Toolbar

    /**
     * 标题文本
     */
    private lateinit var titleView: TextView

    /**
     * 标题栏左边的小图标
     */
    private lateinit var titleLeftImage: ImageView

    /**
     * 标题栏左边的文本
     */
    private lateinit var titleLeftText: TextView

    /**
     * 标题栏右边的小图标
     */
    private lateinit var titleRightImage: ImageView

    /**
     * 标题栏右边的文本
     */
    private lateinit var titleRightText: TextView

    /**
     * 用于适配沉浸式状态栏的专用控件
     */
    private lateinit var statusView: View

    /* * * * * * * * * * * * * * * * * * * 私有可空属性 * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contentView: View =
            inflater.inflate(R.layout.com_jackiepenghe_baselibrary_fragment_base, container, false)
        toolBar = contentView.findViewById(R.id.toolbar)
        titleView = contentView.findViewById(R.id.toolbar_title)
        titleLeftImage = contentView.findViewById(R.id.title_left_image)
        titleLeftText = contentView.findViewById(R.id.title_left_text)
        titleRightImage = contentView.findViewById(R.id.title_right_image)
        titleRightText = contentView.findViewById(R.id.title_right_text)
        statusView = contentView.findViewById(R.id.fragment_base_status_bar)
        doBeforeSetLayout()
        val frameLayout: FrameLayout = contentView.findViewById(R.id.base_frame_content)
        binding = DataBindingUtil.inflate(inflater, setLayout(), frameLayout, false)
        frameLayout.addView(binding.root)
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViewData()
        initEvents()
        doAfterAll()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 抽象方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 在最后执行的操作
     */
    protected abstract fun doAfterAll()

    /**
     * 初始化事件
     */
    protected abstract fun initEvents()

    /**
     * 初始化控件数据
     */
    protected abstract fun initViewData()

    /**
     * 初始化数据
     */
    protected abstract fun initData()

    /**
     * 设置fragment的布局
     *
     * @return 布局id
     */
    protected abstract fun setLayout(): Int

    /**
     * 在设置布局之前进行的操作
     */
    protected abstract fun doBeforeSetLayout()

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 自定义子类可用的方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 隐藏标题栏
     */
    protected fun hideTitleBar() {
        toolBar.visibility = View.GONE
    }

    /**
     * 将整个fragment的所有布局隐藏
     */
    protected fun hideAll() {
        binding.root.visibility = View.GONE
    }

    /**
     * 将整个fragment的所有布局显示
     */
    protected fun showAll() {
        binding.root.visibility = View.VISIBLE
    }

    /**
     * 显示标题栏
     */
    protected fun showTitle() {
        toolBar.visibility = View.VISIBLE
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleRes 标题栏的资源id
     */
    protected fun setTitleText(@StringRes titleRes: Int) {
        toolBar.visibility = View.VISIBLE
        titleView.setText(titleRes)
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleText 标题栏的文本
     */
    protected fun setTitleText(titleText: String?) {
        toolBar.visibility = View.VISIBLE
        titleView.text = titleText
    }

    /**
     * 设置标题栏左边的图片
     *
     * @param drawableRes 图片资源文件
     */
    protected fun setTitleLeftImage(@DrawableRes drawableRes: Int) {
        titleLeftImage.visibility = View.VISIBLE
        titleLeftImage.setImageResource(drawableRes)
    }

    /**
     * 隐藏标题栏左边的图片
     */
    protected fun hideTitleLeftImage() {
        titleLeftImage.visibility = View.GONE
    }

    /**
     * 设置标题栏左边的文本内容
     *
     * @param textRes 文本资源
     */
    protected fun setTitleLeftText(@StringRes textRes: Int) {
        titleLeftImage.visibility = View.VISIBLE
        titleLeftText.setText(textRes)
    }

    /**
     * 设置标题栏左边的文本内容
     *
     * @param text 文本
     */
    protected fun setTitleLeftText(text: String?) {
        titleLeftImage.visibility = View.VISIBLE
        titleLeftText.text = text
    }

    /**
     * 隐藏标题栏左边的文本
     */
    protected fun hideTitleLeftText() {
        titleLeftText.visibility = View.GONE
    }

    /**
     * 设置标题栏右边的图片
     *
     * @param drawableRes 图片资源文件
     */
    protected fun setTitleRightImage(@DrawableRes drawableRes: Int) {
        titleRightImage.visibility = View.VISIBLE
        titleRightImage.setImageResource(drawableRes)
    }

    /**
     * 隐藏标题栏右边的小图标
     */
    protected fun hideTitleRightImage() {
        titleRightImage.visibility = View.GONE
    }

    /**
     * 设置标题栏右边的文字
     *
     * @param textRes 文本资源
     */
    protected fun setTitleRightText(@StringRes textRes: Int) {
        titleRightText.visibility = View.VISIBLE
        titleRightText.setText(textRes)
    }

    /**
     * 设置标题栏右边的文字
     *
     * @param text 文本
     */
    protected fun setTitleRightText(text: String?) {
        titleRightText.visibility = View.VISIBLE
        titleRightText.text = text
    }

    /**
     * 隐藏标题栏右边的文本
     */
    protected fun hideTitleRightText() {
        titleRightText.visibility = View.GONE
    }

    /**
     * 设置标题栏左边的图片的点击事件
     */
    protected fun setTitleLeftImageClickListener(clickListener: View.OnClickListener?) {
        titleLeftImage.isClickable = true
        titleLeftImage.setOnClickListener(clickListener)
    }

    /**
     * 设置标题栏左边的文字的点击事件
     */
    protected fun setTitleLeftTextClickListener(clickListener: View.OnClickListener?) {
        titleLeftText.isClickable = true
        titleLeftText.setOnClickListener(clickListener)
    }

    /**
     * 设置标题栏右边的图片的点击事件
     */
    protected fun setTitleRightImageClickListener(clickListener: View.OnClickListener?) {
        titleRightImage.isClickable = true
        titleRightImage.setOnClickListener(clickListener)
    }

    /**
     * 设置标题栏右边的文字的点击事件
     */
    protected fun setTitleRightTextClickListener(clickListener: View.OnClickListener?) {
        titleRightText.isClickable = true
        titleRightText.setOnClickListener(clickListener)
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 状态栏颜色
     */
    protected fun setFragmentStatusColor(@ColorInt color: Int) {
        statusView.visibility = View.VISIBLE
        statusView.setBackgroundColor(color)
    }

    /**
     * 设置状态栏颜色
     *
     * @param colorRes 状态栏颜色
     */
    protected fun setFragmentStatusColorRes(@ColorRes colorRes: Int) {
        statusView.visibility = View.VISIBLE
        statusView.setBackgroundResource(colorRes)
    }

    /**
     * 隐藏Fragment的状态栏控件
     */
    protected fun hideFragmentStatusView() {
        statusView.visibility = View.GONE
    }

    /**
     * 显示Fragment的状态栏控件
     */
    protected fun showFragmentStatusView() {
        statusView.visibility = View.VISIBLE
    }
}