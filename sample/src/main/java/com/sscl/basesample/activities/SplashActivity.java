package com.sscl.basesample.activities;

import android.content.Intent;

import com.sscl.baselibrary.activity.BaseSplashActivity;
import com.sscl.baselibrary.utils.DebugUtil;

import java.util.ArrayList;

/**
 * 防止应用启动黑白屏
 *
 * @author alm
 */
public class SplashActivity extends BaseSplashActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate() {


        for (int i = 0; i < 16; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < i; j++) {
                stringBuilder.append(j);
            }
            DebugUtil.warnOut(addComma(stringBuilder.toString()));
        }

        //本界面仅用于防止程序黑白屏。想要更改本界面的黑白屏的背景，手动在res文件夹下新建一个xml文件夹，再新建一个files_path.xml。在其中配置即可
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 给字符串添加千位分隔符
     *
     * @param bean_pool_num 添加逗号
     * @return 添加逗号
     */
    private String addComma(String bean_pool_num) {
        if (bean_pool_num == null || bean_pool_num.isEmpty()) {
            return bean_pool_num;
        }
        int length = bean_pool_num.length();
        if (length <= 3) {
            return bean_pool_num;
        }

        char[] chars = bean_pool_num.toCharArray();
        ArrayList<Character> cache = new ArrayList<>();

        for (int i = length - 1; i >= 0; i--) {
            if (i != length - 1) {
                if (i % 3 == 0) {
                    cache.add(',');
                }
                cache.add(chars[i]);
            }

        }
        char[] newChar = new char[cache.size()];
        length = newChar.length;
        for (int i = 0; i < length; i++) {
            newChar[i] = cache.get(length - i - 1);
        }

        return new String(newChar);
    }
}
