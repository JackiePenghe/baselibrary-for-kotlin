package com.sscl.basesample;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.receiver.ScreenStatusReceiver;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.SharedPreferencesTools;
import com.sscl.baselibrary.utils.Tool;
import com.sscl.basesample.activities.WidgetActivity;
import com.sscl.basesample.activities.sample.AllPurposeAdapterActivity;
import com.sscl.basesample.activities.sample.HomeWatcherActivity;
import com.sscl.basesample.activities.sample.ImageLoaderActivity;
import com.sscl.basesample.activities.sample.SampleBannerActivity;
import com.sscl.basesample.activities.sample.SampleBaseAppcompatActivity;
import com.sscl.basesample.activities.sample.SampleBaseDrawerActivity;
import com.sscl.basesample.activities.sample.SampleBaseFragmentActivity;
import com.sscl.basesample.activities.sample.SampleBasePopupWindowActivity;
import com.sscl.basesample.activities.sample.SampleWebViewActivity;
import com.sscl.basesample.activities.sample.SelectFileActivity;
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
    private Button widgetBtn;
    private Button selectFileBtn;
    private Button imageLoaderBtn;
    private Button bannerBtn;
    private Button webViewBtn;

    private final View.OnClickListener onClickListener = view -> {
        Intent intent = null;
        int id = view.getId();
        if (id == R.id.all_purpose_adapter) {
            intent = new Intent(MainActivity.this, AllPurposeAdapterActivity.class);
        } else if (id == R.id.base_appcompat_activity) {
            intent = new Intent(MainActivity.this, SampleBaseAppcompatActivity.class);
        } else if (id == R.id.base_drawer_activity) {
            intent = new Intent(MainActivity.this, SampleBaseDrawerActivity.class);
        } else if (id == R.id.base_fragment) {
            intent = new Intent(MainActivity.this, SampleBaseFragmentActivity.class);
        } else if (id == R.id.base_popup_window) {
            intent = new Intent(MainActivity.this, SampleBasePopupWindowActivity.class);
        } else if (id == R.id.home_watcher) {
            intent = new Intent(MainActivity.this, HomeWatcherActivity.class);
        } else if (id == R.id.toast_test) {
            intent = new Intent(MainActivity.this, ToastTestActivity.class);
        } else if (id == R.id.title_left_text) {
            DebugUtil.warnOut(TAG, "文字左被点击");
        } else if (id == R.id.title_right_text) {
            DebugUtil.warnOut(TAG, "文字右被点击");
        } else if (id == R.id.widget) {
            intent = new Intent(MainActivity.this, WidgetActivity.class);
        } else if (id == R.id.select_file) {
            intent = new Intent(MainActivity.this, SelectFileActivity.class);
        } else if (id == R.id.image_loader) {
            intent = new Intent(MainActivity.this, ImageLoaderActivity.class);
        } else if (id == R.id.banner) {
            intent = new Intent(MainActivity.this, SampleBannerActivity.class);
        } else if (id == R.id.web_view) {
            intent = new Intent(MainActivity.this, SampleWebViewActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    };
    private final ScreenStatusReceiver.OnScreenStatusChangedListener onScreenStatusChangedListener = new ScreenStatusReceiver.OnScreenStatusChangedListener() {
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
    protected boolean titleBackClicked() {
        onBackPressed();
        return true;
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {
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
        widgetBtn = findViewById(R.id.widget);
        selectFileBtn = findViewById(R.id.select_file);
        imageLoaderBtn = findViewById(R.id.image_loader);
        bannerBtn = findViewById(R.id.banner);
        webViewBtn = findViewById(R.id.web_view);
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
        baseAppcompatActivityBtn.setOnClickListener(onClickListener);
        baseDrawerActivityBtn.setOnClickListener(onClickListener);
        baseFragmentBtn.setOnClickListener(onClickListener);
        basePopupWindowBtn.setOnClickListener(onClickListener);
        allPurposeAdapterBtn.setOnClickListener(onClickListener);
        homeWatcherBtn.setOnClickListener(onClickListener);
        toastTestBtn.setOnClickListener(onClickListener);
        widgetBtn.setOnClickListener(onClickListener);
        selectFileBtn.setOnClickListener(onClickListener);
        imageLoaderBtn.setOnClickListener(onClickListener);
        bannerBtn.setOnClickListener(onClickListener);
        webViewBtn.setOnClickListener(onClickListener);
    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {
        Tool.startScreenStatusListener(this);
        Tool.setOnScreenStatusChangedListener(onScreenStatusChangedListener);
        SharedPreferencesTools.getInstance(this, "Test").putValueApply("boolean", true);
        boolean value = SharedPreferencesTools.getInstance(this, "Test").getValue("booleana", false);
        DebugUtil.warnOut(TAG, "value = " + value);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tool.stopScreenStatusListener(this);
    }
}
