package com.sscl.baselibrary.utils;

/**
 * @author LiangYaLong
 * 描述:
 * 时间:     2020/6/18
 * 版本:     1.0
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.sscl.baselibrary.files.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Administrator
 * <p>
 * log打印日志保存,文件的保存以小时为单位
 * permission:<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_LOGS" />
 */
public class LogCatHelper {
    private static LogCatHelper instance = null;
    private String dirPath;//保存路径
    private int appid;//应用pid
    private Thread logThread;

    private LogCatHelper(Context mContext, String path) {
        appid = android.os.Process.myPid();
        if (TextUtils.isEmpty(path)) {
            dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "seeker" + File.separator + mContext.getPackageName();
        } else {
            dirPath = path;
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * @param mContext 上下文
     * @return 本类单例
     */
    public static LogCatHelper getInstance(Context mContext) {
        return getInstance(mContext, true);
    }

    /**
     * @param mContext  上下文
     * @param useSdcard 是否保存到 SD 卡
     * @return 本类单例
     */
    public static LogCatHelper getInstance(Context mContext, boolean useSdcard) {
        String path = null;
        if (useSdcard) {
            File logInfoSdcardDir = FileUtil.getLogInfoSdcardDir();
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
        return getInstance(mContext, path);
    }

    /**
     * @param mContext 上下文
     * @param path     log日志保存根目录
     * @return 本类单例
     */
    private static LogCatHelper getInstance(Context mContext, String path) {
        if (instance == null) {
            instance = new LogCatHelper(mContext, path);
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
        logThread.start();

    }

    private boolean DeleteOverdueLogFile(String PATH) {
        File mfolder = new File(PATH); //打开目录文件夹
        if (mfolder.isDirectory()) {
            File[] AllFiles = mfolder.listFiles(); //列出目录下的所有文件
            ArrayList<String> mFilesList = new ArrayList<String>();  //存放/myLog 下的所有文件
            for (int i = 0; i < AllFiles.length; i++) {
                File mFile = AllFiles[i]; //得到文件
                String Name = mFile.getName(); //得到文件的名字
                if (Name == null || Name.length() < 1)
                    return false;
                if (Name.startsWith("myLog-") && Name.endsWith(".log")) {  //筛选出log
                    mFilesList.add(Name); //把文件名添加到链表里
                }
            }

            Collections.sort(mFilesList);   // 将文件按自然排序升序排列
            //判断日志文件如果大于5，就要处理
            for (int i = 0; i < mFilesList.size() - 10; i++) {
                String Name = mFilesList.get(i); //得到链表最早的文件名
                File mFile = new File(mfolder, Name);  //得到最早的文件
                mFile.delete(); //删除
            }
        }
        return true;
    }

    private static class LogRunnable implements Runnable {

        private Process mProcess;
        private FileOutputStream fos;
        private BufferedReader mReader;
        private String cmds;
        private String mPid;

        private LogRunnable(int pid, String dirPath) {
            this.mPid = "" + pid;
            try {
                File file = new File(dirPath, "myLog-" + FormatDate.getFormatDate() + ".log");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    if (fos != null && line.contains(mPid)) {
                        fos.write((FormatDate.getFormatTime() + "	" + line + "\r\n").getBytes());
                    }
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
                    if (fos != null) {
                        fos.close();
                        fos = null;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static class FormatDate {

        public static String getFormatDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
            return sdf.format(System.currentTimeMillis());
        }

        public static String getFormatTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(System.currentTimeMillis());
        }
    }
}
