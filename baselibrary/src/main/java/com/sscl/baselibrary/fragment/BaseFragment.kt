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

/**
 * fragment的基类
 *
 * @author alm
 */
abstract class BaseFragment : Fragment() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    protected val TAG: String = javaClass.simpleName

   /* * * * * * * * * * * * * * * * * * * protected可空属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 获取此界面的根布局
     *
     * @return 此界面的根布局
     */
    /**
     * 整个Fragment的根布局
     */
    protected var rootView: View? = null
        private set

    /* * * * * * * * * * * * * * * * * * * 私有可空属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 标题
     */
    private var toolBar: Toolbar? = null

    /**
     * 标题文本
     */
    private var titleView: TextView? = null

    /**
     * 标题栏左边的小图标
     */
    private var titleLeftImage: ImageView? = null

    /**
     * 标题栏左边的文本
     */
    private var titleLeftText: TextView? = null

    /**
     * 标题栏右边的小图标
     */
    private var titleRightImage: ImageView? = null

    /**
     * 标题栏右边的文本
     */
    private var titleRightText: TextView? = null

    /**
     * 用于适配沉浸式状态栏的专用控件
     */
    private var statusView: View? = null

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
        rootView = inflater.inflate(setLayout(), frameLayout, false)
        frameLayout.addView(rootView)
        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
        initViews()
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
     * 在设置布局之前进行的操作
     */
    protected abstract fun doBeforeSetLayout()

    /**
     * 设置fragment的布局
     *
     * @return 布局id
     */
    protected abstract fun setLayout(): Int

    /**
     * 初始化数据
     */
    protected abstract fun initData()

    /**
     * 初始化控件
     */
    protected abstract fun initViews()

    /**
     * 初始化控件数据
     */
    protected abstract fun initViewData()

    /**
     * 初始化事件
     */
    protected abstract fun initEvents()

    /**
     * 在最后执行的操作
     */
    protected abstract fun doAfterAll()

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 自定义子类可用的方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 让fragment可以和Activity一样拥有findViewById函数
     *
     * @param viewId 控件id
     * @return 控件
     */
    protected fun <T : View?> findViewById(@IdRes viewId: Int): T? {
        return rootView?.findViewById<T>(viewId)
    }

    /**
     * 隐藏标题栏
     */
    protected fun hideTitleBar() {
        toolBar?.visibility = View.GONE
    }

    /**
     * 将整个fragment的所有布局隐藏
     */
    protected fun hideAll() {
        rootView?.visibility = View.GONE
    }

    /**
     * 将整个fragment的所有布局显示
     */
    protected fun showAll() {
        rootView?.visibility = View.VISIBLE
    }

    /**
     * 显示标题栏
     */
    protected fun showTitle() {
        toolBar?.visibility = View.VISIBLE
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleRes 标题栏的资源id
     */
    protected fun setTitleText(@StringRes titleRes: Int) {
        toolBar?.visibility = View.VISIBLE
        titleView?.setText(titleRes)
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleText 标题栏的文本
     */
    protected fun setTitleText(titleText: String?) {

        toolBar?.visibility = View.VISIBLE
        titleView?.text = titleText
    }

    /**
     * 设置标题栏左边的图片
     *
     * @param drawableRes 图片资源文件
     */
    protected fun setTitleLeftImage(@DrawableRes drawableRes: Int) {
        titleLeftImage?.visibility = View.VISIBLE
        titleLeftImage?.setImageResource(drawableRes)
    }

    /**
     * 隐藏标题栏左边的图片
     */
    protected fun hideTitleLeftImage() {
        titleLeftImage?.visibility = View.GONE
    }

    /**
     * 设置标题栏左边的文本内容
     *
     * @param textRes 文本资源
     */
    protected fun setTitleLeftText(@StringRes textRes: Int) {
        titleLeftImage?.visibility = View.VISIBLE
        titleLeftText?.setText(textRes)
    }

    /**
     * 设置标题栏左边的文本内容
     *
     * @param text 文本
     */
    protected fun setTitleLeftText(text: String?) {
        titleLeftImage?.visibility = View.VISIBLE
        titleLeftText?.text = text
    }

    /**
     * 隐藏标题栏左边的文本
     */
    protected fun hideTitleLeftText() {
        titleLeftText?.visibility = View.GONE
    }

    /**
     * 设置标题栏右边的图片
     *
     * @param drawableRes 图片资源文件
     */
    protected fun setTitleRightImage(@DrawableRes drawableRes: Int) {
        titleRightImage?.visibility = View.VISIBLE
        titleRightImage?.setImageResource(drawableRes)
    }

    /**
     * 隐藏标题栏右边的小图标
     */
    protected fun hideTitleRightImage() {
        titleRightImage?.visibility = View.GONE
    }

    /**
     * 设置标题栏右边的文字
     *
     * @param textRes 文本资源
     */
    protected fun setTitleRightText(@StringRes textRes: Int) {
        titleRightText?.visibility = View.VISIBLE
        titleRightText?.setText(textRes)
    }

    /**
     * 设置标题栏右边的文字
     *
     * @param text 文本
     */
    protected fun setTitleRightText(text: String?) {
        titleRightText?.visibility = View.VISIBLE
        titleRightText?.text = text
    }

    /**
     * 隐藏标题栏右边的文本
     */
    protected fun hideTitleRightText() {
        titleRightText?.visibility = View.GONE
    }

    /**
     * 设置标题栏左边的图片的点击事件
     */
    protected fun setTitleLeftImageClickListener(clickListener: View.OnClickListener?) {
        titleLeftImage?.isClickable = true
        titleLeftImage?.setOnClickListener(clickListener)
    }

    /**
     * 设置标题栏左边的文字的点击事件
     */
    protected fun setTitleLeftTextClickListener(clickListener: View.OnClickListener?) {
        titleLeftText?.isClickable = true
        titleLeftText?.setOnClickListener(clickListener)
    }

    /**
     * 设置标题栏右边的图片的点击事件
     */
    protected fun setTitleRightImageClickListener(clickListener: View.OnClickListener?) {
        titleRightImage?.isClickable = true
        titleRightImage?.setOnClickListener(clickListener)
    }

    /**
     * 设置标题栏右边的文字的点击事件
     */
    protected fun setTitleRightTextClickListener(clickListener: View.OnClickListener?) {
        titleRightText?.isClickable = true
        titleRightText?.setOnClickListener(clickListener)
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 状态栏颜色
     */
    protected fun setFragmentStatusColor(@ColorInt color: Int) {
        statusView?.visibility = View.VISIBLE
        statusView?.setBackgroundColor(color)
    }

    /**
     * 设置状态栏颜色
     *
     * @param colorRes 状态栏颜色
     */
    protected fun setFragmentStatusColorRes(@ColorRes colorRes: Int) {
        statusView?.visibility = View.VISIBLE
        statusView?.setBackgroundResource(colorRes)
    }

    /**
     * 隐藏Fragment的状态栏控件
     */
    protected fun hideFragmentStatusView() {
        statusView?.visibility = View.GONE
    }

    /**
     * 显示Fragment的状态栏控件
     */
    protected fun showFragmentStatusView() {
        statusView?.visibility = View.VISIBLE
    }
}