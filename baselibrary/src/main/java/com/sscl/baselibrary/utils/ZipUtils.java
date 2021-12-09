package com.sscl.baselibrary.utils;

import androidx.annotation.Nullable;

import com.sscl.baselibrary.files.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Java utils 实现的Zip工具
 *
 * @author once
 */
public class ZipUtils {
    // 1M Byte
    private static final int BUFF_SIZE = 1024 * 1024;

    private static final String TAG = ZipUtils.class.getSimpleName();

    public interface OnFileZipListener {

        /**
         * 解压成功的目录
         *
         * @param unzipDir 目录
         */
        void unzipSucceed(String unzipDir);

        /**
         * 解压失败
         */
        void unzipFailed();
    }

    public static void unzip(final File file, @Nullable final String unzipFileDir, @Nullable final OnFileZipListener onFileZipListener) {

        BaseManager.getThreadFactory().newThread(new Runnable() {
            @Override
            public void run() {
                String unzipDir;
                if (unzipFileDir == null) {
                    unzipDir = FileUtil.getCacheDir() + "/unzipFiles";
                }else {
                    unzipDir = unzipFileDir;
                }
                boolean b = upZipFileNew(file, unzipDir);
                DebugUtil.warnOut(TAG, "解压完成");
                if (b) {
                    if (onFileZipListener != null) {
                        onFileZipListener.unzipSucceed(unzipDir);
                    }
                } else {
                    if (onFileZipListener != null) {
                        onFileZipListener.unzipFailed();
                    }
                }
            }
        }).start();
//        String unzipDir = FileUtil.getCacheDir() + "/unzipFiles";
//        upZipFileNew(file, unzipDir);
    }

    public static void unzipCallbackOnMainThread(final File file, @Nullable final OnFileZipListener onFileZipListener) {
        BaseManager.getThreadFactory().newThread(new Runnable() {
            @Override
            public void run() {
                final String unzipDir = FileUtil.getCacheDir() + "/unzipFiles";
                upZipFile(file, unzipDir);
                DebugUtil.warnOut(TAG, "解压完成");
                BaseManager.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (onFileZipListener != null) {
                            onFileZipListener.unzipSucceed(unzipDir);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 列出压缩包中的所有文件名
     *
     * @param zipFile 压缩包文件
     * @return 文件名集合
     */
    @Nullable
    public static ArrayList<String> getEntriesNamesNew(File zipFile) {
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> result;
        FileInputStream fileInputStream = null;
        ZipInputStream zipInputStream = null;
        try {
            fileInputStream = new FileInputStream(zipFile);
            zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry nextEntry;
            do {
                nextEntry = zipInputStream.getNextEntry();
                if (nextEntry == null) {
                    break;
                }
                if (nextEntry.isDirectory()){
                    continue;
                }
                nameList.add(nextEntry.getName());
                zipInputStream.closeEntry();
            } while (true);
            result = nameList;
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 列出压缩包中的所有文件名
     *
     * @param zipFile 压缩包文件
     * @return 文件名集合
     */
    public static ArrayList<String> getEntriesNames(File zipFile) {
        ZipFile sourceFile;
        try {
            sourceFile = new ZipFile(zipFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Enumeration<? extends ZipEntry> entries = sourceFile.entries();
        if (entries == null) {
            DebugUtil.warnOut(TAG, "zip file entries null");
            return null;
        }
        ArrayList<String> fileNames = new ArrayList<>();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if (zipEntry.isDirectory()) {
                continue;
            }
            fileNames.add(zipEntry.getName());
        }

        try {
            sourceFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    /**
     * 解压缩一个文件
     *
     * @param zipFile       压缩文件
     * @param targetDirPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static boolean upZipFile(File zipFile, String targetDirPath) {
        boolean result = true;
        ZipFile sourceFile;
        try {
            sourceFile = new ZipFile(zipFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        File targetDirFile = new File(targetDirPath);
        if (!targetDirFile.exists()) {
            targetDirFile.mkdirs();
        }
        if (targetDirFile.isFile()) {
            targetDirFile.delete();
            targetDirFile.mkdirs();
        }
        Enumeration<? extends ZipEntry> entries = sourceFile.entries();
        if (entries == null) {
            DebugUtil.warnOut(TAG, "zip file entries null");
            return false;
        }
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if (zipEntry.isDirectory()) {
                continue;
            }
            File file = new File(targetDirFile, zipEntry.getName());
            if (file.isDirectory()) {
                FileUtil.deleteDirFiles(file);
            }
            if (file.exists()) {
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = false;
                    break;
                }
            }
            DebugUtil.warnOut(TAG, file.getAbsolutePath());
            FileOutputStream fileOutputStream = null;
            InputStream inputStream = null;
            try {
                inputStream = sourceFile.getInputStream(zipEntry);
                fileOutputStream = new FileOutputStream(file, true);
                byte[] data = new byte[1024];
                int count;
                do {
                    count = inputStream.read(data);
                    if (count < 0) {
                        break;
                    }
                    fileOutputStream.write(data, 0, count);
                } while (true);
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            sourceFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解压文件 新
     *
     * @param zipFile      zip压缩包
     * @param unzipDirPath 解压目录
     * @return
     */
    private static boolean upZipFileNew(File zipFile, String unzipDirPath) {
        if (!zipFile.exists()) {
            return false;
        }
        if (zipFile.isDirectory()) {
            return false;
        }
        File unzipDirFile = new File(unzipDirPath);
        if (!unzipDirFile.exists()) {
            unzipDirFile.mkdirs();
        }
        if (unzipDirFile.isFile()) {
            unzipDirFile.delete();
            unzipDirFile.mkdirs();
        }
        FileInputStream fileInputStream = null;
        ZipInputStream zipInputStream = null;
        boolean result = true;
        try {
            fileInputStream = new FileInputStream(zipFile);
            zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry nextEntry;
            do {
                nextEntry = zipInputStream.getNextEntry();
                if (nextEntry == null) {
                    break;
                }
                long size = nextEntry.getSize();
                String name = nextEntry.getName();
                File targetFile = new File(unzipDirFile, name);
                DebugUtil.warnOut(TAG, "targetFile = " + targetFile.getName());
                DebugUtil.warnOut(TAG, "size = " + size);
                byte[] data = new byte[1024];
                int count;
                FileOutputStream fileOutputStream = new FileOutputStream(targetFile, true);
                do {
                    count = zipInputStream.read(data);
                    if (count < 0) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        break;
                    }
                    fileOutputStream.write(data, 0, count);
                } while (true);
            } while (true);
            DebugUtil.warnOut(TAG, "");
            zipInputStream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}

