package com.sscl.basesample.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.activity.BaseDrawerActivity;
import com.sscl.basesample.R;
import com.sscl.basesample.activities.widget.ArcProgressBarActivity;
import com.sscl.basesample.activities.widget.CircleProgressBarActivity;
import com.sscl.basesample.activities.widget.FlowLayoutActivity;

/**
 * @author pengh
 */
public class WidgetActivity extends BaseDrawerActivity {

    @Override
    protected void doBeforeSetLayout() {

    }

    @Override
    protected int setLayout() {
        return R.layout.activity_widget;
    }

    @Override
    protected void doBeforeInitOthers() {

    }

    @Override
    protected void initViews(){
    }

    @Override
    protected void initViewData() {

    }

    @Override
    protected void initOtherData() {

    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void doAfterAll() {

    }

    @Override
    protected boolean createOptionsMenu(@NonNull Menu menu) {
        return false;
    }

    @Override
    protected boolean optionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void drawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    protected void drawerOpened(View drawerView) {

    }

    @Override
    protected void drawerClosed(View drawerView) {

    }

    @Override
    protected void drawerStateChanged(int newState) {

    }

    @Override
    protected int setNavigationViewHeaderViewLayoutResId() {
        return 0;
    }

    @Override
    protected int setNavigationMenuResId() {
        return R.menu.navigation_activity_widget;
    }

    @Override
    protected void navigationItemSelected(int menuItemId) {
        Intent intent = null;
        if (menuItemId == R.id.arc_progress_bar) {
            intent = new Intent(WidgetActivity.this, ArcProgressBarActivity.class);
        } else if (menuItemId == R.id.circle_progress_bar) {
            intent = new Intent(WidgetActivity.this, CircleProgressBarActivity.class);
        } else if (menuItemId == R.id.flow_layout) {
            intent = new Intent(WidgetActivity.this, FlowLayoutActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
