package com.sscl.basesample.activities.sample

import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.sscl.baselibrary.activity.BaseAppCompatActivity
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.utils.ToastUtil
import com.sscl.basesample.R
import java.io.*

/**
 * SD卡根目录下的文件读写测试
 *
 * @author jackie
 */
class SdcardFileTestActivity : BaseAppCompatActivity() {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private var count = 0

    /**
     * 写入文件按钮
     */
    private var writeFileBtn: Button? = null

    /**
     * 读取文件按钮
     */
    private var readFileBtn: Button? = null

    /**
     * 点击事件监听器
     */
    private val onClickListener = View.OnClickListener { view ->
        val id = view.id
        if (id == writeFileBtn!!.id) {
            // 写入文件
            writeFile()
        } else if (id == readFileBtn!!.id) {
            readFile()
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
     * 设置布局
     *
     * @return 布局id
     */
   override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_sdcard_file_test
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
   override fun doBeforeInitOthers() {}

    /**
     * 初始化布局控件
     */
   override fun initViews() {
        writeFileBtn = findViewById(R.id.write_file_btn)
        readFileBtn = findViewById(R.id.read_file_btn)
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
        writeFileBtn!!.setOnClickListener(onClickListener)
        readFileBtn!!.setOnClickListener(onClickListener)
    }

    /**
     * 在最后进行的操作
     */
   override fun doAfterAll() {}
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 写入文件
     */
    private fun writeFile() {
        //获取SD卡的根目录
        @Suppress("DEPRECATION") val externalStorageDirectory: File = Environment.getExternalStorageDirectory()
        //获取SD卡的根目录的路径
        val externalStorageDirectoryPath = externalStorageDirectory.path
        //在SD卡中创建一个文件夹，文件夹的名字为“test”
        val file = File(externalStorageDirectoryPath + File.separator + "test")
        //判断文件是否为文件夹
        if (file.exists()) {
            if (file.isFile) {
                file.delete()
                file.mkdirs()
            }
        } else {
            file.mkdirs()
        }
        //在目录中创建一个文件，文件名为“test.txt”
        val testFile = File(file, "test.txt")
        if (testFile.exists()) {
            testFile.delete()
        }
        try {
            testFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            DebugUtil.warnOut(TAG, "test.txt 文件创建失败")
            ToastUtil.toastLong(this, "test.txt 文件创建失败")
            return
        }
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(testFile)
            fileWriter.append("test").append(count++.toString())
            fileWriter.flush()
            ToastUtil.toastLong(this, "文件写入成功")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 读取文件
     */
    private fun readFile() {
        //获取SD卡的根目录
        @Suppress("DEPRECATION") val externalStorageDirectory: File = Environment.getExternalStorageDirectory()
        //获取SD卡的根目录的路径
        val externalStorageDirectoryPath = externalStorageDirectory.path
        //指定文件夹路径
        val file = File(externalStorageDirectoryPath + File.separator + "test")
        //判断文件是否为文件夹
        if (!file.isDirectory) {
            ToastUtil.toastLong(this, "指定路径不是文件夹")
            return
        }
        //判断文件是否存在
        if (!file.exists()) {
            ToastUtil.toastLong(this, "指定文件夹不存在")
            return
        }
        //指定文件路径
        val testFile = File(file, "test.txt")
        if (!testFile.isFile) {
            ToastUtil.toastLong(this, "指定路径不是文件")
            return
        }
        if (!testFile.exists()) {
            ToastUtil.toastLong(this, "指定文件不存在")
            return
        }
        var fileReader: FileReader? = null
        var bufferedReader: BufferedReader? = null
        try {
            fileReader = FileReader(testFile)
            bufferedReader = BufferedReader(fileReader)
            val cache = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                DebugUtil.warnOut(TAG, line?:"")
                cache.append(line)
            }
            ToastUtil.toastLong(this, cache.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}