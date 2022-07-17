package com.sscl.baselibrary.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.sscl.baselibrary.R
import com.sscl.baselibrary.utils.BaseManager
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 * 源文件来自 （https://github.com/FENGZEYOU123/yfz_view_password.git）
 * 简介：自定义验证码输入框 (组合+自绘)
 * 作者：游丰泽
 * 主要功能:
 * （以下功能涉及到盒子样式的改变，均可单独控制功能的盒子样式,默认为画笔，可自定设置backgroundDrawable替代）
 *
 *
 * mBox_notInput_backgroundDrawable;//盒子未输入内容背景Drawable
 * mBox_hasInput_backgroundDrawable;//盒子已输入内容背景Drawable
 * mBox_highLight_backgroundDrawable;//盒子高亮背景Drawable
 * mBox_locked_backgroundDrawable;//盒子锁定状态下背景Drawable
 *
 *
 * mEnableHideCode 是否隐藏输入内容
 * mEnableHighLight 是否开启高亮
 * mEnableCursor 是否开启光标
 * mEnableHideNotInputBox 是否将没有输入内容的盒子隐藏
 * mEnableSoftKeyboardAutoClose 开关自动关闭软键盘
 * mEnableSoftKeyboardAutoShow 开关自动展现软键盘
 * mEnableLockCodeTextIfMaxCode 开关输入内容满足长度后是否锁定
 *
 * @author 游丰泽
 */
class PasswordView : LinearLayout {


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 接口回调输入结果
     */
    interface OnResultListener {
        /**
         * 输入完成
         *
         * @param result 输入的结果
         */
        fun finish(result: String?)

        /**
         * 输入中
         *
         * @param typing 输入的内容
         */
        fun typing(typing: String?)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    companion object {

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 属性声明
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /**
         * 默认的隐藏输入过的盒子展示的内容
         */
        private const val BOX_DEFAULT_HIDE_TEXT: String = "*"

        /**
         * 默认的盒子输入过的颜色
         */
        private const val BOX_DEFAULT_HAS_INPUT_COLOR: Int = Color.RED

        /**
         * 默认的盒子未输入过的颜色
         */
        private const val BOX_DEFAULT_NOT_INPUT_COLOR: Int = Color.BLUE

        /**
         * 默认的盒子高亮的颜色
         */
        private const val BOX_DEFAULT_HIGH_LIGHT_COLOR: Int = Color.CYAN

        /**
         * 默认的盒子光标的颜色
         */
        private const val BOX_DEFAULT_CURSOR_COLOR: Int = Color.BLACK

        /**
         * 默认的盒子锁定状态下的颜色
         */
        private const val BOX_DEFAULT_LOCK_COLOR: Int = Color.GRAY

        /**
         * 默认的盒子宽度
         */
        private const val BOX_DEFAULT_STROKE_WIDTH: Int = 1

        /**
         * 圆弧半径
         */
        private const val BOX_DEFAULT_RADIUS: Float = 5f

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 私有静态内部类
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /**
         * 密码输入文本监听器
         */
        private class PasswordTextWatcher constructor(
            /**
             * 密码控件
             */
            private val mPasswordView: PasswordView
        ) : TextWatcher {

            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
             *
             * 实现方法
             *
             * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

            override fun beforeTextChanged(
                text: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence, start: Int, end: Int, count: Int) {}

            override fun afterTextChanged(text: Editable) {
                mPasswordView.mBoxNextInputIndex = text.length //高亮盒子下坐标=当前输入内容长度
                val codeArray = mPasswordView.mCodeArray ?: return
                for (i in 0 until mPasswordView.mBoxSetNumber) {
                    if (i <= text.length - 1) {
                        codeArray[i] = text.toString().substring(i, i + 1)
                    } else {
                        codeArray[i] = ""
                    }
                }
                mPasswordView.mCursorDisplayingByIndex = true
                if (text.length == mPasswordView.mBoxSetNumber) { //内容长度与盒子数量一致->返回回调结果
                    mPasswordView.mIsCodeFull = true
                    mPasswordView.onResultListener?.finish(text.toString())
                    if (mPasswordView.enableSoftKeyboardAutoHide || mPasswordView.enableLockCodeTextIfMaxCode) {
                        mPasswordView.closeSoftKeyboard()
                    }
                    mPasswordView.isLocked = true
                } else {
                    mPasswordView.onResultListener?.typing(text.toString())
                }
                mPasswordView.postInvalidate()
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 是否隐藏输入code
     */
    private var enableHideCode: Boolean = false

    /**
     * 是否开启高亮
     */
    private var enableHighLight: Boolean = false

    /**
     * 是否开启光标
     */
    private var enableCursor: Boolean = false

    /**
     * 是否将没有输入内容的盒子隐藏
     */
    private var enableHideNotInputBox: Boolean = false

    /**
     * 是否自动打开软键盘
     */
    private var enableSoftKeyboardAutoShow: Boolean = true

    /**
     * 是否自动关闭软键盘（输入内容长度==最大长度时会自动关闭软键盘）
     */
    private var enableSoftKeyboardAutoHide: Boolean = true

    /**
     * 是否限制输满后锁定view
     */
    private var enableLockCodeTextIfMaxCode: Boolean = false

    /* * * * * * * * * * * * * * * * * * * 默认设置-盒子画笔相关 * * * * * * * * * * * * * * * * * * */
    /**
     * 笔刷
     */
    private val mBoxDefaultPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 盒子输入过的颜色
     */
    @ColorInt
    private var boxHasInputColor: Int = BOX_DEFAULT_HAS_INPUT_COLOR

    /**
     * 盒子未输入过的颜色
     */
    @ColorInt
    private var boxNotInputColor: Int = BOX_DEFAULT_NOT_INPUT_COLOR

    /**
     * 盒子高亮的颜色
     */
    @ColorInt
    private var boxHighLightColor: Int = BOX_DEFAULT_HIGH_LIGHT_COLOR

    /**
     * 盒子锁定状态下的颜色
     */
    @ColorInt
    private var boxLockColor: Int = BOX_DEFAULT_LOCK_COLOR

    /**
     * 盒子描边的宽度
     */
    private var boxStrokeWidth: Int = BOX_DEFAULT_STROKE_WIDTH

    /* * * * * * * * * * * * * * * * * * * 输入框样式 * * * * * * * * * * * * * * * * * * */

    /**
     * 是否已经锁定
     */
    var isLocked: Boolean = false
        private set

    /**
     * 是否已经输入过
     */
    private var mIsCodeFull: Boolean = false

    /**
     * 是否为输入第一个字符
     */
    private var mIsFirstTime: Int = 0

    /**
     * 待输入盒子的下一个坐标
     */
    private var mBoxNextInputIndex: Int = 0

    /**
     * 隐藏输入code-显示的内容
     */
    private var hideCodeText: String? = BOX_DEFAULT_HIDE_TEXT

    /**
     * 背景
     */
    @DrawableRes
    private var mViewBackground: Int =
        R.drawable.com_jackiepenghe_baselibrary_background_password_view

    /* * * * * * * * * * * * * * * * * * * 盒子 * * * * * * * * * * * * * * * * * * */
    /**
     * 矩形（绘制位置）
     */
    private var mBoxRectF: RectF? = null

    /**
     * 盒子的数量
     */
    private var mBoxSetNumber: Int = 4

    /**
     * 盒子的大小
     */
    private var mBoxSetSize: Int = 50

    /**
     * 盒子的间距
     */
    private var mBoxSetMargin: Int = 10

    /**
     * 盒子的圆角半径
     */
    private var mBoxRadius: Float = BOX_DEFAULT_RADIUS

    /**
     * 盒子未输入内容背景Drawable
     */
    private var mBoxNotInputBackgroundDrawable: Drawable? = null

    /**
     * 盒子已输入内容背景Drawable
     */
    private var mBoxHasInputBackgroundDrawable: Drawable? = null

    /**
     * 盒子高亮背景Drawable
     */
    private var mBoxHighLightBackgroundDrawable: Drawable? = null

    /**
     * 盒子锁定状态下背景Drawable
     */
    private var mBoxLockedBackgroundDrawable: Drawable? = null

    /* * * * * * * * * * * * * * * * * * * 文字 * * * * * * * * * * * * * * * * * * */

    /**
     * 笔刷
     */
    private val mPaintText = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 矩形（绘制位置）
     */
    private var mTextRect: Rect? = null

    /**
     * 输入Code内容
     */
    private var mCodeArray: Array<String>? = null

    /**
     * 颜色
     */
    private var mTextColor: Int = Color.BLACK

    /**
     * 大小
     */
    private var mTextSize: Int = 10

    /**
     * 类型
     */
    private var mTextInputType: Int = InputType.TYPE_CLASS_NUMBER

    /**
     * 是否加粗
     */
    private var mTextBold: Boolean = true

    /**
     * 光标闪烁定时器
     */
    private var mCursorTimer: ScheduledExecutorService? = null

    /**
     * 定时器任务
     */
    private var mCursorTimerTask: Runnable? = null

    /**
     * 光标背景Drawable
     */
    private var mCursorBackgroundDrawable: Drawable? = null

    /**
     * 光标颜色
     */
    private var mCursorColor: Int = 0

    /**
     * 光标上下边距
     */
    private var mCursorHeightMargin: Int = 1

    /**
     * 光标左右边距
     */
    private var mCursorWidth: Int = 1

    /**
     * 光标闪烁间隔时间
     */
    private var mCursorFrequency: Int = 500

    /**
     * 显示光标-定时器-闪烁效果
     */
    private var mCursorDisplayingByTimer: Boolean = false

    /**
     * 显示光标-第一次下坐标
     */
    private var mCursorDisplayingByIndex: Boolean = false

    /**
     * 文本输入框
     */
    private var mEditText: EditText? = null

    /**
     * 文本输入完成的回调
     */
    private var onResultListener: OnResultListener? = null

    /**
     * 输入法管理器
     */
    private var inputMethodManager: InputMethodManager? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttr(context, attrs)
        //初始化
        initial()
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //测量-CodeText大小
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measureWidthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        var measureWidthSize: Int = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeightMode: Int = MeasureSpec.getMode(heightMeasureSpec)
        var measureHeightSize: Int = MeasureSpec.getSize(heightMeasureSpec)
        if (measureWidthMode == MeasureSpec.AT_MOST && measureHeightMode == MeasureSpec.AT_MOST) {
            //宽高均未声明绝对值
            //组件宽 = (盒子大小*数量)+（盒子边距*(数量-1))+画笔宽度
            //组件高 = (盒子大小)
            measureWidthSize = mBoxSetSize * (mBoxSetNumber) + mBoxSetMargin * (mBoxSetNumber - 1)
            measureHeightSize = mBoxSetSize
            setMeasuredDimension(measureWidthSize, measureHeightSize)
        } else if (measureWidthMode == MeasureSpec.EXACTLY && measureHeightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(measureWidthSize, measureHeightSize)
        } else if (measureWidthMode == MeasureSpec.EXACTLY && measureHeightMode == MeasureSpec.AT_MOST) {
            //只声明了宽的绝对值，高未声明
            measureHeightSize = mBoxSetSize
            setMeasuredDimension(measureWidthSize, measureHeightSize)
        } else if (measureHeightMode == MeasureSpec.EXACTLY && measureWidthMode == MeasureSpec.AT_MOST) {
            //只声明了高的绝对值，宽未声明
            measureWidthSize = mBoxSetSize * (mBoxSetNumber) + mBoxSetMargin * (mBoxSetNumber - 1)
            setMeasuredDimension(measureWidthSize, measureHeightSize)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val mBoxRectF = mBoxRectF ?: return
        for (i in 0 until mBoxSetNumber) {
            mBoxRectF.left = ((mBoxSetSize + mBoxSetMargin) * i).toFloat()
            mBoxRectF.top = 0f
            mBoxRectF.right = mBoxRectF.left + mBoxSetSize
            mBoxRectF.bottom = height.toFloat()
            //如果开启了高亮或鼠标 且 i==待输入index
            if (enableHighLight || enableCursor) {
                if (i == mBoxNextInputIndex) {
                    //绘制盒子 - 高亮或光标
                    onDrawHighLightCursor(canvas)
                }
            } else if ((mCodeArray?.get(i)?.length ?: 0) >= 1) {
                //绘制盒子 - 已输入过内容
                onDrawHasInput(canvas, i)
            } else {
                //绘制盒子 - 未输入过内容
                onDrawNotInput(canvas)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (enableCursor) {
            if (null != mCursorTimer) {
                mCursorTimer?.scheduleAtFixedRate(
                    mCursorTimerTask,
                    0,
                    mCursorFrequency.toLong(),
                    TimeUnit.MILLISECONDS
                )
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mCursorTimer?.shutdownNow()
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 锁定CodeText
     */
    fun setOnLock() {
        enableLockCodeTextIfMaxCode = true
        isLocked = true
    }

    /**
     * 解除锁定CodeText
     */
    fun setUnLock() {
        if (mIsCodeFull) {
            openSoftKeyboard()
            isLocked = false
        }
    }

    /**
     * 打开软键盘
     */
    fun openSoftKeyboard() {

        if (null != mEditText) {
            mEditText?.isFocusable = true
            mEditText?.isFocusableInTouchMode = true
            mEditText?.requestFocus()
            inputMethodManager?.showSoftInput(mEditText, 0)
        }
    }

    /**
     * 关闭软键盘
     */
    fun closeSoftKeyboard() {
        if (null != mEditText) {
            if (enableSoftKeyboardAutoHide || isLocked || enableLockCodeTextIfMaxCode) {
                mEditText?.clearFocus()
                inputMethodManager?.hideSoftInputFromWindow(mEditText?.getWindowToken(), 0)
            }
        }
    }

    /**
     * 设置监听接口回调
     *
     * @param onResultListener 监听接口
     */
    fun setOnResultListener(onResultListener: OnResultListener?) {
        this.onResultListener = onResultListener
    }

    /**
     * 设置是否开启隐匿输入内容
     *
     * @param enableHideCode 是否开启隐匿输入内容
     */
    fun setEnableHideCode(enableHideCode: Boolean) {
        this.enableHideCode = enableHideCode
    }

    /**
     * 设置是否开启高亮
     *
     * @param enableHighLight 是否开启高亮
     */
    fun setEnableHighLight(enableHighLight: Boolean) {
        this.enableHighLight = enableHighLight
    }

    /**
     * 设置是否开启光标
     *
     * @param enableCursor 是否开启光标
     */
    fun setEnableCursor(enableCursor: Boolean) {
        this.enableCursor = enableCursor
    }

    /**
     * 设置 是否隐藏未输入内容的盒子
     *
     * @param enableHideNotInputBox 是否隐藏未输入内容的盒子
     */
    fun setEnableHideNotInputBox(enableHideNotInputBox: Boolean) {
        this.enableHideNotInputBox = enableHideNotInputBox
    }

    /**
     * 设置是否开启软键盘自动显示
     *
     * @param enableSoftKeyboardAutoShow 是否开启软键盘自动显示
     */
    fun setEnableSoftKeyboardAutoShow(enableSoftKeyboardAutoShow: Boolean) {
        this.enableSoftKeyboardAutoShow = enableSoftKeyboardAutoShow
    }

    /**
     * 设置是否开启软键盘自动关闭
     *
     * @param enableSoftKeyboardAutoHide 是否开启软键盘自动关闭
     */
    fun setEnableSoftKeyboardAutoHide(enableSoftKeyboardAutoHide: Boolean) {
        this.enableSoftKeyboardAutoHide = enableSoftKeyboardAutoHide
    }

    /**
     * 设置是否开户自动锁定文本（当文本输入达到最大数量之后）
     *
     * @param enableLockCodeTextIfMaxCode 是否开户自动锁定文本（当文本输入达到最大数量之后）
     */
    fun setEnableLockCodeTextIfMaxCode(enableLockCodeTextIfMaxCode: Boolean) {
        this.enableLockCodeTextIfMaxCode = enableLockCodeTextIfMaxCode
    }

    /**
     * 设置盒子有输入内容的颜色
     *
     * @param boxHasInputColor 盒子有输入内容的颜色
     */
    fun setBoxHasInputColor(boxHasInputColor: Int) {
        this.boxHasInputColor = boxHasInputColor
    }

    /**
     * 设置盒子无输入内容的颜色
     *
     * @param boxNotInputColor 盒子无输入内容的颜色
     */
    fun setBoxNotInputColor(boxNotInputColor: Int) {
        this.boxNotInputColor = boxNotInputColor
    }

    /**
     * 设置盒子高亮的颜色
     *
     * @param boxHighLightColor 盒子高亮的颜色
     */
    fun setBoxHighLightColor(boxHighLightColor: Int) {
        this.boxHighLightColor = boxHighLightColor
    }

    /**
     * 设置盒子锁定的颜色
     *
     * @param boxLockColor 盒子锁定的颜色
     */
    fun setBoxLockColor(boxLockColor: Int) {
        this.boxLockColor = boxLockColor
    }

    /**
     * 设置盒子边线的宽度
     *
     * @param boxStrokeWidth 盒子边线的宽度
     */
    fun setBoxStrokeWidth(boxStrokeWidth: Int) {
        this.boxStrokeWidth = boxStrokeWidth
    }

    /**
     * 设置隐藏文本时，显示的替换文本内容
     *
     * @param hideCodeText 替换文本内容
     */
    fun setHideCodeText(hideCodeText: String?) {
        this.hideCodeText = hideCodeText
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 初始化自定义属性
     *
     * @param context 上下文
     * @param attrs   属性集合
     */
    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordView)

        //自动弹出键盘
        enableSoftKeyboardAutoShow = typedArray.getBoolean(
            R.styleable.PasswordView_password_enableSoftKeyboardAutoShow,
            enableSoftKeyboardAutoShow
        )
        //自动隐藏键盘
        enableSoftKeyboardAutoHide = typedArray.getBoolean(
            R.styleable.PasswordView_password_enableSoftKeyboardAutoClose,
            enableSoftKeyboardAutoHide
        )
        //是否隐藏输入内容
        enableHideCode =
            typedArray.getBoolean(R.styleable.PasswordView_password_enableHideCode, enableHideCode)
        //隐藏输入的内容,显示设置的文案
        hideCodeText = typedArray.getString(R.styleable.PasswordView_password_hideCodeText)
        //是否将没有输入内容的盒子隐藏
        enableHideNotInputBox = typedArray.getBoolean(
            R.styleable.PasswordView_password_enableHideBoxWhenNotInput,
            enableHideNotInputBox
        )
        //是否绘制高亮盒子
        enableHighLight = typedArray.getBoolean(
            R.styleable.PasswordView_password_enableHighLight,
            enableHighLight
        )
        //是否绘制光标
        enableCursor =
            typedArray.getBoolean(R.styleable.PasswordView_password_enableCursor, enableCursor)
        //是否锁定组件当输入满长度后(禁止点击,可以主动setUnLock()调解除锁定)
        enableLockCodeTextIfMaxCode = typedArray.getBoolean(
            R.styleable.PasswordView_password_enableLockTextView,
            enableLockCodeTextIfMaxCode
        )
        //View背景Drawable
        mViewBackground = typedArray.getResourceId(
            R.styleable.PasswordView_Password_viewBackground,
            Color.TRANSPARENT
        )
        //文字颜色
        mTextColor = typedArray.getColor(R.styleable.PasswordView_password_textColor, mTextColor)
        //文字大小
        mTextSize =
            typedArray.getDimensionPixelSize(R.styleable.PasswordView_password_textSize, mTextSize)
        //文字输入框类型
        mTextInputType =
            typedArray.getInt(R.styleable.PasswordView_password_inputType, mTextInputType)
        //文字粗细
        mTextBold =
            typedArray.getBoolean(R.styleable.PasswordView_password_text_setIsBold, mTextBold)
        //设置盒子总数量
        mBoxSetNumber =
            typedArray.getInt(R.styleable.PasswordView_password_box_setNumber, mBoxSetNumber)
        //设置盒子间距
        mBoxSetMargin = typedArray.getDimensionPixelSize(
            R.styleable.PasswordView_password_box_setMargin,
            mBoxSetMargin
        )
        //设置盒子大小
        mBoxSetSize = typedArray.getDimensionPixelSize(
            R.styleable.PasswordView_password_box_setSize,
            mBoxSetSize
        )
        //设置盒子-未输入的背景Drawable
        mBoxNotInputBackgroundDrawable =
            typedArray.getDrawable(R.styleable.PasswordView_password_box_notInput_backgroundDrawable)
        //设置盒子-输入后的背景Drawable
        mBoxHasInputBackgroundDrawable =
            typedArray.getDrawable(R.styleable.PasswordView_password_box_hasInput_backgroundDrawable)
        //设置盒子-高亮的背景Drawable
        mBoxHighLightBackgroundDrawable =
            typedArray.getDrawable(R.styleable.PasswordView_password_box_highLight_backgroundDrawable)
        //设置盒子-锁定状态下的背景Drawable
        mBoxLockedBackgroundDrawable =
            typedArray.getDrawable(R.styleable.PasswordView_password_box_locked_backgroundDrawable)
        //设置光标高度
        mCursorHeightMargin = typedArray.getDimensionPixelSize(
            R.styleable.PasswordView_password_cursorHeight,
            mCursorHeightMargin
        )
        //设置光标宽度
        mCursorWidth = typedArray.getDimensionPixelSize(
            R.styleable.PasswordView_password_cursorWidth,
            mCursorWidth
        )
        //设置光标闪烁频率
        mCursorFrequency = typedArray.getInt(
            R.styleable.PasswordView_password_cursorFrequencyMillisecond,
            mCursorFrequency
        )
        //设置光标颜色
        mCursorColor = typedArray.getInt(
            R.styleable.PasswordView_password_cursorColor,
            BOX_DEFAULT_CURSOR_COLOR
        )
        //设置光标背景
        mCursorBackgroundDrawable =
            typedArray.getDrawable(R.styleable.PasswordView_password_cursorBackgroundDrawable)
        //盒子输入过的颜色
        boxHasInputColor = typedArray.getColor(
            R.styleable.PasswordView_password_box_hasInput_setColor,
            boxHasInputColor
        )
        //盒子未输入过的颜色
        boxNotInputColor = typedArray.getColor(
            R.styleable.PasswordView_password_box_notInput_setColor,
            boxNotInputColor
        )
        //盒子高亮的颜色
        boxHighLightColor = typedArray.getColor(
            R.styleable.PasswordView_password_box_highLight_setColor,
            boxHighLightColor
        )
        //盒子锁定状态下的颜色
        boxLockColor =
            typedArray.getColor(R.styleable.PasswordView_password_box_locked_setColor, boxLockColor)
        //盒子宽度
        boxStrokeWidth =
            typedArray.getInt(R.styleable.PasswordView_password_box_strokeWidth, boxStrokeWidth)
        //盒子的圆角
        mBoxRadius = typedArray.getDimension(
            R.styleable.PasswordView_password_box_Radius,
            BOX_DEFAULT_RADIUS
        )
        //文本
        val text: String? = typedArray.getString(R.styleable.PasswordView_password_text)
        if (mEditText != null) {
            mEditText?.setText(text)
        }
        //回收
        typedArray.recycle()
    }

    /**
     * 初始化-CodeText
     */
    private fun initial() {
        try {
            val drawable: Drawable? = ContextCompat.getDrawable(context, mViewBackground)
            background = drawable
        } catch (e: Exception) {
            setBackgroundColor(mViewBackground)
        }
        mCodeArray = Array(mBoxSetNumber) { "" }

        if (null == hideCodeText) {
            hideCodeText = BOX_DEFAULT_HIDE_TEXT
        } else if ((hideCodeText?.length ?: 0) > 0) {
            hideCodeText = hideCodeText?.substring(0, 1)
        }
        mCursorTimerTask = Runnable {
            mCursorDisplayingByTimer = !mCursorDisplayingByTimer
            postInvalidate()
        }
        mCursorTimer = BaseManager.newScheduledExecutorService(1)
        initialEditText()
        initialPaint()
        initialBoxAndRectPosition()
        setOnLayoutListener()
        setOnClickListener {
            if ((!isLocked || !enableLockCodeTextIfMaxCode)) {
                openSoftKeyboard()
            }
        }
    }

    /**
     * 初始化EdiText
     */
    private fun initialEditText() {
        mEditText = EditText(context)

        val editText = mEditText ?: return
        val layoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        editText.layoutParams = layoutParams
        editText.setBackgroundColor(Color.TRANSPARENT)
        editText.setTextColor(Color.TRANSPARENT)
        this.addView(editText)
        editText.width = 1
        editText.height = 1
        editText.filters = arrayOf<InputFilter>(LengthFilter(mBoxSetNumber))
        editText.inputType = mTextInputType
        editText.setSingleLine()
        editText.isCursorVisible = false
        inputMethodManager =
            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        editText.addTextChangedListener(PasswordTextWatcher(this))
    }

    /**
     * 初始化-盒子和位置
     */
    private fun initialBoxAndRectPosition() {
        mBoxRectF = RectF()
        mTextRect = Rect()
    }

    /**
     * 初始化-笔刷
     */
    private fun initialPaint() {
        //文字

        mPaintText.style = Paint.Style.FILL
        mPaintText.textSize = (mTextSize * 2).toFloat()
        mPaintText.color = mTextColor
        mPaintText.isFakeBoldText = mTextBold
        //盒子
        mBoxDefaultPaint.style = Paint.Style.STROKE
        mBoxDefaultPaint.strokeWidth = boxStrokeWidth.toFloat()
    }

    /**
     * 监听View是否渲染完成,如果开启了自动弹出软键盘,则弹出
     */
    private fun setOnLayoutListener() {
        mEditText?.viewTreeObserver?.addOnGlobalLayoutListener {
            if (!mIsCodeFull && (mIsFirstTime <= mBoxSetNumber - 1) && enableSoftKeyboardAutoShow) {
                openSoftKeyboard()
                mIsFirstTime++
            }
        }
    }

    /**
     * 绘制盒子 - 未输入过内容
     *
     * @param canvas 画布
     */
    private fun onDrawNotInput(canvas: Canvas) {
        val mBoxRectF = mBoxRectF ?: return

        //如果没有开启隐藏 未输入内容盒子
        if (!enableHideNotInputBox) {
            //如果有设置drawable，则绘制drawable
            if (null != mBoxNotInputBackgroundDrawable) {
                mBoxNotInputBackgroundDrawable?.setBounds(
                    mBoxRectF.left.toInt(),
                    mBoxRectF.top.toInt(),
                    mBoxRectF.right.toInt(),
                    mBoxRectF.bottom.toInt()
                )
                mBoxNotInputBackgroundDrawable?.draw(canvas)
            } else {
                mBoxDefaultPaint.color = boxNotInputColor
                canvas.drawRoundRect(mBoxRectF, mBoxRadius, mBoxRadius, mBoxDefaultPaint)
            }
        }
    }

    /**
     * 绘制盒子 - 已输入过内容 boxAfter样式
     *
     * @param canvas 画布
     * @param index  第几个盒子
     */
    private fun onDrawHasInput(canvas: Canvas, index: Int) {
        val mBoxRectF = mBoxRectF ?: return
        //如果开启了输入完毕锁定内容,则绘制boxLock样式
        if (isLocked && enableLockCodeTextIfMaxCode) {
            //如果有设置高亮drawable,则绘制drawable,没有则用画笔绘制
            if (null != mBoxLockedBackgroundDrawable) {
                mBoxLockedBackgroundDrawable?.setBounds(
                    mBoxRectF.left.toInt(),
                    mBoxRectF.top.toInt(),
                    mBoxRectF.right.toInt(),
                    mBoxRectF.bottom.toInt()
                )
                mBoxLockedBackgroundDrawable?.draw(canvas)
            } else {
                mBoxDefaultPaint.color = boxLockColor
                canvas.drawRoundRect(
                    mBoxRectF,
                    BOX_DEFAULT_RADIUS,
                    BOX_DEFAULT_RADIUS,
                    mBoxDefaultPaint
                )
            }
        } else {
            //如果有设置drawable，则绘制drawable
            if (null != mBoxHasInputBackgroundDrawable) {
                mBoxHasInputBackgroundDrawable?.setBounds(
                    mBoxRectF.left.toInt(),
                    mBoxRectF.top.toInt(),
                    mBoxRectF.right.toInt(),
                    mBoxRectF.bottom.toInt()
                )
                mBoxHasInputBackgroundDrawable?.draw(canvas)
            } else {
                mBoxDefaultPaint.color = boxHasInputColor
                canvas.drawRoundRect(
                    mBoxRectF,
                    BOX_DEFAULT_RADIUS,
                    BOX_DEFAULT_RADIUS,
                    mBoxDefaultPaint
                )
            }
        }

        //绘制输入的内容文字
        mPaintText.getTextBounds(
            if (enableHideCode) hideCodeText else mCodeArray?.get(index) ?: "",
            0,
            mCodeArray?.get(index)?.length ?: 0,
            mTextRect
        )
        canvas.drawText(
            (if (enableHideCode) hideCodeText else mCodeArray?.get(index)) ?: "",
            ((mBoxRectF?.left ?: 0f) + (mBoxRectF?.right ?: 0f)) / 2 - ((mTextRect?.left
                ?: 0) + (mTextRect?.right ?: 0)) / 2.0f,
            ((mBoxRectF?.top ?: 0f) + (mBoxRectF?.bottom ?: 0f)) / 2 - ((mTextRect?.top
                ?: 0) + (mTextRect?.bottom ?: 0)) / 2.0f,
            mPaintText
        )
    }

    /**
     * 绘制盒子 - 高亮或光标
     *
     * @param canvas 画布
     */
    private fun onDrawHighLightCursor(canvas: Canvas) {
        val mBoxRectF = mBoxRectF ?: return
        //如果有设置高亮drawable,则绘制drawable,没有则用画笔绘制
        if (null != mBoxHighLightBackgroundDrawable) {
            mBoxHighLightBackgroundDrawable?.setBounds(
                mBoxRectF.left.toInt(),
                mBoxRectF.top.toInt(),
                mBoxRectF.right.toInt(),
                mBoxRectF.bottom.toInt()
            )
            mBoxHighLightBackgroundDrawable?.draw(canvas)
        } else {
            mBoxDefaultPaint.color = boxHighLightColor
            canvas.drawRoundRect(
                mBoxRectF,
                BOX_DEFAULT_RADIUS,
                BOX_DEFAULT_RADIUS,
                mBoxDefaultPaint
            )
        }
        //如果开启了光标,则绘制光标
        if (enableCursor) {
            onDrawCursor(canvas, mBoxDefaultPaint, mBoxRectF ?: return)
        }
    }

    /**
     * 绘制-光标
     *
     * @param canvas 画布
     * @param paint  画笔
     * @param rectF  矩形
     */
    private fun onDrawCursor(canvas: Canvas, paint: Paint, rectF: RectF) {
        if (null != mCursorBackgroundDrawable) {
            mCursorBackgroundDrawable?.setBounds(
                ((rectF.left + rectF.right) / 2 - mCursorWidth).toInt(),
                (if (mCursorHeightMargin <= 1) (rectF.top + rectF.bottom) / 4 else mCursorHeightMargin).toInt(),
                ((rectF.left + rectF.right) / 2 + mCursorWidth).toInt(),
                (rectF.bottom - (if (mCursorHeightMargin <= 1) (rectF.top + rectF.bottom) / 4 else mCursorHeightMargin.toFloat())).toInt()
            )
            if ((mCursorDisplayingByTimer || mCursorDisplayingByIndex)) {
                mCursorBackgroundDrawable?.draw(canvas)
            }
        } else {
            paint.color =
                if ((mCursorDisplayingByTimer || mCursorDisplayingByIndex)) mCursorColor else Color.TRANSPARENT
            canvas.drawRect(
                (rectF.left + rectF.right) / 2 - mCursorWidth,
                if (mCursorHeightMargin <= 1) (rectF.top + rectF.bottom) / 4 else mCursorHeightMargin.toFloat(),
                (rectF.left + rectF.right) / 2 + mCursorWidth,
                rectF.bottom - (if (mCursorHeightMargin <= 1) (rectF.top + rectF.bottom) / 4 else mCursorHeightMargin.toFloat()),
                paint
            )
        }
        mCursorDisplayingByIndex = false
    }
}