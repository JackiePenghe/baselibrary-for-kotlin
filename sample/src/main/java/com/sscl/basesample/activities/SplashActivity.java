package com.sscl.basesample.activities;

import android.content.Intent;

import com.sscl.baselibrary.activity.BaseSplashActivity;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.baselibrary.utils.DebugUtil;

/**
 * 防止应用启动黑白屏
 *
 * @author alm
 */
public class SplashActivity extends BaseSplashActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate() {
        byte[] appId = new byte[]{0x11, 0x11, 0x11, 0x11};
        String address1 = "48:59:00:05:13:AD";
        String address2 = "48:59:00:04:7B:53";
        String address3 = "48:59:00:04:d9:18";
        String address4 = "48:59:00:04:fc:3d";
        long verificationCode1 = getVerificationCode(address1, appId);
        long verificationCode2 = getVerificationCode(address2, appId);
        long verificationCode3 = getVerificationCode(address3, appId);
        long verificationCode4 = getVerificationCode(address4, appId);

        DebugUtil.warnOut(TAG, address1 + ":" + verificationCode1);
        DebugUtil.warnOut(TAG, address2 + ":" + verificationCode2);
        DebugUtil.warnOut(TAG, address3 + ":" + verificationCode3);
        DebugUtil.warnOut(TAG, address4 + ":" + verificationCode4);
        //本界面仅用于防止程序黑白屏。想要更改本界面的黑白屏的背景，手动在res文件夹下新建一个xml文件夹，再新建一个files_path.xml。在其中配置即可

        StringBuilder testString = new StringBuilder();

        for (int i = 0; i < 50000; i++) {
            testString.append(i % 10);
        }

        String result = testString.toString();

        DebugUtil.warnOut(TAG, result);

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }


    private long getVerificationCode(String address, byte[] appId) {
        DebugUtil.warnOut(TAG, "appID = " + ConversionUtil.byteArrayToHexStr(appId));
        String[] split = address.split(":");
        byte[] cache = new byte[6];

        int highByte;
        int lowByte;
        for (int i = 0; i < split.length; ++i) {
            lowByte = Integer.valueOf(split[i], 16);
            cache[i] = (byte) lowByte;
            DebugUtil.warnOut(TAG, "addressArray[j] = " + lowByte);
        }

        highByte = ConversionUtil.getUnsignedByte(cache[0]) ^ ConversionUtil.getUnsignedByte(cache[2]) ^ ConversionUtil.getUnsignedByte(cache[4]) ^ ConversionUtil.getUnsignedByte(appId[0]);
        DebugUtil.warnOut(TAG, "highByte = " + highByte);
        lowByte = ConversionUtil.getUnsignedByte(cache[1]) ^ ConversionUtil.getUnsignedByte(cache[3]) ^ ConversionUtil.getUnsignedByte(cache[5]) ^ ConversionUtil.getUnsignedByte(appId[1]);
        DebugUtil.warnOut(TAG, "lowByte = " + lowByte);
        highByte = highByte << 24 | lowByte << 16 | ConversionUtil.getUnsignedByte(appId[2]) << 8 | ConversionUtil.getUnsignedByte(appId[3]);
        DebugUtil.warnOut(TAG, "result = " + highByte);
        return ConversionUtil.getUnsignedInt(highByte);
    }
}
