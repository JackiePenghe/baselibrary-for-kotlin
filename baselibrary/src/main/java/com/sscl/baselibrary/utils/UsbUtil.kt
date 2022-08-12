package com.sscl.baselibrary.utils

import android.content.Context
import android.hardware.usb.UsbDevice
import android.os.Build
import android.util.Log
import com.sscl.baselibrary.receiver.USBListenerReceiver
import java.io.*
import java.util.ArrayList

/**
 * USB工具类
 */
object UsbUtil {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * USB状态的监听广播接收者
     */
    private val USB_LISTENER_RECEIVER = USBListenerReceiver()

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 方法声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 公开方法 * * * * * * * * * * * * * * * * * * */

    /**
     * 判断USB设备类型
     * @param device USB设备
     * @param usbConstants USB常量 [android.hardware.usb.UsbConstants]
     */
    fun checkDeviceClass(device: UsbDevice, usbConstants: Int): Boolean {
        val interfaceCount = device.interfaceCount
        for (i in 0 until interfaceCount) {
            val interfaceDescriptor = device.getInterface(i)
            if (interfaceDescriptor.interfaceClass == usbConstants) {
                return true
            }
        }
        return false
    }

    /**
     * 开始监听USB
     */
    fun startUsbListener(context: Context) {
        context.registerReceiver(USB_LISTENER_RECEIVER, USBListenerReceiver.INTENT_FILTER)
    }

    /**
     * 停止监听USB
     */
    fun stopUsbListener(context: Context) {
        context.unregisterReceiver(USB_LISTENER_RECEIVER)
    }

    /**
     * 设置USB的监听
     */
    fun setOnUSBListener(onUSBListener: USBListenerReceiver.OnUSBListener?) {
        USB_LISTENER_RECEIVER.setOnOnUSBListener(onUSBListener)
    }

    /**
     * 获取U盘路径的监听
     */
    fun getUDiskPath(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getUDiskPathM()
        } else {
            getUDiskPathLow()
        }
    }

    /* * * * * * * * * * * * * * * * * * * 私有方法 * * * * * * * * * * * * * * * * * * */

    /**
     * 安卓6.0以下获取U盘路径
     */
    private fun getUDiskPathLow(): String? {
        val filePath = "/proc/mounts"
        val file = File(filePath)
        val lineList = ArrayList<String>()
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var line: String?
            while (true) {
                line = bufferedReader.readLine() ?: break
//                    Log.e("TAG",line);
                if (line.contains("vfat")) {
                    lineList.add(line)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        if (lineList.isEmpty() || lineList.size < 1) {
            return null
        }
        val editPath = lineList[lineList.size - 1]
        val start = editPath.indexOf("/mnt")
        val end = editPath.indexOf(" vfat")
        if (start == -1 || end == -1) {
            return null
        }
        val path = editPath.substring(start, end)
        Log.d("TAG_SelectBusLineDialog", "path: $path")
        return path
    }

    /**
     * 安卓6.0以上版本获取U盘路径
     */
    private fun getUDiskPathM(): String? {
        //storage目录下的usb路径
        val path = "/storage"
        val storage = File(path)
        if (!storage.exists()) {
            return null
        }
        val files = storage.listFiles()
        if (files == null || files.isEmpty()) {
            return null
        }
        for (file in files) {
            if (file.isDirectory) {
                val fileName = file.name
                DebugUtil.warnOut("fileName = $fileName")
            }
        }
        var usbPath: String?
        usbPath = findUsbPath1(files)
        if (usbPath == null) {
            usbPath = findUsbPath2(files)
        }
        if (usbPath == null) {
            usbPath = getUDiskPathLow()
        }
        return usbPath
    }

    /**
     * 获取U盘路径(方式2)
     */
    private fun findUsbPath2(files: Array<File>): String? {
        var usbPath: String? = null
        for (file in files) {
            if (file.isFile) {
                continue
            }
            val name = file.name
            if (name.startsWith("USB") || name.startsWith("usb")) {
                usbPath = file.absolutePath
                break
            }
        }
        return usbPath
    }

    /**
     * 获取U盘路径(方式1)
     */
    private fun findUsbPath1(
        files: Array<File>,
    ): String? {
        var usbPath: String? = null
        for (file in files) {
            if (file.isFile) {
                continue
            }
            val name = file.name
            if (name.contains("-")) {
                usbPath = file.absolutePath
                break
            }
        }
        return usbPath
    }
}