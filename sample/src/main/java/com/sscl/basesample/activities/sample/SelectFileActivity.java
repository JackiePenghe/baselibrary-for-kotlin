package com.sscl.basesample.activities.sample;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.files.FileSystemUtil;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.basesample.R;

public class SelectFileActivity extends BaseAppCompatActivity {

    private static final int SELECT_ANY_FILE = 1;
    private static final int SELECT_TEXT_FILE = 2;
    private static final int SELECT_BINARY_FILE = 3;
    private static final String TAG = SelectFileActivity.class.getSimpleName();
    /**
     * 选择任意文件
     */
    private Button selectAnyFileBtn;

    /**
     * 选择文本文件
     */
    private Button selectTextFileBtn;

    /**
     * 选择二进制文件
     */
    private Button selectBinaryFileBtn;

    /**
     * 点击事件的监听
     */
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.select_any_file) {
                selectAnyFile();
            } else if (id == R.id.select_text_file) {
                selectTextFile();
            } else if (id == R.id.select_binary_file) {
                selectBinaryFile();
            }
        }
    };
    private final FileSystemUtil.OnFileSelectedListener onFileSelectedListener = (requestCode, uri, filePath) -> {
        switch (requestCode) {
            case SELECT_ANY_FILE:
                showDialog(getString(R.string.select_any_file), filePath, uri);
                break;
            case SELECT_TEXT_FILE:
                showDialog(getString(R.string.select_text_file), filePath, uri);
                break;
            case SELECT_BINARY_FILE:
                showDialog(getString(R.string.select_binary_file), filePath, uri);
                break;
            default:
                break;
        }
    };

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
    @Override
    protected void titleBackClicked() {
        onBackPressed();
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {
        getIntentData();
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_select_file;
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
        selectAnyFileBtn = findViewById(R.id.select_any_file);
        selectTextFileBtn = findViewById(R.id.select_text_file);
        selectBinaryFileBtn = findViewById(R.id.select_binary_file);
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
        selectAnyFileBtn.setOnClickListener(onClickListener);
        selectTextFileBtn.setOnClickListener(onClickListener);
        selectBinaryFileBtn.setOnClickListener(onClickListener);
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

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileSystemUtil.onActivityResult(this, requestCode, resultCode, data);
    }

    /**
     * Handle onNewIntent() to inform the fragment manager that the
     * state is not saved.  If you are handling new intents and may be
     * making changes to the fragment state, you want to be sure to call
     * through to the super-class here first.  Otherwise, if your state
     * is saved but the activity is not stopped, you could get an
     * onNewIntent() call which happens before onResume() and trying to
     * perform fragment operations at that point will throw IllegalStateException
     * because the fragment manager thinks the state is still saved.
     *
     * @param intent Intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentData(intent);
    }

    private void selectAnyFile() {
        boolean succeed = FileSystemUtil.openSystemFile(this, SELECT_ANY_FILE);
        if (!succeed) {
            ToastUtil.toastLong(this, R.string.open_file_manager_failed);
        }
    }

    private void selectTextFile() {
        boolean succeed = FileSystemUtil.openSystemFile(this, SELECT_TEXT_FILE, FileSystemUtil.FileType.TEXT_FILE);
        if (!succeed) {
            ToastUtil.toastLong(this, R.string.open_file_manager_failed);
        }
    }

    private void selectBinaryFile() {
        boolean succeed = FileSystemUtil.openSystemFile(this, SELECT_BINARY_FILE, FileSystemUtil.FileType.OCTET_STREAM_FILE);
        if (!succeed) {
            ToastUtil.toastLong(this, R.string.open_file_manager_failed);
        }
    }

    /**
     * 显示对话框
     *
     * @param title    对话框标题
     * @param filePath 文件路径
     * @param uri      文件URI
     */
    private void showDialog(@NonNull String title, @Nullable String filePath, @Nullable Uri uri) {

        String message = "filePath = " + filePath + "\n" + "uri = " + uri;
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, null)
                .show();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        String dataString = intent.getDataString();
        DebugUtil.warnOut(TAG, "dataString = " + dataString);
        if (dataString == null) {
            return;
        }
        Uri parse = Uri.parse(dataString);
        String path = FileSystemUtil.getPath(this, parse);
        DebugUtil.warnOut(TAG, "path = " + path);
    }

    private void getIntentData(Intent intent) {
        String dataString = intent.getDataString();
        DebugUtil.warnOut(TAG, "dataString = " + dataString);
        if (dataString == null) {
            return;
        }
        Uri parse = Uri.parse(dataString);
        String path = FileSystemUtil.getPath(this, parse);
        DebugUtil.warnOut(TAG, "path = " + path);
    }
}
