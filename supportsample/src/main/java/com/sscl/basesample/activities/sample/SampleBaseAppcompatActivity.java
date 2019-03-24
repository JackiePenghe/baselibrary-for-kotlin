package com.sscl.basesample.activities.sample;

import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.OSHelper;
import com.sscl.baselibrary.utils.StatusBarUtil;
import com.sscl.basesample.R;

/**
 * @author jacke
 */
public class SampleBaseAppcompatActivity extends BaseAppCompatActivity {

    private static final String TAG = SampleBaseAppcompatActivity.class.getSimpleName();


    /**
     * 标题栏的返回按钮被按下的时候回调此函数
     */
    @Override
    protected void titleBackClicked() {
        //一般情况下直接调用onBackPressed即可
        onBackPressed();
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {
        //在Activity中，有时候会设置一些属性，这些属性在setContentView()之前，可在这里实现
        if (OSHelper.isEMUI()) {
            OSHelper.miuiSetStatusBarLightMode(getWindow(), true);
            StatusBarUtil.setColor(this, Color.WHITE, 60);
        } else if (OSHelper.isFlyme()) {
            OSHelper.flymeSetStatusBarLightMode(getWindow(), true);
            StatusBarUtil.setColor(this, Color.WHITE, 60);
        } else {
            Log.w(TAG, "不是miui或者flyme,不设置状态栏字体深色");
        }
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_sample_base_appcompat;
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    @Override
    protected void doBeforeInitOthers() {
    }

    /**
     * 初始化布局控件
     */
    @Override
    protected void initViews() {
        //通常在这里findViewById()
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {
        //在这里设置View的数据。如：ListView.setAdapter()
    }

    /**
     * 初始化其他数据
     */
    @Override
    protected void initOtherData() {

    }

    /**
     * 初始化事件
     */
    @Override
    protected void initEvents() {
        //在这里设置监听事件
    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {

    }

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    @Override
    protected boolean createOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    @Override
    protected boolean optionsItemSelected(MenuItem item) {
        return false;
    }
}
