package com.sscl.basesample.activities.sample

import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.files.FileSystemUtil
import com.sscl.baselibrary.files.FileSystemUtil.onStartActivity
import com.sscl.baselibrary.files.FileSystemUtil.openSystemFile
import com.sscl.baselibrary.files.FileSystemUtil.setOnFileSelectedListener
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.utils.ToastUtil
import com.sscl.basesample.R
import java.io.File

/**
 * @author jackie
 */
class SelectFileActivity : BaseAppCompatActivity() {
    /**
     * 选择任意文件
     */
    private var selectAnyFileBtn: Button? = null

    /**
     * 选择文本文件
     */
    private var selectTextFileBtn: Button? = null

    /**
     * 选择二进制文件
     */
    private var selectBinaryFileBtn: Button? = null

    /**
     * 点击事件的监听
     */
    private val onClickListener = View.OnClickListener { v: View ->
        when (v.id) {
            R.id.select_any_file -> {
                selectAnyFile()
            }
            R.id.select_text_file -> {
                selectTextFile()
            }
            R.id.select_binary_file -> {
                selectBinaryFile()
            }
            R.id.select_image_file -> {
                selectImageFile()
            }
        }
    }
    private var requestCode = 0
    private val onFileSelectedListener: FileSystemUtil.OnFragmentActivityFileSelectedListener =
        object : FileSystemUtil.OnFragmentActivityFileSelectedListener {
            override fun fileSelected(
                resultCode: Int,
                uri: Uri?,
                filePath: String?
            ) {
                when (requestCode) {
                    SELECT_ANY_FILE -> showDialog(
                        getString(R.string.com_sscl_basesample_select_any_file),
                        filePath,
                        uri
                    )
                    SELECT_TEXT_FILE -> showDialog(
                        getString(R.string.com_sscl_basesample_select_text_file),
                        filePath,
                        uri
                    )
                    SELECT_BINARY_FILE -> showDialog(
                        getString(R.string.com_sscl_basesample_select_binary_file),
                        filePath,
                        uri
                    )
                    SELECT_IMAGE_FILE -> showDialog(
                        getString(R.string.com_sscl_basesample_select_image_file),
                        filePath,
                        uri
                    )
                    else -> {}
                }
            }
        }

    /**
     * 选择图片文件
     */
    private var selectImageFileBtn: Button? = null

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
    override fun titleBackClicked(): Boolean {
        return false
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    override fun doBeforeSetLayout() {
        intentData
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_select_file
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {}

    /**
     * 初始化布局控件
     */
    override fun initViews() {
        selectAnyFileBtn = findViewById(R.id.select_any_file)
        selectTextFileBtn = findViewById(R.id.select_text_file)
        selectBinaryFileBtn = findViewById(R.id.select_binary_file)
        selectImageFileBtn = findViewById(R.id.select_image_file)
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
        selectAnyFileBtn!!.setOnClickListener(onClickListener)
        selectTextFileBtn!!.setOnClickListener(onClickListener)
        selectBinaryFileBtn!!.setOnClickListener(onClickListener)
        selectImageFileBtn!!.setOnClickListener(onClickListener)
        setOnFileSelectedListener(onFileSelectedListener)
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {}

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

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getIntentData(intent)
    }

    override fun onStart() {
        onStartActivity()
        super.onStart()
    }

    private fun selectAnyFile() {
        requestCode = SELECT_ANY_FILE
        val succeed: Boolean = openSystemFile()
        if (!succeed) {
            ToastUtil.toastLong(this, R.string.com_sscl_basesample_open_file_manager_failed)
        }
    }

    private fun selectTextFile() {
        requestCode = SELECT_TEXT_FILE
        val succeed: Boolean =
            openSystemFile(FileSystemUtil.FileType.TEXT_FILE)
        if (!succeed) {
            ToastUtil.toastLong(this, R.string.com_sscl_basesample_open_file_manager_failed)
        }
    }

    private fun selectBinaryFile() {
        requestCode = SELECT_BINARY_FILE
        val succeed: Boolean = openSystemFile(
            FileSystemUtil.FileType.OCTET_STREAM_FILE
        )
        if (!succeed) {
            ToastUtil.toastLong(this, R.string.com_sscl_basesample_open_file_manager_failed)
        }
    }

    private fun selectImageFile() {
        requestCode = SELECT_IMAGE_FILE
        val succeed: Boolean = openSystemFile(FileSystemUtil.FileType.IMAGE_FILE)
        if (!succeed) {
            ToastUtil.toastLong(this, R.string.com_sscl_basesample_open_file_manager_failed)
        }
    }

    /**
     * 显示对话框
     *
     * @param title    对话框标题
     * @param filePath 文件路径
     * @param uri      文件URI
     */
    private fun showDialog(title: String, filePath: String?, uri: Uri?) {
        val message = "filePath = $filePath\nuri = $uri"
        if (filePath != null) {
            val file = File(filePath)
            DebugUtil.warnOut(TAG, "file exists " + file.exists())
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.com_sscl_basesample_confirm, null)
            .show()
    }

    private val intentData: Unit
        get() {
            val intent: Intent = intent
            val dataString: String? = intent.dataString
            DebugUtil.warnOut(TAG, "dataString = $dataString")
            if (dataString == null) {
                return
            }
            val parse = Uri.parse(dataString)
            val path: String? = FileSystemUtil.getPath(this, parse)
            DebugUtil.warnOut(TAG, "path = $path")
        }

    private fun getIntentData(intent: Intent) {
        val dataString: String? = intent.dataString
        DebugUtil.warnOut(TAG, "dataString = $dataString")
        if (dataString == null) {
            return
        }
        val parse = Uri.parse(dataString)
        val path: String? = FileSystemUtil.getPath(this, parse)
        DebugUtil.warnOut(TAG, "path = $path")
    }

    companion object {
        private const val SELECT_ANY_FILE = 1
        private const val SELECT_TEXT_FILE = 2
        private const val SELECT_BINARY_FILE = 3
        private const val SELECT_IMAGE_FILE = 4
    }
}