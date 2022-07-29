package com.sscl.basesample

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.receiver.ScreenStatusReceiver.OnScreenStatusChangedListener
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.utils.SharedPreferencesTools
import com.sscl.baselibrary.utils.Tool
import com.sscl.basesample.activities.WidgetActivity
import com.sscl.basesample.activities.sample.*

/**
 * @author jacke
 */
class MainActivity : BaseAppCompatActivity() {
    private lateinit var baseAppcompatActivityBtn: Button
    private lateinit var baseDrawerActivityBtn: Button
    private lateinit var baseFragmentBtn: Button
    private lateinit var basePopupWindowBtn: Button
    private lateinit var allPurposeAdapterBtn: Button
    private lateinit var homeWatcherBtn: Button
    private lateinit var toastTestBtn: Button
    private lateinit var widgetBtn: Button
    private lateinit var selectFileBtn: Button
    private lateinit var bannerBtn: Button
    private lateinit var webViewBtn: Button
    private lateinit var numberInputMethodBtn: Button
    private lateinit var customPasswordViewBtn: Button
    private lateinit var zipFileOperationBtn: Button
    private lateinit var animationTestBtn: Button
    private lateinit var sdcardFileTestBtn: Button
    private lateinit var sampleDataBindingBtn: Button

    private val onClickListener = View.OnClickListener { view: View ->
        var intent: Intent? = null
        when (view.id) {
            R.id.all_purpose_adapter -> {
                intent = Intent(this@MainActivity, AllPurposeAdapterActivity::class.java)
            }
            R.id.base_appcompat_activity -> {
                intent = Intent(this@MainActivity, SampleBaseAppcompatActivity::class.java)
            }
            R.id.base_drawer_activity -> {
                intent = Intent(this@MainActivity, SampleBaseDrawerActivity::class.java)
            }
            R.id.base_fragment -> {
                intent = Intent(this@MainActivity, SampleBaseFragmentActivity::class.java)
            }
            R.id.base_popup_window -> {
                intent = Intent(this@MainActivity, SampleBasePopupWindowActivity::class.java)
            }
            R.id.home_watcher -> {
                intent = Intent(this@MainActivity, HomeWatcherActivity::class.java)
            }
            R.id.toast_test -> {
                intent = Intent(this@MainActivity, ToastTestActivity::class.java)
            }
            R.id.title_left_text -> {
                DebugUtil.warnOut(TAG, "文字左被点击")
            }
            R.id.title_right_text -> {
                DebugUtil.warnOut(TAG, "文字右被点击")
            }
            R.id.widget -> {
                intent = Intent(this@MainActivity, WidgetActivity::class.java)
            }
            R.id.select_file -> {
                intent = Intent(this@MainActivity, SelectFileActivity::class.java)
            }
            R.id.banner -> {
                intent = Intent(this@MainActivity, SampleNewBannerActivity::class.java)
            }
            R.id.web_view -> {
                intent = Intent(this@MainActivity, SampleWebViewActivity::class.java)
            }
            numberInputMethodBtn.id -> {
                intent = Intent(this@MainActivity, NumberInputMethodActivity::class.java)
            }
            customPasswordViewBtn.id -> {
                intent = Intent(this@MainActivity, CustomPasswordViewActivity::class.java)
            }
            zipFileOperationBtn.id -> {
                intent = Intent(this@MainActivity, ZipFileOperationActivity::class.java)
            }
            animationTestBtn.id -> {
                intent = Intent(this@MainActivity, AnimationSampleActivity::class.java)
            }
            sdcardFileTestBtn.id -> {
                intent = Intent(this@MainActivity, SdcardFileTestActivity::class.java)
            }
            sampleDataBindingBtn.id -> {
                intent = Intent(this@MainActivity, SampleDataBindingActivity::class.java)
            }
        }
        if (intent != null) {
            startActivity(intent)
        }
    }
    private val onScreenStatusChangedListener: OnScreenStatusChangedListener =
        object : OnScreenStatusChangedListener {
            override fun onScreenOn() {
                DebugUtil.warnOut(TAG, "屏幕开启")
            }

            override fun onScreenOff() {
                DebugUtil.warnOut(TAG, "屏幕关闭")
            }

            override fun onUserUnlock() {
                DebugUtil.warnOut(TAG, "用户解锁完成")
            }
        }

    override fun titleBackClicked(): Boolean {
        return false
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    override fun doBeforeSetLayout() {}

    /**
     * 设置布局
     *
     * @return 布局id
     */
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_main
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {
        hideTitleBackButton()
    }

    /**
     * 初始化布局控件
     */
    override fun initViews() {
        baseAppcompatActivityBtn = findViewById(R.id.base_appcompat_activity)
        baseDrawerActivityBtn = findViewById(R.id.base_drawer_activity)
        baseFragmentBtn = findViewById(R.id.base_fragment)
        basePopupWindowBtn = findViewById(R.id.base_popup_window)
        allPurposeAdapterBtn = findViewById(R.id.all_purpose_adapter)
        homeWatcherBtn = findViewById(R.id.home_watcher)
        toastTestBtn = findViewById(R.id.toast_test)
        widgetBtn = findViewById(R.id.widget)
        selectFileBtn = findViewById(R.id.select_file)
        bannerBtn = findViewById(R.id.banner)
        webViewBtn = findViewById(R.id.web_view)
        numberInputMethodBtn = findViewById(R.id.number_input_method_btn)
        customPasswordViewBtn = findViewById(R.id.custom_password_view_btn)
        zipFileOperationBtn = findViewById(R.id.zip_file_operation_btn)
        animationTestBtn = findViewById(R.id.animation_test_btn)
        sdcardFileTestBtn = findViewById(R.id.sdcard_file_test_btn)
        sampleDataBindingBtn = findViewById(R.id.sample_data_binding_btn)
    }

    /**
     * 初始化控件数据
     */
    override fun initViewData() {}

    /**
     * 初始化其他数据
     */
    override fun initOtherData() {}

    /**
     * 初始化事件
     */
    override fun initEvents() {
        baseAppcompatActivityBtn.setOnClickListener(onClickListener)
        baseDrawerActivityBtn.setOnClickListener(onClickListener)
        baseFragmentBtn.setOnClickListener(onClickListener)
        basePopupWindowBtn.setOnClickListener(onClickListener)
        allPurposeAdapterBtn.setOnClickListener(onClickListener)
        homeWatcherBtn.setOnClickListener(onClickListener)
        toastTestBtn.setOnClickListener(onClickListener)
        widgetBtn.setOnClickListener(onClickListener)
        selectFileBtn.setOnClickListener(onClickListener)
        bannerBtn.setOnClickListener(onClickListener)
        webViewBtn.setOnClickListener(onClickListener)
        numberInputMethodBtn.setOnClickListener(onClickListener)
        customPasswordViewBtn.setOnClickListener(onClickListener)
        zipFileOperationBtn.setOnClickListener(onClickListener)
        animationTestBtn.setOnClickListener(onClickListener)
        sdcardFileTestBtn.setOnClickListener(onClickListener)
        sampleDataBindingBtn.setOnClickListener(onClickListener)
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {
        Tool.startScreenStatusListener(this)
        Tool.setOnScreenStatusChangedListener(onScreenStatusChangedListener)
        SharedPreferencesTools.getInstance(this, "Test").putValueApply("boolean", true)
        val value: Boolean =
            SharedPreferencesTools.getInstance(this, "Test").getValue("booleana", false)
        DebugUtil.warnOut(TAG, "value = $value")
    }

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    override fun createOptionsMenu(menu: Menu): Boolean {
        return false
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    override fun optionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        Tool.stopScreenStatusListener(this)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}