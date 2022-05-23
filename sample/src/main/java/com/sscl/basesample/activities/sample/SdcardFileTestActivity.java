package com.sscl.basesample.activities.sample;

import androidx.annotation.NonNull;

import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.files.FileUtil;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.basesample.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * SD卡根目录下的文件读写测试
 *
 * @author jackie
 */
public class SdcardFileTestActivity extends BaseAppCompatActivity {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private int count;
    /**
     * 写入文件按钮
     */
    private Button writeFileBtn;
    /**
     * 读取文件按钮
     */
    private Button readFileBtn;
    /**
     * 点击事件监听器
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == writeFileBtn.getId()) {
                // 写入文件
                writeFile();
            } else if (id == readFileBtn.getId()) {
                readFile();
            }
        }
    };

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
    @Override
    protected boolean titleBackClicked() {
        return false;
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {

    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.com_sscl_basesample_activity_sdcard_file_test;
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    @Override
    protected void doBeforeInitOthers() {

    }

    /**
     * 初始化布局控件
     */
    @Override
    protected void initViews() {
        writeFileBtn = findViewById(R.id.write_file_btn);
        readFileBtn = findViewById(R.id.read_file_btn);
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {

    }

    /**
     * 初始化其他数据
     */
    @Override
    protected void initOtherData() {

    }

    /**
     * 初始化事件
     */
    @Override
    protected void initEvents() {
        writeFileBtn.setOnClickListener(onClickListener);
        readFileBtn.setOnClickListener(onClickListener);
    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {

    }

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    @Override
    protected boolean createOptionsMenu(@NonNull Menu menu) {
        return false;
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    @Override
    protected boolean optionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 写入文件
     */
    private void writeFile() {
        //获取SD卡的根目录
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        //获取SD卡的根目录的路径
        String externalStorageDirectoryPath = externalStorageDirectory.getPath();
        //在SD卡中创建一个文件夹，文件夹的名字为“gc_ad”
        File file = new File(externalStorageDirectoryPath + File.separator + "gc_ad");
        //判断文件是否为文件夹
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }
        //在目录中创建一个文件，文件名为“test.txt”
        File testFile = new File(file, "test.txt");
        if (testFile.exists()) {
            testFile.delete();
        }
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            DebugUtil.warnOut(TAG, "test.txt 文件创建失败");
            ToastUtil.toastLong(this, "test.txt 文件创建失败");
            return;
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(testFile);
            fileWriter.append("test").append(String.valueOf(count++));
            fileWriter.flush();
            ToastUtil.toastLong(this,"文件写入成功");
        } catch (IOException e) {
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

    /**
     * 读取文件
     */
    private void readFile() {
        //获取SD卡的根目录
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        //获取SD卡的根目录的路径
        String externalStorageDirectoryPath = externalStorageDirectory.getPath();
        //指定文件夹路径
        File file = new File(externalStorageDirectoryPath + File.separator + "gc_ad");
        //判断文件是否为文件夹
        if (!file.isDirectory()) {
            ToastUtil.toastLong(this, "指定路径不是文件夹");
            return;
        }
        //判断文件是否存在
        if (!file.exists()) {
            ToastUtil.toastLong(this, "指定文件夹不存在");
            return;
        }
        //指定文件路径
        File testFile = new File(file, "test.txt");
        if (!testFile.isFile()) {
            ToastUtil.toastLong(this, "指定路径不是文件");
            return;
        }
        if (!testFile.exists()) {
            ToastUtil.toastLong(this, "指定文件不存在");
            return;
        }
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(testFile);
            bufferedReader = new BufferedReader(fileReader);
            StringBuilder cache = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                DebugUtil.warnOut(TAG, line);
                cache.append(line);
            }
            ToastUtil.toastLong(this, cache.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}