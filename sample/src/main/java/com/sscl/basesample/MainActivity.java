package com.sscl.basesample;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.files.FileSystemUtil;
import com.sscl.baselibrary.receiver.ScreenStatusReceiver;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.Tool;
import com.sscl.basesample.activities.WidgetActivity;
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
    private Button widgetBtn;

    private View.OnClickListener onClickListener = view -> {
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
            case R.id.widget:
                intent = new Intent(MainActivity.this, WidgetActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
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
    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {
        Tool.startScreenStatusListener(this);
        Tool.setOnScreenStatusChangedListener(onScreenStatusChangedListener);
        DebugUtil.warnOut(TAG, ConversionUtil.byteArrayToHexStr(ConversionUtil.longToByteArray(0x112233445566L, 1)));
        DebugUtil.warnOut(TAG, ConversionUtil.byteArrayToHexStr(ConversionUtil.longToByteArray(0x112233445566L, 2)));
        DebugUtil.warnOut(TAG, ConversionUtil.byteArrayToHexStr(ConversionUtil.longToByteArray(0x112233445566L, 3)));
        DebugUtil.warnOut(TAG, ConversionUtil.byteArrayToHexStr(ConversionUtil.longToByteArray(0x112233445566L, 4)));
        DebugUtil.warnOut(TAG, ConversionUtil.byteArrayToHexStr(ConversionUtil.longToByteArray(0x112233445566L, 5)));
        DebugUtil.warnOut(TAG, ConversionUtil.byteArrayToHexStr(ConversionUtil.longToByteArray(0x112233445566L, 6)));

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
