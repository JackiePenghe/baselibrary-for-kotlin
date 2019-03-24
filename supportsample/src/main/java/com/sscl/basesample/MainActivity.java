package com.sscl.basesample;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.receiver.ScreenStatusReceiver;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.Tool;
import com.sscl.basesample.activities.sample.AllPurposeAdapterActivity;
import com.sscl.basesample.activities.sample.HomeWatcherActivity;
import com.sscl.basesample.activities.sample.SampleBaseAppcompatActivity;
import com.sscl.basesample.activities.sample.SampleBaseDrawerActivity;
import com.sscl.basesample.activities.sample.SampleBaseFragmentActivity;
import com.sscl.basesample.activities.sample.SampleBasePopupWindowActivity;
import com.sscl.basesample.activities.sample.ToastTestActivity;


/**
 * @author jacke
 */
public class MainActivity extends BaseAppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button baseAppcompatActivityBtn;
    private Button baseDrawerActivityBtn;
    private Button baseFragmentBtn;
    private Button basePopupWindowBtn;
    private Button allPurposeAdapterBtn;
    private Button homeWatcherBtn;
    private Button toastTestBtn;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()) {
                case R.id.all_purpose_adapter:
                    intent = new Intent(MainActivity.this, AllPurposeAdapterActivity.class);
                    break;
                case R.id.base_appcompat_activity:
                    intent = new Intent(MainActivity.this, SampleBaseAppcompatActivity.class);
                    break;
                case R.id.base_drawer_activity:
                    intent = new Intent(MainActivity.this, SampleBaseDrawerActivity.class);
                    break;
                case R.id.base_fragment:
                    intent = new Intent(MainActivity.this, SampleBaseFragmentActivity.class);
                    break;
                case R.id.base_popup_window:
                    intent = new Intent(MainActivity.this, SampleBasePopupWindowActivity.class);
                    break;
                case R.id.home_watcher:
                    intent = new Intent(MainActivity.this, HomeWatcherActivity.class);
                    break;
                case R.id.toast_test:
                    intent = new Intent(MainActivity.this, ToastTestActivity.class);
                    break;
                case R.id.title_left_text:
                    DebugUtil.warnOut(TAG, "文字左被点击");
                    break;
                case R.id.title_right_text:
                    DebugUtil.warnOut(TAG, "文字右被点击");
                    break;
                default:
                    break;
            }
            if (intent != null) {
                startActivity(intent);
            }
        }
    };
    private ScreenStatusReceiver.OnScreenStatusChangedListener onScreenStatusChangedListener = new ScreenStatusReceiver.OnScreenStatusChangedListener() {
        @Override
        public void onScreenOn() {
            DebugUtil.warnOut(TAG, "屏幕开启");
        }

        @Override
        public void onScreenOff() {
            DebugUtil.warnOut(TAG, "屏幕关闭");
        }

        @Override
        public void onUserUnlock() {
            DebugUtil.warnOut(TAG, "用户解锁完成");
        }
    };

    @Override
    protected void titleBackClicked() {
        onBackPressed();
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {
//        Tool.isZhCN();
//        /* 设置白色背景状态栏，仅支持MIUI6以上，Flyme4.0以上
//         * 安卓6.0以上，深色字体通过style配置
//         */
//        if (OSHelper.isEMUI()) {
//            OSHelper.miuiSetStatusBarLightMode(getWindow(), true);
//            StatusBarUtil.setColor(this, Color.WHITE, 60);
//        } else if (OSHelper.isFlyme()) {
//            OSHelper.flymeSetStatusBarLightMode(getWindow(), true);
//            StatusBarUtil.setColor(this, Color.WHITE, 60);
//        } else {
////            Log.w(TAG, "不是miui或者flyme,不设置状态栏字体深色");
//        }
        //noinspection JavadocReference
//        new Thread() {
//            /**
//             * If this thread was constructed using a separate
//             * <code>Runnable</code> run object, then that
//             * <code>Runnable</code> object's <code>run</code> method is called;
//             * otherwise, this method does nothing and returns.
//             * <p>
//             * Subclasses of <code>Thread</code> should override this method.
//             *
//             * @see #start()
//             * @see #stop()
//             * @see #Thread(ThreadGroup, Runnable, String)
//             */
//            @Override
//            public void run() {
//                super.run();
//                Tool.toastL(MainActivity.this, "测试在线程中弹出吐司");
//            }
//        }.start();
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    @Override
    protected void doBeforeInitOthers() {
//        setTitleLeftText("文字左");
//        setTitleRightText("文字右");
        hideTitleBackButton();
    }

    /**
     * 初始化布局控件
     */
    @Override
    protected void initViews() {
        baseAppcompatActivityBtn = findViewById(R.id.base_appcompat_activity);
        baseDrawerActivityBtn = findViewById(R.id.base_drawer_activity);
        baseFragmentBtn = findViewById(R.id.base_fragment);
        basePopupWindowBtn = findViewById(R.id.base_popup_window);
        allPurposeAdapterBtn = findViewById(R.id.all_purpose_adapter);
        homeWatcherBtn = findViewById(R.id.home_watcher);
        toastTestBtn = findViewById(R.id.toast_test);
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
//        Uri uriForFile = FileProviderUtil.getUriFromFile(this, FileUtil.getAppDir());
//        Tool.warnOut(TAG,"uriForFile = " + uriForFile);
    }

    /**
     * 初始化事件
     */
    @Override
    protected void initEvents() {
        baseAppcompatActivityBtn.setOnClickListener(onClickListener);
        baseDrawerActivityBtn.setOnClickListener(onClickListener);
        baseFragmentBtn.setOnClickListener(onClickListener);
        basePopupWindowBtn.setOnClickListener(onClickListener);
        allPurposeAdapterBtn.setOnClickListener(onClickListener);
        homeWatcherBtn.setOnClickListener(onClickListener);
        toastTestBtn.setOnClickListener(onClickListener);
//        setOnTitleLeftTextClickListener(onClickListener);
//        setOnTitleRightTextClickListener(onClickListener);
    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {
        Tool.startScreenStatusListener(this);
        Tool.setOnScreenStatusChangedListener(onScreenStatusChangedListener);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tool.stopScreenStatusListener(this);
    }
}
