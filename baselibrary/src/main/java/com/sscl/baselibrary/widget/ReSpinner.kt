package com.sscl.baselibrary.widget

import androidx.appcompat.widget.AppCompatSpinner
import android.content.*
import android.util.AttributeSet

/**
 * 可重复选择同一选项并触发监听的Spinner
 *
 * @author jackie
 */
class ReSpinner : AppCompatSpinner {
    /*---------------------------------------成员变量---------------------------------------*/ /*---------------------------------------公开方法---------------------------------------*/
    /**
     * 标志下拉列表是否正在显示
     */
    var isDropDownMenuShown: Boolean = false

    /*---------------------------------------构造方法---------------------------------------*/
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    /*---------------------------------------重写父类方法---------------------------------------*/
    public override fun setSelection(position: Int, animate: Boolean) {
        val sameSelected: Boolean = position == getSelectedItemPosition()
        super.setSelection(position, animate)
        if (sameSelected) {
            val onItemSelectedListener: OnItemSelectedListener? = getOnItemSelectedListener()
            if (onItemSelectedListener != null) {
                // 如果选择项是Spinner当前已选择的项,则 OnItemSelectedListener并不会触发,因此这里手动触发回调
                onItemSelectedListener.onItemSelected(
                    this,
                    getSelectedView(),
                    position,
                    getSelectedItemId()
                )
            }
        }
    }

    public override fun performClick(): Boolean {
        isDropDownMenuShown = true
        return super.performClick()
    }

    public override fun setSelection(position: Int) {
        val sameSelected: Boolean = position == getSelectedItemPosition()
        super.setSelection(position)
        if (sameSelected) {
            val onItemSelectedListener: OnItemSelectedListener? = getOnItemSelectedListener()
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(
                    this,
                    getSelectedView(),
                    position,
                    getSelectedItemId()
                )
            }
        }
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}