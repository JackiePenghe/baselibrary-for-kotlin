package com.sscl.baselibrary.adapter;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sscl.baselibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager适配器
 *
 * @author pengh
 */
public class FragmentViewPager2Adapter extends BaseQuickAdapter<Fragment, BaseViewHolder> {

    private FragmentManager supportFragmentManager;

    private ArrayList<String> titles = new ArrayList<>();

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization fragments.
     *
     * @param fragments A new list is created out of this one to avoid mutable list
     */
    public FragmentViewPager2Adapter( @NonNull FragmentActivity fragmentActivity,@NonNull ArrayList<Fragment> fragments,@NonNull ArrayList<String> titles) {
        super(R.layout.adapter_fragment_view_pager_2, fragments);
        if (titles.size() < fragments.size()){
            throw new IllegalArgumentException("titleString size must be more than the fragments size");
        }
        this.titles.addAll(titles);
        supportFragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(@NonNull BaseViewHolder helper, Fragment item) {
        int layoutPosition = helper.getLayoutPosition();
        View itemView = helper.itemView;
        Object tag = itemView.getTag();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (tag == null) {
            itemView.setTag(layoutPosition);
            if (fragmentTransaction.isEmpty()) {
                fragmentTransaction
                        .add(R.id.frame_layout, item)
                        .show(item)
                        .commit();
            } else {
                fragmentTransaction
                        .replace(R.id.frame_layout, item)
                        .commit();
            }
            return;
        }

        if ((int) tag == layoutPosition) {
            if (fragmentTransaction.isEmpty()) {
                fragmentTransaction
                        .add(R.id.frame_layout, item)
                        .show(item)
                        .commit();
            } else {
                fragmentTransaction
                        .replace(R.id.frame_layout, item)
                        .commit();
            }
        }
    }

//    public void attachTabLayoutToViewPager2(@NonNull TabLayout tabLayout, @NonNull ViewPager2 viewPager2){
//        viewPager2.setAdapter(this);
//        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout,viewPager2 , new TabLayoutMediator.TabConfigurationStrategy() {
//            @Override
//            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                tab.setText(titles.get(position));
//            }
//        });
//        tabLayoutMediator.attach();
//    }
}
