package com.sscl.baselibrary.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * 自定义的圆弧形进度条
 *
 * @author pengh
 */
class ArcProgressBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 文字描述的paint
     */
    private var mTextPaint = Paint()
    /**
     * 根据数据显示的圆弧Paint
     */
    private val mArcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 圆弧开始的角度
     */
    private val startAngle: Float = 135f

    /**
     * 圆弧结束的角度
     */
    private val endAngle: Float = 45f

    /**
     * 圆弧背景的开始和结束间的夹角大小
     */
    private val mAngle: Float = 270f

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 当前进度夹角大小
     */
    private var mIncludedAngle: Float = 0f

    /**
     * 圆弧的画笔的宽度
     */
    private val mStrokeWith: Float = 10f

    /**
     * 中心的文字描述
     */
    private var mDes: String = ""

    /**
     * 动画时间
     */
    private var duration: Long = 2500

    /**
     * 动画效果的数据及最大/小值
     */
    private var mAnimatorValue: Int = 0
    private var mMinValue: Int = 0
    private var mMaxValue: Int = 0

    /**
     * 中心点的XY坐标
     */
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    /**
     * 文本的颜色
     */
    private var textColor: Int = Color.parseColor("#999999")

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpecCache: Int = widthMeasureSpec
        var heightMeasureSpecCache: Int = heightMeasureSpec
        val xSize: Int = MeasureSpec.getSize(widthMeasureSpecCache)
        var ySize: Int = MeasureSpec.getSize(heightMeasureSpecCache)
        ySize = if (xSize < ySize) {
            xSize
        } else {
            xSize
        }
        widthMeasureSpecCache = MeasureSpec.makeMeasureSpec(xSize, MeasureSpec.EXACTLY)
        heightMeasureSpecCache = MeasureSpec.makeMeasureSpec(ySize, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpecCache, heightMeasureSpecCache)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private fun initPaint() {
        //圆弧的paint

        //抗锯齿
        mArcPaint.isAntiAlias = true
        mArcPaint.color = Color.parseColor("#666666")
        //设置透明度（数值为0-255）
        mArcPaint.alpha = 100
        //设置画笔的画出的形状
        mArcPaint.strokeJoin = Paint.Join.ROUND
        mArcPaint.strokeCap = Paint.Cap.ROUND
        //设置画笔类型
        mArcPaint.style = Paint.Style.STROKE
        mArcPaint.strokeWidth = dp2px(mStrokeWith)
        //中心文字的paint

        mTextPaint.isAntiAlias = true
        mTextPaint.color = Color.parseColor("#FF4A40")
        //设置文本的对齐方式
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = dp2px(25f)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        centerX = (width / 2).toFloat()
        centerY = (height / 2).toFloat()
        //初始化paint
        initPaint()
        //绘制弧度
        drawArc(canvas)
        //绘制文本
        drawText(canvas)
    }

    /**
     * 绘制文本
     *
     * @param canvas 画布
     */
    private fun drawText(canvas: Canvas) {
        val mRect = Rect()
        val mValue: String = mAnimatorValue.toString()
        //绘制中心的数值
        mTextPaint.getTextBounds(mValue, 0, mValue.length, mRect)
        canvas.drawText(
            mAnimatorValue.toString(),
            centerX,
            centerY + mRect.height(),
            (mTextPaint)
        )
        //绘制中心文字描述
        mTextPaint.color = textColor
        mTextPaint.textSize = dp2px(12f)
        mTextPaint.getTextBounds(mDes, 0, mDes.length, mRect)
        //绘制最小值
        canvas.drawText(mDes, centerX, centerY + (2 * mRect.height()) + dp2px(10f), (mTextPaint))
        val minValue: String = mMinValue.toString()
        val maxValue: String = mMaxValue.toString()
        mTextPaint.textSize = dp2px(18f)
        mTextPaint.getTextBounds(minValue, 0, minValue.length, mRect)
        canvas.drawText(
            minValue,
            (centerX - (0.6 * centerX) - dp2px(5f)).toFloat(),
            (centerY + (0.75 * centerY) + mRect.height() + dp2px(5f)).toFloat(),
            (mTextPaint)
        )
        //绘制最大指
        mTextPaint.getTextBounds(maxValue, 0, maxValue.length, mRect)
        canvas.drawText(
            maxValue,
            (centerX + (0.6 * centerX) + dp2px(5f)).toFloat(),
            (centerY + (0.75 * centerY) + mRect.height() + dp2px(5f)).toFloat(),
            (mTextPaint)
        )
    }

    /**
     * 绘制圆弧
     *
     * @param canvas 画布
     */
    private fun drawArc(canvas: Canvas) {
        //绘制圆弧背景
        val mRectF = RectF(
            mStrokeWith + dp2px(5f),
            mStrokeWith + dp2px(5f),
            width - mStrokeWith - dp2px(5f),
            height - mStrokeWith
        )
        canvas.drawArc(mRectF, startAngle, mAngle, false, (mArcPaint))
        //绘制当前数值对应的圆弧
        mArcPaint.color = Color.parseColor("#FF4A40")
        //根据当前数据绘制对应的圆弧
        canvas.drawArc(mRectF, startAngle, mIncludedAngle, false, (mArcPaint))
    }

    /**
     * 为绘制弧度及数据设置动画
     *
     * @param currentAngle 需要绘制的弧度
     * @param currentValue 需要绘制的数据
     */
    private fun setAnimation( currentAngle: Float, currentValue: Int) {
        //绘制当前数据对应的圆弧的动画效果
        val progressAnimator: ValueAnimator = ValueAnimator.ofFloat(startAngle, currentAngle)
        progressAnimator.duration = duration
        progressAnimator.setTarget(mIncludedAngle)
        progressAnimator.addUpdateListener { animation ->
            mIncludedAngle = animation.animatedValue as Float
            //重新绘制，不然不会出现效果
            postInvalidate()
        }
        //开始执行动画
        progressAnimator.start() //中心数据的动画效果
        val valueAnimator: ValueAnimator = ValueAnimator.ofInt(mAnimatorValue, currentValue)
        valueAnimator.duration = duration
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener {
            mAnimatorValue = it.animatedValue as Int
            postInvalidate()
        }
        valueAnimator.start()
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
    }

    /**
     * 设置数据
     *
     * @param minValue     最小值
     * @param maxValue     最大值
     * @param currentValue 当前绘制的值
     * @param des          描述信息
     */
    fun setValues(minValue: Int, maxValue: Int, currentValue: Int, des: String) {
        var currentValueCache = currentValue
        mDes = des
        mMaxValue = maxValue
        mMinValue = minValue
        //完全覆盖背景弧度
        if (currentValueCache > maxValue) {
            currentValueCache = maxValue
        }
        //计算弧度比重
        val scale: Float = currentValueCache.toFloat() / maxValue
        //计算弧度
        val currentAngle: Float = scale * mAngle
        //开始执行动画
        setAnimation(currentAngle, currentValueCache)
    }

    fun dp2px(dp: Float): Float {
        val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
        return dp * metrics.density
    }
}