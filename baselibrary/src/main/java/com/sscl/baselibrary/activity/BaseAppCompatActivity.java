package com.sscl.baselibrary.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.utils.StatusBarUtil;


/**
 * 基于AppCompatActivity的封装的Activity基类
 *
 * @author alm
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 标题
     */
    private Toolbar toolbar;
    /**
     * 标题的返回按钮处的点击事件
     */
    private View.OnClickListener mTitleBackButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            titleBackClicked();
        }
    };

    /**
     * 子类继承此类之后，设置的布局都在FrameLayout中
     */
    private FrameLayout content;
    /**
     * 标题栏的文字
     */
    private TextView titleView;

    /**
     * 整个BaseActivity的根布局（setContentLayout传入的布局）
     */
    private LinearLayout rootView;

    /*--------------------------------重写父类方法--------------------------------*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doBeforeSetLayout();

        setContentView(R.layout.activity_base_appcompat);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int color = ContextCompat.getColor(this, R.color.colorPrimary);
            StatusBarUtil.setColor(this, color);
        }
        initThisView();
        initThisData();
        initThisEvents();

        doBeforeInitOthers();
        initViews();
        initViewData();
        initOtherData();
        initEvents();
        doAfterAll();
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        //创建菜单选项
        return createOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //菜单的选项被点击时的处理
        return optionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toolbar.setNavigationOnClickListener(null);
        toolbar = null;
        mTitleBackButtonOnClickListener = null;
        content.removeAllViews();
        content = null;
    }

    /*--------------------------------抽象方法--------------------------------*/

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
    protected abstract void titleBackClicked();

    /**
     * 在设置布局之前需要进行的操作
     */
    protected abstract void doBeforeSetLayout();

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @LayoutRes
    protected abstract int setLayout();

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    protected abstract void doBeforeInitOthers();

    /**
     * 初始化布局控件
     */
    protected abstract void initViews();

    /**
     * 初始化控件数据
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
     * 在最后进行的操作
     */
    protected abstract void doAfterAll();

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    protected abstract boolean createOptionsMenu(@NonNull Menu menu);

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    protected abstract boolean optionsItemSelected(@NonNull MenuItem item);

    /*--------------------------------私有方法--------------------------------*/

    /**
     * 初始化本类固定的控件
     */
    private void initThisView() {
        toolbar = findViewById(R.id.toolbar);
        titleView = findViewById(R.id.toolbar_title);
        content = findViewById(R.id.base_frame_content);
        rootView = findViewById(R.id.base_root_view);
    }

    /**
     * 初始化本类固定的数据
     */
    private void initThisData() {
        titleView.setText(R.string.app_title);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            //设置返回键可用
            supportActionBar.setHomeButtonEnabled(true);
//            //不显示标题
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        int layoutResId = setLayout();
        if (layoutResId == 0) {
            throw new RuntimeException("setLayout with wrong layout resource id");
        }
        View view = getLayoutInflater().inflate(layoutResId, null);
        content.addView(view);
    }

    /**
     * 初始化本类固定的事件
     */
    private void initThisEvents() {
        toolbar.setNavigationOnClickListener(mTitleBackButtonOnClickListener);
    }

    /*--------------------------------子类可用方法--------------------------------*/

    /**
     * 设置title返回按钮的处理事件
     *
     * @param titleBackButtonOnClickListener 返回按钮的处理事件
     */
    @SuppressWarnings("unused")
    protected void setTitleBackOnClickListener(@Nullable View.OnClickListener titleBackButtonOnClickListener) {
        mTitleBackButtonOnClickListener = titleBackButtonOnClickListener;
    }

    /**
     * 隐藏标题栏的返回按钮
     */
    protected void hideTitleBackButton() {
        ActionBar supportActionBar = getSupportActionBar();
        if (null == supportActionBar) {
            throw new RuntimeException("toolbar is null!Please invoke this method after method \"setLayout()\"");
        }
        //左侧不添加默认的返回图标
        supportActionBar.setDisplayHomeAsUpEnabled(false);
        //设置返回键不可用
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    /**
     * 显示标题栏的返回按钮
     */
    @SuppressWarnings("unused")
    protected void showTitleBackButton() {
        ActionBar supportActionBar = getSupportActionBar();
        if (null == supportActionBar) {
            throw new RuntimeException("toolbar is null!Please invoke this method after method \"setLayout()\"");
        }
        //左侧添加一个默认的返回图标
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * 设置根布局（整个Activity）的背景色
     *
     * @param drawableRes 背景色
     */
    @SuppressWarnings("unused")
    protected void setRootBackGroundResource(@DrawableRes int drawableRes) {
        rootView.setBackgroundResource(drawableRes);
    }

    /**
     * 设置根布局（整个Activity）的背景色
     *
     * @param color 背景色
     */
    @SuppressWarnings("unused")
    protected void setRootBackGroundColor(@ColorInt int color) {
        rootView.setBackgroundColor(color);
    }

    /**
     * 隐藏标题栏
     */
    protected void hideTitleBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (null == supportActionBar) {
            throw new RuntimeException("supportActionBar is null!Please invoke this method after method \"setLayout()\"");
        }
        supportActionBar.hide();
    }

    /**
     * 显示标题栏
     */
    @SuppressWarnings("unused")
    protected void showTitleBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.show();
        }
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleRes 标题栏的资源id
     */
    @SuppressWarnings("unused")
    protected void setTitleText(@StringRes int titleRes) {
        if (null == titleView) {
            throw new RuntimeException("titleView is null!Please invoke this method after method \"setLayout()\"");
        }
        titleView.setText(titleRes);
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleText 标题栏的文本
     */
    @SuppressWarnings("unused")
    protected void setTitleText(String titleText) {
        if (null == titleView) {
            throw new RuntimeException("titleView is null!Please invoke this method after method \"setLayout()\"");
        }
        titleView.setText(titleText);
    }

    /**
     * 设置标题栏文本颜色
     *
     * @param color 文本颜色
     */
    protected void setTitleTextColor(@ColorInt int color) {
        if (toolbar == null) {
            throw new RuntimeException("toolbar is null!Please invoke this method after method \"setLayout()\"");
        }
        toolbar.setTitleTextColor(color);
    }

    /**
     * 设置标题栏的背景色
     *
     * @param color 标题栏的背景色
     */
    @SuppressWarnings("unused")
    protected void setTitleBackgroundColor(@ColorInt int color) {
        if (toolbar == null) {
            throw new RuntimeException("appBarLayout is null!Please invoke this method after method \"setLayout()\"");
        }
        toolbar.setBackgroundColor(color);
    }

    /**
     * 设置标题栏的背景
     *
     * @param drawable 标题栏的背景
     */
    @SuppressWarnings("unused")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void setTitleBackgroundDrawable(Drawable drawable) {
        if (toolbar == null) {
            throw new RuntimeException("appBarLayout is null!Please invoke this method after method \"setLayout()\"");
        }
        toolbar.setBackground(drawable);
    }

    /**
     * 设置标题栏的背景
     *
     * @param drawableRes 标题栏的背景
     */
    @SuppressWarnings("unused")
    protected void setTitleBackgroundResource(@DrawableRes int drawableRes) {
        if (toolbar == null) {
            throw new RuntimeException("appBarLayout is null!Please invoke this method after method \"setLayout()\"");
        }
        toolbar.setBackgroundResource(drawableRes);
    }

    /**
     * 设置标题栏文本颜色
     *
     * @param colorRes 文本颜色
     */
    @SuppressWarnings("unused")
    protected void setTitleTextColorRes(@ColorRes int colorRes) {
        int color = ContextCompat.getColor(this, colorRes);
        setTitleTextColor(color);
    }

    /**
     * 设置标题栏左边的图片
     *
     * @param drawableId 标题栏左边的图片资源id
     */
    @SuppressWarnings("unused")
    protected void setTitleBackIcon(@DrawableRes int drawableId) {
        toolbar.setNavigationIcon(drawableId);
    }

    /**
     * 获取Activity布局的整个布局
     *
     * @return Activity布局的整个布局
     */
    @SuppressWarnings("unused")
    @NonNull
    protected FrameLayout getContentView() {
        return content;
    }
}
