package com.sscl.baselibrary.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * UDP广播工具
 *
 * @author jackie
 */
@SuppressWarnings("unused")
public class UdpBroadcastUtil {

    /*---------------------------------------静态常量---------------------------------------*/

    private static final String TAG = UdpBroadcastUtil.class.getSimpleName();

    /*---------------------------------------成员常量---------------------------------------*/

    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 数据发送目标端口
     */
    private int mDestPort;
    /**
     * UDP Socket类
     */
    private DatagramSocket mSocket;

    /*---------------------------------------构造方法---------------------------------------*/

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public UdpBroadcastUtil(@NonNull Context context) {
        mContext = context;
    }

    /*---------------------------------------公开方法---------------------------------------*/

    /**
     * 打开UDP
     *
     * @param localPort 本地端口
     * @param destPort  目标端口
     * @return true表示打开成功
     */
    public boolean open(int localPort, int destPort) {
        mDestPort = destPort;
        try {
            mSocket = new DatagramSocket(localPort);
            mSocket.setBroadcast(true);
            mSocket.setReuseAddress(true);
            return true;
        } catch (SocketException e) {
            return false;
        }
    }


    /**
     * 关闭
     */
    public boolean close() {
        if (mSocket == null) {
            return false;
        }
        if (!mSocket.isClosed()) {
            mSocket.close();
        }
        return true;
    }

    /**
     * 发送广播包
     *
     * @param buffer 数据内容
     */
    public boolean sendPacket(byte[] buffer) {
        try {
            InetAddress addr = getBroadcastAddress(mContext);
            DebugUtil.warnOut(TAG, "addr = " + addr.toString());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            packet.setAddress(addr);
            packet.setPort(mDestPort);
            mSocket.send(packet);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送广播包
     *
     * @param buffer 数据内容
     * @param port   目标端口
     */
    public boolean sendPacket(@NonNull byte[] buffer, int port) {
        try {
            InetAddress addr = getBroadcastAddress(mContext);
            DebugUtil.warnOut(TAG, "addr = " + addr.toString());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            packet.setAddress(addr);
            packet.setPort(port);
            mSocket.send(packet);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 接收广播
     *
     * @param buffer 收到的数据
     */
    public boolean recvPacket(@NonNull byte[] buffer) {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            if (mSocket != null) {
                mSocket.receive(packet);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 接收数据
     *
     * @param packet 收到的数据包
     * @return true表示成功
     */
    public boolean recvPacket(@NonNull DatagramPacket packet) {
        if (mSocket == null) {
            return false;
        }
        try {
            mSocket.receive(packet);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*---------------------------------------私有方法---------------------------------------*/

    /**
     * 获取本机当前网络的广播地址
     *
     * @param context 上下文
     */
    private static InetAddress getBroadcastAddress(@NonNull Context context) throws UnknownHostException {
        //判断wifi热点是否打开
        if (isWifiApEnabled(context)) {
            //直接返回
            return InetAddress.getByName("192.168.43.255");
        }
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return InetAddress.getByName("255.255.255.255");
        }
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null) {
            return InetAddress.getByName("255.255.255.255");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | (~dhcp.netmask);
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }

    /**
     * check whether the wifiAp is Enable
     *
     * @param context 上下文
     */
    private static boolean isWifiApEnabled(@NonNull Context context) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (manager == null) {
                return false;
            }
            Method method = manager.getClass().getMethod("isWifiApEnabled");
            return (boolean) method.invoke(manager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        }
        return false;
    }

}