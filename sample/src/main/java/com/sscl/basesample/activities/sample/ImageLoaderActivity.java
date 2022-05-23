package com.sscl.basesample.activities.sample;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.image.ImageLoader;
import com.sscl.basesample.R;

/**
 * @author jackie
 */
public class ImageLoaderActivity extends BaseAppCompatActivity {

    private static final String URL = "https://ywmdata.mohoo.club/upload/wxcode_ywm863412042671753.png";

    private ImageView imageView;

    @Override
    protected boolean titleBackClicked() {
        return false;
    }

    @Override
    protected void doBeforeSetLayout() {

    }

    @Override
    protected int setLayout() {
        return R.layout.com_sscl_basesample_activity_image_loader;
    }

    @Override
    protected void doBeforeInitOthers() {

    }

    @Override
    protected void initViews() {
        imageView = findViewById(R.id.image);
    }

    @Override
    protected void initViewData() {
        ImageLoader.getInstance(getApplicationContext()).displayImage(URL, imageView, false);
    }

    @Override
    protected void initOtherData() {

    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void doAfterAll() {

    }

    @Override
    protected boolean createOptionsMenu(@NonNull Menu menu) {
        return false;
    }

    @Override
    protected boolean optionsItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
