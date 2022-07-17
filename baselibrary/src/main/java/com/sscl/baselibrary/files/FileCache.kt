package com.sscl.baselibrary.files

import android.content.*
import android.os.Environment
import com.sscl.baselibrary.utils.DebugUtil
import java.io.File

/**
 * 文件缓存工具
 *
 * @author ALM
 */
class FileCache(context: Context) {
    /*--------------------------------成员变量--------------------------------*/
    /**
     * 缓存目录
     */
    private var cacheDir: File? = null
    /*--------------------------------公开函数--------------------------------*/
    /**
     * 根据url获取缓存文件
     *
     * @param url 缓存文件url
     * @return 缓存文件
     */
    fun getFile(url: String): File {
        var split = url.split("\\.".toRegex()).toTypedArray()
        var fileName: String
        var length = split.size
        if (length == 2) {
            fileName = split[0]
        } else {
            if (length > 2) {
                length -= 1
                val cache = StringBuilder()
                for (i in 0 until length) {
                    cache.append(split[i])
                }
                fileName = cache.toString()
            } else {
                fileName = url
            }
        }
        split = fileName.split("/".toRegex()).toTypedArray()
        length = split.size
        if (split.size != 1) {
            fileName = split[length - 1]
        }
        return File(cacheDir, fileName)
    }
    /*--------------------------------库内函数--------------------------------*/
    /**
     * 清除缓存
     */
    fun clear() {
        val files = cacheDir?.listFiles() ?: return
        for (f in files) {
            val delete = f.delete()
            DebugUtil.warnOut(TAG, "delete file " + f.name + delete)
        }
    }

    companion object {
        /*--------------------------------静态常量--------------------------------*/
        private val TAG = FileCache::class.java.simpleName
    }
    /*--------------------------------构造函数--------------------------------*/

    init {
        // 如果有SD卡则在SD卡中建一个LazyList的目录存放缓存的图片
        // 没有SD卡就放在系统的缓存目录中
        cacheDir = if (Environment.getExternalStorageState() ==
            Environment.MEDIA_MOUNTED
        ) {
            FileUtil.cacheDir
        } else {
            context.cacheDir
        }
        val cacheDir = cacheDir ?: throw NullPointerException("cacheDir == null")
        //如果目录不存在，那么创建一个缓存目录
        if (!cacheDir.exists()) {
            val mkdirs = cacheDir.mkdirs()
            DebugUtil.warnOut(TAG, "mkdirs $mkdirs")
        }
    }
}