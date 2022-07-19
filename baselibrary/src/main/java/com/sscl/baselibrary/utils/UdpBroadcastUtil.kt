package com.sscl.baselibrary.utils

import kotlin.Throws
import android.net.wifi.WifiManager
import android.net.DhcpInfo
import android.content.*
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.*

/**
 * UDP广播工具
 *
 * @author jackie
 */
class UdpBroadcastUtil constructor(
    /**
     * 上下文
     */
    private val mContext: Context
) {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    companion object {

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 属性声明
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        private val TAG: String = UdpBroadcastUtil::class.java.simpleName

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 私有方法
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /**
         * 获取本机当前网络的广播地址
         *
         * @param context 上下文
         */
        @Throws(UnknownHostException::class)
        private fun getBroadcastAddress(context: Context): InetAddress {
            //判断wifi热点是否打开
            if (isWifiApEnabled(context)) {
                //直接返回
                return InetAddress.getByName("255.255.255.255")
            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                val connectivityManager =
//                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//                        ?: return InetAddress.getByName("255.255.255.255")
//                val activeNetwork = connectivityManager.activeNetwork
//                    ?: return InetAddress.getByName("255.255.255.255")
//                val linkProperties = connectivityManager.getLinkProperties(activeNetwork)
//                    ?: return InetAddress.getByName("255.255.255.255")
//                val linkAddresses = linkProperties.linkAddresses
//                for (linkAddress in linkAddresses) {
//                    //TODO
//                    if (linkAddress.address is Inet4Address) {
//                        DebugUtil.warnOut(TAG, "linkAddress.address = ${linkAddress.address}")
//                    }
//                }
//                return InetAddress.getByName("255.255.255.255")
//            } else {
            val wifiManager: WifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager?
                ?: return InetAddress.getByName("255.255.255.255")
            val dhcp: DhcpInfo =
                wifiManager.dhcpInfo ?: return InetAddress.getByName("255.255.255.255")
            val broadcast: Int = (dhcp.ipAddress and dhcp.netmask) or (dhcp.netmask.inv())
            val quads = ByteArray(4)
            for (k in 0..3) {
                quads[k] = ((broadcast shr k * 8) and 0xFF).toByte()
            }
            return InetAddress.getByAddress(quads)
//            }
        }

        /**
         * check whether the wifiAp is Enable
         *
         * @param context 上下文
         */
        private fun isWifiApEnabled(context: Context): Boolean {
            try {
                val manager: WifiManager = context.applicationContext.getSystemService(
                    Context.WIFI_SERVICE
                ) as WifiManager? ?: return false
                val method: Method = manager.javaClass.getMethod("isWifiApEnabled")
                return method.invoke(manager) as Boolean
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e1: IllegalAccessException) {
                e1.printStackTrace()
            } catch (e2: InvocationTargetException) {
                e2.printStackTrace()
            }
            return false
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 数据发送目标端口
     */
    private var mDestPort: Int = 0

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    /**
     * UDP Socket类
     */
    private var mSocket: DatagramSocket? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 打开UDP
     *
     * @param localPort 本地端口
     * @param destPort  目标端口
     * @return true表示打开成功
     */
    fun open(localPort: Int, destPort: Int): Boolean {
        mDestPort = destPort
        return try {
            mSocket = DatagramSocket(localPort)
            mSocket?.broadcast = true
            mSocket?.reuseAddress = true
            mSocket != null
        } catch (e: SocketException) {
            false
        }
    }

    /**
     * 关闭
     */
    fun close(): Boolean {
        return if (mSocket?.isClosed == false) {
            mSocket?.close()
            true
        } else {
            false
        }
    }

    /**
     * 发送广播包
     *
     * @param buffer 数据内容
     */
    fun sendPacket(buffer: ByteArray): Boolean {
        try {
            val address: InetAddress = getBroadcastAddress(mContext)
            DebugUtil.warnOut(TAG, "address = $address")
            val packet = DatagramPacket(buffer, buffer.size)
            packet.address = address
            packet.port = mDestPort
            mSocket?.send(packet)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 发送广播包
     *
     * @param buffer 数据内容
     * @param port   目标端口
     */
    fun sendPacket(buffer: ByteArray, port: Int): Boolean {
        try {
            val address: InetAddress = getBroadcastAddress(mContext)
            DebugUtil.warnOut(TAG, "address = $address")
            val packet = DatagramPacket(buffer, buffer.size)
            packet.address = address
            packet.port = port
            mSocket?.send(packet)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 接收广播
     *
     * @param buffer 收到的数据
     */
    fun receivePacket(buffer: ByteArray): Boolean {
        val packet = DatagramPacket(buffer, buffer.size)
        try {
            if (mSocket != null) {
                mSocket?.receive(packet)
                return true
            }
            return false
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 接收数据
     *
     * @param packet 收到的数据包
     * @return true表示成功
     */
    fun receivePacket(packet: DatagramPacket): Boolean {
        if (mSocket == null) {
            return false
        }
        try {
            mSocket?.receive(packet)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}