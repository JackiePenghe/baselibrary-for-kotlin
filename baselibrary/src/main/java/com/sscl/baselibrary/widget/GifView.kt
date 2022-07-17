package com.sscl.baselibrary.widget

import kotlin.jvm.JvmOverloads
import com.sscl.baselibrary.R
import android.annotation.SuppressLint
import android.content.res.TypedArray
import kotlin.jvm.Volatile
import android.content.*
import android.graphics.*
import android.os.*
import android.util.AttributeSet
import android.view.*

/**
 * 自定义用于显示gif动画的控件
 *
 * @author jackie
 */
class GifView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private var mMovieResourceId: Int = 0
    private var mMovie: Movie? = null
    private var mMovieStart: Long = 0
    private var mCurrentAnimationTime: Int = 0
    private var mLeft: Float = 0f
    private var mTop: Float = 0f
    private var mScale: Float = 0f
    private var mMeasuredMovieWidth: Int = 0
    private var mMeasuredMovieHeight: Int = 0
    private var mVisible: Boolean = true

    @Volatile
    private var mPaused: Boolean = false
    @SuppressLint("NewApi")
    private fun setViewAttributes(
        context: Context, attrs: AttributeSet?,
        defStyle: Int
    ) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        // 从描述文件中读出gif的值，创建出Movie实例
        val array: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.GifView, defStyle, R.style.Widget_GifView
        )
        mMovieResourceId = array.getResourceId(R.styleable.GifView_gif, -1)
        mPaused = array.getBoolean(R.styleable.GifView_paused, false)
        array.recycle()
        if (mMovieResourceId != -1) {
            mMovie = Movie.decodeStream(
                getResources().openRawResource(
                    mMovieResourceId
                )
            )
        }
    }

    /**
     * 设置gif图资源
     *
     * @param movieResId 要设置的gif图资源
     */
    fun setMovieResource(movieResId: Int) {
        mMovieResourceId = movieResId
        mMovie = Movie.decodeStream(
            getResources().openRawResource(
                mMovieResourceId
            )
        )
        requestLayout()
    }
    /**
     * 获取gif的动画(视频)
     *
     * @return gif的动画(视频)
     */
    /**
     * 设置gif的动画(视频)
     *
     * @param movie 要设置的gif动画(视频)
     */
    var movie: Movie?
        get() {
            return mMovie
        }
        set(movie) {
            mMovie = movie
            requestLayout()
        }

    /**
     * 设置gif的动画(视频)的时间
     *
     * @param time 要设置的gif动画(视频)时间
     */
    fun setMovieTime(time: Int) {
        mCurrentAnimationTime = time
        invalidate()
    }
    /**
     * 判断gif图是否停止了
     *
     * @return true表示已经停止了，false表示没有停止
     */
    /**
     * 设置暂停
     *
     * @param paused 要设置的暂停标识(true表示暂停，false表示播放)
     */
    var isPaused: Boolean
        get() {
            return mPaused
        }
        set(paused) {
            mPaused = paused
            if (!paused) {
                mMovieStart = (SystemClock.uptimeMillis()
                        - mCurrentAnimationTime)
            }
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mMovie != null) {
            val movieWidth: Int = mMovie!!.width()
            val movieHeight: Int = mMovie!!.height()
            val maximumWidth: Int = MeasureSpec.getSize(widthMeasureSpec)
            val scaleWidth: Float = movieWidth.toFloat() / maximumWidth.toFloat()
            mScale = 1f / scaleWidth
            mMeasuredMovieWidth = maximumWidth
            mMeasuredMovieHeight = (movieHeight * mScale).toInt()
            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight)
        } else {
            setMeasuredDimension(
                getSuggestedMinimumWidth(),
                getSuggestedMinimumHeight()
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mLeft = (getWidth() - mMeasuredMovieWidth) / 2f
        mTop = (getHeight() - mMeasuredMovieHeight) / 2f
        mVisible = getVisibility() == VISIBLE
    }

    override fun onDraw(canvas: Canvas) {
        if (mMovie != null) {
            if (!mPaused) {
                updateAnimationTime()
                drawMovieFrame(canvas)
                invalidateView()
            } else {
                drawMovieFrame(canvas)
            }
        }
    }

    private fun invalidateView() {
        if (mVisible) {
            postInvalidateOnAnimation()
        }
    }

    private fun updateAnimationTime() {
        val now: Long = SystemClock.uptimeMillis()
        // 如果第一帧，记录起始时间
        if (mMovieStart == 0L) {
            mMovieStart = now
        }
        // 取出动画的时长
        var dur: Int = mMovie!!.duration()
        if (dur == 0) {
            dur = DEFAULT_MOVIE_DURATION
        }
        // 算出需要显示第几帧
        mCurrentAnimationTime = ((now - mMovieStart) % dur).toInt()
    }

    private fun drawMovieFrame(canvas: Canvas) {
        // 设置要显示的帧，绘制即可
        mMovie!!.setTime(mCurrentAnimationTime)
        //        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.save()
        canvas.scale(mScale, mScale)
        mMovie!!.draw(canvas, mLeft / mScale, mTop / mScale)
        canvas.restore()
    }

    @SuppressLint("NewApi")
    public override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        mVisible = screenState == SCREEN_STATE_ON
        invalidateView()
    }

    @SuppressLint("NewApi")
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        mVisible = visibility == VISIBLE
        invalidateView()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        mVisible = visibility == VISIBLE
        invalidateView()
    }

    companion object {
        /**
         * 默认为1秒
         */
        private val DEFAULT_MOVIE_DURATION: Int = 1000
    }
    /**
     * 构造方法
     *
     * @param context  上下文
     * @param attrs    参数
     * @param defStyle 默认风格
     */
    /**
     * 构造方法
     *
     * @param context 上下文
     * @param attrs   参数
     */
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    init {
        setViewAttributes(context, attrs, defStyle)
    }
}