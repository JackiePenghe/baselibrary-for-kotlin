package com.sscl.baselibrary.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.utils.BaseManager;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Banner extends RelativeLayout {

    /**
     * 点指示器
     */
    public static final int INDICATOR_SPOT = 1;

    /*--------------------------------公开静态常量--------------------------------*/
    /**
     * 数字指示器
     */
    public static final int INDICATOR_NUMBER = 2;
    /**
     * ViewPager
     */
    private ViewPager viewPager;



    /*--------------------------------成员变量--------------------------------*/
    /**
     * 执行定时任务的定时器
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * Banner适配器
     */
    @SuppressWarnings("rawtypes")
    private BaseBannerAdapter adapter;
    /**
     * 自动轮播到下一个内容的延时
     */
    private long delayTime = 3;
    /**
     * 自动轮播延时的单位
     */
    private TimeUnit delayTimeUnit = TimeUnit.SECONDS;
    /**
     * 当前轮播展示的位置
     */
    private int mCurrentPosition;
    /**
     * 保存上次轮播的时间，用于暂停一次轮播。（如果500毫秒内轮播过，即使下一次轮播时间到了，也不执行内容切换）
     */
    private long lastScrollTime;
    private final int mIndicatorPaddingL = 0;
    private final int mIndicatorPaddingT = 0;
    private final int mIndicatorPaddingR = 0;
    private final int mIndicatorPaddingB = 0;
    /**
     * 指示器点资源
     */
    private final int mPointDrawableResId = R.drawable.com_jackiepenghe_baselibrary_selector_banner_point;
    //是否可以自动播放
    private final boolean mAutoPlayAble = true;
    //是否正在播放
    private final boolean mIsAutoPlaying = false;
    //自动播放时间
    private final int mAutoPalyTime = 5000;
    //设置指示器容器
    private LinearLayout mPointRealContainerLl;
    /**
     * ViewPager状态切换的监听
     */
    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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
            switchToPoint(toRealPosition(position));
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
    private LayoutParams mPointRealContainerLp;
    /**
     * 指示点位置
     */
    private int mPointPosition = POSITION.CENTER_HORIZONTAL.value;
    /**
     *
     */
    private int mPointLayoutPosition = POSITION.PARENT_BOTTOM.value;
    /**
     * 指示点是否可见
     */
    private boolean mPointsIsVisible = true;
    private Drawable mPointContainerBackgroundDrawable;

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(attrs);
    }

    /*--------------------------------构造方法--------------------------------*/

    public Banner(@NonNull Context context) {
        this(context, null);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (adapter == null) {
            return super.dispatchTouchEvent(ev);
        }
        if (mAutoPlayAble && !adapter.mData.isEmpty()) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stop();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    start();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /*--------------------------------重写父类方法--------------------------------*/

    public void setAdapter(@SuppressWarnings("rawtypes") BaseBannerAdapter adapter) {
        this.adapter = adapter;
        initViews();
        initListener();
        adapter.bindToViewPager(viewPager);
        if (adapter.mData.size() > 1) {
            addPoints();
        }
    }

    /*--------------------------------setter--------------------------------*/

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public void setDelayTimeUnit(TimeUnit delayTimeUnit) {
        this.delayTimeUnit = delayTimeUnit;
    }

    /*--------------------------------自定义公开方法--------------------------------*/

    public void start() {
        stop();

        scheduledExecutorService = BaseManager.newScheduledExecutorService(8);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            long diffTime = currentTimeMillis - lastScrollTime;
            long delayTimeMillis = delayTimeUnit.toMillis(delayTime);
            if (diffTime < delayTimeMillis / 2) {
                return;
            }
            BaseManager.getHandler().post(() -> {
                mCurrentPosition++;
                viewPager.setCurrentItem(mCurrentPosition);
            });
        }, delayTime, delayTime, delayTimeUnit);
    }

    public void stop() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
    }

    /**
     * 设置指示点是否可见
     *
     * @param isVisible 是否可见
     */
    public void setPointsIsVisible(boolean isVisible) {
        if (mPointRealContainerLl != null) {
            if (isVisible) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置指示器位置
     *
     * @param position 位置
     */
    public void setPointPosition(POSITION position) {
        //设置指示器布局位置
        mPointPosition = position.value;
    }

    /**
     * 设置扬声器布局位置
     *
     * @param position 位置
     */
    public void setPointLayoutPosition(POSITION position) {
        //设置指示器布局位置
        mPointLayoutPosition = position.value;
    }

    public void setData(@SuppressWarnings("rawtypes") ArrayList data) {
        adapter.mData = data;
        if (data.size() > 0) {
            if (data.size() > 1) {
                viewPager.setCurrentItem(1, false);
                addPoints();
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        //关闭view的OverScroll
        setOverScrollMode(OVER_SCROLL_NEVER);
        //设置指示器背景
        if (mPointContainerBackgroundDrawable == null) {
            mPointContainerBackgroundDrawable = new ColorDrawable(Color.parseColor("#00aaaaaa"));
        }
        //设置指示器背景容器
        RelativeLayout pointContainerRl = new RelativeLayout(getContext());
        pointContainerRl.setBackground(mPointContainerBackgroundDrawable);
        //设置内边距
        pointContainerRl.setPadding(mIndicatorPaddingL, mIndicatorPaddingT, mIndicatorPaddingR, mIndicatorPaddingB);
        //设定指示器容器布局及位置
        LayoutParams pointContainerLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(pointContainerRl, pointContainerLp);
        //设置指示器容器
        mPointRealContainerLl = new LinearLayout(getContext());
        mPointRealContainerLl.setOrientation(LinearLayout.HORIZONTAL);
        mPointRealContainerLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mPointRealContainerLp.addRule(mPointLayoutPosition);
        viewPager = new ViewPager(getContext());
        viewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(viewPager);
        pointContainerRl.addView(mPointRealContainerLl, mPointRealContainerLp);
        //设置指示器容器是否可见
        if (mPointRealContainerLl != null) {
            if (mPointsIsVisible) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }
        //设置指示器布局位置
        mPointRealContainerLp.addRule(mPointPosition);
    }

    /*--------------------------------自定义私有方法--------------------------------*/

    /**
     * 添加指示点
     */
    private void addPoints() {
        mPointRealContainerLl.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);
        ImageView imageView;
        int length = adapter.mData.size();
        for (int i = 0; i < length; i++) {
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(lp);
            imageView.setImageResource(mPointDrawableResId);
            mPointRealContainerLl.addView(imageView);
        }

        switchToPoint(0);
    }

    /**
     * 切换指示器
     *
     * @param targetPosition 目标位置
     */
    private void switchToPoint(final int targetPosition) {
        int childCount = mPointRealContainerLl.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mPointRealContainerLl.getChildAt(i).setEnabled(false);
        }
        if (childCount > 0) {
            mPointRealContainerLl.getChildAt(targetPosition).setEnabled(true);
        }

    }

    /**
     * 解析自定义属性
     *
     * @param attrs 自定义属性
     */
    private void parseAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Banner);

        mPointsIsVisible = a.getBoolean(R.styleable.Banner_points_visibility, true);
        mPointPosition = a.getInt(R.styleable.Banner_points_position, POSITION.CENTER_HORIZONTAL.value);
        mPointContainerBackgroundDrawable
                = a.getDrawable(R.styleable.Banner_points_container_background);
        //设置指示器背景
        if (mPointContainerBackgroundDrawable == null) {
            mPointContainerBackgroundDrawable = a.getDrawable(R.styleable.Banner_points_container_background);
        }
        a.recycle();
    }

    /**
     * 初始化事件监听
     */
    private void initListener() {
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    /**
     * 返回真实的位置
     *
     * @param position 当前位置
     * @return  真实的位置
     */
    private int toRealPosition(int position) {
        int realPosition;
        realPosition = (position - 1) % adapter.mData.size();
        if (realPosition < 0) {
            realPosition += adapter.mData.size();
        }
        return realPosition;
    }

    public enum POSITION {
        PARENT_TOP(RelativeLayout.ALIGN_PARENT_TOP),
        PARENT_START(RelativeLayout.ALIGN_PARENT_START),
        PARENT_LEFT(RelativeLayout.ALIGN_PARENT_LEFT),
        PARENT_END(RelativeLayout.ALIGN_PARENT_END),
        PARENT_RIGHT(RelativeLayout.ALIGN_PARENT_RIGHT),
        PARENT_BOTTOM(RelativeLayout.ALIGN_PARENT_BOTTOM),
        PARENT_CENTER(RelativeLayout.CENTER_IN_PARENT),
        CENTER_VERTICAL(RelativeLayout.CENTER_VERTICAL),
        CENTER_HORIZONTAL(RelativeLayout.CENTER_HORIZONTAL),
        START(RelativeLayout.ALIGN_START),
        LEFT(RelativeLayout.ALIGN_LEFT),
        TOP(RelativeLayout.ALIGN_TOP),
        END(RelativeLayout.ALIGN_END),
        RIGHT(RelativeLayout.ALIGN_RIGHT),
        BOTTOM(RelativeLayout.ALIGN_BOTTOM);
        private final int value;

        POSITION(int value) {
            this.value = value;
        }
    }
}
