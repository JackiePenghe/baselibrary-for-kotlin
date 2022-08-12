package com.sscl.basesample.activities.sample

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sscl.baselibrary.activity.BaseDataBindingAppCompatActivity
import com.sscl.baselibrary.receiver.USBListenerReceiver
import com.sscl.baselibrary.utils.DebugUtil
import com.sscl.baselibrary.utils.UsbUtil
import com.sscl.basesample.R
import com.sscl.basesample.adapter.FileListRecyclerViewAdapter
import com.sscl.basesample.databinding.ComSsclBasesampleActivityUsbListenerBinding
import com.sscl.basesample.viewmodel.USBListenerActivityViewModel
import java.io.File

/**
 * USB监听Activity
 */
class USBListenerActivity :
    BaseDataBindingAppCompatActivity<ComSsclBasesampleActivityUsbListenerBinding>() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    private val usbFileListAdapter: FileListRecyclerViewAdapter = FileListRecyclerViewAdapter()

    private val onOnUSBListener: USBListenerReceiver.OnUSBListener =
        object : USBListenerReceiver.OnUSBListener {
            /**
             * USB设备连接状态变化
             * @param device USB设备
             * @param attached 是否为连接状态
             */
            override fun onUSBConnectStateChanged(device: UsbDevice, attached: Boolean) {
                DebugUtil.warnOut(
                    TAG,
                    "onUSBConnectStateChanged: device = $device, attached = $attached"
                )
                if (UsbUtil.checkDeviceClass(device, UsbConstants.USB_CLASS_MASS_STORAGE)) {
                    if (attached) {
                        DebugUtil.warnOut(
                            TAG,
                            "onUSBConnectStateChanged: 连接U盘"
                        )
                    } else {
                        DebugUtil.warnOut(TAG, "onUSBConnectStateChanged: 断开U盘")
                    }
                    binding.getUsbPathBtn.performClick()
                } else if (UsbUtil.checkDeviceClass(device, UsbConstants.USB_CLASS_PRINTER)) {
                    if (attached) {
                        DebugUtil.warnOut(TAG, "onUSBConnectStateChanged: 连接打印机")
                    } else {
                        DebugUtil.warnOut(TAG, "onUSBConnectStateChanged: 断开打印机")
                    }
                }
            }

            /**
             * USB设备权限变化
             * @param device USB设备
             * @param granted 是否为可用状态
             */
            override fun devicePermissionGrantResult(device: UsbDevice, granted: Boolean) {
                DebugUtil.warnOut(
                    TAG,
                    "devicePermissionGrantResult: device = $device, granted = $granted"
                )
            }

        }

    private val usbListenerActivityViewModel by viewModels<USBListenerActivityViewModel> {
        USBListenerActivityViewModel.USBListenerActivityViewModelFactory
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
    override fun doBeforeSetLayout() {

    }

    /**
     * 初始化数据绑定
     */
    override fun inflateLayout(layoutInflater: LayoutInflater): ComSsclBasesampleActivityUsbListenerBinding {
        val binding = ComSsclBasesampleActivityUsbListenerBinding.inflate(layoutInflater)
        binding.viewModel = usbListenerActivityViewModel
        return binding
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    override fun doBeforeInitOthers() {
        initFileListRvList()
    }

    /**
     * 初始化控件数据
     */
    override fun initViewData() {
    }

    /**
     * 初始化其他数据
     */
    override fun initOtherData() {
    }

    /**
     * 初始化事件
     */
    override fun initEvents() {
        UsbUtil.setOnUSBListener(onOnUSBListener)
        binding.getUsbPathBtn.setOnClickListener {
            val uDiskPath = UsbUtil.getUDiskPath()
            DebugUtil.warnOut(TAG, "getUDiskPath: uDiskPath = $uDiskPath")
            usbListenerActivityViewModel.uDiskPath.value = uDiskPath
        }
        binding.getUsbListBtn.setOnClickListener {
            val uDiskPath = binding.uDiskPathTv.text.toString()
            if (uDiskPath.isEmpty()) {
                return@setOnClickListener
            }
            val uDiskFile = File(uDiskPath)
            if (!uDiskFile.exists()) {
                DebugUtil.warnOut(TAG, "uDiskPath \"$uDiskPath\"  is not exist")
                return@setOnClickListener
            }
            val listFiles = uDiskFile.listFiles()
            if (listFiles == null || listFiles.isEmpty()) {
                DebugUtil.warnOut(TAG, "uDiskPath \"$uDiskPath\"  is empty")
                return@setOnClickListener
            }
            for (file in listFiles) {
                DebugUtil.warnOut(TAG, "file = ${file.absolutePath}")
            }
            usbFileListAdapter.setList(listFiles.toList())
        }
    }

    /**
     * 在最后进行的操作
     */
    override fun doAfterAll() {
        UsbUtil.startUsbListener(this)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onDestroy() {
        UsbUtil.stopUsbListener(this)
        super.onDestroy()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private fun initFileListRvList() {
        binding.usbFileListRv.layoutManager = LinearLayoutManager(this)
        binding.usbFileListRv.adapter = usbFileListAdapter
    }
}