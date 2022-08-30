package com.sscl.baselibrary.popupwindow

import android.content.*
import android.util.AttributeSet
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.sscl.baselibrary.utils.BaseManager

/**
 * 自定义PopupWindow基类
 *
 * @author pengh
 */
abstract class BaseDataBindingPopupWindow<T : ViewDataBinding> @JvmOverloads constructor(
    protected val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    width: Int = 0,
    height: Int = 0,
    focusable: Boolean = false
) : PopupWindow(
    context, attrs, defStyleAttr, defStyleRes
) {

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
    protected val binding: T

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    init {
        val layoutRes: Int = this.setLayout()
        if (layoutRes == 0) {
            throw IllegalArgumentException("setLayout returned 0")
        }
        val contentView: View = View.inflate(context, layoutRes, null)
        binding = DataBindingUtil.bind(contentView)!!
        this.contentView = contentView
        this.width = width
        this.height = height
        isFocusable = focusable
        BaseManager.handler.post {
            doBeforeInitOthers()
            initViews()
            initViewData()
            initOtherData()
            initEvents()
            doAfterAll()
        }
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 抽象方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 设置布局
     * @return 布局文件资源ID
     */
    @LayoutRes
    protected abstract fun setLayout(): Int

    /**
     * 在初始化其他数据之前执行的操作
     */
    protected abstract fun doBeforeInitOthers()

    /**
     * 初始化控件
     */
    protected abstract fun initViews()

    /**
     * 初始化控件的数据
     */
    protected abstract fun initViewData()

    /**
     * 初始化其他数据
     */
    protected abstract fun initOtherData()

    /**
     * 初始化事件
     */
    protected abstract fun initEvents()

    /**
     * 在最后执行的操纵
     */
    protected abstract fun doAfterAll()

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * protected方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 让PopupWindow可以和Activity一样拥有findViewById方法
     *
     * @param viewId 控件id
     * @return 控件
     */
    protected fun <T : View> findViewById(@IdRes viewId: Int): T {
        val contentView: View? = contentView
        if (contentView != null) {
            return contentView.findViewById(viewId)
        }
        throw NullPointerException("contentView is null")
    }

    /**
     * 显示popup window
     *
     * @param parent  父布局
     * @param gravity Gravity
     * @param x       x坐标
     * @param y       y坐标
     */
    @JvmOverloads
    fun show(parent: View, gravity: Int = Gravity.CENTER, x: Int = 0, y: Int = 0) {
        showAtLocation(parent, gravity, x, y)
    }
}