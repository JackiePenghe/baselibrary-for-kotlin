package com.sscl.basesample.activities.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.sscl.baselibrary.adapter.FragmentViewPagerAdapter;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.basesample.R;
import com.sscl.basesample.fragment.SampleFragment1;
import com.sscl.basesample.fragment.SampleFragment2;


/**
 * @author alm
 */
public class SampleBaseFragmentActivity extends AppCompatActivity {



    private static final String TAG = SampleBaseFragmentActivity.class.getSimpleName();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Button button;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_base_fragment);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        button = findViewById(R.id.button);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_title);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final SampleFragment1 sampleFragment1 = new SampleFragment1();
        final SampleFragment2 sampleFragment2 = new SampleFragment2();
        Fragment[] fragments = new Fragment[]{sampleFragment1, sampleFragment2};
        String[] titles = new String[]{"fragment1", "fragment2"};
        FragmentViewPagerAdapter fragmentViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(fragmentViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sampleFragment1.isVisibilityForUser()){
                    DebugUtil.warnOut(TAG,"sampleFragment1 可见");
                }else{
                    DebugUtil.warnOut(TAG,"sampleFragment1 不可见");
                }

                if (sampleFragment2.isVisibilityForUser()){
                    DebugUtil.warnOut(TAG,"sampleFragment2 可见");
                }else{
                    DebugUtil.warnOut(TAG,"sampleFragment2 不可见");
                }
            }
        });
    }
}
