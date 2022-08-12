package com.sscl.baselibrary.receiver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.sscl.baselibrary.utils.DebugUtil

/**
 * USB监听
 */
class USBListenerReceiver : BroadcastReceiver() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    companion object {

        const val ACTION_USB_PERMISSION = "com.sscl.baselibrary.USB_PERMISSION"

        private val TAG: String = USBListenerReceiver::class.java.simpleName

        val INTENT_FILTER: IntentFilter
            get() {
                val intentFilter = IntentFilter()
                intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
                return intentFilter
            }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    interface OnUSBListener {
        /**
         * USB设备连接状态变化
         * @param device USB设备
         * @param attached 是否为连接状态
         */
        fun onUSBConnectStateChanged(device: UsbDevice, attached: Boolean)

        /**
         * USB设备权限变化
         * @param device USB设备
         * @param granted 是否为可用状态
         */
        fun devicePermissionGrantResult(device: UsbDevice, granted: Boolean)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    /**
     * USB相关监听
     */
    private var onUSBListener: OnUSBListener? = null

    /**
     * USB管理器
     */
    private var usbManager: UsbManager? = null

    /**
     * 权限请求Intent
     */
    private var permissionIntent: PendingIntent? = null

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action ?: return
        when (action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                val device =
                    intent.getParcelableExtra<UsbDevice?>(UsbManager.EXTRA_DEVICE)
                if (device == null) {
                    DebugUtil.warnOut(TAG, "USB device is null")
                    return
                }
                onUSBListener?.onUSBConnectStateChanged(device, true)
            }
            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                val device =
                    intent.getParcelableExtra<UsbDevice?>(UsbManager.EXTRA_DEVICE)
                if (device == null) {
                    DebugUtil.warnOut(TAG, "USB device is null")
                    return
                }
                onUSBListener?.onUSBConnectStateChanged(device, false)
            }
            ACTION_USB_PERMISSION -> {
                val device = intent.getParcelableExtra<UsbDevice?>(UsbManager.EXTRA_DEVICE)
                if (device == null) {
                    DebugUtil.warnOut(TAG, "USB device is null")
                    return
                }
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    onUSBListener?.devicePermissionGrantResult(device, true)
                } else {
                    onUSBListener?.devicePermissionGrantResult(device, false)
                }
            }
            else -> {
                DebugUtil.warnOut(TAG, "Unknown action: $action")
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 请求USB设备使用权限
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    fun requestUsbPermission(context: Context, device: UsbDevice) {
        if (usbManager == null) {
            usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        }
        if (permissionIntent == null) {
            permissionIntent =
                PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), 0)
        }
        val usbManager = usbManager ?: return
        usbManager.requestPermission(
            device,
            permissionIntent
        )
    }

    /**
     * 注册USB监听
     */
    fun setOnOnUSBListener(onOnUSBListener: OnUSBListener?) {
        this.onUSBListener = onOnUSBListener
    }
}