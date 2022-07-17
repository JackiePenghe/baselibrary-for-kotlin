package com.sscl.basesample.activities.sample

import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.image.ImageLoader
import com.sscl.basesample.R

/**
 * @author jackie
 */
class ImageLoaderActivity : BaseAppCompatActivity() {
    private lateinit var imageView: ImageView
    override fun titleBackClicked(): Boolean {
        return false
    }

    override fun doBeforeSetLayout() {}
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_image_loader
    }

    override fun doBeforeInitOthers() {}
    override fun initViews() {
        imageView = findViewById(R.id.image)
    }

    override fun initViewData() {
        ImageLoader.getInstance(applicationContext).displayImage(URL, imageView, false)
    }

    override fun initOtherData() {}
    override fun initEvents() {}
    override fun doAfterAll() {}
    override fun createOptionsMenu(menu: Menu): Boolean {
        return false
    }

    override fun optionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    companion object {
        private const val URL = "http://img.sccnn.com/bimg/342/16609.jpg"
    }
}