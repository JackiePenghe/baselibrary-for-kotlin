package com.sscl.basesample.adapter

import com.sscl.baselibrary.adapter.BasePurposeAdapter
import com.sscl.basesample.R

/**
 * @author alm
 * @date 2017/10/12
 * AllPurposeAdapter举例
 */
class SampleAdapter
/**
 * 构造器
 *
 * @param dataList 适配器数据源
 */
    (dataList: ArrayList<String>) :
    BasePurposeAdapter<String>(dataList, R.layout.com_sscl_basesample_adapter_sample) {
    /**
     * 在这个方法中可以直接设置ListView的item显示内容
     *
     * @param viewHolder ViewHolder，存储单独的一个Item中的所有控件
     * @param position   当前的item位置
     * @param item       当前item所在位置的泛型类（如本类泛型String，此处的类型就是String）
     */
    override fun convert(viewHolder: ViewHolder, position: Int, item: String) {
        viewHolder.setText(R.id.text1, item)
            .setText(R.id.text2, item)
    }
}