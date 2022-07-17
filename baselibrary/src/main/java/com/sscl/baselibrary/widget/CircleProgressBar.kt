package com.sscl.baselibrary.widget

import android.content.*
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import androidx.annotation.IntRange
import java.lang.IllegalArgumentException
import java.util.*

import kotlin.jvm.JvmOverloads
import com.sscl.baselibrary.R
import com.sscl.baselibrary.utils.BaseManager
import android.content.res.TypedArray

/**
 * 自定义圆形进度条
 *
 * @author pengh
 */
class CircleProgressBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    private var animateThread: Thread? = null

    private var rectF: RectF? = null

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 这个圆形进度条的最小宽度
     */
    private var minWidth: Double = 0.0

    /**
     * 这个圆形进度条的最小高度
     */
    private var minHeight: Double = 0.0

    /**
     * 进度条进度
     */
    private var targetProgress: Double = 0.0

    /**
     * 目标进度值
     */
    private var currentProgress: Double = 0.0

    /**
     * 最大进度
     */
    var maxProgress: Double = 0.0
        private set

    /**
     * 圆环的底色
     */
    private var roundColor: Int = 0

    /**
     * 进度条的颜色
     */
    private var progressColor: Int = 0

    /**
     * 在进度条中间显示的进度文字的颜色
     */
    private var textColor: Int = 0

    /**
     * 在进度条中间显示的文字的大小
     */
    private var textSize: Float = 0f

    /**
     * 进度条的宽度（进度的粗细）
     */
    private var roundWidth: Float = 0f

    /**
     * 是否显示进度百分比文字
     */
    private var showText: Boolean = false

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 画笔
     */
    private val paint: Paint = Paint()

    private val animateRunnable: Runnable = Runnable {
        val needPlus: Boolean
        val sub: Double
        if (currentProgress < targetProgress) {
            sub = targetProgress - currentProgress
            needPlus = true
        } else {
            sub = currentProgress - targetProgress
            needPlus = false
        }
        val diffProgress: Double = sub / (animateTime.toDouble() / delay)
        var needAnimate = true
        while (needAnimate) {
            val animateThread = animateThread ?:break
            if (animateThread.isInterrupted) {
                break
            }
            setProgress(currentProgress, targetProgress)
            try {
                Thread.sleep(delay)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (needPlus) {
                currentProgress += diffProgress
                if (currentProgress + diffProgress > targetProgress) {
                    needAnimate = false
                }
            } else {
                currentProgress -= diffProgress
                if (currentProgress - diffProgress < targetProgress) {
                    needAnimate = false
                }
            }
        }
    }
    private var delay: Long = 10
    private var animateTime: Long = 1000

    /**
     * 测量控件宽度
     *
     * @param widthMeasureSpec  宽度的规格
     * @param heightMeasureSpec 高度的规格
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode: Int = MeasureSpec.getMode(heightMeasureSpec)
        var widthSize: Int = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize: Int = MeasureSpec.getSize(heightMeasureSpec)
        when (widthMode) {
            MeasureSpec.AT_MOST -> widthSize = minWidth.toInt()
            MeasureSpec.EXACTLY -> {}
            MeasureSpec.UNSPECIFIED -> {}
        }
        when (heightMode) {
            MeasureSpec.AT_MOST -> heightSize = minHeight.toInt()
            MeasureSpec.EXACTLY -> {}
            MeasureSpec.UNSPECIFIED -> {}
        }

        //判断宽高,以最小的为准(正方形)
        if (widthSize < heightSize) {
            heightSize = widthSize
        } else {
            widthSize = heightSize
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    /**
     * 绘制
     *
     * @param canvas 画布
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val center: Int = width / 2

        //画背景圆环
        val radius: Float = center - roundWidth / 2
        paint.color = roundColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = roundWidth
        paint.isAntiAlias = true
        canvas.drawCircle(center.toFloat(), center.toFloat(), radius, paint)

        //画进度百分比
        if (showText) {
            paint.color = textColor
            paint.strokeWidth = 0f
            paint.textSize = textSize
            paint.typeface = Typeface.DEFAULT_BOLD
            val percent: Double = currentProgress * 1.0 / maxProgress * 100
            val percentString: String = String.format(Locale.getDefault(), "%.2f", percent) + "%"
            val fontMetrics: Paint.FontMetrics = paint.fontMetrics
            canvas.drawText(
                percentString,
                width / 2f - paint.measureText(percentString) / 2,
                width / 2f + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom,
                paint
            )
        }

        //画圆弧（进度）
        if (rectF == null) {
            rectF = RectF(center - radius, center - radius, center + radius, center + radius)
        }
        paint.color = progressColor
        paint.strokeWidth = roundWidth
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(
            rectF?:return,
            -90f,
            (360f * currentProgress / maxProgress).toFloat(),
            false,
            paint
        )
    }

    fun setProgress(currentProgress: Double) {
        setProgress(currentProgress, currentProgress)
    }

    fun setProgressWithAnimate(progress: Double) {
        if (animateThread != null && animateThread!!.isAlive) {
            animateThread!!.interrupt()
            setProgress(targetProgress)
        }
        currentProgress = targetProgress
        targetProgress = progress
        animateThread = BaseManager.threadFactory.newThread(animateRunnable)
        animateThread?.start()
    }

    fun setAnimateProgressDelay(@IntRange(from = 1) delay: Long) {
        this.delay = delay
    }

    fun setAnimateTimeMillis(@IntRange(from = 0) animateTime: Long) {
        this.animateTime = animateTime
    }

    private fun setProgress(currentProgress: Double, targetProgress: Double) {
        if (currentProgress < 0) {
            throw IllegalArgumentException("targetProgress must be a positive number,but a wrong number" + targetProgress + "we got!")
        } else if (currentProgress > maxProgress) {
            this.currentProgress = maxProgress
            this.targetProgress = maxProgress
        } else if (currentProgress <= maxProgress) {
            this.currentProgress = currentProgress
            this.targetProgress = targetProgress
        }
        postInvalidate()
    }

    /**
     * 获取并属性
     *
     * @param attrs 属性
     */
    private fun getAttributeSet(attrs: AttributeSet?) {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar)
        maxProgress =
            typedArray.getFloat(R.styleable.CircleProgressBar_max_progress, 100f).toDouble()
        roundColor = typedArray.getColor(R.styleable.CircleProgressBar_round_color, Color.RED)
        progressColor =
            typedArray.getColor(R.styleable.CircleProgressBar_progress_color, Color.BLUE)
        textColor = typedArray.getColor(R.styleable.CircleProgressBar_text_color, Color.GREEN)
        textSize = typedArray.getDimension(R.styleable.CircleProgressBar_text_size, 55f)
        roundWidth = typedArray.getDimension(R.styleable.CircleProgressBar_round_width, 10f)
        showText = typedArray.getBoolean(R.styleable.CircleProgressBar_show_text, true)
        typedArray.recycle()
    }

    /**
     * 初始化
     */
    private fun initial() {
        val windowManager: WindowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager? ?: return
        val point = Point()
        val defaultDisplay: Display = windowManager.defaultDisplay
        defaultDisplay.getSize(point)
        minWidth = point.x * 1.0 / 3
        minHeight = point.y * 1.0 / 3
    }
    /**
     * 构造器
     *
     * @param context      上下文
     * @param attrs        属性
     * @param defStyleAttr 默认属性
     */
    /**
     * 构造器
     *
     * @param context 上下文
     * @param attrs   属性
     */
    /**
     * 构造器
     *
     * @param context 上下文
     */
    init {
        initial()
        getAttributeSet(attrs)
    }
}