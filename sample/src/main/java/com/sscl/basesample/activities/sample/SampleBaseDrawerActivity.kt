package com.sscl.basesample.activities.sample

import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sscl.baselibrary.activity.BaseDrawerActivity
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.utils.ToastUtil
import com.sscl.basesample.R

/**
 * @author jackie
 */
class SampleBaseDrawerActivity : BaseDrawerActivity() {
    /**
     * 在设置布局之前需要进行的操作
     */
    override fun doBeforeSetLayout() {
        //在Activity中，有时候会设置一些属性，这些属性在setContentView()之前，可在这里实现
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_sample_base_drawer
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {

        //重命名标题栏的内容
        setTitleText(R.string.com_sscl_basesample_base_drawer_activity)
        //设置标题栏文本的颜色
        setTitleTextColorRes(R.color.white)
    }

    /**
     * 初始化布局控件
     */
    override fun initViews() {
        //通常在这里findViewById()
    }

    /**
     * 初始化控件数据
     */
    override fun initViewData() {
        //在这里设置View的数据。如：ListView.setAdapter()
    }

    /**
     * 初始化其他数据
     */
    override fun initOtherData() {}

    /**
     * 初始化事件
     */
    override fun initEvents() {
        //在这里设置监听事件
    }

    /**
     * 在最后执行的操作
     */
    override fun doAfterAll() {}

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    override fun createOptionsMenu(menu: Menu): Boolean {
        return false
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    override fun optionsItemSelected(item: MenuItem): Boolean {
        return false
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
    override fun drawerSlide(drawerView: View?, slideOffset: Float) {
        DebugUtil.warnOut(TAG, "slideOffset = $slideOffset")
    }

    /**
     * DrawerLayout已经完全打开了
     *
     * @param drawerView 侧边栏
     */
    override fun drawerOpened(drawerView: View?) {
        ToastUtil.toastLong(this, "侧边栏完全打开了")
    }

    /**
     * DrawerLayout已经完全关闭了
     *
     * @param drawerView 侧边栏
     */
    override fun drawerClosed(drawerView: View?) {
        ToastUtil.toastLong(this, "侧边栏完全关闭了")
    }

    /**
     * DrawerLayout的状态改变了
     *
     * @param newState 新的状态
     */
    override fun drawerStateChanged(newState: Int) {
        ToastUtil.toastLong(this, "侧边栏的状态改变了：$newState")
    }

    /**
     * 获取侧边栏的头部的资源id，如果不设置此选项，会以默认的头部布局进行填充
     *
     * @return 侧边栏的头部的资源id
     */
    override fun setNavigationViewHeaderViewLayoutResId(): Int {
        return 0
    }

    /**
     * 获取侧边栏的菜单的资源id，如果不设置此选项，会以默认的菜单进行填充
     *
     * @return 侧边栏的菜单的资源id
     */
    override fun setNavigationMenuResId(): Int {
        return 0
    }

    /**
     * 侧边栏选项被选中时执行的回调
     *
     * @param menuItemId 被选中的侧边栏选项ID
     */
    override fun navigationItemSelected(menuItemId: Int) {}

    companion object {
        private val TAG = SampleBaseDrawerActivity::class.java.simpleName
    }
}