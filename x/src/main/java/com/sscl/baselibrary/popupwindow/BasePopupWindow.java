package com.sscl.baselibrary.popupwindow;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.sscl.baselibrary.utils.BaseManager;


/**
 * 自定义PopupWindow基类
 *
 * @author pengh
 */
public abstract class BasePopupWindow extends PopupWindow {

    /*--------------------------------构造方法--------------------------------*/

    private Context context;

    /**
     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does provide a background.</p>
     *
     * @param context 上下文
     */
    public BasePopupWindow(@NonNull Context context) {
        this(context, null);
    }

    /**
     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does provide a background.</p>
     *
     * @param context 上下文
     * @param attrs   控件属性
     */
    @SuppressWarnings("WeakerAccess")
    public BasePopupWindow(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does provide a background.</p>
     *
     * @param context      上下文
     * @param attrs        控件属性
     * @param defStyleAttr 默认的控件属性
     */
    @SuppressWarnings("WeakerAccess")
    public BasePopupWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * <p>Create a new, empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does not provide a background.</p>
     *
     * @param context      上下文
     * @param attrs        控件属性
     * @param defStyleAttr 默认的控件属性
     * @param defStyleRes  默认的风格属性
     */
    @SuppressWarnings("WeakerAccess")
    public BasePopupWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr, defStyleRes, 0, 0);
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
     */
    @SuppressWarnings("WeakerAccess")
    public BasePopupWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, int width, int height) {
        this(context, attrs, defStyleAttr, defStyleRes, width, height, false);

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
    @SuppressWarnings("WeakerAccess")
    public BasePopupWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, int width, int height, boolean focusable) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        int layoutRes = setLayout();
        if (layoutRes == 0) {
            throw new IllegalArgumentException("setLayout returned 0");
        }
        View contentView = View.inflate(context, layoutRes, null);
        setContentView(contentView);
        setWidth(width);
        setHeight(height);
        setFocusable(focusable);
        BaseManager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                doBeforeInitOthers();
                initViews();
                initViewData();
                initOtherData();
                initEvents();
                doAfterAll();
            }
        });
    }

    /*--------------------------------抽象方法--------------------------------*/

    @LayoutRes
    protected abstract int setLayout();

    /**
     * 在初始化其他数据之前执行的操作
     */
    protected abstract void doBeforeInitOthers();

    /**
     * 初始化控件
     */
    protected abstract void initViews();

    /**
     * 初始化控件的数据
     */
    protected abstract void initViewData();

    /**
     * 初始化其他数据
     */
    protected abstract void initOtherData();

    /**
     * 初始化事件
     */
    protected abstract void initEvents();

    /**
     * 在最后执行的操纵
     */
    protected abstract void doAfterAll();

    /*--------------------------------子类可用方法--------------------------------*/

    /**
     * 让PopupWindow可以和Activity一样拥有findViewById方法
     *
     * @param viewId 控件id
     * @return 控件
     */
    @Nullable
    protected <T extends View> T findViewById(@IdRes int viewId) {
        View contentView = getContentView();
        if (contentView != null) {
            return contentView.findViewById(viewId);
        }
        return null;
    }

    /**
     * 返回创建这个PopupWindow时的上下文
     *
     * @return 上下文
     */
    @NonNull
    protected Context getContext() {
        return context;
    }

    /**
     * 显示popup window
     *
     * @param parent 父布局
     */
    public void show(View parent) {
        show(parent, Gravity.CENTER);
    }

    /**
     * 显示popup window
     *
     * @param parent  父布局
     * @param gravity Gravity
     */
    @SuppressWarnings("WeakerAccess")
    public void show(View parent, int gravity) {
        show(parent, gravity, 0, 0);
    }

    /**
     * 显示popup window
     *
     * @param parent  父布局
     * @param gravity Gravity
     * @param x       x坐标
     * @param y       y坐标
     */
    @SuppressWarnings("WeakerAccess")
    public void show(View parent, int gravity, int x, int y) {
        showAtLocation(parent, gravity, x, y);
    }
}
