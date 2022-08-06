package com.sscl.basesample.activities.sample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
        val viewPager2: ViewPager2 = findViewById(R.id.view_pager2)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.app_name)
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
        val fragmentViewPagerAdapter = object :FragmentStateAdapter(this){

            override fun getItemCount(): Int {
               return fragments.size
            }
            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }

        }
        viewPager2.adapter = fragmentViewPagerAdapter
        initTabLayout(tabLayout, titles, viewPager2)
    }

    private fun initTabLayout(tabLayout: TabLayout, titles: Array<String>, viewPager2: ViewPager2) {
        TabLayoutMediator(tabLayout,viewPager2){tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}