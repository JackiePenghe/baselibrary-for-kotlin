package com.sscl.basesample.activities;

import android.content.Intent;
import android.util.Log;

import com.sscl.baselibrary.activity.BaseSplashActivity;
import com.sscl.basesample.MyApplication;
import com.sscl.basesample.beans.MessageBean;

import java.io.StringReader;

/**
 * 防止应用启动黑白屏
 *
 * @author alm
 */
public class SplashActivity extends BaseSplashActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate() {
        String cache = "{\"cmd\":999005,\"data\":{\"deviceName\":\"GulheiGzbskXHj2KDvzh\",\"deviceSecret\":\"1c2b0ab2648a9478d1fc239fdf9c0e6c\",\"dueDate\":\"2022-03-26 11:50:18\",\"isEternal\":1,\"productKey\":\"g63gVlyYswG\"},\"deviceId\":\"GulheiGzbskXHj2KDvzh\"}";
        MessageBean messageBean = MyApplication.getGSON().fromJson(new StringReader(cache), MessageBean.class);
        Object data = messageBean.getData();
        Log.i(TAG, "data = " + data.toString());
        //本界面仅用于防止程序黑白屏。想要更改本界面的黑白屏的背景，手动在res文件夹下新建一个xml文件夹，再新建一个files_path.xml。在其中配置即可
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
