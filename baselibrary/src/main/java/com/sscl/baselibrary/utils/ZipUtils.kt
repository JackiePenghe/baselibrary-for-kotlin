package com.sscl.baselibrary.utils

import com.sscl.baselibrary.files.FileUtil
import java.io.*
import java.util.ArrayList
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * Java utils 实现的Zip工具
 *
 * @author once
 */
object ZipUtils {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    interface OnFileUnzipListener {
        /**
         * 解压成功的目录
         *
         * @param unzipDir 目录
         */
        fun unzipSucceed(unzipDir: String?)

        /**
         * 解压失败
         */
        fun unzipFailed()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    // 1M Byte
    private const val BUFF_SIZE = 1024 * 1024
    private val TAG = ZipUtils::class.java.simpleName

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 解压zip文件到指定目录
     *
     * @param file zip文件
     * @param unzipFileDir 解压目录,如果为空则解压到 appDir/cache/unzipFiles目录下
     * @param onFileUnzipListener 解压监听器回调
     */
    fun unzip(file: File, unzipFileDir: String?, onFileUnzipListener: OnFileUnzipListener?) {
        BaseManager.threadFactory.newThread {
            val unzipDir: String = unzipFileDir ?: (FileUtil.cacheDir.toString() + "/unzipFiles")
            val b = upZipFileNew(file, unzipDir)
            DebugUtil.warnOut(TAG, "解压完成")
            if (b) {
                onFileUnzipListener?.unzipSucceed(unzipDir)
            } else {
                onFileUnzipListener?.unzipFailed()
            }
        }.start()
        //        String unzipDir = FileUtil.getCacheDir() + "/unzipFiles";
//        upZipFileNew(file, unzipDir);
    }

    fun unzipCallbackOnMainThread(file: File?, onFileUnzipListener: OnFileUnzipListener?) {
        BaseManager.threadFactory.newThread {
            val unzipDir = FileUtil.cacheDir.toString() + "/unzipFiles"
            upZipFile(file, unzipDir)
            DebugUtil.warnOut(TAG, "解压完成")
            BaseManager.handler.post { onFileUnzipListener?.unzipSucceed(unzipDir) }
        }.start()
    }

    /**
     * 列出压缩包中的所有文件名
     *
     * @param zipFile 压缩包文件
     * @return 文件名集合
     */
    @kotlin.jvm.JvmStatic
    fun getEntriesNamesNew(zipFile: File?): ArrayList<String>? {
        val nameList = ArrayList<String>()
        var result: ArrayList<String>?
        var fileInputStream: FileInputStream? = null
        var zipInputStream: ZipInputStream? = null
        try {
            fileInputStream = FileInputStream(zipFile)
            zipInputStream = ZipInputStream(fileInputStream)
            var nextEntry: ZipEntry?
            do {
                nextEntry = zipInputStream.nextEntry
                if (nextEntry == null) {
                    break
                }
                if (nextEntry.isDirectory) {
                    continue
                }
                nameList.add(nextEntry.name)
                zipInputStream.closeEntry()
            } while (true)
            result = nameList
        } catch (e: IOException) {
            e.printStackTrace()
            result = null
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (zipInputStream != null) {
                try {
                    zipInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    /**
     * 列出压缩包中的所有文件名
     *
     * @param zipFile 压缩包文件
     * @return 文件名集合
     */
    fun getEntriesNames(zipFile: File?): ArrayList<String>? {
        val sourceFile: ZipFile
        sourceFile = try {
            ZipFile(zipFile)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        val entries = sourceFile.entries()
        if (entries == null) {
            DebugUtil.warnOut(TAG, "zip file entries null")
            return null
        }
        val fileNames = ArrayList<String>()
        while (entries.hasMoreElements()) {
            val zipEntry = entries.nextElement()
            if (zipEntry.isDirectory) {
                continue
            }
            fileNames.add(zipEntry.name)
        }
        try {
            sourceFile.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fileNames
    }

    /**
     * 解压缩一个文件
     *
     * @param zipFile       压缩文件
     * @param targetDirPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    fun upZipFile(zipFile: File?, targetDirPath: String?): Boolean {
        var result = true
        val sourceFile: ZipFile
        sourceFile = try {
            ZipFile(zipFile)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        val targetDirFile = File(targetDirPath)
        if (!targetDirFile.exists()) {
            targetDirFile.mkdirs()
        }
        if (targetDirFile.isFile) {
            targetDirFile.delete()
            targetDirFile.mkdirs()
        }
        val entries = sourceFile.entries()
        if (entries == null) {
            DebugUtil.warnOut(TAG, "zip file entries null")
            return false
        }
        while (entries.hasMoreElements()) {
            val zipEntry = entries.nextElement()
            if (zipEntry.isDirectory) {
                continue
            }
            val file = File(targetDirFile, zipEntry.name)
            if (file.isDirectory) {
                FileUtil.deleteDirFiles(file)
            }
            if (file.exists()) {
                file.delete()
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                    result = false
                    break
                }
            }
            DebugUtil.warnOut(TAG, file.absolutePath)
            var fileOutputStream: FileOutputStream? = null
            var inputStream: InputStream? = null
            try {
                inputStream = sourceFile.getInputStream(zipEntry)
                fileOutputStream = FileOutputStream(file, true)
                val data = ByteArray(1024)
                var count: Int
                do {
                    count = inputStream.read(data)
                    if (count < 0) {
                        break
                    }
                    fileOutputStream.write(data, 0, count)
                } while (true)
            } catch (e: IOException) {
                e.printStackTrace()
                result = false
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        try {
            sourceFile.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 解压文件 新
     *
     * @param zipFile      zip压缩包
     * @param unzipDirPath 解压目录
     * @return
     */
    private fun upZipFileNew(zipFile: File, unzipDirPath: String): Boolean {
        if (!zipFile.exists()) {
            return false
        }
        if (zipFile.isDirectory) {
            return false
        }
        val unzipDirFile = File(unzipDirPath)
        if (!unzipDirFile.exists()) {
            unzipDirFile.mkdirs()
        }
        if (unzipDirFile.isFile) {
            unzipDirFile.delete()
            unzipDirFile.mkdirs()
        }
        var fileInputStream: FileInputStream? = null
        var zipInputStream: ZipInputStream? = null
        var result = true
        try {
            fileInputStream = FileInputStream(zipFile)
            zipInputStream = ZipInputStream(fileInputStream)
            var nextEntry: ZipEntry?
            do {
                nextEntry = zipInputStream.nextEntry
                if (nextEntry == null) {
                    break
                }
                val size = nextEntry.size
                val name = nextEntry.name
                val targetFile = File(unzipDirFile, name)
                DebugUtil.warnOut(TAG, "targetFile = " + targetFile.name)
                DebugUtil.warnOut(TAG, "size = $size")
                val data = ByteArray(1024)
                var count: Int
                val fileOutputStream = FileOutputStream(targetFile, true)
                do {
                    count = zipInputStream.read(data)
                    if (count < 0) {
                        fileOutputStream.flush()
                        fileOutputStream.close()
                        break
                    }
                    fileOutputStream.write(data, 0, count)
                } while (true)
            } while (true)
            DebugUtil.warnOut(TAG, "")
            zipInputStream.closeEntry()
        } catch (e: IOException) {
            e.printStackTrace()
            result = false
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (zipInputStream != null) {
                try {
                    zipInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}