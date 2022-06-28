package com.sscl.basesample.activities.sample;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.widget.banner.Banner;
import com.sscl.baselibrary.widget.banner.BaseBannerAdapter;
import com.sscl.basesample.R;
import com.sscl.basesample.adapter.BannerAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author jackie
 */
public class SampleBannerActivity extends BaseAppCompatActivity {

    private static final String TAG = SampleBannerActivity.class.getSimpleName();
    private Banner banner;

    private final ArrayList<String> bannerData = new ArrayList<>();

    private final BannerAdapter bannerAdapter = new BannerAdapter();

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
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

    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.com_sscl_basesample_activity_sample_banner;
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
        banner = findViewById(R.id.banner);
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {
        for (int i = 0; i < 4; i++) {
            bannerData.add(String.valueOf(i + 1));
        }
        banner.setAdapter(bannerAdapter);
        banner.setData(bannerData);
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

    }

    private int bannerDelay = 1;

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {
        bannerAdapter.setOnItemClickListener((itemView, itemData, position) -> {
            DebugUtil.warnOut(TAG, "onItemClick position = " + position + ",itemData = " + itemData);
            banner.setDelayTime(bannerDelay++);
            if (bannerDelay > 5) {
                bannerDelay = 1;
            }
            banner.setDelayTimeUnit(TimeUnit.SECONDS);
            banner.startAutoPlay();
        });
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
    protected void onStart() {
        super.onStart();
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        banner.destroy();
    }
}
