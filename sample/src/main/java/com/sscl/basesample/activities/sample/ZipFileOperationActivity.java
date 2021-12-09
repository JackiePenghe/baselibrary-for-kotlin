package com.sscl.basesample.activities.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.files.FileProviderUtil;
import com.sscl.baselibrary.files.FileSystemUtil;
import com.sscl.baselibrary.files.FileUtil;
import com.sscl.baselibrary.utils.BaseManager;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.baselibrary.utils.ZipUtils;
import com.sscl.basesample.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * zip文件操作界面
 *
 * @author pengh
 */
public class ZipFileOperationActivity extends BaseAppCompatActivity {

    private static final int REQUEST_CODE_ZIP = 1;

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_zip_file_operation;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 文件URI
     */
    private Uri fileUri;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 显示文件路劲的文本
     */
    private TextView fileNameTv;
    /**
     * 选择文件
     */
    private Button selectFileBtn;
    /**
     * 列出压缩包内的文件列表
     */
    private Button listFilesBtn;
    /**
     * 解压文件
     */
    private Button unzipFileBtn;
    /**
     * 文件
     */
    private FileSystemUtil.OnFileSelectedListener onFileSelectedListener = new FileSystemUtil.OnFileSelectedListener() {
        @Override
        public void fileSelected(int requestCode, Uri uri, String filePath) {
            if (requestCode == REQUEST_CODE_ZIP) {
                DebugUtil.warnOut(TAG, "fileSelected " + filePath);
                if (uri == null || filePath == null) {
                    ToastUtil.toastLong(ZipFileOperationActivity.this, R.string.no_file_selected);
                    return;
                }
                fileNameTv.setText(filePath);
                ZipFileOperationActivity.this.fileUri = uri;
                ZipFileOperationActivity.this.filePath = filePath;
            }
        }
    };

    /**
     * 点击事件的处理
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == selectFileBtn.getId()) {
                selectFile();
            } else if (id == listFilesBtn.getId()) {
                listFiles();
            } else if (id == unzipFileBtn.getId()) {
                unzipFile();
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
        selectFileBtn = findViewById(R.id.select_file_btn);
        listFilesBtn = findViewById(R.id.list_files_btn);
        fileNameTv = findViewById(R.id.file_name_tv);
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {
        unzipFileBtn = findViewById(R.id.unzip_file_btn);
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
        selectFileBtn.setOnClickListener(onClickListener);
        listFilesBtn.setOnClickListener(onClickListener);
        unzipFileBtn.setOnClickListener(onClickListener);

        FileSystemUtil.setOnFileSelectedListener(onFileSelectedListener);

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
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileSystemUtil.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectFileBtn = null;
        listFilesBtn = null;
        onClickListener = null;
        FileSystemUtil.setOnFileSelectedListener(null);
        onFileSelectedListener = null;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 选择文件
     */
    private void selectFile() {
        boolean b = FileSystemUtil.openSystemFile(this, REQUEST_CODE_ZIP, FileSystemUtil.FileType.ZIP_FILE);
        if (!b) {
            ToastUtil.toastLong(this, R.string.open_file_manager_failed);
        }
    }

    /**
     * 列出压缩包中的文件列表
     */
    private void listFiles() {
        if (fileUri == null || filePath == null) {
            ToastUtil.toastLong(this, R.string.no_file_selected);
            return;
        }
        ArrayList<String> entriesNames = ZipUtils.getEntriesNamesNew(new File(filePath));
        showFileListDialog(entriesNames);
    }

    /**
     * 显示文件列表的对话框
     *
     * @param entriesNames 文件列表
     */
    private void showFileListDialog(ArrayList<String> entriesNames) {
        String[] items;
        if (entriesNames != null) {
            String[] cache = new String[entriesNames.size()];
            items = entriesNames.toArray(cache);
        } else {
            items = new String[]{"空文件列表"};
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.file_list)
                .setItems(items, null)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, null)
                .show();
    }

    /**
     * 解压文件
     */
    private void unzipFile() {
        if (fileUri == null || filePath == null) {
            ToastUtil.toastLong(this, R.string.no_file_selected);
            return;
        }
        File file = new File(filePath);
        DebugUtil.warnOut(TAG, "开始解压");
        ZipUtils.unzip(file, FileUtil.getSdCardCacheDir().getPath() + "/unzipFiles", new ZipUtils.OnFileZipListener() {
            @Override
            public void unzipSucceed(String unzipDir) {
                DebugUtil.warnOut(TAG, "解压完成");
                ToastUtil.toastLong(ZipFileOperationActivity.this, R.string.unzip_file_succeed);
//                FileUtil.deleteDirFiles(new File(unzipDir));
                DebugUtil.warnOut(TAG, "file dir " + unzipDir);
            }

            @Override
            public void unzipFailed() {
                DebugUtil.warnOut(TAG, "解压失败");
                ToastUtil.toastLong(ZipFileOperationActivity.this, R.string.unzip_file_failed);
            }
        });
    }
}