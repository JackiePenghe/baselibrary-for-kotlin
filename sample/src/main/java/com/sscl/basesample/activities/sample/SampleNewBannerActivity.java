package com.sscl.basesample.activities.sample;

import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.sscl.baselibrary.widget.banner.news.Banner;
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
     * Banner数据集合
     */
    private ArrayList<VideoAndImageBannerData> bannerData = new ArrayList<>();

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_sscl_basesample_activity_sample_new_banner);
        initBannerData();
        banner = findViewById(R.id.banner);
        banner.setDataList(bannerData);
        banner.setEnableSlide(true);
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

    private void initBannerData() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File dir = new File(externalStorageDirectory, "gongcunad");
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
            return;
        }
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            if (!isMp4File(file)) {
                bannerData.add(new VideoAndImageBannerData(BannerType.IMAGE, file));
            } else {
                bannerData.add(new VideoAndImageBannerData(BannerType.VIDEO, file));
            }
        }
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
