package com.sscl.baselibrary.fragment;

import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sscl.baselibrary.R;


/**
 * fragment的基类
 *
 * @author alm
 */
public abstract class BaseFragment extends Fragment {

    /*---------------成员变量---------------*/

    /**
     * 整个Fragment的根布局
     */
    private View root;
    /**
     * 标题
     */
    private Toolbar toolBar;
    /**
     * 标题文本
     */
    private TextView titleView;
    /**
     * 标题栏左边的小图标
     */
    private ImageView titleLeftImage;
    /**
     * 标题栏左边的文本
     */
    private TextView titleLeftText;
    /**
     * 标题栏右边的小图标
     */
    private ImageView titleRightImage;
    /**
     * 标题栏右边的文本
     */
    private TextView titleRightText;
    /**
     * 用于适配沉浸式状态栏的专用控件
     */
    private View statusView;

    /*---------------重写父类函数---------------*/

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_base, container, false);
        toolBar = contentView.findViewById(R.id.toolbar);
        titleView = contentView.findViewById(R.id.toolbar_title);
        titleLeftImage = contentView.findViewById(R.id.title_left_image);
        titleLeftText = contentView.findViewById(R.id.title_left_text);
        titleRightImage = contentView.findViewById(R.id.title_right_image);
        titleRightText = contentView.findViewById(R.id.title_right_text);
        statusView = contentView.findViewById(R.id.fragment_base_status_bar);
        doBeforeSetLayout();
        FrameLayout frameLayout = contentView.findViewById(R.id.base_frame_content);
        root = inflater.inflate(setLayout(), frameLayout, false);
        frameLayout.addView(root);
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initViews();
        initViewData();
        initEvents();
        doAfterAll();
    }


    /*---------------抽象方法---------------*/

    /**
     * 在设置布局之前进行的操作
     */
    protected abstract void doBeforeSetLayout();

    /**
     * 设置fragment的布局
     *
     * @return 布局id
     */
    protected abstract int setLayout();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化控件
     */
    protected abstract void initViews();

    /**
     * 初始化控件数据
     */
    protected abstract void initViewData();

    /**
     * 初始化事件
     */
    protected abstract void initEvents();

    /**
     * 在最后执行的操作
     */
    protected abstract void doAfterAll();

    /*---------------自定义子类可用函数---------------*/

    /**
     * 让fragment可以和Activity一样拥有findViewById函数
     *
     * @param viewId 控件id
     * @return 控件
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewById(@IdRes int viewId) {
        return (T) root.findViewById(viewId);
    }

    /**
     * 隐藏标题栏
     */
    protected void hideTitleBar() {
        if (null == toolBar) {
            return;
        }
        toolBar.setVisibility(View.GONE);
    }

    /**
     * 将整个fragment的所有布局隐藏
     */
    protected void hideAll() {
        if (root == null) {
            return;
        }
        root.setVisibility(View.GONE);
    }

    /**
     * 将整个fragment的所有布局显示
     */
    protected void showAll() {
        if (null == root) {
            return;
        }
        root.setVisibility(View.VISIBLE);
    }

    /**
     * 显示标题栏
     */
    protected void showTitle() {
        if (null == toolBar) {
            return;
        }
        toolBar.setVisibility(View.VISIBLE);
    }


    /**
     * 设置标题栏的内容
     *
     * @param titleRes 标题栏的资源id
     */
    protected void setTitleText(@StringRes int titleRes) {
        if (null == toolBar) {
            return;
        }
        if (null == titleView) {
            return;
        }
        toolBar.setVisibility(View.VISIBLE);
        titleView.setText(titleRes);
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleText 标题栏的文本
     */
    protected void setTitleText(String titleText) {
        if (null == toolBar) {
            return;
        }
        if (null == titleView) {
            return;
        }
        toolBar.setVisibility(View.VISIBLE);
        titleView.setText(titleText);
    }

    /**
     * 设置标题栏左边的图片
     *
     * @param drawableRes 图片资源文件
     */
    protected void setTitleLeftImage(@DrawableRes int drawableRes) {
        if (null == titleLeftImage) {
            return;
        }
        titleLeftImage.setVisibility(View.VISIBLE);
        titleLeftImage.setImageResource(drawableRes);
    }

    /**
     * 隐藏标题栏左边的图片
     */
    protected void hideTitleLeftImage() {
        if (null == titleLeftImage) {
            return;
        }
        titleLeftImage.setVisibility(View.GONE);
    }

    /**
     * 设置标题栏左边的文本内容
     *
     * @param textRes 文本资源
     */
    protected void setTitleLeftText(@StringRes int textRes) {
        if (null == titleLeftImage) {
            return;
        }
        if (null == titleLeftText) {
            return;
        }
        titleLeftImage.setVisibility(View.VISIBLE);
        titleLeftText.setText(textRes);
    }

    /**
     * 设置标题栏左边的文本内容
     *
     * @param text 文本
     */
    protected void setTitleLeftText(String text) {
        if (null == titleLeftImage) {
            return;
        }

        if (null == titleLeftText) {
            return;
        }
        titleLeftImage.setVisibility(View.VISIBLE);
        titleLeftText.setText(text);
    }

    /**
     * 隐藏标题栏左边的文本
     */
    protected void hideTitleLeftText() {
        if (null == titleLeftText) {
            return;
        }
        titleLeftText.setVisibility(View.GONE);
    }

    /**
     * 设置标题栏右边的图片
     *
     * @param drawableRes 图片资源文件
     */
    protected void setTitleRightImage(@DrawableRes int drawableRes) {
        if (null == titleRightImage) {
            return;
        }
        titleRightImage.setVisibility(View.VISIBLE);
        titleRightImage.setImageResource(drawableRes);
    }

    /**
     * 隐藏标题栏右边的小图标
     */
    protected void hideTitleRightImage() {
        if (null == titleRightImage) {
            return;
        }
        titleRightImage.setVisibility(View.GONE);
    }

    /**
     * 设置标题栏右边的文字
     *
     * @param textRes 文本资源
     */
    protected void setTitleRightText(@StringRes int textRes) {
        if (null == titleRightText) {
            return;
        }
        titleRightText.setVisibility(View.VISIBLE);
        titleRightText.setText(textRes);
    }

    /**
     * 设置标题栏右边的文字
     *
     * @param text 文本
     */
    protected void setTitleRightText(String text) {
        if (null == titleRightText) {
            return;
        }
        titleRightText.setVisibility(View.VISIBLE);
        titleRightText.setText(text);
    }

    /**
     * 隐藏标题栏右边的文本
     */
    protected void hideTitleRightText() {
        if (null == titleRightText) {
            return;
        }
        titleRightText.setVisibility(View.GONE);
    }

    /**
     * 设置标题栏左边的图片的点击事件
     */
    protected void setTitleLeftImageClickListener(View.OnClickListener clickListener) {
        if (null == titleLeftImage) {
            return;
        }
        titleLeftImage.setClickable(true);
        titleLeftImage.setOnClickListener(clickListener);
    }

    /**
     * 设置标题栏左边的文字的点击事件
     */
    protected void setTitleLeftTextClickListener(View.OnClickListener clickListener) {
        if (null == titleLeftText) {
            return;
        }
        titleLeftText.setClickable(true);
        titleLeftText.setOnClickListener(clickListener);
    }

    /**
     * 设置标题栏右边的图片的点击事件
     */
    protected void setTitleRightImageClickListener(View.OnClickListener clickListener) {
        if (null == titleRightImage) {
            return;
        }
        titleRightImage.setClickable(true);
        titleRightImage.setOnClickListener(clickListener);
    }

    /**
     * 设置标题栏右边的文字的点击事件
     */
    protected void setTitleRightTextClickListener(View.OnClickListener clickListener) {
        if (null == titleRightText) {
            return;
        }
        titleRightText.setClickable(true);
        titleRightText.setOnClickListener(clickListener);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 状态栏颜色
     */
    protected void setFragmentStatusColor(@ColorInt int color) {
        if (null == statusView) {
            return;
        }
        statusView.setVisibility(View.VISIBLE);
        statusView.setBackgroundColor(color);
    }

    /**
     * 设置状态栏颜色
     *
     * @param colorRes 状态栏颜色
     */
    protected void setFragmentStatusColorRes(@ColorRes int colorRes) {
        if (null == statusView) {
            return;
        }
        statusView.setVisibility(View.VISIBLE);
        statusView.setBackgroundResource(colorRes);
    }

    /**
     * 隐藏Fragment的状态栏控件
     */
    protected void hideFragmentStatusView() {
        statusView.setVisibility(View.GONE);
    }

    /**
     * 显示Fragment的状态栏控件
     */
    protected void showFragmentStatusView() {
        statusView.setVisibility(View.VISIBLE);
    }
}
