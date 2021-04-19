package com.sscl.baselibrary.utils;

/**
 * @author LiangYaLong
 * 描述:
 * 时间:     2020/6/18
 * 版本:     1.0
 */


import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.sscl.baselibrary.files.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

/**
 * @author Administrator
 * <p>
 * log打印日志保存,文件的保存以小时为单位
 * permission:<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_LOGS" />
 */
public class LogCatHelper {
    private static LogCatHelper instance = null;
    /**
     * 保存路径
     */
    private final String dirPath;
    /**
     * 应用pid
     */
    private final int appid;
    private Thread logThread;

    private LogCatHelper(String path) {
        appid = android.os.Process.myPid();
        if (TextUtils.isEmpty(path)) {
            dirPath = FileUtil.getLogInfoDir().getAbsolutePath();
        } else {
            dirPath = path;
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * @return 本类单例
     */
    public static LogCatHelper getInstance() {
        return getInstance(true);
    }

    /**
     * @param useSdcard 是否保存到 SD 卡
     * @return 本类单例
     */
    public static LogCatHelper getInstance(boolean useSdcard) {
        String path = null;
        if (useSdcard) {
            File logInfoSdcardDir = FileUtil.getSdcardLogInfoDir();
            if (logInfoSdcardDir != null) {
                path = logInfoSdcardDir.getAbsolutePath();
            }
        } else {
            File logInfoDir = FileUtil.getLogInfoDir();
            if (logInfoDir != null) {
                path = logInfoDir.getAbsolutePath();
            }
        }
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        return getInstance(path);
    }

    /**
     * @param path log日志保存根目录
     * @return 本类单例
     */
    private static LogCatHelper getInstance(String path) {
        if (instance == null) {
            instance = new LogCatHelper(path);
        }
        return instance;
    }

    /**
     * 启动log日志保存
     */
    public void init() {
        if (logThread == null) {
            logThread = BaseManager.getThreadFactory().newThread(new LogRunnable(appid, dirPath));
        }
        try {
            logThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLogcatDirectoryPath() {
        return dirPath;
    }

    private static class LogRunnable implements Runnable {

        private Process mProcess;
        private BufferedReader mReader;
        private final String cmds;
        private final String mPid;
        private final String dirPath;

        private LogRunnable(int pid, String dirPath) {
            this.mPid = "" + pid;
            this.dirPath = dirPath;
            cmds = "logcat *:v | grep \"(" + mPid + ")\"";
        }

        @Override
        public void run() {
            try {
                mProcess = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()), 1024);
                String line;
                while ((line = mReader.readLine()) != null) {
                    if (line.length() == 0) {
                        continue;
                    }
                    saveToFile(line);

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mProcess != null) {
                    mProcess.destroy();
                    mProcess = null;
                }
                try {
                    if (mReader != null) {
                        mReader.close();
                        mReader = null;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

        private void saveToFile(String line) {
            FileWriter fileWriter = null;
            try {
                File file = new File(dirPath, "log-" + FormatDate.getFormatDate() + ".log");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fileWriter = new FileWriter(file, true);
                if (line.contains(mPid)) {
                    fileWriter.append(FormatDate.getFormatTime())
                            .append("	")
                            .append(line)
                            .append("\r\n");
                    fileWriter.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static class FormatDate {

        public static String getFormatDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
            return sdf.format(System.currentTimeMillis());
        }

        public static String getFormatTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(System.currentTimeMillis());
        }
    }
}
