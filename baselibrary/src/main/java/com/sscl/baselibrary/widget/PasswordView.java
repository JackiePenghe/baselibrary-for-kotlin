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
                    if (null != mPasswordView.onResultListener) {
                        mPasswordView.onResultListener.finish(text.toString());
                    }
                    if (mPasswordView.enableSoftKeyboardAutoHide || mPasswordView.enableLockCodeTextIfMaxCode) {
                        mPasswordView.closeSoftKeyboard();
                    }
                    mPasswordView.locked = true;
                } else {
                    if (null != mPasswordView.onResultListener) {
                        mPasswordView.onResultListener.typing(text.toString());
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
    private boolean enableHideCode = false;
    /**
     * 是否开启高亮
     */
    private boolean enableHighLight = false;
    /**
     * 是否开启光标
     */
    private boolean enableCursor = false;
    /**
     * 是否将没有输入内容的盒子隐藏
     */
    private boolean enableHideNotInputBox = false;
    /**
     * 是否自动打开软键盘
     */
    private boolean enableSoftKeyboardAutoShow = true;
    /**
     * 是否自动关闭软键盘（输入内容长度==最大长度时会自动关闭软键盘）
     */
    private boolean enableSoftKeyboardAutoHide = true;
    /**
     * 是否限制输满后锁定view
     */
    private boolean enableLockCodeTextIfMaxCode = false;

    /* * * * * * * * * * * * * * * * * * * 默认设置-盒子画笔相关 * * * * * * * * * * * * * * * * * * */

    /**
     * 笔刷
     */
    private Paint mBoxDefaultPaint;
    /**
     * 盒子输入过的颜色
     */
    @ColorInt
    private int boxHasInputColor = BOX_DEFAULT_HAS_INPUT_COLOR;
    /**
     * 盒子未输入过的颜色
     */
    @ColorInt
    private int boxNotInputColor = BOX_DEFAULT_NOT_INPUT_COLOR;
    /**
     * 盒子高亮的颜色
     */
    @ColorInt
    private int boxHighLightColor = BOX_DEFAULT_HIGH_LIGHT_COLOR;
    /**
     * 盒子锁定状态下的颜色
     */
    @ColorInt
    private int boxLockColor = BOX_DEFAULT_LOCK_COLOR;
    /**
     * 盒子描边的宽度
     */
    private int boxStrokeWidth = BOX_DEFAULT_STROKE_WIDTH;

    /* * * * * * * * * * * * * * * * * * * 输入框样式 * * * * * * * * * * * * * * * * * * */

    /**
     * 是否已经锁定
     */
    private boolean locked = false;
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
    private String hideCodeText = BOX_DEFAULT_HIDE_TEXT;
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
    private OnResultListener onResultListener;
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
            if (enableHighLight || enableCursor) {
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
        if (enableCursor) {
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
        enableLockCodeTextIfMaxCode = true;
        locked = true;
    }

    /**
     * 解除锁定CodeText
     */
    public void setUnLock() {
        if (mIsCodeFull) {
            openSoftKeyboard();
            locked = false;
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
            if (enableSoftKeyboardAutoHide || locked || enableLockCodeTextIfMaxCode) {
                mEditText.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }
        }
    }

    /**
     * 设置监听接口回调
     *
     * @param onResultListener 监听接口
     */
    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    /**
     * 设置是否开启隐匿输入内容
     *
     * @param enableHideCode 是否开启隐匿输入内容
     */
    public void setEnableHideCode(boolean enableHideCode) {
        this.enableHideCode = enableHideCode;
    }

    /**
     * 设置是否开启高亮
     *
     * @param enableHighLight 是否开启高亮
     */
    public void setEnableHighLight(boolean enableHighLight) {
        this.enableHighLight = enableHighLight;
    }

    /**
     * 设置是否开启光标
     *
     * @param enableCursor 是否开启光标
     */
    public void setEnableCursor(boolean enableCursor) {
        this.enableCursor = enableCursor;
    }

    /**
     * 设置 是否隐藏未输入内容的盒子
     *
     * @param enableHideNotInputBox 是否隐藏未输入内容的盒子
     */
    public void setEnableHideNotInputBox(boolean enableHideNotInputBox) {
        this.enableHideNotInputBox = enableHideNotInputBox;
    }

    /**
     * 设置是否开启软键盘自动显示
     *
     * @param enableSoftKeyboardAutoShow 是否开启软键盘自动显示
     */
    public void setEnableSoftKeyboardAutoShow(boolean enableSoftKeyboardAutoShow) {
        this.enableSoftKeyboardAutoShow = enableSoftKeyboardAutoShow;
    }

    /**
     * 设置是否开启软键盘自动关闭
     *
     * @param enableSoftKeyboardAutoHide 是否开启软键盘自动关闭
     */
    public void setEnableSoftKeyboardAutoHide(boolean enableSoftKeyboardAutoHide) {
        this.enableSoftKeyboardAutoHide = enableSoftKeyboardAutoHide;
    }

    /**
     * 设置是否开户自动锁定文本（当文本输入达到最大数量之后）
     *
     * @param enableLockCodeTextIfMaxCode 是否开户自动锁定文本（当文本输入达到最大数量之后）
     */
    public void setEnableLockCodeTextIfMaxCode(boolean enableLockCodeTextIfMaxCode) {
        this.enableLockCodeTextIfMaxCode = enableLockCodeTextIfMaxCode;
    }

    /**
     * 设置盒子有输入内容的颜色
     *
     * @param boxHasInputColor 盒子有输入内容的颜色
     */
    public void setBoxHasInputColor(int boxHasInputColor) {
        this.boxHasInputColor = boxHasInputColor;
    }

    /**
     * 设置盒子无输入内容的颜色
     *
     * @param boxNotInputColor 盒子无输入内容的颜色
     */
    public void setBoxNotInputColor(int boxNotInputColor) {
        this.boxNotInputColor = boxNotInputColor;
    }

    /**
     * 设置盒子高亮的颜色
     *
     * @param boxHighLightColor 盒子高亮的颜色
     */
    public void setBoxHighLightColor(int boxHighLightColor) {
        this.boxHighLightColor = boxHighLightColor;
    }

    /**
     * 设置盒子锁定的颜色
     *
     * @param boxLockColor 盒子锁定的颜色
     */
    public void setBoxLockColor(int boxLockColor) {
        this.boxLockColor = boxLockColor;
    }

    /**
     * 设置盒子边线的宽度
     *
     * @param boxStrokeWidth 盒子边线的宽度
     */
    public void setBoxStrokeWidth(int boxStrokeWidth) {
        this.boxStrokeWidth = boxStrokeWidth;
    }

    /**
     * 获取当前盒子锁定的状态
     *
     * @return 盒子锁定的状态
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * 设置隐藏文本时，显示的替换文本内容
     *
     * @param hideCodeText 替换文本内容
     */
    public void setHideCodeText(String hideCodeText) {
        this.hideCodeText = hideCodeText;
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
        enableSoftKeyboardAutoShow = typedArray.getBoolean(R.styleable.PasswordView_password_enableSoftKeyboardAutoShow, enableSoftKeyboardAutoShow);
        //自动隐藏键盘
        enableSoftKeyboardAutoHide = typedArray.getBoolean(R.styleable.PasswordView_password_enableSoftKeyboardAutoClose, enableSoftKeyboardAutoHide);
        //是否隐藏输入内容
        enableHideCode = typedArray.getBoolean(R.styleable.PasswordView_password_enableHideCode, enableHideCode);
        //隐藏输入的内容,显示设置的文案
        hideCodeText = typedArray.getString(R.styleable.PasswordView_password_hideCodeText);
        //是否将没有输入内容的盒子隐藏
        enableHideNotInputBox = typedArray.getBoolean(R.styleable.PasswordView_password_enableHideBoxWhenNotInput, enableHideNotInputBox);
        //是否绘制高亮盒子
        enableHighLight = typedArray.getBoolean(R.styleable.PasswordView_password_enableHighLight, enableHighLight);
        //是否绘制光标
        enableCursor = typedArray.getBoolean(R.styleable.PasswordView_password_enableCursor, enableCursor);
        //是否锁定组件当输入满长度后(禁止点击,可以主动setUnLock()调解除锁定)
        enableLockCodeTextIfMaxCode = typedArray.getBoolean(R.styleable.PasswordView_password_enableLockTextView, enableLockCodeTextIfMaxCode);
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
        boxHasInputColor = typedArray.getColor(R.styleable.PasswordView_password_box_hasInput_setColor, boxHasInputColor);
        //盒子未输入过的颜色
        boxNotInputColor = typedArray.getColor(R.styleable.PasswordView_password_box_notInput_setColor, boxNotInputColor);
        //盒子高亮的颜色
        boxHighLightColor = typedArray.getColor(R.styleable.PasswordView_password_box_highLight_setColor, boxHighLightColor);
        //盒子锁定状态下的颜色
        boxLockColor = typedArray.getColor(R.styleable.PasswordView_password_box_locked_setColor, boxLockColor);
        //盒子宽度
        boxStrokeWidth = typedArray.getInt(R.styleable.PasswordView_password_box_strokeWidth, boxStrokeWidth);
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
        if (null == this.hideCodeText) {
            this.hideCodeText = BOX_DEFAULT_HIDE_TEXT;
        } else if (this.hideCodeText.length() > 0) {
            this.hideCodeText = hideCodeText.substring(0, 1);
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
            if ((!locked || !enableLockCodeTextIfMaxCode)) {
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
        this.mPaintText.setTextSize(mTextSize * 2);
        this.mPaintText.setColor(mTextColor);
        this.mPaintText.setFakeBoldText(mTextBold);
        //盒子
        this.mBoxDefaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mBoxDefaultPaint.setStyle(Paint.Style.STROKE);
        this.mBoxDefaultPaint.setStrokeWidth(boxStrokeWidth);
    }

    /**
     * 监听View是否渲染完成,如果开启了自动弹出软键盘,则弹出
     */
    private void setOnLayoutListener() {
        if (null != mEditText) {
            mEditText.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                if (!mIsCodeFull && mIsFirstTime <= mBoxSetNumber - 1 && enableSoftKeyboardAutoShow) {
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
        if (!enableHideNotInputBox) {
            //如果有设置drawable，则绘制drawable
            if (null != mBoxNotInputBackgroundDrawable) {
                mBoxNotInputBackgroundDrawable.setBounds((int) mBoxRectF.left, (int) mBoxRectF.top, (int) mBoxRectF.right, (int) mBoxRectF.bottom);
                mBoxNotInputBackgroundDrawable.draw(canvas);
            } else {
                mBoxDefaultPaint.setColor(boxNotInputColor);
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
        if (locked && enableLockCodeTextIfMaxCode) {
            //如果有设置高亮drawable,则绘制drawable,没有则用画笔绘制
            if (null != mBoxLockedBackgroundDrawable) {
                mBoxLockedBackgroundDrawable.setBounds((int) mBoxRectF.left, (int) mBoxRectF.top, (int) mBoxRectF.right, (int) mBoxRectF.bottom);
                mBoxLockedBackgroundDrawable.draw(canvas);
            } else {
                mBoxDefaultPaint.setColor(boxLockColor);
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
                mBoxDefaultPaint.setColor(boxHasInputColor);
                canvas.drawRoundRect(mBoxRectF, BOX_DEFAULT_RADIUS, BOX_DEFAULT_RADIUS, mBoxDefaultPaint);
            }

        }

        //绘制输入的内容文字
        mPaintText.getTextBounds(enableHideCode ? hideCodeText : mCodeArray[index], 0, mCodeArray[index].length(), mTextRect);
        canvas.drawText(enableHideCode ? hideCodeText : mCodeArray[index], (mBoxRectF.left + mBoxRectF.right) / 2 - (mTextRect.left + mTextRect.right) / 2.0f, (mBoxRectF.top + mBoxRectF.bottom) / 2 - (mTextRect.top + mTextRect.bottom) / 2.0f, mPaintText);

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
            mBoxDefaultPaint.setColor(boxHighLightColor);
            canvas.drawRoundRect(mBoxRectF, BOX_DEFAULT_RADIUS, BOX_DEFAULT_RADIUS, mBoxDefaultPaint);
        }
        //如果开启了光标,则绘制光标
        if (enableCursor) {
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
