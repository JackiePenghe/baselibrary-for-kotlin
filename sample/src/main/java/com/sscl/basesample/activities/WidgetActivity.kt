package com.sscl.basesample.activities

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sscl.baselibrary.activity.BaseDrawerActivity
import com.sscl.basesample.R
import com.sscl.basesample.activities.widget.ArcProgressBarActivity
import com.sscl.basesample.activities.widget.CircleProgressBarActivity
import com.sscl.basesample.activities.widget.FlowLayoutActivity

/**
 * @author pengh
 */
class WidgetActivity : BaseDrawerActivity() {
    override fun doBeforeSetLayout() {}
    override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_activity_widget
    }

    override fun doBeforeInitOthers() {}
    override fun initViews() {}
    override fun initViewData() {}
    override fun initOtherData() {}
    override fun initEvents() {}
    override fun doAfterAll() {}

    override fun drawerSlide(drawerView: View, slideOffset: Float) {}
    override fun drawerOpened(drawerView: View) {}
    override fun drawerClosed(drawerView: View) {}
    override fun drawerStateChanged(newState: Int) {}
    override fun setNavigationViewHeaderViewLayoutResId(): Int {
        return 0
    }

    override fun setNavigationMenuResId(): Int {
        return R.menu.com_sscl_basesample_navigation_activity_widget
    }

    override fun navigationItemSelected(menuItemId: Int) {
        var intent: Intent? = null
        when (menuItemId) {
            R.id.arc_progress_bar -> {
                intent = Intent(this@WidgetActivity, ArcProgressBarActivity::class.java)
            }
            R.id.circle_progress_bar -> {
                intent = Intent(this@WidgetActivity, CircleProgressBarActivity::class.java)
            }
            R.id.flow_layout -> {
                intent = Intent(this@WidgetActivity, FlowLayoutActivity::class.java)
            }
        }
        if (intent != null) {
            startActivity(intent)
        }
    }
}