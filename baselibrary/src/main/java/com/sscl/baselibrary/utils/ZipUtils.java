package com.sscl.baselibrary.utils;

import androidx.annotation.Nullable;

import com.sscl.baselibrary.files.FileUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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
         *
         * @param e 异常信息
         */
        void unzipFailed(IOException e);
    }

    /**
     * 批量压缩文件（夹）
     *
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile     生成的压缩文件
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFiles(Collection<File> resFileList, File zipFile) throws IOException {
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        for (File resFile : resFileList) {
            zipFile(resFile, zipout, "");
        }
        zipout.close();
    }

    /**
     * 批量压缩文件（夹）
     *
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile     生成的压缩文件
     * @param comment     压缩文件的注释
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFiles(Collection<File> resFileList, File zipFile, String comment)
            throws IOException {
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        for (File resFile : resFileList) {
            zipFile(resFile, zipout, "");
        }
        zipout.setComment(comment);
        zipout.close();
    }

    public static void unzip(final File file, @Nullable final OnFileZipListener onFileZipListener) {
        BaseManager.getThreadFactory().newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String unzipDir = FileUtil.getCacheDir() + "/unzipFiles";
                    upZipFile(file, unzipDir);
                    DebugUtil.warnOut(TAG, "解压完成");
                    if (onFileZipListener != null) {
                        onFileZipListener.unzipSucceed(unzipDir);
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                    DebugUtil.warnOut(TAG, "解压失败");
                    if (onFileZipListener != null) {
                        onFileZipListener.unzipFailed(e);
                    }
                }
            }
        }).start();
    }

    public static void unzipCallbackOnMainThread(final File file, @Nullable final OnFileZipListener onFileZipListener) {
        BaseManager.getThreadFactory().newThread(new Runnable() {
            @Override
            public void run() {
                try {
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
                } catch (final IOException e) {
                    e.printStackTrace();
                    DebugUtil.warnOut(TAG, "解压失败");
                    BaseManager.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (onFileZipListener != null) {
                                onFileZipListener.unzipFailed(e);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 解压缩一个文件
     *
     * @param zipFile    压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void upZipFile(File zipFile, String folderPath) throws IOException {
        File desDir = new File(folderPath);
        if (desDir.exists()) {
            FileUtil.deleteDirFiles(desDir);
        }
        desDir.mkdirs();
        ZipFile zf;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            zf = new ZipFile(zipFile, Charset.forName(readFileCharset(zipFile)));
        } else {
            zf = new ZipFile(zipFile);
        }
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            InputStream in = zf.getInputStream(entry);
            File desFile = new File(folderPath, entry.getName());
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                } else {
                    if (fileParentDir.isFile()) {
                        fileParentDir.mkdirs();
                    }
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte buffer[] = new byte[BUFF_SIZE];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }
    }

    /**
     * 解压文件名包含传入文字的文件
     *
     * @param zipFile      压缩文件
     * @param folderPath   目标文件夹
     * @param nameContains 传入的文件匹配名
     * @throws ZipException 压缩格式有误时抛出
     * @throws IOException  IO错误时抛出
     */
    public static ArrayList<File> upZipSelectedFile(File zipFile, String folderPath,
                                                    String nameContains) throws ZipException, IOException {
        ArrayList<File> fileList = new ArrayList<File>();

        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdir();
        }
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            if (entry.getName().contains(nameContains)) {
                InputStream in = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                str = new String(str.getBytes("8859_1"), "GB2312");
                // str.getBytes("GB2312"),"8859_1" 输出
                // str.getBytes("8859_1"),"GB2312" 输入
                File desFile = new File(str);
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                OutputStream out = new FileOutputStream(desFile);
                byte buffer[] = new byte[BUFF_SIZE];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
                in.close();
                out.close();
                fileList.add(desFile);
            }
        }
        return fileList;
    }

    /**
     * 获得压缩文件内文件列表
     *
     * @param zipFile 压缩文件
     * @return 压缩文件内文件名称
     * @throws ZipException 压缩文件格式有误时抛出
     * @throws IOException  当解压缩过程出错时抛出
     */
    public static ArrayList<String> getEntriesNames(File zipFile) throws ZipException, IOException {
        ArrayList<String> entryNames = new ArrayList<>();
        Enumeration<?> entries = getEntriesEnumeration(zipFile);
        while (entries.hasMoreElements()) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            entryNames.add(new String(getEntryName(entry).getBytes()));
        }
        return entryNames;
    }

    /**
     * 获得压缩文件内压缩文件对象以取得其属性
     *
     * @param zipFile 压缩文件
     * @return 返回一个压缩文件列表
     * @throws ZipException 压缩文件格式有误时抛出
     * @throws IOException  IO操作有误时抛出
     */
    public static Enumeration<?> getEntriesEnumeration(File zipFile) throws ZipException,
            IOException {
        ZipFile zf;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            zf = new ZipFile(zipFile, ZipFile.OPEN_READ, Charset.forName(readFileCharset(zipFile)));
        } else {
            zf = new ZipFile(zipFile);
        }
        return zf.entries();
    }

    public static String readFileCharset(File file) {
        String code = "US-ASCII";
        try {

            FileInputStream input = new FileInputStream(file);

            int pre = (input.read() << 8) + input.read();


            switch (pre) {
                case 0xefbb:
                    if (input.read() == 0xbf) {
                        code = "UTF-8";
                    }
                    break;
                case 0xfffe:
                    code = "Unicode";
                    break;
                case 0xfeff:
                    code = "UTF-16BE";
                    break;
                default:
                    code = "GBK";
                    break;
            }
            System.out.println("CodeType: " + code);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String readFileCharset(String path) {
        File file = new File(path);
        return readFileCharset(file);
    }

    /**
     * 取得压缩文件对象的注释
     *
     * @param entry 压缩文件对象
     * @return 压缩文件对象的注释
     * @throws UnsupportedEncodingException
     */
    public static String getEntryComment(ZipEntry entry) throws UnsupportedEncodingException {
        return new String(entry.getComment().getBytes("GB2312"), "8859_1");
    }

    /**
     * 取得压缩文件对象的名称
     *
     * @param entry 压缩文件对象
     * @return 压缩文件对象的名称
     * @throws UnsupportedEncodingException
     */
    public static String getEntryName(ZipEntry entry) throws UnsupportedEncodingException {
        return new String(entry.getName().getBytes());
    }

    /**
     * 压缩文件
     *
     * @param resFile  需要压缩的文件（夹）
     * @param zipout   压缩的目的文件
     * @param rootpath 压缩的文件路径
     * @throws FileNotFoundException 找不到文件时抛出
     * @throws IOException           当压缩过程出错时抛出
     */
    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
            throws FileNotFoundException, IOException {
        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
                + resFile.getName();
        rootpath = new String(rootpath.getBytes());
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipout, rootpath);
            }
        } else {
            byte buffer[] = new byte[BUFF_SIZE];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
                    BUFF_SIZE);
            zipout.putNextEntry(new ZipEntry(rootpath));
            int realLength;
            while ((realLength = in.read(buffer)) != -1) {
                zipout.write(buffer, 0, realLength);
            }
            in.close();
            zipout.flush();
            zipout.closeEntry();
        }
    }
}

