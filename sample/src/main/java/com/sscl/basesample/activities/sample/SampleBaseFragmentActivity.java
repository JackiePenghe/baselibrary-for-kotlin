package com.sscl.basesample.activities.sample;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sscl.baselibrary.adapter.FragmentViewPagerAdapter;
import com.sscl.baselibrary.utils.Tool;
import com.sscl.basesample.R;
import com.sscl.basesample.fragment.SampleFragment1;
import com.sscl.basesample.fragment.SampleFragment2;


/**
 * @author alm
 */
public class SampleBaseFragmentActivity extends AppCompatActivity {

    private static final String TAG = SampleBaseFragmentActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_base_fragment);

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_title);
        toolbar.setBackgroundColor(Tool.getColorPrimary(this));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        final SampleFragment1 sampleFragment1 = new SampleFragment1();
        final SampleFragment2 sampleFragment2 = new SampleFragment2();
        Fragment[] fragments = new Fragment[]{sampleFragment1, sampleFragment2};
        String[] titles = new String[]{"fragment1", "fragment2"};
        FragmentViewPagerAdapter fragmentViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(fragmentViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
