package com.sscl.baselibrary.widget

import android.content.*
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.*


/**
 * 自定义圆形控件
 * @author pengh
 */
class CirclePointView constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0): View(context, attrs, defStyleAttr) {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 画笔
     */
    private val paint = Paint()

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 颜色
     */
    private var color: Int = 0

    /**
     * 屏幕的宽度
     */
    private var screenWidth: Float = 0f

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    init {
        val defaultDisplay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.display
        } else {
            val windowManager: WindowManager? =
                context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            windowManager?.defaultDisplay
        }
        if (defaultDisplay!=null) {
            val point = Point()
            defaultDisplay.getSize(point)
            screenWidth = point.x.toFloat()
            color = Color.RED
        }
    }

   /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    *
    * 重写方法
    *
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 在onDraw方法中画一个圆
     *
     * @param canvas 画内容的画布
     */
    override fun onDraw(canvas: Canvas) {
        paint.color = color

        //画一个圆心坐标为(40,40)，半径为屏幕宽度的1/30的圆
        canvas.drawCircle(40f, 40f, screenWidth / 30, paint)
    }

    /**
     * 计算圆形在屏幕中的大小(仅处理在match_parent时候的宽高)
     *
     * @param widthMeasureSpec  宽度的MeasureSpec
     * @param heightMeasureSpec 高度的MeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode: Int = MeasureSpec.getMode(heightMeasureSpec)
        var width: Int = MeasureSpec.getSize(widthMeasureSpec)
        var height: Int = MeasureSpec.getSize(heightMeasureSpec)
        when (widthMode) {
            MeasureSpec.AT_MOST -> width = (screenWidth / 7).toInt()
            MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED -> {}
            else -> {}
        }
        when (heightMode) {
            MeasureSpec.AT_MOST -> height = (screenWidth / 7).toInt()
            MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED -> {}
            else -> {}
        }
        setMeasuredDimension(width, height)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 改变圆的颜色
     *
     * @param color 要改变的颜色值
     */
    fun setColor(color: Int) {
        this.color = color
        postInvalidate()
    }
}