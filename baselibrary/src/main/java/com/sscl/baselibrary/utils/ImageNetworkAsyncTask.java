package com.sscl.baselibrary.utils;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageNetworkAsyncTask extends AsyncTask<String, Object, File> {

    /*--------------------------------静态常量--------------------------------*/

    private static final String TAG = ImageNetworkAsyncTask.class.getSimpleName();

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 图片文件下载路径
     */
    private File file;

    /*--------------------------------构造方法--------------------------------*/

    public ImageNetworkAsyncTask(@NonNull File file) {
        this.file = file;
    }

    /*--------------------------------实现父类方法--------------------------------*/

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This will normally run on a background thread. But to better
     * support testing frameworks, it is recommended that this also tolerates
     * direct execution on the foreground thread, as part of the {@link #execute} call.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param strings The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected File doInBackground(String... strings) {
        int length = strings.length;
        if (length <= 0) {
            return null;
        }
        URL imageUrl;
        try {
            imageUrl = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) imageUrl
                    .openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setInstanceFollowRedirects(true);
        InputStream is;
        try {
            is = conn.getInputStream();
        } catch (IOException e) {
            return null;
        }
        OutputStream os;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        copyStream(is, os);
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /*--------------------------------重写父类方法--------------------------------*/

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.
     * To better support testing frameworks, it is recommended that this be
     * written to tolerate direct execution as part of the execute() call.
     * The default version does nothing.</p>
     *
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param file The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(File)
     */
    @Override
    protected void onPostExecute(File file) {
        DebugUtil.warnOut(TAG, "onPostExecute file = " + file.getAbsolutePath());
    }

    /*--------------------------------私有函数--------------------------------*/

    /**
     * 下载图片操作
     *
     * @param is InputStream
     * @param os OutputStream
     */
    private void copyStream(@NonNull InputStream is, @NonNull OutputStream os) {
        final int bufferSize = 1024;
        try {
            byte[] bytes = new byte[bufferSize];
            while (true) {
                int count = is.read(bytes, 0, bufferSize);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception e) {
            DebugUtil.warnOut(TAG, "copyStream with exception! " + e.getMessage());
        }
    }
}
