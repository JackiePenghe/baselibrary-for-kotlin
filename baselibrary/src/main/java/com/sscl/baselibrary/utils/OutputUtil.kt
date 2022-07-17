package com.sscl.baselibrary.utils

import com.sscl.baselibrary.files.FileUtil
import android.os.Environment
import android.content.*
import java.io.*

/**
 * 文件输出流工具类
 *
 * @author ALM Sound Technology
 */
class OutputUtil<T> {
    /*--------------------------------公开方法--------------------------------*/
    /**
     * 将对象保存到本地
     *
     * @param context 上下文
     * @param fileName 文件名
     * @param bean     对象
     * @return true 保存成功
     */
    fun writeObjectIntoLocal(context: Context, fileName: String, bean: T): Boolean {
        return try {
            // 通过openFileOutput方法得到一个输出流，方法参数为创建的文件名（不能有斜杠），操作模式
            val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            //写入
            oos.writeObject(bean)
            fos.close() //关闭输入流
            oos.close()
            true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 将对象写入sd卡
     *
     * @param fileName 文件名
     * @param bean     对象
     * @return true 保存成功
     */
    fun writeObjectIntoSdCard(fileName: String, bean: T): Boolean {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val appDir = FileUtil.appDir
            val sdFile = File(appDir, fileName)
            try {
                val fos = FileOutputStream(sdFile)
                val oos = ObjectOutputStream(fos)
                //写入
                oos.writeObject(bean)
                fos.close()
                oos.close()
                true
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                false
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }

    /**
     * 将集合写入sd卡
     *
     * @param fileName 文件名
     * @param list     集合
     * @return true 保存成功
     */
    fun writeListIntoSdCard(fileName: String, list: List<T>): Boolean {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val appDir = FileUtil.appDir
            val sdFile = File(appDir, fileName)
            try {
                val fos = FileOutputStream(sdFile)
                val oos = ObjectOutputStream(fos)
                //写入
                oos.writeObject(list)
                fos.close()
                oos.close()
                true
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                false
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }
}