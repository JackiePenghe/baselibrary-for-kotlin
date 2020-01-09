package com.sscl.baselibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * 自定义圆形控件
 * @author pengh
 */
public class CirclePointView extends View {

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 颜色
     */
    private int color;
    /**
     * 屏幕的宽度
     */
    private float screenWidth;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public CirclePointView(Context context) {
        super(context);
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     * @param attrs   参数
     */
    public CirclePointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return;
        }
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        screenWidth = point.x;
        color = Color.RED;
        paint = new Paint();
    }

    /**
     * 构造方法
     *
     * @param context      上下文
     * @param attrs        参数
     * @param defStyleAttr 风格
     */
    public CirclePointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*--------------------------------重写父类方法--------------------------------*/

    /**
     * 在onDraw方法中画一个圆
     *
     * @param canvas 画内容的画布
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        paint.setColor(color);

        //画一个圆心坐标为(40,40)，半径为屏幕宽度的1/30的圆
        canvas.drawCircle(40, 40, screenWidth / 30, paint);
    }

    /**
     * 计算圆形在屏幕中的大小(仅处理在match_parent时候的宽高)
     *
     * @param widthMeasureSpec  宽度的MeasureSpec
     * @param heightMeasureSpec 高度的MeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                width = (int) (screenWidth / 7);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
            default:
                break;
        }

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = (int) (screenWidth / 7);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
            default:
                break;
        }

        setMeasuredDimension(width, height);
    }

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 改变圆的颜色
     *
     * @param color 要改变的颜色值
     */
    public void setColor(int color) {
        this.color = color;
        postInvalidate();
    }
}
