package com.sscl.baselibrary.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.utils.BaseManager;

import java.util.Locale;

/**
 * 自定义圆形进度条
 *
 * @author pengh
 */

public class CircleProgressBar extends View {

    private Thread animateThread;

    /**
     * 这个圆形进度条的最小宽度
     */
    private double minWidth;
    /**
     * 这个圆形进度条的最小高度
     */
    private double minHeight;
    /**
     * 进度条进度
     */
    private double targetProgress;

    /**
     * 目标进度值
     */
    private double currentProgress;
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 最大进度
     */
    private double maxProgress;
    /**
     * 圆环的底色
     */
    private int roundColor;
    /**
     * 进度条的颜色
     */
    private int progressColor;
    /**
     * 在进度条中间显示的进度文字的颜色
     */
    private int textColor;
    /**
     * 在进度条中间显示的文字的大小
     */
    private float textSize;
    /**
     * 进度条的宽度（进度的粗细）
     */
    private float roundWidth;
    /**
     * 是否显示进度百分比文字
     */
    private boolean showText;
    private RectF rectF;
    private Runnable animateRunnable = new Runnable() {
        @Override
        public void run() {
            boolean needPlus;
            double sub;
            if (currentProgress < targetProgress) {
                sub = targetProgress - currentProgress;
                needPlus = true;
            } else {
                sub = currentProgress - targetProgress;
                needPlus = false;
            }
            double diffProgress = sub / ((double) animateTime / delay);
            boolean needAnimate = true;
            while (needAnimate) {
                if (animateThread.isInterrupted()) {
                    break;
                }

                setProgress(currentProgress, targetProgress);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (needPlus) {
                    currentProgress = currentProgress + diffProgress;
                    if (currentProgress + diffProgress > targetProgress) {
                        needAnimate = false;
                    }
                } else {
                    currentProgress = currentProgress - diffProgress;
                    if (currentProgress - diffProgress < targetProgress) {
                        needAnimate = false;
                    }
                }
            }
        }
    };
    private long delay = 10;

    private long animateTime = 1000;

    /**
     * 构造器
     *
     * @param context 上下文
     */
    public CircleProgressBar(Context context) {
        this(context, null);
    }

    /**
     * 构造器
     *
     * @param context 上下文
     * @param attrs   属性
     */
    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造器
     *
     * @param context      上下文
     * @param attrs        属性
     * @param defStyleAttr 默认属性
     */
    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial();
        getAttributeSet(attrs);
    }

    /**
     * 测量控件宽度
     *
     * @param widthMeasureSpec  宽度的规格
     * @param heightMeasureSpec 高度的规格
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                widthSize = (int) minWidth;
                break;
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                heightSize = (int) minHeight;
                break;
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        //判断宽高,以最小的为准(正方形)
        if (widthSize < heightSize) {
            //noinspection SuspiciousNameCombination
            heightSize = widthSize;
        } else {
            //noinspection SuspiciousNameCombination
            widthSize = heightSize;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 绘制
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;

        //画背景圆环
        float radius = center - roundWidth / 2;
        paint.setColor(roundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(center, center, radius, paint);

        //画进度百分比
        if (showText) {
            paint.setColor(textColor);
            paint.setStrokeWidth(0);
            paint.setTextSize(textSize);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            double percent = currentProgress * 1.0 / maxProgress * 100;
            String percentString = String.format(Locale.getDefault(), "%.2f", percent) + "%";
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            canvas.drawText(percentString, getWidth() / (float) 2 - paint.measureText(percentString) / 2, getWidth() / (float) 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, paint);
        }

        //画圆弧（进度）
        if (rectF == null) {
            rectF = new RectF(center - radius, center - radius, center + radius, center + radius);
        }
        paint.setColor(progressColor);
        paint.setStrokeWidth(roundWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectF, -90, (float) (360f * currentProgress / maxProgress), false, paint);
    }

    public void setProgress(double currentProgress) {
        setProgress(currentProgress, currentProgress);
    }

    public void setProgressWithAnimate(double progress) {
        if (animateThread != null && animateThread.isAlive()) {
            animateThread.interrupt();
            setProgress(targetProgress);
        }
        currentProgress = this.targetProgress;
        this.targetProgress = progress;
        animateThread = BaseManager.getThreadFactory().newThread(animateRunnable);
        animateThread.start();
    }

    public void setAnimateProgressDelay(@IntRange(from = 1) long delay) {
        this.delay = delay;
    }

    public void setAnimateTimeMillis(@IntRange(from = 0) long animateTime) {
        this.animateTime = animateTime;
    }

    public double getMaxProgress() {
        return maxProgress;
    }

    private void setProgress(double currentProgress, double targetProgress) {
        if (currentProgress < 0) {
            throw new IllegalArgumentException("targetProgress must be a positive number,but a wrong number" + targetProgress + "we got!");
        } else if (currentProgress > maxProgress) {
            this.currentProgress = maxProgress;
            this.targetProgress = maxProgress;
        } else if (currentProgress <= maxProgress) {
            this.currentProgress = currentProgress;
            this.targetProgress = targetProgress;
        }
        postInvalidate();
    }

    /**
     * 获取并属性
     *
     * @param attrs 属性
     */
    private void getAttributeSet(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        maxProgress = typedArray.getFloat(R.styleable.CircleProgressBar_max_progress, 100);
        roundColor = typedArray.getColor(R.styleable.CircleProgressBar_round_color, Color.RED);
        progressColor = typedArray.getColor(R.styleable.CircleProgressBar_progress_color, Color.BLUE);
        textColor = typedArray.getColor(R.styleable.CircleProgressBar_text_color, Color.GREEN);
        textSize = typedArray.getDimension(R.styleable.CircleProgressBar_text_size, 55);
        roundWidth = typedArray.getDimension(R.styleable.CircleProgressBar_round_width, 10);
        showText = typedArray.getBoolean(R.styleable.CircleProgressBar_show_text, true);
        typedArray.recycle();
    }

    /**
     * 初始化
     */
    private void initial() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null){
            return;
        }
        Point point = new Point();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        defaultDisplay.getSize(point);
        minWidth = point.x * 1.0 / 3;
        minHeight = point.y * 1.0 / 3;
        paint = new Paint();
    }
}
