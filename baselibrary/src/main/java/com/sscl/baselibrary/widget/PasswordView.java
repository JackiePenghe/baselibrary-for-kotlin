package com.sscl.baselibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.sscl.baselibrary.R;

/**
 * 自定义密码输入框
 *
 * @author jackie
 */
public class PasswordView extends AppCompatEditText {

//    private EditText passwordEt;

    private Paint mPaint;
//    private Path mPath;

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLetterSpacing(2);
//        inflate(context, R.layout.view_password, this);
//        passwordEt = findViewById(R.id.view_password_et);
//        passwordEt.setBackground(null);
//        obtainStyledAttributes(attrs);
        setCursorVisible(false);
        initPaint();
        setBackgroundResource(R.drawable.password_view_bg);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("TAG", "onDraw: ");
//        canvas.drawLine();
    }


    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //默认使用textview当前颜色
        mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
//        mPath = new Path();
        //设置虚线距离
        setPadding(0, 0, 0, 3);
    }

//    private void obtainStyledAttributes(AttributeSet attrs) {
//        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PasswordView);
//        String text = typedArray.getString(R.styleable.PasswordView_android_text);
//        int maxEms = typedArray.getInt(R.styleable.PasswordView_android_maxEms, 4);
//        passwordEt.setMaxEms(maxEms);
//        passwordEt.setLetterSpacing(2);
//        passwordEt.setText(text);
//        typedArray.recycle();
//    }
}
