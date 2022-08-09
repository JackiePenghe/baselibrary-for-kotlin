package com.sscl.basesample.activities.sample

import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.files.FileSystemUtil
import com.sscl.baselibrary.files.FileSystemUtil.onStartActivity
import com.sscl.baselibrary.files.FileSystemUtil.openSystemFile
import com.sscl.baselibrary.files.FileSystemUtil.setOnFileSelectedListener
import com.sscl.baselibrary.files.FileUtil
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.utils.ToastUtil
import com.sscl.baselibrary.utils.ZipUtils
import com.sscl.basesample.R
import java.io.File

/**
 * zip文件操作界面
 *
 * @author pengh
 */
class ZipFileOperationActivity : BaseAppCompatActivity() {
    /**
     * 设置布局
     *
     * @return 布局id
     */
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_zip_file_operation
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 文件URI
     */
    private var fileUri: Uri? = null

    /**
     * 文件路径
     */
    private var filePath: String? = null

    /**
     * 显示文件路劲的文本
     */
    private lateinit var fileNameTv: TextView

    /**
     * 选择文件
     */
    private lateinit var selectFileBtn: Button

    /**
     * 列出压缩包内的文件列表
     */
    private lateinit var listFilesBtn: Button

    /**
     * 解压文件
     */
    private lateinit var unzipFileBtn: Button

    /**
     * 文件
     */
    private val onFileSelectedListener: FileSystemUtil.OnFragmentActivityFileSelectedListener =
        object :
            FileSystemUtil.OnFragmentActivityFileSelectedListener {
            override fun fileSelected(
                resultCode: Int,
                uri: Uri?,
                filePath: String?
            ) {
                if (uri == null || filePath == null) {
                    ToastUtil.toastLong(
                        this@ZipFileOperationActivity,
                        R.string.com_sscl_basesample_no_file_selected
                    )
                    return
                }
                fileNameTv.text = filePath
                fileUri = uri
                this@ZipFileOperationActivity.filePath = filePath
            }
        }

    /**
     * 点击事件的处理
     */
    private val onClickListener: View.OnClickListener = View.OnClickListener { v ->
        when (v.id) {
            selectFileBtn.id -> {
                selectFile()
            }
            listFilesBtn.id -> {
                listFiles()
            }
            unzipFileBtn.id -> {
                unzipFile()
            }
        }
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
    override fun titleBackClicked(): Boolean {
        return false
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    override fun doBeforeSetLayout() {}

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {}

    /**
     * 初始化布局控件
     */
    override fun initViews() {
        selectFileBtn = findViewById(R.id.select_file_btn)
        listFilesBtn = findViewById(R.id.list_files_btn)
        fileNameTv = findViewById(R.id.file_name_tv)
    }

    /**
     * 初始化控件数据
     */
    override fun initViewData() {
        unzipFileBtn = findViewById(R.id.unzip_file_btn)
    }

    /**
     * 初始化其他数据
     */
    override fun initOtherData() {}

    /**
     * 初始化事件
     */
    override fun initEvents() {
        selectFileBtn.setOnClickListener(onClickListener)
        listFilesBtn.setOnClickListener(onClickListener)
        unzipFileBtn.setOnClickListener(onClickListener)
        setOnFileSelectedListener(onFileSelectedListener)
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {}

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onStart() {
        onStartActivity()
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        setOnFileSelectedListener(null as FileSystemUtil.OnFragmentActivityFileSelectedListener?)
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 选择文件
     */
    private fun selectFile() {
        val b: Boolean =
            openSystemFile(FileSystemUtil.FileType.ZIP_FILE)
        if (!b) {
            ToastUtil.toastLong(this, R.string.com_sscl_basesample_open_file_manager_failed)
        }
    }

    /**
     * 列出压缩包中的文件列表
     */
    private fun listFiles() {
        if (fileUri == null) {
            ToastUtil.toastLong(this, R.string.com_sscl_basesample_no_file_selected)
            return
        }
        if (filePath == null) {
            ToastUtil.toastLong(this, R.string.com_sscl_basesample_no_file_selected)
            return
        }
        val fileUri = fileUri ?: return
        DebugUtil.warnOut(TAG, "fileUri:$fileUri")
        val filePath = filePath ?: return
        val entriesNames = ZipUtils.getEntriesNamesNew(File(filePath))
        showFileListDialog(entriesNames)
    }

    /**
     * 显示文件列表的对话框
     *
     * @param entriesNames 文件列表
     */
    private fun showFileListDialog(entriesNames: ArrayList<String>?) {
        val items: Array<String?> = if (entriesNames != null) {
            val cache = arrayOfNulls<String>(entriesNames.size)
            entriesNames.toArray(cache)
        } else {
            arrayOf("空文件列表")
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.com_sscl_basesample_file_list)
            .setItems(items, null)
            .setCancelable(false)
            .setPositiveButton(R.string.com_sscl_basesample_confirm, null)
            .show()
    }

    /**
     * 解压文件
     */
    private fun unzipFile() {
        if (fileUri == null) {
            ToastUtil.toastLong(this, R.string.com_sscl_basesample_no_file_selected)
            return
        }
        if (filePath == null) {
            ToastUtil.toastLong(this, R.string.com_sscl_basesample_no_file_selected)
            return
        }
        val fileUri = fileUri ?: return
        DebugUtil.warnOut(TAG, "fileUri $fileUri")
        val filePath = filePath ?: return
        val file = File(filePath)
        DebugUtil.warnOut(TAG, "开始解压")
        ZipUtils.unzip(
            file,
            FileUtil.sdCardCacheDir?.path + "/unzipFiles",
            object : ZipUtils.OnFileUnzipListener {
                override fun unzipSucceed(unzipDir: String?) {
                    DebugUtil.warnOut(TAG, "解压完成")
                    ToastUtil.toastLong(
                        this@ZipFileOperationActivity,
                        R.string.com_sscl_basesample_unzip_file_succeed
                    )
                    DebugUtil.warnOut(TAG, "file dir $unzipDir")
                }

                override fun unzipFailed() {
                    DebugUtil.warnOut(TAG, "解压失败")
                    ToastUtil.toastLong(
                        this@ZipFileOperationActivity,
                        R.string.com_sscl_basesample_unzip_file_failed
                    )
                }
            })
    }

    companion object {
        private const val REQUEST_CODE_ZIP = 1
    }
}