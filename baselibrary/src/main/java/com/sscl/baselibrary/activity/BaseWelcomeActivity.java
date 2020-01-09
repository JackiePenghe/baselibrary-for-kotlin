package com.sscl.baselibrary.activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sscl.baselibrary.R;
import com.sscl.baselibrary.utils.StatusBarUtil;


/**
 * 欢迎页
 *
 * @author alm
 */
public abstract class BaseWelcomeActivity extends BaseAppCompatActivity {

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 欢迎页的图片
     */
    private ImageView imageView;
    /**
     * 欢迎页图片的动画
     */
    private Animation animation;
    /**
     * 欢迎页图片动画的监听
     */
    private Animation.AnimationListener animationListener;

    /*--------------------------------实现父类方法--------------------------------*/

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
    @Override
    protected void titleBackClicked() {

    }


    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {
        animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doAfterAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_welcome;
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    @Override
    protected void doBeforeInitOthers() {
        hideTitleBar();
        StatusBarUtil.hideFakeStatusBarView(BaseWelcomeActivity.this);
    }

    /**
     * 初始化布局控件
     */
    @Override
    protected void initViews() {
        imageView = findViewById(R.id.welcome_activity_image_view);
    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {
        int imageSource = setImageViewSource();
        if (imageSource != 0) {
            try {
                imageView.setImageResource(imageSource);
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.baselibrary_default_main);
            }
        } else {
            imageView.setImageResource(R.drawable.baselibrary_default_main);
        }
        animation = AnimationUtils.loadAnimation(BaseWelcomeActivity.this, R.anim.com_jackiepenghe_baselibrary_anim_welcome);
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
        animation.setAnimationListener(animationListener);
        imageView.startAnimation(animation);
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

    /*--------------------------------重写父类方法--------------------------------*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageView = null;
        animation = null;
        animationListener = null;
    }

    /*--------------------------------抽象方法--------------------------------*/

    /**
     * 当动画执行完成后调用这个方法
     */
    protected abstract void doAfterAnimation();

    /**
     * 设置ImageView的图片资源
     *
     * @return 图片资源ID
     */
    @DrawableRes
    protected abstract int setImageViewSource();
}
