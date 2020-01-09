package com.sscl.basesample.activities.sample;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseDrawerActivity;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.basesample.R;

/**
 * @author jackie
 */
public class SampleBaseDrawerActivity extends BaseDrawerActivity {

    private static final String TAG = "SampleBaseDrawerActivit";

    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {
        //在Activity中，有时候会设置一些属性，这些属性在setContentView()之前，可在这里实现
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_sample_base_drawer;
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    @Override
    protected void doBeforeInitOthers() {

        //重命名标题栏的内容
        setTitleText(R.string.base_drawer_activity);
        //设置标题栏文本的颜色
        setTitleTextColorRes(android.R.color.white);
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
     * 在最后执行的操作
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
    protected boolean createOptionsMenu(@NonNull Menu menu) {
        return false;
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    @Override
    protected boolean optionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    //想要更改侧边栏的显示内容，方法如下:
    //1.在res目录下新建menu文件夹
    //2.在menu文件夹中新建文件 activity_main_drawer.xml，文件内容参考sample模块下同目录的同名文件

    /**
     * DrawerLayout的滑动监听
     *
     * @param drawerView  侧边栏
     * @param slideOffset 滑动距离
     */
    @Override
    protected void drawerSlide(View drawerView, float slideOffset) {
        DebugUtil.warnOut(TAG,"slideOffset = " + slideOffset);
    }

    /**
     * DrawerLayout已经完全打开了
     *
     * @param drawerView 侧边栏
     */
    @Override
    protected void drawerOpened(View drawerView) {
        ToastUtil.toastLong(this,"侧边栏完全打开了");
    }

    /**
     * DrawerLayout已经完全关闭了
     *
     * @param drawerView 侧边栏
     */
    @Override
    protected void drawerClosed(View drawerView) {
        ToastUtil.toastLong(this,"侧边栏完全关闭了");
    }

    /**
     * DrawerLayout的状态改变了
     *
     * @param newState 新的状态
     */
    @Override
    protected void drawerStateChanged(int newState) {
        ToastUtil.toastLong(this,"侧边栏的状态改变了：" + newState);
    }

    /**
     * 获取侧边栏的头部的资源id，如果不设置此选项，会以默认的头部布局进行填充
     *
     * @return 侧边栏的头部的资源id
     */
    @Override
    protected int setNavigationViewHeaderViewLayoutResId() {
        return 0;
    }

    /**
     * 获取侧边栏的菜单的资源id，如果不设置此选项，会以默认的菜单进行填充
     *
     * @return 侧边栏的菜单的资源id
     */
    @Override
    protected int setNavigationMenuResId() {
        return 0;
    }

    /**
     * 侧边栏选项被选中时执行的回调
     *
     * @param menuItemId 被选中的侧边栏选项ID
     */
    @Override
    protected void navigationItemSelected(int menuItemId) {

    }
}
