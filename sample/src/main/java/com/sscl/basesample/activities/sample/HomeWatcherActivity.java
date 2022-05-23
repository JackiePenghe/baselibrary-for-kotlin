package com.sscl.basesample.activities.sample;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.receiver.HomeWatcherReceiver;
import com.sscl.baselibrary.utils.HomeWatcher;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.basesample.R;

/**
 * @author jackie
 */
public class HomeWatcherActivity extends BaseAppCompatActivity {

    private HomeWatcher homeWatcher;

    private final HomeWatcherReceiver.OnHomePressedListener onHomePressedListener = new HomeWatcherReceiver.OnHomePressedListener() {
        @Override
        public void onHomePressed() {
            ToastUtil.toastLong(HomeWatcherActivity.this, "home button pressed");
        }

        @Override
        public void onHomeLongPressed() {
            ToastUtil.toastLong(HomeWatcherActivity.this, "home button long pressed");
        }
    };

    /**
     * 标题栏的返回按钮被按下的时候回调此函数
     */
    @Override
    protected boolean titleBackClicked() {
        onBackPressed();
        return true;
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {
        homeWatcher = new HomeWatcher(this);
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.com_sscl_basesample_activity_home_watcher;
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

    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {

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
        homeWatcher.setOnHomePressedListener(onHomePressedListener);
    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {
        homeWatcher.startWatch();
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

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        homeWatcher.stopWatch();
    }
}
