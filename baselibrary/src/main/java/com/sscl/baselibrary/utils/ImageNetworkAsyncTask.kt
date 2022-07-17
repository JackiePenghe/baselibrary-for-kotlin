package com.sscl.baselibrary.utils

import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class ImageNetworkAsyncTask constructor(
    /**
     * 图片文件下载路径
     */
    private val file: File
)  {

    companion object {
        private val TAG: String = ImageNetworkAsyncTask::class.java.simpleName
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    interface OnImageDownloadListener {
        fun onImageDownload(file: File)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        // 发生异常时的捕获
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 下载图片操作
     *
     * @param inputStream InputStream
     * @param outputStream OutputStream
     */
    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        val bufferSize: Int = 1024
        try {
            val bytes = ByteArray(bufferSize)
            while (true) {
                val count: Int = inputStream.read(bytes, 0, bufferSize)
                if (count == -1) {
                    break
                }
                outputStream.write(bytes, 0, count)
            }
        } catch (e: Exception) {
            DebugUtil.warnOut(TAG, "copyStream with exception! " + e.message)
        }
    }

    fun execute(url: String, onImageDownloadListener: OnImageDownloadListener?) {
        GlobalScope.launch(errorHandler) {
            withContext(Dispatchers.IO) {
                val imageUrl: URL
                try {
                    imageUrl = URL(url)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                    return@withContext
                }
                val conn: HttpURLConnection
                try {
                    conn = imageUrl
                        .openConnection() as HttpURLConnection
                } catch (e: IOException) {
                    e.printStackTrace()
                    return@withContext
                }
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.instanceFollowRedirects = true
                val inputStream: InputStream
                try {
                    inputStream = conn.inputStream
                } catch (e: IOException) {
                    return@withContext
                }
                val os: OutputStream
                try {
                    os = FileOutputStream(file)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    return@withContext
                }
                copyStream(inputStream, os)
                try {
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                doOnUiCode(onImageDownloadListener)
            }
        }
    }

    private suspend fun doOnUiCode(onImageDownloadListener: OnImageDownloadListener?) {
        withContext(Dispatchers.Main) {
            onImageDownloadListener?.onImageDownload(file)
        }
    }
}