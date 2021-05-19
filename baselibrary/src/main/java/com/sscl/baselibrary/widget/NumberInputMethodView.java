package com.sscl.baselibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.utils.BaseManager;
import com.sscl.baselibrary.utils.DebugUtil;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 数字键盘
 *
 * @author jackie
 */
public class NumberInputMethodView extends FrameLayout {

    private static final String TAG = NumberInputMethodView.class.getSimpleName();

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Activity
     */
    private Activity activity;
    /**
     * 输入框
     */
    private EditText editText;
    /**
     * 数字1，2，3，4，5，6，7，8，9，0
     */
    private TextView numberTv1, numberTv2, numberTv3, numberTv4, numberTv5, numberTv6, numberTv7, numberTv8, numberTv9, numberTv0;
    /**
     * 清空
     */
    private TextView numberTvClear;
    /**
     * 删除
     */
    private RelativeLayout numberTvDelete;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == numberTv1.getId()) {
                pressKey(KeyEvent.KEYCODE_1);
            } else if (id == numberTv2.getId()) {
                pressKey(KeyEvent.KEYCODE_2);
            } else if (id == numberTv3.getId()) {
                pressKey(KeyEvent.KEYCODE_3);
            } else if (id == numberTv4.getId()) {
                pressKey(KeyEvent.KEYCODE_4);
            } else if (id == numberTv5.getId()) {
                pressKey(KeyEvent.KEYCODE_5);
            } else if (id == numberTv6.getId()) {
                pressKey(KeyEvent.KEYCODE_6);
            } else if (id == numberTv7.getId()) {
                pressKey(KeyEvent.KEYCODE_7);
            } else if (id == numberTv8.getId()) {
                pressKey(KeyEvent.KEYCODE_8);
            } else if (id == numberTv9.getId()) {
                pressKey(KeyEvent.KEYCODE_9);
            } else if (id == numberTv0.getId()) {
                pressKey(KeyEvent.KEYCODE_0);
            } else if (id == numberTvDelete.getId()) {
                pressKey(KeyEvent.KEYCODE_DEL);
            } else if (id == numberTvClear.getId()) {
                if (editText != null) {
                    editText.setText("");
                }
            }
        }
    };

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ScheduledExecutorService scheduledExecutorService;

    public NumberInputMethodView(@NonNull Context context) {
        this(context, null);
    }

    public NumberInputMethodView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public NumberInputMethodView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_number_input_method, this);
        initViews();
        initEvents();
    }

    /**
     * 绑定Activity
     *
     * @param activity activity
     */
    public void attach(Activity activity, EditText editText) {
        this.activity = activity;
        this.editText = editText;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 解绑Activity
     */
    public void detach() {
        activity = null;
        editText = null;
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        numberTv1 = findViewById(R.id.number_tv_1);
        numberTv2 = findViewById(R.id.number_tv_2);
        numberTv3 = findViewById(R.id.number_tv_3);
        numberTv4 = findViewById(R.id.number_tv_4);
        numberTv5 = findViewById(R.id.number_tv_5);
        numberTv6 = findViewById(R.id.number_tv_6);
        numberTv7 = findViewById(R.id.number_tv_7);
        numberTv8 = findViewById(R.id.number_tv_8);
        numberTv9 = findViewById(R.id.number_tv_9);
        numberTv0 = findViewById(R.id.number_tv_0);
        numberTvClear = findViewById(R.id.number_tv_clear);
        numberTvDelete = findViewById(R.id.number_tv_delete);
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        numberTv1.setOnClickListener(onClickListener);
        numberTv2.setOnClickListener(onClickListener);
        numberTv3.setOnClickListener(onClickListener);
        numberTv4.setOnClickListener(onClickListener);
        numberTv5.setOnClickListener(onClickListener);
        numberTv6.setOnClickListener(onClickListener);
        numberTv7.setOnClickListener(onClickListener);
        numberTv8.setOnClickListener(onClickListener);
        numberTv9.setOnClickListener(onClickListener);
        numberTv0.setOnClickListener(onClickListener);
        numberTvClear.setOnClickListener(onClickListener);
        numberTvDelete.setOnClickListener(onClickListener);
        numberTvDelete.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DebugUtil.warnOut(TAG, "deleteTv onLongClick");
                startAutoDeleteTimer();
                return true;
            }
        });
        numberTvDelete.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopAutoDeleteTimer();
                }
                return false;
            }
        });
    }

    private void stopAutoDeleteTimer() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdownNow();
        }
        scheduledExecutorService = null;
    }

    private void startAutoDeleteTimer() {
        stopAutoDeleteTimer();
        scheduledExecutorService = BaseManager.newScheduledExecutorService(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                BaseManager.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        pressKey(KeyEvent.KEYCODE_DEL);
                    }
                });
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * 触发按键
     *
     * @param keycode 按键码
     */
    private void pressKey(int keycode) {
        if (activity != null) {
            activity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
            activity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keycode));
        }
    }
}
