package com.sscl.basesample.adapter;

import android.view.View;

import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.widget.banner.BannerHolder;
import com.sscl.baselibrary.widget.banner.BaseBannerAdapter;
import com.sscl.basesample.R;

public class BannerAdapter extends BaseBannerAdapter<String> {

    private static final String TAG = BannerAdapter.class.getSimpleName();

    public BannerAdapter() {
        super(R.layout.adapter_banner);
    }

    @Override
    public void bindView(BannerHolder holder, String itemData, int position) {
        holder.setText(R.id.honeNoticeBannerTitleTv, itemData);
        holder.setText(R.id.honeNoticeBannerIntroduceTv, itemData);
        holder.setText(R.id.honeNoticeBannerViewBtn, itemData);
        holder.getView(R.id.honeNoticeBannerViewBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugUtil.warnOut(TAG, "honeNoticeBannerViewBtn clicked");
            }
        });
    }
}
