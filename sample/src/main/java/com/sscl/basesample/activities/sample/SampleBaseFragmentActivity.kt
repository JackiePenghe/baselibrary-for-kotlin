package com.sscl.basesample.activities.sample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.sscl.baselibrary.adapter.FragmentViewPagerAdapter
import com.sscl.baselibrary.utils.Tool
import com.sscl.basesample.R
import com.sscl.basesample.fragment.SampleFragment1
import com.sscl.basesample.fragment.SampleFragment2

/**
 * @author alm
 */
class SampleBaseFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.com_sscl_basesample_activity_sample_base_fragment)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.com_jackiepenghe_app_title)
        toolbar.setBackgroundColor(Tool.getColorPrimary(this))
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        val supportActionBar: ActionBar? = supportActionBar
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar.setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener { finish() }
        val sampleFragment1 = SampleFragment1()
        val sampleFragment2 = SampleFragment2()
        val fragments = arrayOf<Fragment>(sampleFragment1, sampleFragment2)
        val titles = arrayOf("fragment1", "fragment2")
        val fragmentViewPagerAdapter =
            FragmentViewPagerAdapter(supportFragmentManager, fragments, titles)
        viewPager.adapter = fragmentViewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    companion object {
        private val TAG = SampleBaseFragmentActivity::class.java.simpleName
    }
}