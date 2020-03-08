package com.sscl.basesample.activities.sample;

import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.SystemUtil;
import com.sscl.basesample.R;
import com.sscl.basesample.adapter.SampleAdapter;

import java.util.ArrayList;

/**
 * @author jacke
 */
public class AllPurposeAdapterActivity extends BaseAppCompatActivity {

    private ListView listView;
    private ArrayList<String> dataList = new ArrayList<>();

    /**
     * 标题栏的返回按钮被按下的时候回调此函数
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
        for (int i = 0; i < 4; i++) {
            dataList.add("test" + i);
        }
        SystemUtil.setNavigationBar(this, View.GONE);
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_all_purpose_adapter;
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
        listView = findViewById(R.id.list_view);
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {
        SampleAdapter adapter = new SampleAdapter(dataList);
        listView.setAdapter(adapter);
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
}
