package com.sscl.baselibrary.utils;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.files.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 文件输出流工具类
 *
 * @author ALM Sound Technology
 */
@SuppressWarnings("unused")
public class OutputUtil<T> {

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 将对象保存到本地
     *
     * @param context 上下文
     * @param fileName 文件名
     * @param bean     对象
     * @return true 保存成功
     */
    @SuppressWarnings("unused")
    public boolean writeObjectIntoLocal(@NonNull Context context,@NonNull String fileName,@NonNull T bean) {
        try {
            // 通过openFileOutput方法得到一个输出流，方法参数为创建的文件名（不能有斜杠），操作模式
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            //写入
            oos.writeObject(bean);
            fos.close();//关闭输入流
            oos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将对象写入sd卡
     *
     * @param fileName 文件名
     * @param bean     对象
     * @return true 保存成功
     */
    @SuppressWarnings("unused")
    public boolean writeObjectIntoSdCard(@NonNull String fileName, @NonNull T bean) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File appDir = FileUtil.getAppDir();
            File sdFile = new File(appDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(sdFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                //写入
                oos.writeObject(bean);
                fos.close();
                oos.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 将集合写入sd卡
     *
     * @param fileName 文件名
     * @param list     集合
     * @return true 保存成功
     */
    @SuppressWarnings("unused")
    public boolean writeListIntoSdCard(@NonNull String fileName, @NonNull List<T> list) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File appDir = FileUtil.getAppDir();
            File sdFile = new File(appDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(sdFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                //写入
                oos.writeObject(list);
                fos.close();
                oos.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
