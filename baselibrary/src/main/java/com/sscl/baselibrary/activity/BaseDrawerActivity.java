package com.sscl.baselibrary.activity;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.sscl.baselibrary.R;


/**
 * 带有侧边栏的Activity基类
 *
 * @author alm
 */
public abstract class BaseDrawerActivity extends AppCompatActivity {

    /*--------------------------------静态常量--------------------------------*/

    protected final String TAG = getClass().getSimpleName();

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 标题
     */
    private Toolbar toolbar;
    /**
     * 抽屉布局
     */
    private DrawerLayout drawerLayout;
    /**
     * 左上角可展示侧边栏状态的小控件
     */
    private ActionBarDrawerToggle actionBarDrawerToggle;

    /**
     * 侧边栏
     */
    private NavigationView navigationView;
    /**
     * 侧边栏被选中的ID
     */
    private int menuItemId;
    /**
     * 标题文字
     */
    private TextView titleView;
    /**
     * 记录当前侧边栏的打开状态
     */
    private boolean drawerOpen;

    /**
     * 子类继承此类之后，设置的布局都在FrameLayout中
     */
    private FrameLayout content;

    /**
     * 抽屉布局侧边栏的相关监听
     */
    private DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            drawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            drawerOpened(drawerView);
            drawerOpen = true;
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            drawerClosed(drawerView);
            drawerOpen = false;
            if (menuItemId != -1) {
                navigationItemSelected(menuItemId);
                menuItemId = -1;
            }
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            switch (newState) {
                case DrawerLayout.STATE_DRAGGING:
                case DrawerLayout.STATE_SETTLING:
                    drawerOpen = true;
                    break;
                case DrawerLayout.STATE_IDLE:
                default:
                    break;
            }
            drawerStateChanged(newState);
        }
    };
    /**
     * 侧边栏的选项点击监听
     */
    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            menuItemId = item.getItemId();
            closeDrawer();
            return true;
        }
    };
    /*--------------------------------重写父类方法--------------------------------*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetLayout();

        setContentView(R.layout.com_jackiepenghe_baselibrary_activity_base_drawer);

        toolbar = findViewById(R.id.toolbar);
        titleView = findViewById(R.id.toolbar_title);
        if (titleView != null) {
            titleView.setText(R.string.com_jackiepenghe_app_title);
        }
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.com_jackiepenghe_navigation_drawer_open, R.string.com_jackiepenghe_navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        drawerLayout.addDrawerListener(drawerListener);
        actionBarDrawerToggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        View headerView;
        try {
            headerView = View.inflate(BaseDrawerActivity.this, setNavigationViewHeaderViewLayoutResId(), null);
        } catch (Exception e) {
            headerView = View.inflate(BaseDrawerActivity.this, R.layout.nav_header_main, null);
        }
        navigationView.addHeaderView(headerView);
        try {
            navigationView.inflateMenu(setNavigationMenuResId());
        } catch (Exception e) {
            navigationView.inflateMenu(R.menu.com_jackiepenghe_activity_main_drawer);
        }
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        content = findViewById(R.id.base_frame_content);
        int layoutResId = setLayout();
        if (layoutResId == 0) {
            throw new RuntimeException("setLayout with wrong layout resource id");
        }
        View view = getLayoutInflater().inflate(layoutResId, null);
        content.addView(view);

        doBeforeInitOthers();
        initViews();
        initViewData();
        initOtherData();
        initEvents();
        doAfterAll();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toolbar.setNavigationOnClickListener(null);
        toolbar.setNavigationIcon(null);
        toolbar = null;
        drawerLayout.removeAllViews();
        drawerLayout.removeDrawerListener(drawerListener);
        drawerLayout.removeDrawerListener(actionBarDrawerToggle);
        drawerLayout = null;
        drawerListener = null;
        actionBarDrawerToggle = null;
        navigationView.setNavigationItemSelectedListener(null);
        navigationView = null;
        onNavigationItemSelectedListener = null;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return createOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return optionsItemSelected(item);
    }

    /*--------------------------------抽象方法--------------------------------*/

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
     * 在最后执行的操作
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

    /**
     * DrawerLayout的滑动监听
     *
     * @param drawerView  侧边栏
     * @param slideOffset 滑动距离
     */
    protected abstract void drawerSlide(View drawerView, float slideOffset);

    /**
     * DrawerLayout已经完全打开了
     *
     * @param drawerView 侧边栏
     */
    protected abstract void drawerOpened(View drawerView);

    /**
     * DrawerLayout已经完全关闭了
     *
     * @param drawerView 侧边栏
     */
    protected abstract void drawerClosed(View drawerView);

    /**
     * DrawerLayout的状态改变了
     *
     * @param newState 新的状态
     */
    protected abstract void drawerStateChanged(int newState);

    /**
     * 获取侧边栏的头部的资源id，如果不设置此选项，会以默认的头部布局进行填充
     *
     * @return 侧边栏的头部的资源id
     */
    @LayoutRes
    protected abstract int setNavigationViewHeaderViewLayoutResId();

    /**
     * 获取侧边栏的菜单的资源id，如果不设置此选项，会以默认的菜单进行填充
     *
     * @return 侧边栏的菜单的资源id
     */
    @MenuRes
    protected abstract int setNavigationMenuResId();

    /**
     * 侧边栏选项被选中时执行的回调
     *
     * @param menuItemId 被选中的侧边栏选项ID
     */
    protected abstract void navigationItemSelected(@IdRes int menuItemId);

    /*--------------------------------子类可用方法--------------------------------*/

    /**
     * 关闭侧边栏
     */
    protected void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleResId 标题栏的资源id
     */
    protected void setTitleText(@StringRes int titleResId) {
        titleView.setText(titleResId);
    }

    /**
     * 设置标题栏的内容
     *
     * @param titleText 标题栏内容
     */
    protected void setTitleText(String titleText) {
        titleView.setText(titleText);
    }

    /**
     * 设置标题栏文本颜色
     *
     * @param color 文本颜色
     */
    protected void setTitleTextColor(@ColorInt int color) {
        if (titleView == null) {
            throw new RuntimeException("titleView is null!Please invoke this method after method \"setLayout()\"");
        }
        titleView.setTextColor(color);
    }

    /**
     * 设置标题栏文本颜色
     *
     * @param colorRes 文本颜色
     */
    @SuppressWarnings("SameParameterValue")
    protected void setTitleTextColorRes(@ColorRes int colorRes) {
        int color = ContextCompat.getColor(this, colorRes);
        setTitleTextColor(color);
    }

    /**
     * 设置标题栏背景色
     *
     * @param color 标题栏背景色
     */
    protected void setTitleBackgroundColor(@ColorInt int color) {
        if (toolbar == null) {
            throw new RuntimeException("appBarLayout is null!Please invoke this method after method \"setLayout()\"");
        }
        toolbar.setBackgroundColor(color);
    }

    /**
     * 设置标题栏背景
     *
     * @param drawable 标题栏背景
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void setTitleBackgroundDrawable(Drawable drawable) {
        if (toolbar == null) {
            throw new RuntimeException("appBarLayout is null!Please invoke this method after method \"setLayout()\"");
        }
        toolbar.setBackground(drawable);
    }

    /**
     * 设置标题栏背景
     *
     * @param drawableRes 标题栏背景
     */
    @SuppressWarnings("unused")
    protected void setTitleBackgroundResource(@DrawableRes int drawableRes) {
        if (toolbar == null) {
            throw new RuntimeException("appBarLayout is null!Please invoke this method after method \"setLayout()\"");
        }
        toolbar.setBackgroundResource(drawableRes);
    }

    /**
     * 获取当前侧边栏的打开状态
     *
     * @return 当前侧边栏的打开状态
     */
    @SuppressWarnings("unused")
    protected boolean isDrawerOpen() {
        return drawerOpen;
    }

    /**
     * 获取整个抽屉布局
     *
     * @return 整个抽屉布局
     */
    @SuppressWarnings("unused")
    protected DrawerLayout getDrawerLayout() {
        return findViewById(R.id.drawer_layout);
    }

    /**
     * 获取整个侧边栏
     *
     * @return 整个侧边栏
     */
    @SuppressWarnings("unused")
    protected NavigationView getNavigationView() {
        return findViewById(R.id.nav_view);
    }

    /**
     * 获取Activity布局的整个布局
     *
     * @return Activity布局的整个布局
     */
    @SuppressWarnings("unused")
    protected FrameLayout getContentView() {
        return content;
    }
}
