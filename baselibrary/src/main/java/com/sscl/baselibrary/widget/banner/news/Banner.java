package com.sscl.baselibrary.widget.banner.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sscl.baselibrary.files.FileProviderUtil;
import com.sscl.baselibrary.utils.BaseManager;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.widget.banner.news.enums.BannerDataType;
import com.sscl.baselibrary.widget.banner.news.enums.BannerType;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author pengh
 */
public class Banner extends FrameLayout {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有静态常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static final String TAG = Banner.class.getSimpleName();

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 是否开启自动轮播，默认为false
     */
    private boolean enableAutoScroll;
    /**
     * 自动轮播的时间间隔（非视频类型的时候应用此时间）
     */
    private long autoScrollTime = 3000;
    /**
     * 实际应用的自动轮播的时间间隔（非视频类自动应用{@link Banner#autoScrollTime},视频类则使用视频文件的播放时长，自定义类型则需要用户手动处理并返回）
     */
    private long delayedTime;
    /**
     * 记录上一次选择的位置
     */
    private int lastPosition = -1;
    /**
     * 当前显示位置
     */
    private int currentPosition;
    /**
     * 自动轮播的定时器
     */
    private ScheduledExecutorService autoScrollTimer;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Banner ViewPager
     */
    private final BannerViewPager viewPager;
    /**
     * Banner适配器
     */
    private final BannerAdapter adapter = new BannerAdapter();
    /**
     * banner数据缓存
     */
    private final ArrayList<BannerData> bannerDataList = new ArrayList<>();
    /**
     * ViewPager滑动监听
     */
    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            lastPosition = currentPosition;
            currentPosition = position;
            calculateCurrentDelayTime(position);
            if (enableAutoScroll) {
                startAutoScrollTimer();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (enableAutoScroll && state != ViewPager.SCROLL_STATE_IDLE) {
                stopAutoScrollTimer();
            }
            //ViewPager跳转
            int pageIndex = currentPosition;
            if (currentPosition == 0) {
                pageIndex = adapter.getViews().size() - 2;
            } else if (currentPosition == adapter.getViews().size() - 1) {
                pageIndex = 1;
            }
            if (pageIndex != currentPosition) {
                //无滑动动画，直接跳转
                viewPager.setCurrentItem(pageIndex, false);
            }
        }
    };

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        viewPager = new BannerViewPager(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(params);
        viewPager.setAdapter(adapter);
        addView(viewPager);
        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 获取是否允许滑动
     *
     * @return 是否允许滑动
     */
    public final boolean isEnableSlide() {
        return viewPager.isEnableSlide();
    }

    /**
     * 设置是否允许滑动
     *
     * @param enableSlide 是否允许滑动
     */
    public final void setEnableSlide(boolean enableSlide) {
        viewPager.setEnableSlide(enableSlide);
    }

    /**
     * 设置自动轮播的时间间隔（非视频类型的时候应用此时间）
     *
     * @param autoScrollTime 自动轮播的时间间隔（非视频类型的时候应用此时间）
     */
    public void setAutoScrollTime(long autoScrollTime) {
        this.autoScrollTime = autoScrollTime;
    }

    /**
     * @param dataList 轮播数组
     * @param <K>      泛型
     * @param <T>      泛型
     */
    public <T, K extends BannerData<T>> void setDataList(ArrayList<K> dataList) {
        setDataList(dataList, -1);
    }

    /**
     * @param dataList 轮播数组
     * @param index    索引
     * @param <T>      泛型
     * @param <K>      泛型
     */
    public <T, K extends BannerData<T>> void setDataList(@NonNull ArrayList<K> dataList, int index) {
        bannerDataList.clear();
        if (index >= 0) {
            lastPosition = -1;
            currentPosition = index;
        }
        adapter.clear();
        if (dataList.size() > 1) {
            handleDataListSizeBiggerThan1(dataList);
        } else if (dataList.size() == 1) {
            handleDataListSizeEqualTo1(dataList);
        } else {
            handleDataListSizeEqualTo0();
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 开启自动轮播
     */
    public void startAutoScroll() {
        enableAutoScroll = true;
        if (bannerDataList.size() > 1) {
            calculateCurrentDelayTime(currentPosition);
            startAutoScrollTimer();
        }
    }

    /**
     * 停止自动轮播
     */
    public void stopAutoScroll() {
        enableAutoScroll = false;
        stopAutoScrollTimer();
    }

    /**
     * 销毁
     */
    public void destroy() {
        stopAutoScroll();
        viewPager.setAdapter(null);
        bannerDataList.clear();
        adapter.clear();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 获取delyedTime
     *
     * @param position 当前位置
     */
    private void calculateCurrentDelayTime(int position) {
        if (lastPosition >= 0 && !adapter.getViews().isEmpty() && adapter.getViews().size() > lastPosition) {
            //noinspection rawtypes
            BannerData bannerData = bannerDataList.get(lastPosition);
            if (bannerData.getBannerType() == BannerType.VIDEO) {
                VideoView videoView = (VideoView) adapter.getViews().get(lastPosition);
                videoView.pause();
            }
        }
        //noinspection rawtypes
        BannerData bannerData = bannerDataList.get(position);
        BannerType bannerType = bannerData.getBannerType();
        switch (bannerType) {
            case IMAGE:
                delayedTime = autoScrollTime;
                break;
            case VIDEO:
                View view = adapter.getViews().get(position);
                VideoView videoView = (VideoView) view;
                videoView.start();
                videoView.seekTo(0);
                int duration = videoView.getDuration();
                DebugUtil.warnOut(TAG,"position video view duration:" + duration);
                DebugUtil.warnOut(TAG,"position video view file :" + bannerDataList.get(position).getFileData());
                if (duration < 0) {
                    delayedTime = autoScrollTime;
                } else {
                    delayedTime = duration;
                }
                break;
            case CUSTOM:
                //TODO 处理自定义数据
                delayedTime = autoScrollTime;
                break;
            default:
                DebugUtil.warnOut(TAG, "未处理的Banner类型");
                delayedTime = autoScrollTime;
                break;
        }
    }

    /**
     * 处理数据
     *
     * @param dataList 数据列表
     * @param <T>      泛型
     * @param <K>      泛型
     */
    private <T, K extends BannerData<T>> void handleDataListSizeBiggerThan1(@NonNull ArrayList<K> dataList) {
        lastPosition = -1;
        currentPosition = 1;
        for (int i = 0; i < dataList.size() + 2; i++) {
            if (i == 0) {
                bannerDataList.add(dataList.get(dataList.size() - 1));
            } else if (i == dataList.size() + 1) {
                bannerDataList.add(dataList.get(0));
            } else {
                bannerDataList.add(dataList.get(i - 1));
            }
        }
        for (int i = 0; i < bannerDataList.size(); i++) {
            //noinspection rawtypes
            BannerData bannerData = bannerDataList.get(i);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            BannerType bannerType = bannerData.getBannerType();
            switch (bannerType) {
                case IMAGE:
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    handleImageBannerData(imageView, bannerData);
                    adapter.getViews().add(imageView);
                    break;
                case VIDEO:
                    VideoView videoView = new VideoView(getContext());
                    videoView.setLayoutParams(layoutParams);
                    videoView.setOnErrorListener((mp, what, extra) -> true);
                    handleVideoBannerData(videoView, bannerData);
                    videoView.start();
                    adapter.getViews().add(videoView);
                    break;
                case CUSTOM:
                    //TODO 处理自定义数据
                    break;
                default:
                    DebugUtil.warnOut(TAG, "未处理的Banner类型");
                    break;
            }
        }
    }

    /**
     * 处理Banner数据
     *
     * @param dataList 数据列表
     * @param <K>      泛型
     */
    private <T, K extends BannerData<T>> void handleDataListSizeEqualTo1(ArrayList<K> dataList) {
        lastPosition = -1;
        currentPosition = 0;
        bannerDataList.addAll(dataList);
        //noinspection rawtypes
        BannerData bannerData = bannerDataList.get(0);
        BannerType bannerType = bannerData.getBannerType();
        switch (bannerType) {
            case IMAGE:
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                handleImageBannerData(imageView, bannerData);
                adapter.getViews().add(imageView);
                break;
            case VIDEO:
                VideoView videoView = new VideoView(getContext());
                videoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                videoView.setOnErrorListener((mp, what, extra) -> true);
                handleVideoBannerData(videoView, bannerData);
                //监听视频播放完的代码
                videoView.setOnCompletionListener(mPlayer -> {
                    mPlayer.start();
                    mPlayer.setLooping(true);
                });
                videoView.start();
                adapter.getViews().add(videoView);
                break;
            case CUSTOM:
                //TODO 处理自定义数据
                break;
            default:
                DebugUtil.warnOut(TAG, "未处理的Banner类型");
                break;
        }
    }

    /**
     * 处理Banner数据
     */
    private void handleDataListSizeEqualTo0() {
        //添加一个默认的View
        adapter.getViews().add(new LinearLayout(getContext()));
    }

    /**
     * 处理视频数据
     *
     * @param videoView  视频视图
     * @param bannerData Banner数据
     */
    private void handleVideoBannerData(VideoView videoView, @SuppressWarnings("rawtypes") BannerData bannerData) {
        BannerDataType bannerDataType = bannerData.getBannerDataType();
        switch (bannerDataType) {
            case FILE: {
                File fileData = bannerData.getFileData();
                if (fileData == null) {
                    DebugUtil.warnOut(TAG, "视频文件为空");
                    return;
                }
                videoView.setVideoURI(FileProviderUtil.getUriFromFile(getContext(),fileData));
            }
            break;
            case URI: {
                Uri uriData = bannerData.getUriData();
                if (uriData == null) {
                    DebugUtil.warnOut(TAG, "视频文件为空");
                    return;
                }
                videoView.setVideoURI(uriData);
            }
            break;
            default:
                DebugUtil.warnOut(TAG, "未处理的Banner数据类型");
                break;
        }
    }

    /**
     * 处理图片数据
     *
     * @param imageView  图片视图
     * @param bannerData Banner数据
     */
    private void handleImageBannerData(ImageView imageView, @SuppressWarnings("rawtypes") BannerData bannerData) {
        switch (bannerData.getBannerDataType()) {
            case FILE:
                File fileData = bannerData.getFileData();
                if (fileData == null) {
                    DebugUtil.warnOut(TAG, "图片文件为空");
                    return;
                }
                imageView.setImageBitmap(BitmapFactory.decodeFile(fileData.getAbsolutePath()));
                break;
            case URI:
                Uri uriData = bannerData.getUriData();
                if (uriData == null) {
                    DebugUtil.warnOut(TAG, "图片文件为空");
                    return;
                }
                imageView.setImageURI(uriData);
                break;
            default:
                DebugUtil.warnOut(TAG, "未处理的Banner数据类型");
                break;
        }
    }

    /**
     * 开启自动轮播定时器
     */
    private void startAutoScrollTimer() {
        stopAutoScrollTimer();
        autoScrollTimer = BaseManager.newScheduledExecutorService(1);
        autoScrollTimer.schedule(() -> {
            BaseManager.getHandler().post(() -> viewPager.setCurrentItem(currentPosition + 1, true));
        }, delayedTime, TimeUnit.MILLISECONDS);
        DebugUtil.warnOut(TAG, "开启自动轮播定时器 delayedTime = " + delayedTime);
    }

    /**
     * 停止自动轮播定时器
     */
    private void stopAutoScrollTimer() {
        if (autoScrollTimer != null) {
            autoScrollTimer.shutdownNow();
        }
        autoScrollTimer = null;
        DebugUtil.warnOut(TAG, "停止自动轮播定时器");
    }
}
