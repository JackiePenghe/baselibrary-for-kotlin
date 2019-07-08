package com.sscl.baselibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.sscl.baselibrary.R;

/**
 * 用于显示GIF图片的控件
 *
 * @author jackie
 */
public class GifView extends View {

    /*--------------------------------静态常量--------------------------------*/

    private static final int DEFAULT_MOVIE_VIEW_DURATION = 1000;

    /*--------------------------------成员变量--------------------------------*/

    private int movieMovieResourceId;
    private Movie movie;
    private long movieStart;
    private int currentAnimationTime;

    private float movieLeft;
    private float movieTop;
    private float movieScale;

    private int movieMeasuredMovieWidth;
    private int movieMeasuredMovieHeight;

    private boolean isPaused;

    private boolean isVisible = true;

    /*--------------------------------构造方法--------------------------------*/

    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setViewAttributes(context, attrs, defStyleAttr);
    }

    /*--------------------------------重写父类方法--------------------------------*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (movie != null) {
            int movieWidth = movie.width();
            int movieHeight = movie.height();
            /*
             * Calculate horizontal scaling
             */
            float scaleHeight = 1f;
            int measureModeWidth = MeasureSpec.getMode(widthMeasureSpec);

            if (measureModeWidth != MeasureSpec.UNSPECIFIED) {
                int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
                if (movieWidth > maximumWidth) {
                    scaleHeight = ((float) movieWidth) / maximumWidth;
                }
            }
            /*
             * calculate vertical scaling
             */
            float scaleWidth = 1f;
            int measureModeHeight = MeasureSpec.getMode(heightMeasureSpec);

            if (measureModeHeight != MeasureSpec.UNSPECIFIED) {
                int maximumHeight = MeasureSpec.getSize(heightMeasureSpec);
                if (movieHeight > maximumHeight) {
                    scaleWidth = ((float) movieHeight) / maximumHeight;
                }
            }
            /*
             * calculate overall scale
             */
            movieScale = 1f / Math.max(scaleHeight, scaleWidth);
            movieMeasuredMovieWidth = (int) (movieWidth * movieScale);
            movieMeasuredMovieHeight = (int) (movieHeight * movieScale);
            setMeasuredDimension(movieMeasuredMovieWidth, movieMeasuredMovieHeight);
        } else {
            /*
             * No movie set, just set minimum available size.
             */
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // Calculate movieLeft / movieTop for drawing in center
        movieLeft = (getWidth() - movieMeasuredMovieWidth) / 2f;
        movieTop = (getHeight() - movieMeasuredMovieHeight) / 2f;
        isVisible = getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (movie != null) {
            if (!isPaused) {
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            isVisible = screenState == View.SCREEN_STATE_ON;
            invalidateView();
        } else {
            isVisible = screenState == 1;
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        isVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        isVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    /*--------------------------------自定义公开方法--------------------------------*/

    public void play() {
        if (this.isPaused) {
            this.isPaused = false;
            //Calculate new movie start time, so that it resumes from the same frame.
            movieStart = android.os.SystemClock.uptimeMillis() - currentAnimationTime;
            invalidate();
        }
    }

    public void pause() {
        if (!this.isPaused) {
            this.isPaused = true;
            invalidate();
        }
    }

    public int getGifResource() {
        return movieMovieResourceId;
    }

    public void setGifRecource(int movieResourceId) {
        movieMovieResourceId = movieResourceId;
        movie = Movie.decodeStream(getResources().openRawResource(movieMovieResourceId));
        requestLayout();
    }

    public boolean isPlaying() {
        return !isPaused;
    }

    /*--------------------------------自定义私有方法--------------------------------*/

    /**
     * 初始化控件属性
     *
     * @param context      上下文
     * @param attrs        控件属性
     * @param defStyleAttr 默认的属性定义
     */
    private void setViewAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GifView, defStyleAttr, R.style.Widget_GifView);


        //-1 is default value
        movieMovieResourceId = array.getResourceId(R.styleable.GifView_gif, -1);
        isPaused = array.getBoolean(R.styleable.GifView_paused, false);

        array.recycle();

        if (movieMovieResourceId != -1) {
            movie = Movie.decodeStream(getResources().openRawResource(movieMovieResourceId));
        }
    }

    /**
     * Calculate current animation time
     */
    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();

        if (movieStart == 0L) {
            movieStart = now;
        }

        int duration = movie.duration();

        if (duration == 0) {
            duration = DEFAULT_MOVIE_VIEW_DURATION;
        }

        currentAnimationTime = (int) ((now - movieStart) % duration);
    }

    /**
     * Draw current GIF frame
     */
    private void drawMovieFrame(Canvas canvas) {
        movie.setTime(currentAnimationTime);
        canvas.save();
        canvas.scale(movieScale, movieScale);
        movie.draw(canvas, movieLeft / movieScale, movieTop / movieScale);
        canvas.restore();
    }

    private void invalidateView() {
        if (isVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }
}
