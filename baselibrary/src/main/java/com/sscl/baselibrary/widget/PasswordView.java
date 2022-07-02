package com.sscl.baselibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.utils.BaseManager;

import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 源文件来自 （https://github.com/FENGZEYOU123/yfz_view_password.git）
 * 简介：自定义验证码输入框 (组合+自绘)
 * 作者：游丰泽
 * 主要功能:
 * （以下功能涉及到盒子样式的改变，均可单独控制功能的盒子样式,默认为画笔，可自定设置backgroundDrawable替代）
 * <p>
 * mBox_notInput_backgroundDrawable;//盒子未输入内容背景Drawable
 * mBox_hasInput_backgroundDrawable;//盒子已输入内容背景Drawable
 * mBox_highLight_backgroundDrawable;//盒子高亮背景Drawable
 * mBox_locked_backgroundDrawable;//盒子锁定状态下背景Drawable
 * <p>
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
public class PasswordView extends LinearLayout {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 接口回调输入结果
     */
    public interface OnResultListener {
        /**
         * 输入完成
         *
         * @param result 输入的结果
         */
        void finish(String result);

        /**
         * 输入中
         *
         * @param typing 输入的内容
         */
        void typing(String typing);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有静态内部类
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 密码输入文本监听器
     */
    private static class PasswordTextWatcher implements TextWatcher {

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 私有成员变量
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /**
         * 密码控件
         */
        private final PasswordView mPasswordView;

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 构造方法
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        public PasswordTextWatcher(PasswordView passwordView) {
            mPasswordView = passwordView;
        }

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 实现方法
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int end, int count) {
        }

        @Override
        public void afterTextChanged(Editable text) {
            mPasswordView.mBoxNextInputIndex = text.length(); //高亮盒子下坐标=当前输入内容长度
            if (null != mPasswordView.mCodeArray) {
                for (int i = 0; i < mPasswordView.mBoxSetNumber; i++) {
                    if (i <= text.length() - 1) {
                        mPasswordView.mCodeArray[i] = text.toString().substring(i, i + 1);
                    } else {
                        mPasswordView.mCodeArray[i] = "";
                    }
                }
                mPasswordView.mCursorDisplayingByIndex = true;
                if (text.length() == mPasswordView.mBoxSetNumber) { //内容长度与盒子数量一致->返回回调结果
                    mPasswordView.mIsCodeFull = true;
                    if (null != mPasswordView.mOnResultListener) {
                        mPasswordView.mOnResultListener.finish(text.toString());
                    }
                    if (mPasswordView.mEnableSoftKeyboardAutoClose || mPasswordView.mEnableLockCodeTextIfMaxCode) {
                        mPasswordView.closeSoftKeyboard();
                    }
                    mPasswordView.mIsLocked = true;
                } else {
                    if (null != mPasswordView.mOnResultListener) {
                        mPasswordView.mOnResultListener.typing(text.toString());
                    }
                }
                mPasswordView.postInvalidate();

            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有静态常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 默认的隐藏输入过的盒子展示的内容
     */
    private static final String BOX_DEFAULT_HIDE_TEXT = "*";
    /**
     * 默认的盒子输入过的颜色
     */
    private static final int BOX_DEFAULT_HAS_INPUT_COLOR = Color.RED;
    /**
     * 默认的盒子未输入过的颜色
     */
    private static final int BOX_DEFAULT_NOT_INPUT_COLOR = Color.BLUE;
    /**
     * 默认的盒子高亮的颜色
     */
    private static final int BOX_DEFAULT_HIGH_LIGHT_COLOR = Color.CYAN;
    /**
     * 默认的盒子光标的颜色
     */
    private static final int BOX_DEFAULT_CURSOR_COLOR = Color.BLACK;
    /**
     * 默认的盒子锁定状态下的颜色
     */
    private static final int BOX_DEFAULT_LOCK_COLOR = Color.GRAY;
    /**
     * 默认的盒子宽度
     */
    private static final int BOX_DEFAULT_STROKE_WIDTH = 1;
    /**
     * 圆弧半径
     */
    private static final float BOX_DEFAULT_RADIUS = 5f;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 是否隐藏输入code
     */
    private boolean mEnableHideCode = false;
    /**
     * 是否开启高亮
     */
    private boolean mEnableHighLight = false;
    /**
     * 是否开启光标
     */
    private boolean mEnableCursor = false;
    /**
     * 是否将没有输入内容的盒子隐藏
     */
    private boolean mEnableHideNotInputBox = false;
    /**
     * 是否自动打开软键盘
     */
    private boolean mEnableSoftKeyboardAutoShow = true;
    /**
     * 是否自动关闭软键盘（输入内容长度==最大长度时会自动关闭软键盘）
     */
    private boolean mEnableSoftKeyboardAutoClose = true;
    /**
     * 是否限制输满后锁定view
     */
    private boolean mEnableLockCodeTextIfMaxCode = false;

    /* * * * * * * * * * * * * * * * * * * 默认设置-盒子画笔相关 * * * * * * * * * * * * * * * * * * */

    /**
     * 笔刷
     */
    private Paint mBoxDefaultPaint;
    /**
     * 盒子输入过的颜色
     */
    @ColorInt
    private int mBoxHasInputColor = BOX_DEFAULT_HAS_INPUT_COLOR;
    /**
     * 盒子未输入过的颜色
     */
    @ColorInt
    private int mBoxNotInputColor = BOX_DEFAULT_NOT_INPUT_COLOR;
    /**
     * 盒子高亮的颜色
     */
    @ColorInt
    private int mBoxHighLightColor = BOX_DEFAULT_HIGH_LIGHT_COLOR;
    /**
     * 盒子锁定状态下的颜色
     */
    @ColorInt
    private int mBoxLockColor = BOX_DEFAULT_LOCK_COLOR;
    /**
     * 盒子描边的宽度
     */
    private int mBoxStrokeWidth = BOX_DEFAULT_STROKE_WIDTH;

    /* * * * * * * * * * * * * * * * * * * 输入框样式 * * * * * * * * * * * * * * * * * * */

    /**
     * 是否已经锁定
     */
    private boolean mIsLocked = false;
    /**
     * 是否已经输入过
     */
    private boolean mIsCodeFull = false;
    /**
     * 是否为输入第一个字符
     */
    private int mIsFirstTime = 0;
    /**
     * 待输入盒子的下一个坐标
     */
    private int mBoxNextInputIndex = 0;
    /**
     * 隐藏输入code-显示的内容
     */
    private String mEnableHideCodeText = BOX_DEFAULT_HIDE_TEXT;
    /**
     * 背景
     */
    @DrawableRes
    private int mViewBackground = R.drawable.com_jackiepenghe_baselibrary_background_password_view;

    /* * * * * * * * * * * * * * * * * * * 盒子 * * * * * * * * * * * * * * * * * * */

    /**
     * 矩形（绘制位置）
     */
    private RectF mBoxRectF;
    /**
     * 盒子的数量
     */
    private int mBoxSetNumber = 4;
    /**
     * 盒子的大小
     */
    private int mBoxSetSize = 50;
    /**
     * 盒子的间距
     */
    private int mBoxSetMargin = 10;
    /**
     * 盒子的圆角半径
     */
    private float mBoxRadius = BOX_DEFAULT_RADIUS;
    /**
     * 盒子未输入内容背景Drawable
     */
    private Drawable mBoxNotInputBackgroundDrawable;
    /**
     * 盒子已输入内容背景Drawable
     */
    private Drawable mBoxHasInputBackgroundDrawable;
    /**
     * 盒子高亮背景Drawable
     */
    private Drawable mBoxHighLightBackgroundDrawable;
    /**
     * 盒子锁定状态下背景Drawable
     */
    private Drawable mBoxLockedBackgroundDrawable;

    /* * * * * * * * * * * * * * * * * * * 文字 * * * * * * * * * * * * * * * * * * */

    /**
     * 笔刷
     */
    private Paint mPaintText;
    /**
     * 矩形（绘制位置）
     */
    private Rect mTextRect;
    /**
     * 输入Code内容
     */
    private String[] mCodeArray;
    /**
     * 颜色
     */
    private int mTextColor = Color.BLACK;
    /**
     * 大小
     */
    private int mTextSize = 10;
    /**
     * 类型
     */
    private int mTextInputType = InputType.TYPE_CLASS_NUMBER;
    /**
     * 是否加粗
     */
    private boolean mTextBold = true;
    /**
     * 光标闪烁定时器
     */
    private ScheduledExecutorService mCursorTimer;
    /**
     * 定时器任务
     */
    private Runnable mCursorTimerTask;
    /**
     * 光标背景Drawable
     */
    private Drawable mCursorBackgroundDrawable;
    /**
     * 光标颜色
     */
    private int mCursorColor;
    /**
     * 光标上下边距
     */
    private int mCursorHeightMargin = 1;
    /**
     * 光标左右边距
     */
    private int mCursorWidth = 1;
    /**
     * 光标闪烁间隔时间
     */
    private int mCursorFrequency = 500;
    /**
     * 显示光标-定时器-闪烁效果
     */
    private boolean mCursorDisplayingByTimer = false;
    /**
     * 显示光标-第一次下坐标
     */
    private boolean mCursorDisplayingByIndex = false;
    /**
     * 文本输入框
     */
    private EditText mEditText;
    /**
     * 文本输入完成的回调
     */
    private OnResultListener mOnResultListener;
    /**
     * 输入法管理器
     */
    private InputMethodManager inputMethodManager;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public PasswordView(@NonNull Context context) {
        this(context, null);
    }

    public PasswordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public PasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        //初始化
        initial();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //测量-CodeText大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (measureWidthMode == MeasureSpec.AT_MOST && measureHeightMode == MeasureSpec.AT_MOST) {
            //宽高均未声明绝对值
            //组件宽 = (盒子大小*数量)+（盒子边距*(数量-1))+画笔宽度
            //组件高 = (盒子大小)
            measureWidthSize = mBoxSetSize * (mBoxSetNumber) + mBoxSetMargin * (mBoxSetNumber - 1);
            measureHeightSize = mBoxSetSize;
            setMeasuredDimension(measureWidthSize, measureHeightSize);
        } else if (measureWidthMode == MeasureSpec.EXACTLY && measureHeightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(measureWidthSize, measureHeightSize);
        } else if (measureWidthMode == MeasureSpec.EXACTLY && measureHeightMode == MeasureSpec.AT_MOST) {
            //只声明了宽的绝对值，高未声明
            measureHeightSize = mBoxSetSize;
            setMeasuredDimension(measureWidthSize, measureHeightSize);
        } else if (measureHeightMode == MeasureSpec.EXACTLY && measureWidthMode == MeasureSpec.AT_MOST) {
            //只声明了高的绝对值，宽未声明
            measureWidthSize = mBoxSetSize * (mBoxSetNumber) + mBoxSetMargin * (mBoxSetNumber - 1);
            setMeasuredDimension(measureWidthSize, measureHeightSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mBoxSetNumber; i++) {
            mBoxRectF.left = (mBoxSetSize + mBoxSetMargin) * i;
            mBoxRectF.top = 0;
            mBoxRectF.right = mBoxRectF.left + mBoxSetSize;
            mBoxRectF.bottom = getHeight();
            //如果开启了高亮或鼠标 且 i==待输入index
            if (mEnableHighLight || mEnableCursor) {
                if (i == mBoxNextInputIndex) {
                    //绘制盒子 - 高亮或光标
                    onDrawHighLightCursor(canvas);
                }
            }
            //如果盒子内容的长度>=1,则视为已输入过内容的盒子
            else if (mCodeArray[i].length() >= 1) {
                //绘制盒子 - 已输入过内容
                onDrawHasInput(canvas, i);
            }
            //未输入过内容的盒子
            else {
                //绘制盒子 - 未输入过内容
                onDrawNotInput(canvas);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mEnableCursor) {
            if (null != mCursorTimer) {
                mCursorTimer.scheduleAtFixedRate(mCursorTimerTask, 0, mCursorFrequency, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCursorTimer.shutdownNow();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 锁定CodeText
     */
    public void setOnLock() {
        mEnableLockCodeTextIfMaxCode = true;
        mIsLocked = true;
    }

    /**
     * 解除锁定CodeText
     */
    public void setUnLock() {
        if (mIsCodeFull) {
            openSoftKeyboard();
            mIsLocked = false;
        }
    }

    /**
     * 打开软键盘
     */
    public void openSoftKeyboard() {
        if (null != mEditText) {
            mEditText.setFocusable(true);
            mEditText.setFocusableInTouchMode(true);
            mEditText.requestFocus();
            inputMethodManager.showSoftInput(mEditText, 0);
        }

    }

    /**
     * 关闭软键盘
     */
    public void closeSoftKeyboard() {
        if (null != mEditText) {
            if (mEnableSoftKeyboardAutoClose || mIsLocked || mEnableLockCodeTextIfMaxCode) {
                mEditText.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }
        }
    }

    /**
     * 监听接口回调
     *
     * @param onResultListener 监听接口
     */
    public void setOnResultListener(OnResultListener onResultListener) {
        mOnResultListener = onResultListener;
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
    private void initAttr(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordView);

        //自动弹出键盘
        mEnableSoftKeyboardAutoShow = typedArray.getBoolean(R.styleable.PasswordView_password_enableSoftKeyboardAutoShow, mEnableSoftKeyboardAutoShow);
        //自动隐藏键盘
        mEnableSoftKeyboardAutoClose = typedArray.getBoolean(R.styleable.PasswordView_password_enableSoftKeyboardAutoClose, mEnableSoftKeyboardAutoClose);
        //是否隐藏输入内容
        mEnableHideCode = typedArray.getBoolean(R.styleable.PasswordView_password_enableHideCode, mEnableHideCode);
        //隐藏输入的内容,显示设置的文案
        mEnableHideCodeText = typedArray.getString(R.styleable.PasswordView_password_enableHideCodeSetText);
        //是否将没有输入内容的盒子隐藏
        mEnableHideNotInputBox = typedArray.getBoolean(R.styleable.PasswordView_password_enableHideBoxWhenNotInput, mEnableHideNotInputBox);
        //是否绘制高亮盒子
        mEnableHighLight = typedArray.getBoolean(R.styleable.PasswordView_password_enableHighLight, mEnableHighLight);
        //是否绘制光标
        mEnableCursor = typedArray.getBoolean(R.styleable.PasswordView_password_enableCursor, mEnableCursor);
        //是否锁定组件当输入满长度后(禁止点击,可以主动setUnLock()调解除锁定)
        mEnableLockCodeTextIfMaxCode = typedArray.getBoolean(R.styleable.PasswordView_password_enableLockTextView, mEnableLockCodeTextIfMaxCode);
        //View背景Drawable
        mViewBackground = typedArray.getResourceId(R.styleable.PasswordView_Password_viewBackground, Color.TRANSPARENT);
        //文字颜色
        mTextColor = typedArray.getColor(R.styleable.PasswordView_android_textColor, mTextColor);
        //文字大小
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.PasswordView_android_textSize, mTextSize);
        //文字输入框类型
        mTextInputType = typedArray.getInt(R.styleable.PasswordView_android_inputType, mTextInputType);
        //文字粗细
        mTextBold = typedArray.getBoolean(R.styleable.PasswordView_password_text_setIsBold, mTextBold);
        //设置盒子总数量
        mBoxSetNumber = typedArray.getInt(R.styleable.PasswordView_password_box_setNumber, mBoxSetNumber);
        //设置盒子间距
        mBoxSetMargin = typedArray.getDimensionPixelSize(R.styleable.PasswordView_password_box_setMargin, mBoxSetMargin);
        //设置盒子大小
        mBoxSetSize = typedArray.getDimensionPixelSize(R.styleable.PasswordView_password_box_setSize, mBoxSetSize);
        //设置盒子-未输入的背景Drawable
        mBoxNotInputBackgroundDrawable = typedArray.getDrawable(R.styleable.PasswordView_password_box_notInput_backgroundDrawable);
        //设置盒子-输入后的背景Drawable
        mBoxHasInputBackgroundDrawable = typedArray.getDrawable(R.styleable.PasswordView_password_box_hasInput_backgroundDrawable);
        //设置盒子-高亮的背景Drawable
        mBoxHighLightBackgroundDrawable = typedArray.getDrawable(R.styleable.PasswordView_password_box_highLight_backgroundDrawable);
        //设置盒子-锁定状态下的背景Drawable
        mBoxLockedBackgroundDrawable = typedArray.getDrawable(R.styleable.PasswordView_password_box_locked_backgroundDrawable);
        //设置光标高度
        mCursorHeightMargin = typedArray.getDimensionPixelSize(R.styleable.PasswordView_password_cursorHeight, mCursorHeightMargin);
        //设置光标宽度
        mCursorWidth = typedArray.getDimensionPixelSize(R.styleable.PasswordView_password_cursorWidth, mCursorWidth);
        //设置光标闪烁频率
        mCursorFrequency = typedArray.getInt(R.styleable.PasswordView_password_cursorFrequencyMillisecond, mCursorFrequency);
        //设置光标颜色
        mCursorColor = typedArray.getInt(R.styleable.PasswordView_password_cursorColor, BOX_DEFAULT_CURSOR_COLOR);
        //设置光标背景
        mCursorBackgroundDrawable = typedArray.getDrawable(R.styleable.PasswordView_password_cursorBackgroundDrawable);
        //盒子输入过的颜色
        mBoxHasInputColor = typedArray.getColor(R.styleable.PasswordView_password_box_hasInput_setColor, mBoxHasInputColor);
        //盒子未输入过的颜色
        mBoxNotInputColor = typedArray.getColor(R.styleable.PasswordView_password_box_notInput_setColor, mBoxNotInputColor);
        //盒子高亮的颜色
        mBoxHighLightColor = typedArray.getColor(R.styleable.PasswordView_password_box_highLight_setColor, mBoxHighLightColor);
        //盒子锁定状态下的颜色
        mBoxLockColor = typedArray.getColor(R.styleable.PasswordView_password_box_locked_setColor, mBoxLockColor);
        //盒子宽度
        mBoxStrokeWidth = typedArray.getInt(R.styleable.PasswordView_password_box_strokeWidth, mBoxStrokeWidth);
        //盒子的圆角
        mBoxRadius = typedArray.getDimension(R.styleable.PasswordView_password_box_Radius, BOX_DEFAULT_RADIUS);
        //文本
        String text = typedArray.getString(R.styleable.PasswordView_android_text);
        if (mEditText != null) {
            mEditText.setText(text);
        }
        //回收
        typedArray.recycle();
    }

    /**
     * 初始化-CodeText
     */
    private void initial() {
        try {
            Drawable drawable = ContextCompat.getDrawable(getContext(), mViewBackground);
            this.setBackground(drawable);
        } catch (Exception e) {
            this.setBackgroundColor(mViewBackground);
        }
        mCodeArray = new String[mBoxSetNumber];
        Arrays.fill(mCodeArray, "");
        if (null == this.mEnableHideCodeText) {
            this.mEnableHideCodeText = BOX_DEFAULT_HIDE_TEXT;
        } else if (this.mEnableHideCodeText.length() > 0) {
            this.mEnableHideCodeText = mEnableHideCodeText.substring(0, 1);
        }

        mCursorTimerTask = () -> {
            mCursorDisplayingByTimer = !mCursorDisplayingByTimer;
            postInvalidate();
        };
        mCursorTimer = BaseManager.newScheduledExecutorService(1);
        initialEditText();
        initialPaint();
        initialBoxAndRectPosition();
        setOnLayoutListener();
        setOnClickListener(v -> {
            if ((!mIsLocked || !mEnableLockCodeTextIfMaxCode)) {
                openSoftKeyboard();
            }
        });
    }

    /**
     * 初始化EdiText
     */
    private void initialEditText() {
        this.mEditText = new EditText(this.getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mEditText.setLayoutParams(layoutParams);
        this.mEditText.setBackgroundColor(Color.TRANSPARENT);
        this.mEditText.setTextColor(Color.TRANSPARENT);
        this.addView(mEditText);
        this.mEditText.setWidth(1);
        this.mEditText.setHeight(1);
        this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mBoxSetNumber)});
        this.mEditText.setInputType(mTextInputType);
        this.mEditText.setSingleLine();
        this.mEditText.setCursorVisible(false);
        inputMethodManager = (InputMethodManager) this.mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        this.mEditText.addTextChangedListener(new PasswordTextWatcher(this));
    }

    /**
     * 初始化-盒子和位置
     */
    private void initialBoxAndRectPosition() {
        this.mBoxRectF = new RectF();
        this.mTextRect = new Rect();
    }

    /**
     * 初始化-笔刷
     */
    private void initialPaint() {
        //文字
        this.mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaintText.setStyle(Paint.Style.FILL);
        this.mPaintText.setTextSize(mTextSize* 2);
        this.mPaintText.setColor(mTextColor);
        this.mPaintText.setFakeBoldText(mTextBold);
        //盒子
        this.mBoxDefaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mBoxDefaultPaint.setStyle(Paint.Style.STROKE);
        this.mBoxDefaultPaint.setStrokeWidth(mBoxStrokeWidth);
    }

    /**
     * 监听View是否渲染完成,如果开启了自动弹出软键盘,则弹出
     */
    private void setOnLayoutListener() {
        if (null != mEditText) {
            mEditText.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                if (!mIsCodeFull && mIsFirstTime <= mBoxSetNumber - 1 && mEnableSoftKeyboardAutoShow) {
                    openSoftKeyboard();
                    mIsFirstTime++;
                }
            });
        }
    }

    /**
     * 绘制盒子 - 未输入过内容
     *
     * @param canvas 画布
     */
    private void onDrawNotInput(Canvas canvas) {
        //如果没有开启隐藏 未输入内容盒子
        if (!mEnableHideNotInputBox) {
            //如果有设置drawable，则绘制drawable
            if (null != mBoxNotInputBackgroundDrawable) {
                mBoxNotInputBackgroundDrawable.setBounds((int) mBoxRectF.left, (int) mBoxRectF.top, (int) mBoxRectF.right, (int) mBoxRectF.bottom);
                mBoxNotInputBackgroundDrawable.draw(canvas);
            } else {
                mBoxDefaultPaint.setColor(mBoxNotInputColor);
                canvas.drawRoundRect(mBoxRectF, mBoxRadius, mBoxRadius, mBoxDefaultPaint);
            }
        }
    }

    /**
     * 绘制盒子 - 已输入过内容 boxAfter样式
     *
     * @param canvas 画布
     * @param index  第几个盒子
     */
    private void onDrawHasInput(Canvas canvas, int index) {
        //如果开启了输入完毕锁定内容,则绘制boxLock样式
        if (mIsLocked && mEnableLockCodeTextIfMaxCode) {
            //如果有设置高亮drawable,则绘制drawable,没有则用画笔绘制
            if (null != mBoxLockedBackgroundDrawable) {
                mBoxLockedBackgroundDrawable.setBounds((int) mBoxRectF.left, (int) mBoxRectF.top, (int) mBoxRectF.right, (int) mBoxRectF.bottom);
                mBoxLockedBackgroundDrawable.draw(canvas);
            } else {
                mBoxDefaultPaint.setColor(mBoxLockColor);
                canvas.drawRoundRect(mBoxRectF, BOX_DEFAULT_RADIUS, BOX_DEFAULT_RADIUS, mBoxDefaultPaint);
            }
        }
        //没有开启锁定,绘制正常的boxAfter样式
        else {
            //如果有设置drawable，则绘制drawable
            if (null != mBoxHasInputBackgroundDrawable) {
                mBoxHasInputBackgroundDrawable.setBounds((int) mBoxRectF.left, (int) mBoxRectF.top, (int) mBoxRectF.right, (int) mBoxRectF.bottom);
                mBoxHasInputBackgroundDrawable.draw(canvas);
            } else {
                mBoxDefaultPaint.setColor(mBoxHasInputColor);
                canvas.drawRoundRect(mBoxRectF, BOX_DEFAULT_RADIUS, BOX_DEFAULT_RADIUS, mBoxDefaultPaint);
            }

        }

        //绘制输入的内容文字
        mPaintText.getTextBounds(mEnableHideCode ? mEnableHideCodeText : mCodeArray[index], 0, mCodeArray[index].length(), mTextRect);
        canvas.drawText(mEnableHideCode ? mEnableHideCodeText : mCodeArray[index], (mBoxRectF.left + mBoxRectF.right) / 2 - (mTextRect.left + mTextRect.right) / 2.0f, (mBoxRectF.top + mBoxRectF.bottom) / 2 - (mTextRect.top + mTextRect.bottom) / 2.0f, mPaintText);

    }

    /**
     * 绘制盒子 - 高亮或光标
     *
     * @param canvas 画布
     */
    private void onDrawHighLightCursor(Canvas canvas) {
        //如果有设置高亮drawable,则绘制drawable,没有则用画笔绘制
        if (null != mBoxHighLightBackgroundDrawable) {
            mBoxHighLightBackgroundDrawable.setBounds((int) mBoxRectF.left, (int) mBoxRectF.top, (int) mBoxRectF.right, (int) mBoxRectF.bottom);
            mBoxHighLightBackgroundDrawable.draw(canvas);
        } else {
            mBoxDefaultPaint.setColor(mBoxHighLightColor);
            canvas.drawRoundRect(mBoxRectF, BOX_DEFAULT_RADIUS, BOX_DEFAULT_RADIUS, mBoxDefaultPaint);
        }
        //如果开启了光标,则绘制光标
        if (mEnableCursor) {
            onDrawCursor(canvas, mBoxDefaultPaint, mBoxRectF);
        }
    }

    /**
     * 绘制-光标
     *
     * @param canvas 画布
     * @param paint  画笔
     * @param rectF  矩形
     */
    private void onDrawCursor(Canvas canvas, Paint paint, RectF rectF) {
        if (null != mCursorBackgroundDrawable) {
            mCursorBackgroundDrawable.setBounds(
                    (int) ((rectF.left + rectF.right) / 2 - mCursorWidth),
                    (int) (mCursorHeightMargin <= 1 ? (rectF.top + rectF.bottom) / 4 : mCursorHeightMargin),
                    (int) ((rectF.left + rectF.right) / 2 + mCursorWidth),
                    (int) (rectF.bottom - (mCursorHeightMargin <= 1 ? (rectF.top + rectF.bottom) / 4 : mCursorHeightMargin))
            );
            if ((mCursorDisplayingByTimer || mCursorDisplayingByIndex)) {
                mCursorBackgroundDrawable.draw(canvas);
            }
        } else {
            paint.setColor((mCursorDisplayingByTimer || mCursorDisplayingByIndex) ? mCursorColor : Color.TRANSPARENT);
            canvas.drawRect(
                    (rectF.left + rectF.right) / 2 - mCursorWidth,
                    mCursorHeightMargin <= 1 ? (rectF.top + rectF.bottom) / 4 : mCursorHeightMargin,
                    (rectF.left + rectF.right) / 2 + mCursorWidth,
                    rectF.bottom - (mCursorHeightMargin <= 1 ? (rectF.top + rectF.bottom) / 4 : mCursorHeightMargin)
                    , paint);
        }
        mCursorDisplayingByIndex = false;
    }

}
