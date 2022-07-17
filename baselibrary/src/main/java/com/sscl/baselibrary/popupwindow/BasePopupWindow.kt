package com.sscl.baselibrary.popupwindow

import android.content.*
import android.util.AttributeSet
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.sscl.baselibrary.utils.BaseManager

/**
 * 自定义PopupWindow基类
 *
 * @author pengh
 */
abstract class BasePopupWindow @JvmOverloads constructor(
    /**
     * 返回创建这个PopupWindow时的上下文
     *
     * @return 上下文
     */
    /*--------------------------------构造方法--------------------------------*/
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
    /*--------------------------------静态常量--------------------------------*/
    protected val TAG: String = javaClass.getSimpleName()
    /*--------------------------------抽象方法--------------------------------*/
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
    /*--------------------------------子类可用方法--------------------------------*/
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
    /**
     * 显示popup window
     *
     * @param parent  父布局
     * @param gravity Gravity
     */
    /**
     * 显示popup window
     *
     * @param parent 父布局
     */
    @JvmOverloads
    fun show(parent: View, gravity: Int = Gravity.CENTER, x: Int = 0, y: Int = 0) {
        showAtLocation(parent, gravity, x, y)
    }
    /**
     * 构造方法
     *
     * @param context      上下文
     * @param attrs        控件属性
     * @param defStyleAttr 默认的控件属性
     * @param defStyleRes  默认的风格属性
     * @param width        宽度
     * @param height       高度
     * @param focusable    是否能获取焦点
     */
    /**
     * 构造方法
     *
     * @param context      上下文
     * @param attrs        控件属性
     * @param defStyleAttr 默认的控件属性
     * @param defStyleRes  默认的风格属性
     * @param width        宽度
     * @param height       高度
     */
    /**
     *
     * Create a new, empty, non focusable popup window of dimension (0,0).
     *
     *
     * The popup does not provide a background.
     *
     * @param context      上下文
     * @param attrs        控件属性
     * @param defStyleAttr 默认的控件属性
     * @param defStyleRes  默认的风格属性
     */
    /**
     *
     * Create a new empty, non focusable popup window of dimension (0,0).
     *
     *
     * The popup does provide a background.
     *
     * @param context      上下文
     * @param attrs        控件属性
     * @param defStyleAttr 默认的控件属性
     */
    /**
     *
     * Create a new empty, non focusable popup window of dimension (0,0).
     *
     *
     * The popup does provide a background.
     *
     * @param context 上下文
     * @param attrs   控件属性
     */
    /**
     *
     * Create a new empty, non focusable popup window of dimension (0,0).
     *
     *
     * The popup does provide a background.
     *
     * @param context 上下文
     */
    init {
        val layoutRes: Int = setLayout()
        if (layoutRes == 0) {
            throw IllegalArgumentException("setLayout returned 0")
        }
        val contentView: View = View.inflate(context, layoutRes, null)
        setContentView(contentView)
        setWidth(width)
        setHeight(height)
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
}