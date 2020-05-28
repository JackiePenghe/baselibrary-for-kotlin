package com.sscl.baselibrary.widget.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.sscl.baselibrary.utils.BaseManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Banner extends FrameLayout {

    /*--------------------------------成员变量--------------------------------*/

    private ViewPager viewPager;

    private ScheduledExecutorService scheduledExecutorService;
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
            mCurrentPosition = position % (adapter.mData.size() + 2);
            lastScrollTime = System.currentTimeMillis();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                int current = viewPager.getCurrentItem();
                int lastReal = adapter.getCount() - 2;
                if (current == 0) {
                    viewPager.setCurrentItem(lastReal, false);
                } else if (current == lastReal + 1) {
                    viewPager.setCurrentItem(1, false);
                }
            }
        }
    };
    private long delayTime = 3;

    private TimeUnit delayTimeUnit = TimeUnit.SECONDS;

    private int mCurrentPosition;

    private long lastScrollTime;

    /*--------------------------------构造方法--------------------------------*/

    public Banner(@NonNull Context context) {
        this(context, null);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        adapter.bindToViewPager(viewPager);
    }


    /*--------------------------------自定义私有方法--------------------------------*/

    /**
     * 初始化控件
     */
    private void initViews() {
        viewPager = new ViewPager(getContext());
        viewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(viewPager);
    }

    private void initListener() {
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    public void start() {
        stop();

        scheduledExecutorService = BaseManager.newScheduledExecutorService(8);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                long diffTime = currentTimeMillis - lastScrollTime;
                long delayTimeMillis = delayTimeUnit.toMillis(delayTime);
                if (diffTime < delayTimeMillis / 2){
                    return;
                }
                BaseManager.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mCurrentPosition++;
                        viewPager.setCurrentItem(mCurrentPosition);
                    }
                });
            }
        }, delayTime, delayTime, delayTimeUnit);
    }

    public void stop(){
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
    }
}
