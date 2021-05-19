package com.sscl.baselibrary.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
 * 字母与数字的组合键盘
 *
 * @author jackie
 */
public class LetterInputMethodView extends FrameLayout {

    private static final String TAG = LetterInputMethodView.class.getSimpleName();

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 自动删除文本的定时器
     */
    private ScheduledExecutorService autoDeleteTimer;

    /**
     * Activity
     */
    private Activity activity;
    /**
     * 显示键盘的容器
     */
    private LinearLayout keyboardContent;

    /**
     * 数字键 1，2，3，4，5，6，7，8，9，0
     */
    private TextView numberTv1, numberTv2, numberTv3, numberTv4, numberTv5, numberTv6, numberTv7, numberTv8, numberTv9, numberTv0;
    /**
     * 字母 Q,W,E,R,T,Y,U,I,O,P
     */
    private TextView letterTvQ, letterTvW, letterTvE, letterTvR, letterTvT, letterTvY, letterTvU, letterTvI, letterTvO, letterTvP;
    /**
     * 字母 A,S,D,F,G,H,J,K,L
     */
    private TextView letterTvA, letterTvS, letterTvD, letterTvF, letterTvG, letterTvH, letterTvJ, letterTvK, letterTvL;
    /**
     * 字母 Z,X,C,V,B,N,M
     */
    private TextView letterTvZ, letterTvX, letterTvC, letterTvV, letterTvB, letterTvN, letterTvM;
    /**
     * 清空
     */
    private CheckBox capslockCb;
    /**
     * 删除
     */
    private RelativeLayout deleteTv;
    /**
     * 显示键盘的按钮
     */
    private CheckBox showKeyboardCb;
    /**
     * 显示键盘状态的文本
     */
    private TextView showKeyboardHintTv;
    /**
     * 显示键盘的按钮的容器
     */
    private RelativeLayout showKeyboardContent;

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
            } else if (id == letterTvQ.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_Q);
            } else if (id == letterTvW.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_W);
            } else if (id == letterTvE.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_E);
            } else if (id == letterTvR.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_R);
            } else if (id == letterTvT.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_T);
            } else if (id == letterTvY.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_Y);
            } else if (id == letterTvU.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_U);
            } else if (id == letterTvI.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_I);
            } else if (id == letterTvO.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_O);
            } else if (id == letterTvP.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_P);
            } else if (id == letterTvA.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_A);
            } else if (id == letterTvS.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_S);
            } else if (id == letterTvD.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_D);
            } else if (id == letterTvF.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_F);
            } else if (id == letterTvG.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_G);
            } else if (id == letterTvH.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_H);
            } else if (id == letterTvJ.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_J);
            } else if (id == letterTvK.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_K);
            } else if (id == letterTvL.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_L);
            } else if (id == letterTvZ.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_Z);
            } else if (id == letterTvX.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_X);
            } else if (id == letterTvC.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_C);
            } else if (id == letterTvV.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_V);
            } else if (id == letterTvB.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_B);
            } else if (id == letterTvN.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_N);
            } else if (id == letterTvM.getId()) {
                pressLetterKey(KeyEvent.KEYCODE_M);
            } else if (id == deleteTv.getId()) {
                pressKey(KeyEvent.KEYCODE_DEL);
            } else if (id == showKeyboardContent.getId()) {
                showKeyboardCb.setChecked(!showKeyboardCb.isChecked());
            }
        }
    };

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public LetterInputMethodView(@NonNull Context context) {
        this(context, null);
    }

    public LetterInputMethodView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterInputMethodView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_letter_input_method, this);
        initViews();
        initEvents();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 绑定
     *
     * @param activity Activity
     */
    public void attach(@NonNull Activity activity) {
        this.activity = activity;
    }

    /**
     * 解绑
     */
    public void detach() {
        activity = null;
    }

    public void showHideKeyboardBtn(boolean visible) {
        if (visible) {
            showKeyboardContent.setVisibility(VISIBLE);
        } else {
            showKeyboardContent.setVisibility(GONE);
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 初始化视图控件
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

        letterTvQ = findViewById(R.id.letter_tv_q);
        letterTvW = findViewById(R.id.letter_tv_w);
        letterTvE = findViewById(R.id.letter_tv_e);
        letterTvR = findViewById(R.id.letter_tv_r);
        letterTvT = findViewById(R.id.letter_tv_t);
        letterTvY = findViewById(R.id.letter_tv_y);
        letterTvU = findViewById(R.id.letter_tv_u);
        letterTvI = findViewById(R.id.letter_tv_i);
        letterTvO = findViewById(R.id.letter_tv_o);
        letterTvP = findViewById(R.id.letter_tv_p);

        letterTvA = findViewById(R.id.letter_tv_a);
        letterTvS = findViewById(R.id.letter_tv_s);
        letterTvD = findViewById(R.id.letter_tv_d);
        letterTvF = findViewById(R.id.letter_tv_f);
        letterTvG = findViewById(R.id.letter_tv_g);
        letterTvH = findViewById(R.id.letter_tv_h);
        letterTvJ = findViewById(R.id.letter_tv_j);
        letterTvK = findViewById(R.id.letter_tv_k);
        letterTvL = findViewById(R.id.letter_tv_l);

        letterTvZ = findViewById(R.id.letter_tv_z);
        letterTvX = findViewById(R.id.letter_tv_x);
        letterTvC = findViewById(R.id.letter_tv_c);
        letterTvV = findViewById(R.id.letter_tv_v);
        letterTvB = findViewById(R.id.letter_tv_b);
        letterTvN = findViewById(R.id.letter_tv_n);
        letterTvM = findViewById(R.id.letter_tv_m);

        deleteTv = findViewById(R.id.letter_tv_delete);
        capslockCb = findViewById(R.id.letter_tv_capslock);

        showKeyboardCb = findViewById(R.id.show_keyboard_btn);
        showKeyboardHintTv = findViewById(R.id.show_keyboard_hint_tv);
        keyboardContent = findViewById(R.id.key_board_content);
        showKeyboardContent = findViewById(R.id.show_keyboard_content);
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

        letterTvQ.setOnClickListener(onClickListener);
        letterTvW.setOnClickListener(onClickListener);
        letterTvE.setOnClickListener(onClickListener);
        letterTvR.setOnClickListener(onClickListener);
        letterTvT.setOnClickListener(onClickListener);
        letterTvY.setOnClickListener(onClickListener);
        letterTvU.setOnClickListener(onClickListener);
        letterTvI.setOnClickListener(onClickListener);
        letterTvO.setOnClickListener(onClickListener);
        letterTvP.setOnClickListener(onClickListener);

        letterTvA.setOnClickListener(onClickListener);
        letterTvS.setOnClickListener(onClickListener);
        letterTvD.setOnClickListener(onClickListener);
        letterTvF.setOnClickListener(onClickListener);
        letterTvG.setOnClickListener(onClickListener);
        letterTvH.setOnClickListener(onClickListener);
        letterTvJ.setOnClickListener(onClickListener);
        letterTvK.setOnClickListener(onClickListener);
        letterTvL.setOnClickListener(onClickListener);

        letterTvZ.setOnClickListener(onClickListener);
        letterTvX.setOnClickListener(onClickListener);
        letterTvC.setOnClickListener(onClickListener);
        letterTvV.setOnClickListener(onClickListener);
        letterTvB.setOnClickListener(onClickListener);
        letterTvN.setOnClickListener(onClickListener);
        letterTvM.setOnClickListener(onClickListener);

        deleteTv.setOnClickListener(onClickListener);

        showKeyboardContent.setOnClickListener(onClickListener);

        deleteTv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DebugUtil.warnOut(TAG, "deleteTv onLongClick");
                startAutoDeleteTimer();
                return true;
            }
        });
        deleteTv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopAutoDeleteTimer();
                }
                return false;
            }
        });
        capslockCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showUpperText();
                } else {
                    showLowerText();
                }
            }
        });

        showKeyboardCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    keyboardContent.setVisibility(GONE);
                    showKeyboardHintTv.setText("弹出键盘");
                } else {
                    keyboardContent.setVisibility(VISIBLE);
                    showKeyboardHintTv.setText("隐藏键盘");
                }
            }
        });
    }

    /**
     * 显示大写文本
     */
    private void showUpperText() {
        letterTvQ.setText(letterTvQ.getText().toString().toUpperCase());
        letterTvW.setText(letterTvW.getText().toString().toUpperCase());
        letterTvE.setText(letterTvE.getText().toString().toUpperCase());
        letterTvR.setText(letterTvR.getText().toString().toUpperCase());
        letterTvT.setText(letterTvT.getText().toString().toUpperCase());
        letterTvY.setText(letterTvY.getText().toString().toUpperCase());
        letterTvU.setText(letterTvU.getText().toString().toUpperCase());
        letterTvI.setText(letterTvI.getText().toString().toUpperCase());
        letterTvO.setText(letterTvO.getText().toString().toUpperCase());
        letterTvP.setText(letterTvP.getText().toString().toUpperCase());

        letterTvA.setText(letterTvA.getText().toString().toUpperCase());
        letterTvS.setText(letterTvS.getText().toString().toUpperCase());
        letterTvD.setText(letterTvD.getText().toString().toUpperCase());
        letterTvF.setText(letterTvF.getText().toString().toUpperCase());
        letterTvG.setText(letterTvG.getText().toString().toUpperCase());
        letterTvH.setText(letterTvH.getText().toString().toUpperCase());
        letterTvJ.setText(letterTvJ.getText().toString().toUpperCase());
        letterTvK.setText(letterTvK.getText().toString().toUpperCase());
        letterTvL.setText(letterTvL.getText().toString().toUpperCase());

        letterTvZ.setText(letterTvZ.getText().toString().toUpperCase());
        letterTvX.setText(letterTvX.getText().toString().toUpperCase());
        letterTvC.setText(letterTvC.getText().toString().toUpperCase());
        letterTvV.setText(letterTvV.getText().toString().toUpperCase());
        letterTvB.setText(letterTvB.getText().toString().toUpperCase());
        letterTvN.setText(letterTvN.getText().toString().toUpperCase());
        letterTvM.setText(letterTvM.getText().toString().toUpperCase());
    }

    /**
     * 显示小写文本
     */
    private void showLowerText() {
        letterTvQ.setText(letterTvQ.getText().toString().toLowerCase());
        letterTvW.setText(letterTvW.getText().toString().toLowerCase());
        letterTvE.setText(letterTvE.getText().toString().toLowerCase());
        letterTvR.setText(letterTvR.getText().toString().toLowerCase());
        letterTvT.setText(letterTvT.getText().toString().toLowerCase());
        letterTvY.setText(letterTvY.getText().toString().toLowerCase());
        letterTvU.setText(letterTvU.getText().toString().toLowerCase());
        letterTvI.setText(letterTvI.getText().toString().toLowerCase());
        letterTvO.setText(letterTvO.getText().toString().toLowerCase());
        letterTvP.setText(letterTvP.getText().toString().toLowerCase());

        letterTvA.setText(letterTvA.getText().toString().toLowerCase());
        letterTvS.setText(letterTvS.getText().toString().toLowerCase());
        letterTvD.setText(letterTvD.getText().toString().toLowerCase());
        letterTvF.setText(letterTvF.getText().toString().toLowerCase());
        letterTvG.setText(letterTvG.getText().toString().toLowerCase());
        letterTvH.setText(letterTvH.getText().toString().toLowerCase());
        letterTvJ.setText(letterTvJ.getText().toString().toLowerCase());
        letterTvK.setText(letterTvK.getText().toString().toLowerCase());
        letterTvL.setText(letterTvL.getText().toString().toLowerCase());

        letterTvZ.setText(letterTvZ.getText().toString().toLowerCase());
        letterTvX.setText(letterTvX.getText().toString().toLowerCase());
        letterTvC.setText(letterTvC.getText().toString().toLowerCase());
        letterTvV.setText(letterTvV.getText().toString().toLowerCase());
        letterTvB.setText(letterTvB.getText().toString().toLowerCase());
        letterTvN.setText(letterTvN.getText().toString().toLowerCase());
        letterTvM.setText(letterTvM.getText().toString().toLowerCase());
    }

    /**
     * 停止自动删除的定时器
     */
    private void stopAutoDeleteTimer() {
        DebugUtil.warnOut(TAG, "stopAutoDeleteTimer");
        if (autoDeleteTimer != null && !autoDeleteTimer.isShutdown()) {
            autoDeleteTimer.shutdownNow();
        }
        autoDeleteTimer = null;
    }

    /**
     * 开启自动删除的定时器
     */
    private void startAutoDeleteTimer() {
        stopAutoDeleteTimer();
        autoDeleteTimer = BaseManager.newScheduledExecutorService(1);
        autoDeleteTimer.scheduleAtFixedRate(new Runnable() {
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
     * 触发数字按键
     *
     * @param keycode 按键码
     */
    private synchronized void pressKey(int keycode) {
        if (activity != null) {
            activity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
            activity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keycode));
        }
    }

    /**
     * 触发小写字母按键
     *
     * @param keycode 按键码
     */
    private synchronized void pressLetterKey(int keycode) {
        if (!capslockCb.isChecked()) {
            pressKey(keycode);
        } else {
            pressUpperLetterKey(keycode);
        }
    }

    /**
     * 触发大写字母按键
     *
     * @param keycode 按键码
     */
    private void pressUpperLetterKey(int keycode) {
        @SuppressLint("HardwareIds") KeyEvent keyEvent = new KeyEvent(System.currentTimeMillis(), String.valueOf(((char) (keycode + 36))), 1, KeyEvent.FLAG_VIRTUAL_HARD_KEY);
        activity.dispatchKeyEvent(keyEvent);
    }

    public void showKeyboard() {

    }
}
