package com.sscl.baselibrary.widget.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.utils.BaseManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Banner extends FrameLayout {

    /*--------------------------------成员变量--------------------------------*/

    private ViewPager viewPager;

    private ScheduledExecutorService scheduledExecutorService;

    private int viewPagerStates = ViewPager.SCROLL_STATE_IDLE;
    private BaseBannerAdapter adapter;
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (adapter == null) {
                return;
            }
            if (adapter.mData.size() == 0) {
                return;
            }
            if (viewPager.getCurrentItem() == 0) {
                viewPager.setCurrentItem(adapter.mData.size(), false);//切换，不要动画效果
            } else if (viewPager.getCurrentItem() == adapter.mData.size() - 1) {
                viewPager.setCurrentItem(1, false);//切换，不要动画效果
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            viewPagerStates = state;
        }
    };
    private long delayTime = 3;

    private TimeUnit delayTimeUnit = TimeUnit.SECONDS;

    /*--------------------------------构造方法--------------------------------*/

    public Banner(@NonNull Context context) {
        this(context, null);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.banner, this);
        initViews();
        initListener();
    }

    /*--------------------------------setter--------------------------------*/

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public void setDelayTimeUnit(TimeUnit delayTimeUnit) {
        this.delayTimeUnit = delayTimeUnit;
    }

    /*--------------------------------自定义公开方法--------------------------------*/

    public void setAdapter(BaseBannerAdapter adapter) {
        this.adapter = adapter;
        adapter.boundToViewPater(viewPager);
    }


    /*--------------------------------自定义私有方法--------------------------------*/

    /**
     * 初始化控件
     */
    private void initViews() {
        viewPager = findViewById(R.id.view_pager);
    }

    private void initListener() {
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    public void start() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
        scheduledExecutorService = BaseManager.newScheduledExecutorService(8);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                BaseManager.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int currentItem = viewPager.getCurrentItem();
                            if (currentItem == 0) {
                                viewPager.setCurrentItem(adapter.mData.size(), false);//切换，不要动画效果
                            } else if (currentItem == adapter.mData.size() - 1) {
                                viewPager.setCurrentItem(1, false);//切换，不要动画效果
                            } else {
                                viewPager.setCurrentItem(currentItem + 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 0, delayTime, delayTimeUnit);


    }
}
