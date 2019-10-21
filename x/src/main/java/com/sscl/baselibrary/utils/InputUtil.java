package com.sscl.baselibrary.utils;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.files.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.List;

/**
 * 文件输入流工具类
 *
 * @author ALM Sound Technology
 */
@SuppressWarnings("unused")
public class InputUtil<T> {

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 读取本地对象
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return 读取到的对象
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public T readObjectFromLocal(@NonNull Context context, @NonNull String fileName) {
        T bean;
        try {
            //获得输入流
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            bean = (T) ois.readObject();
            fis.close();
            ois.close();
            return bean;
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            return null;
        } catch (OptionalDataException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取sd卡对象
     *
     * @param fileName 文件名
     * @return 泛型对象
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public T readObjectFromSdCard(@NonNull String fileName) {
        //检测sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            T bean;
            File appDir = FileUtil.getAppDir();
            File sdFile = new File(appDir, fileName);
            try {
                FileInputStream fis = new FileInputStream(sdFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                bean = (T) ois.readObject();
                fis.close();
                ois.close();
                return bean;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
                return null;
            } catch (OptionalDataException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 读取sd卡对象
     *
     * @param fileName 文件名
     * @return 读取到的list
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public List<T> readListFromSdCard(@NonNull String fileName) {
        //检测sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            List<T> list;
            File appDir = FileUtil.getAppDir();

            File sdFile = new File(appDir, fileName);
            try {
                FileInputStream fis = new FileInputStream(sdFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (List<T>) ois.readObject();
                fis.close();
                ois.close();
                return list;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
                return null;
            } catch (OptionalDataException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

}

