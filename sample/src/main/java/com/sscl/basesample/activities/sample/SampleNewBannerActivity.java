package com.sscl.basesample.activities.sample;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sscl.baselibrary.widget.banner.news.Banner;
import com.sscl.baselibrary.widget.banner.news.BannerData;
import com.sscl.baselibrary.widget.banner.news.enums.BannerDataType;
import com.sscl.baselibrary.widget.banner.news.enums.BannerType;
import com.sscl.basesample.R;
import com.sscl.basesample.beans.VideoAndImageBannerData;

import java.io.File;
import java.util.ArrayList;

/**
 * 新Banner的使用
 *
 * @author pengh
 */
public class SampleNewBannerActivity extends AppCompatActivity {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Banner
     */
    private Banner banner;
    /**
     * 自定义的Banner数据处理回调接口
     */
    private Banner.OnCustomDataHandleListener onCustomDataHandleListener = new Banner.OnCustomDataHandleListener() {

        /**
         * 获取用户自定义数据的View（需要重复播放时的View，仅当只有一个数据时回调此方法）
         *
         *
         * @param context 上下文
         * @param bannerData 用户自定义数据
         * @return 用户自定义数据的View
         */
        @Override
        public View getAutoPlayRepeatItemView(Context context, @SuppressWarnings("rawtypes") BannerData bannerData) {
            String customData = (String) bannerData.getCustomData();
            TextView textView = new TextView(context);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(40);
            textView.setText(customData);
            return textView;
        }

        /**
         * 获取用户自定义数据的View
         *
         * @param context    上下文
         * @param bannerData 用户自定义数据
         * @param position   当前数据的位置
         * @return 用户自定义数据的View
         */
        @Override
        public View getItemView(Context context, @SuppressWarnings("rawtypes") BannerData bannerData, int position) {
            String customData = (String) bannerData.getCustomData();
            TextView textView = new TextView(context);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(40);
            textView.setText(customData);
            return textView;
        }

        /**
         * 让用户设置自定义数据的VideoView数据(设置完数据的VideoView会在显示时自动播放)
         *
         * @param context    上下文
         * @param videoView  视频播放控件
         * @param bannerData 用户自定义数据
         */
        @Override
        public void setVideoViewData(Context context, VideoView videoView, @SuppressWarnings("rawtypes") BannerData bannerData) {

        }

        /**
         * 让用户设置自定义数据的ImageView数据
         *
         * @param context    上下文
         * @param imageView  图片播放控件
         * @param bannerData 用户自定义数据
         */
        @Override
        public void setImageViewData(Context context, ImageView imageView, @SuppressWarnings("rawtypes") BannerData bannerData) {

        }

        /**
         * 让用户设置自定义数据滚动延时
         *
         * @param view              当前显示的View
         * @param bannerData        用户自定义数据
         * @param position          当前数据的位置
         * @param defaultScrollTime 默认的滚动延时
         * @return 返回滚动延时
         */
        @Override
        public long onGetDelayTime(View view, @SuppressWarnings("rawtypes") BannerData bannerData, int position, long defaultScrollTime) {
            return defaultScrollTime;
        }
    };

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_sscl_basesample_activity_sample_new_banner);
        banner = findViewById(R.id.banner);
        //默认获取SD卡的根目录下”advertiseDir“文件夹下的图片与视频，没有则使用getDefaultBannerData()获取默认数据
        ArrayList<VideoAndImageBannerData> videoAndImageBannerData = initBannerData();
        if (videoAndImageBannerData != null) {
            banner.setDataList(videoAndImageBannerData);
        } else {
            banner.setOnCustomDataHandleListener(onCustomDataHandleListener);
            banner.setDataList(getDefaultBannerData());
        }
        banner.setEnableSlide(true);
        banner.setImageScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    private ArrayList<BannerData<String>> getDefaultBannerData() {
        ArrayList<BannerData<String>> bannerData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            bannerData.add(new BannerData<String>() {
                @NonNull
                @Override
                public BannerType getBannerType() {
                    return BannerType.CUSTOM;
                }

                @NonNull
                @Override
                public BannerDataType getBannerDataType() {
                    return BannerDataType.CUSTOM;
                }

                @Nullable
                @Override
                public File getFileData() {
                    return null;
                }

                @Nullable
                @Override
                public String getUrlData() {
                    return null;
                }

                @Nullable
                @Override
                public Uri getUriData() {
                    return null;
                }

                @Nullable
                @Override
                public String getCustomData() {
                    return "自定义Banner数据" + finalI;
                }
            });
        }
        return bannerData;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (!banner.isStart()) {
            banner.startBanner();
        }
        banner.startAutoScroll();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        banner.stopAutoScroll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        banner.destroy();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private ArrayList<VideoAndImageBannerData> initBannerData() {
        ArrayList<VideoAndImageBannerData> videoAndImageBannerData = new ArrayList<>();
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File dir = new File(externalStorageDirectory, "advertiseDir");
        if (dir.exists()) {
            if (dir.isFile()) {
                //noinspection ResultOfMethodCallIgnored
                dir.delete();
            }
        } else {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            if (!isMp4File(file)) {
                videoAndImageBannerData.add(new VideoAndImageBannerData(BannerType.IMAGE, file));
            } else {
                videoAndImageBannerData.add(new VideoAndImageBannerData(BannerType.VIDEO, file));
            }
        }
        return videoAndImageBannerData;
    }

    /**
     * 判断是否为MP4文件
     *
     * @param file 文件
     * @return 是否为MP4文件
     */
    private boolean isMp4File(File file) {
        return file.getName().endsWith("mp4");
    }
}
