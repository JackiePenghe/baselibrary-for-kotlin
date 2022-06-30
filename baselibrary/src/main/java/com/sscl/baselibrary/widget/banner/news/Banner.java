package com.sscl.baselibrary.widget.banner.news;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
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
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 用户自定义数据处理接口
     */
    public interface OnCustomDataHandleListener {

        /**
         * 获取用户自定义数据的View（需要重复播放时的View，仅当只有一个数据时回调此方法）
         *
         * @param context    上下文
         * @param bannerData 用户自定义数据
         * @return 用户自定义数据的View
         */
        View getAutoPlayRepeatItemView(Context context, @SuppressWarnings("rawtypes") BannerData bannerData);

        /**
         * 获取用户自定义数据的View
         *
         * @param context    上下文
         * @param bannerData 用户自定义数据
         * @param position   当前数据的位置
         * @return 用户自定义数据的View
         */
        View getItemView(Context context, @SuppressWarnings("rawtypes") BannerData bannerData, int position);

        /**
         * 让用户设置自定义数据的VideoView数据(设置完数据的VideoView会在显示时自动播放)
         *
         * @param context    上下文
         * @param videoView  视频播放控件
         * @param bannerData 用户自定义数据
         */
        void setVideoViewData(Context context, VideoView videoView, @SuppressWarnings("rawtypes") BannerData bannerData);

        /**
         * 让用户设置自定义数据的ImageView数据
         *
         * @param context    上下文
         * @param imageView  图片播放控件
         * @param bannerData 用户自定义数据
         */
        void setImageViewData(Context context, ImageView imageView, @SuppressWarnings("rawtypes") BannerData bannerData);

        /**
         * 让用户设置自定义数据滚动延时
         * @param view     当前显示的View
         * @param bannerData  用户自定义数据
         * @param position 当前数据的位置
         * @param defaultScrollTime 默认的滚动延时
         * @return 返回滚动延时
         */
        long onGetDelayTime(View view, @SuppressWarnings("rawtypes") BannerData bannerData, int position, long defaultScrollTime);
    }

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
    /**
     * Banner是否已经开启
     */
    private boolean isStart;
    /**
     * 用户自定义数据处理接口
     */
    private OnCustomDataHandleListener onCustomDataHandleListener;

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
    @SuppressWarnings("rawtypes")
    private final ArrayList<BannerData> bannerDataList = new ArrayList<>();
    /**
     * ViewPager滑动监听
     */
    @SuppressWarnings("FieldCanBeLocal")
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
     * 设置自定义数据处理接口
     *
     * @param onCustomDataHandleListener 自定义数据处理接口
     */
    public final void setOnCustomDataHandleListener(OnCustomDataHandleListener onCustomDataHandleListener) {
        this.onCustomDataHandleListener = onCustomDataHandleListener;
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

    public void startBanner() {
        if (isStart) {
            return;
        }
        isStart = true;
        viewPager.setAdapter(adapter);
    }

    public boolean isStart() {
        return isStart;
    }

    /**
     * 开启自动轮播
     */
    public void startAutoScroll() {
        enableAutoScroll = true;
        viewPager.setCurrentItem(currentPosition);
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
                delayedTime = Long.MAX_VALUE;
                videoView.setOnCompletionListener(mp -> viewPager.setCurrentItem(currentPosition + 1, true));
                videoView.setOnErrorListener((mp, what, extra) -> {
                    DebugUtil.errorOut(TAG, "VideoView error: " + what + " " + extra);
                    viewPager.setCurrentItem(currentPosition + 1, true);
                    return true;
                });
                break;
            case CUSTOM:
                if (onCustomDataHandleListener != null) {
                    delayedTime = onCustomDataHandleListener.onGetDelayTime(adapter.getViews().get(position),bannerData,position,autoScrollTime);
                } else {
                    throw new RuntimeException("请先设置onCustomDataHandleListener接口");
                }
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
                    adapter.getViews().add(videoView);
                    break;
                case CUSTOM:
                    if (onCustomDataHandleListener != null) {
                        View itemView = onCustomDataHandleListener.getItemView(getContext(), bannerData, i);
                        adapter.getViews().add(itemView);
                    } else {
                        throw new RuntimeException("请设置OnCustomDataHandleListener接口");
                    }
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
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
                //监听视频播放完的代码
                videoView.setOnCompletionListener(mPlayer -> {
                    mPlayer.start();
                    mPlayer.setLooping(true);
                });
                videoView.setOnErrorListener((mp, what, extra) -> {
                    DebugUtil.warnOut(TAG, "视频播放错误");
                    return true;
                });
                adapter.getViews().add(videoView);
                break;
            case CUSTOM:
                if (onCustomDataHandleListener != null) {
                    View view = onCustomDataHandleListener.getAutoPlayRepeatItemView(getContext(), bannerData);
                    adapter.getViews().add(view);
                } else {
                    throw new RuntimeException("请设置OnCustomDataHandleListener接口");
                }
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
        OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            if (hasFocus) {
                videoView.start();
                videoView.seekTo(0);
            } else {
                videoView.pause();
            }
        };
        switch (bannerDataType) {
            case FILE: {
                File fileData = bannerData.getFileData();
                if (fileData == null) {
                    DebugUtil.warnOut(TAG, "视频文件为空");
                    return;
                }
                videoView.setVideoURI(FileProviderUtil.getUriFromFile(getContext(), fileData));
                videoView.setOnFocusChangeListener(onFocusChangeListener);
            }
            break;
            case URI: {
                Uri uriData = bannerData.getUriData();
                if (uriData == null) {
                    DebugUtil.warnOut(TAG, "视频文件为空");
                    return;
                }
                videoView.setVideoURI(uriData);
                videoView.setOnFocusChangeListener(onFocusChangeListener);
            }
            break;
            case CUSTOM:
                if (onCustomDataHandleListener != null) {
                    onCustomDataHandleListener.setVideoViewData(getContext(), videoView, bannerData);
                    videoView.setOnFocusChangeListener(onFocusChangeListener);
                } else {
                    throw new RuntimeException("BannerDataType为CUSTOM时，请设置OnCustomDataHandleListener接口");
                }
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
        BannerDataType bannerDataType = bannerData.getBannerDataType();
        switch (bannerDataType) {
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
            case CUSTOM:
                if (onCustomDataHandleListener != null) {
                    onCustomDataHandleListener.setImageViewData(getContext(), imageView, bannerData);
                } else {
                    throw new RuntimeException("BannerDataType为CUSTOM时，请设置OnCustomDataHandleListener接口");
                }
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
