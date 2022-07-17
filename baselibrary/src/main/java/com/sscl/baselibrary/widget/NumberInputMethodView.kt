package com.sscl.baselibrary.widget

import android.content.*
import android.util.AttributeSet
import android.view.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import com.sscl.baselibrary.utils.DebugUtil
import kotlin.jvm.JvmOverloads
import android.app.Activity
import com.sscl.baselibrary.R
import com.sscl.baselibrary.utils.BaseManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * 数字键盘
 *
 * @author jackie
 */
class NumberInputMethodView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * Activity
     */
    private var activity: Activity? = null

    /**
     * 输入框
     */
    private var editText: EditText? = null

    /**
     * 数字1，2，3，4，5，6，7，8，9，0
     */
    private var numberTv1: TextView? = null
    private var numberTv2: TextView? = null
    private var numberTv3: TextView? = null
    private var numberTv4: TextView? = null
    private var numberTv5: TextView? = null
    private var numberTv6: TextView? = null
    private var numberTv7: TextView? = null
    private var numberTv8: TextView? = null
    private var numberTv9: TextView? = null
    private var numberTv0: TextView? = null

    /**
     * 清空
     */
    private var numberTvClear: TextView? = null

    /**
     * 删除
     */
    private var numberTvDelete: RelativeLayout? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private val onClickListener: OnClickListener = OnClickListener { v ->
        val id: Int = v.getId()
        if (id == numberTv1?.getId()) {
            pressKey(KeyEvent.KEYCODE_1)
        } else if (id == numberTv2?.id) {
            pressKey(KeyEvent.KEYCODE_2)
        } else if (id == numberTv3?.id) {
            pressKey(KeyEvent.KEYCODE_3)
        } else if (id == numberTv4?.id) {
            pressKey(KeyEvent.KEYCODE_4)
        } else if (id == numberTv5?.id) {
            pressKey(KeyEvent.KEYCODE_5)
        } else if (id == numberTv6?.id) {
            pressKey(KeyEvent.KEYCODE_6)
        } else if (id == numberTv7?.id) {
            pressKey(KeyEvent.KEYCODE_7)
        } else if (id == numberTv8?.id) {
            pressKey(KeyEvent.KEYCODE_8)
        } else if (id == numberTv9?.id) {
            pressKey(KeyEvent.KEYCODE_9)
        } else if (id == numberTv0?.id) {
            pressKey(KeyEvent.KEYCODE_0)
        } else if (id == numberTvDelete?.id) {
            pressKey(KeyEvent.KEYCODE_DEL)
        } else if (id == numberTvClear?.id) {
                editText?.setText("")
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private var scheduledExecutorService: ScheduledExecutorService? = null

    /**
     * 绑定Activity
     *
     * @param activity activity
     */
    fun attach(activity: Activity?, editText: EditText?) {
        this.activity = activity
        this.editText = editText
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 解绑Activity
     */
    fun detach() {
        activity = null
        editText = null
    }

    /**
     * 初始化控件
     */
    private fun initViews() {
        numberTv1 = findViewById(R.id.number_tv_1)
        numberTv2 = findViewById(R.id.number_tv_2)
        numberTv3 = findViewById(R.id.number_tv_3)
        numberTv4 = findViewById(R.id.number_tv_4)
        numberTv5 = findViewById(R.id.number_tv_5)
        numberTv6 = findViewById(R.id.number_tv_6)
        numberTv7 = findViewById(R.id.number_tv_7)
        numberTv8 = findViewById(R.id.number_tv_8)
        numberTv9 = findViewById(R.id.number_tv_9)
        numberTv0 = findViewById(R.id.number_tv_0)
        numberTvClear = findViewById(R.id.number_tv_clear)
        numberTvDelete = findViewById(R.id.number_tv_delete)
    }

    /**
     * 初始化事件
     */
    private fun initEvents() {
        numberTv1?.setOnClickListener(onClickListener)
        numberTv2?.setOnClickListener(onClickListener)
        numberTv3?.setOnClickListener(onClickListener)
        numberTv4?.setOnClickListener(onClickListener)
        numberTv5?.setOnClickListener(onClickListener)
        numberTv6?.setOnClickListener(onClickListener)
        numberTv7?.setOnClickListener(onClickListener)
        numberTv8?.setOnClickListener(onClickListener)
        numberTv9?.setOnClickListener(onClickListener)
        numberTv0?.setOnClickListener(onClickListener)
        numberTvClear?.setOnClickListener(onClickListener)
        numberTvDelete?.setOnClickListener(onClickListener)
        numberTvDelete?.setOnLongClickListener(object : OnLongClickListener {
            public override fun onLongClick(v: View): Boolean {
                DebugUtil.warnOut(TAG, "deleteTv onLongClick")
                startAutoDeleteTimer()
                return true
            }
        })
        numberTvDelete?.setOnTouchListener(object : OnTouchListener {
            public override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopAutoDeleteTimer()
                }
                return false
            }
        })
    }

    private fun stopAutoDeleteTimer() {
            scheduledExecutorService?.shutdownNow()
        scheduledExecutorService = null
    }

    private fun startAutoDeleteTimer() {
        stopAutoDeleteTimer()
        scheduledExecutorService = BaseManager.newScheduledExecutorService(1)
        scheduledExecutorService?.scheduleAtFixedRate({
            BaseManager.handler.post { pressKey(KeyEvent.KEYCODE_DEL) }
        }, 0, 100, TimeUnit.MILLISECONDS)
    }

    /**
     * 触发按键
     *
     * @param keycode 按键码
     */
    private fun pressKey(keycode: Int) {
            activity?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keycode))
            activity?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keycode))
    }

    companion object {
        private val TAG: String = NumberInputMethodView::class.java.getSimpleName()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    init {
        inflate(context, R.layout.layout_number_input_method, this)
        initViews()
        initEvents()
    }
}