package com.sscl.basesample.activities.sample;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.basesample.R;

/**
 * Toast测试
 *
 * @author jackie
 */
public class ToastTestActivity extends BaseAppCompatActivity {

    private Button longTimeBtn;
    private Button shortTimeBtn;
    private Button time100Btn;
    private Button time500Btn;

    private RadioGroup reuseRadioGroup;
    private final View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.long_time:
                ToastUtil.toastLong(ToastTestActivity.this, R.string.long_time);
                break;
            case R.id.short_time:
                ToastUtil.toastShort(ToastTestActivity.this, R.string.short_time);
                break;
            case R.id.time_100:
                ToastUtil.toast(ToastTestActivity.this, R.string.time_100, 100);
                break;
            case R.id.time_500:
                ToastUtil.toast(ToastTestActivity.this, R.string.time_500, 500);
                break;
            default:
                break;
        }
    };
    private final RadioGroup.OnCheckedChangeListener onCheckedChangeListener = (group, checkedId) -> {
        //noinspection SwitchStatementWithTooFewBranches
        switch (group.getId()) {
            case R.id.reuse_toast_group:
                onReuseGroupButtonCheckedChanged(checkedId);
                break;
            default:
                break;
        }
    };

    private void onReuseGroupButtonCheckedChanged(int checkedId) {
        switch (checkedId) {
            case R.id.open_reuse:
                ToastUtil.setToastReuse(true);
                break;
            case R.id.close_reuse:
                ToastUtil.setToastReuse(false);
                break;
            default:
                break;
        }
    }

    /**
     * 标题栏的返回按钮被按下的时候回调此函数
     */
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
        return R.layout.activity_toast_test;
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
        longTimeBtn = findViewById(R.id.long_time);
        shortTimeBtn = findViewById(R.id.short_time);
        time100Btn = findViewById(R.id.time_100);
        time500Btn = findViewById(R.id.time_500);
        reuseRadioGroup = findViewById(R.id.reuse_toast_group);
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
        reuseRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        longTimeBtn.setOnClickListener(onClickListener);
        shortTimeBtn.setOnClickListener(onClickListener);
        time100Btn.setOnClickListener(onClickListener);
        time500Btn.setOnClickListener(onClickListener);
    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {
        reuseRadioGroup.check(R.id.open_reuse);
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
}
