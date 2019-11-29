package com.sscl.basesample.activities.sample;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sscl.baselibrary.adapter.FragmentViewPager2Adapter;
import com.sscl.basesample.R;
import com.sscl.basesample.fragment.SampleFragment1;
import com.sscl.basesample.fragment.SampleFragment2;

import java.util.ArrayList;

/**
 * @author pengh
 */
public class SampleFragmentPager2AdapterActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    private ViewPager2 viewPager2;

    private FragmentViewPager2Adapter fragmentViewPager2Adapter;

    private SampleFragment1 sampleFragment1 = new SampleFragment1();
    private SampleFragment2 sampleFragment2 = new SampleFragment2();
    /**
     * TabLayout关联Fragment标题
     */
    private ArrayList<String> titleString = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_fragment_pager2_adapter);
        fragments.add(sampleFragment1);
        fragments.add(sampleFragment2);
        titleString.add("fragment1");
        titleString.add("fragment2");
        fragmentViewPager2Adapter = new FragmentViewPager2Adapter(this, fragments,titleString);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager_2);
        viewPager2.setAdapter(fragmentViewPager2Adapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titleString.get(position));
            }
        });
        tabLayoutMediator.attach();
//        fragmentViewPager2Adapter.attachTabLayoutToViewPager2(tabLayout,viewPager2);
    }
}
