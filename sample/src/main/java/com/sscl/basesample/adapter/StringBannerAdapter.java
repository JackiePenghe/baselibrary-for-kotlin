package com.sscl.basesample.adapter;

import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.widget.bannerold.BannerHolder;
import com.sscl.baselibrary.widget.bannerold.BaseBannerAdapter;
import com.sscl.basesample.R;

/**
 * @author pengh
 */
public class StringBannerAdapter extends BaseBannerAdapter<String> {

    private static final String TAG = StringBannerAdapter.class.getSimpleName();

    public StringBannerAdapter() {
        super(R.layout.com_sscl_basesample_adapter_banner);
    }

    @Override
    public void bindView(BannerHolder holder, String itemData, int position) {
        holder.setText(R.id.honeNoticeBannerTitleTv, itemData);
        holder.setText(R.id.honeNoticeBannerIntroduceTv, itemData);
        holder.setText(R.id.honeNoticeBannerViewBtn, itemData);
        holder.getView(R.id.honeNoticeBannerViewBtn).setOnClickListener(v -> {
            DebugUtil.warnOut(TAG, "honeNoticeBannerViewBtn clicked");
            DebugUtil.warnOut(TAG, "do nothing in banner click listener");
        });
    }

    @Override
    public void onPageSelected(int position) {
        //do nothing
    }

    @Override
    public long getDelayTime(int position, long defaultDelayTime) {
        return 0;
    }
}
