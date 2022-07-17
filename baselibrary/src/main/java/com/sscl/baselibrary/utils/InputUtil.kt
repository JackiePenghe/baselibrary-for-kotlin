package com.sscl.baselibrary.utils

import com.sscl.baselibrary.files.FileUtil
import android.os.Environment
import android.content.*
import java.io.*

/**
 * 文件输入流工具类
 *
 * @author ALM Sound Technology
 */
class InputUtil<T> {
    /*--------------------------------公开方法--------------------------------*/
    /**
     * 读取本地对象
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return 读取到的对象
     */
    fun readObjectFromLocal(context: Context, fileName: String): T? {
        val bean: T
        return try {
            //获得输入流
            val fis = context.openFileInput(fileName)
            val ois = ObjectInputStream(fis)
            bean = ois.readObject() as T
            fis.close()
            ois.close()
            bean
        } catch (e: StreamCorruptedException) {
            e.printStackTrace()
            null
        } catch (e: OptionalDataException) {
            e.printStackTrace()
            null
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 读取sd卡对象
     *
     * @param fileName 文件名
     * @return 泛型对象
     */
    fun readObjectFromSdCard(fileName: String): T? {
        //检测sd卡是否存在
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val bean: T
            val appDir = FileUtil.appDir
            val sdFile = File(appDir, fileName)
            try {
                val fis = FileInputStream(sdFile)
                val ois = ObjectInputStream(fis)
                bean = ois.readObject() as T
                fis.close()
                ois.close()
                bean
            } catch (e: StreamCorruptedException) {
                e.printStackTrace()
                null
            } catch (e: OptionalDataException) {
                e.printStackTrace()
                null
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    /**
     * 读取sd卡对象
     *
     * @param fileName 文件名
     * @return 读取到的list
     */
    fun readListFromSdCard(fileName: String): List<T>? {
        //检测sd卡是否存在
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val list: List<T>
            val appDir = FileUtil.appDir
            val sdFile = File(appDir, fileName)
            try {
                val fis = FileInputStream(sdFile)
                val ois = ObjectInputStream(fis)
                list = ois.readObject() as List<T>
                fis.close()
                ois.close()
                list
            } catch (e: StreamCorruptedException) {
                e.printStackTrace()
                null
            } catch (e: OptionalDataException) {
                e.printStackTrace()
                null
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}