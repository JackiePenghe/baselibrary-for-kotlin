package com.sscl.basesample.adapter;

import com.sscl.baselibrary.adapter.BasePurposeAdapter;
import com.sscl.basesample.R;

import java.util.ArrayList;

/**
 * @author alm
 * @date 2017/10/12
 * AllPurposeAdapter举例
 */

public class SampleAdapter extends BasePurposeAdapter<String> {


    /**
     * 构造器
     *
     * @param dataList 适配器数据源
     */
    public SampleAdapter(ArrayList<String> dataList) {
        //在此处我直接使用安卓默认的布局
        super(dataList, R.layout.adapter_sample);
    }

    /**
     * 在这个方法中可以直接设置ListView的item显示内容
     *
     * @param viewHolder ViewHolder，存储单独的一个Item中的所有控件
     * @param position   当前的item位置
     * @param item       当前item所在位置的泛型类（如本类泛型String，此处的类型就是String）
     */
    @Override
    protected void convert(ViewHolder viewHolder, int position, String item) {
        viewHolder.setText(R.id.text1, item)
                .setText(R.id.text2, item);
    }
}
