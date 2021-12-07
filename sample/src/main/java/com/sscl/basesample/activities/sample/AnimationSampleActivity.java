package com.sscl.basesample.activities.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.sscl.basesample.R;

/**
 * 动画测试
 *
 * @author pengh
 */
public class AnimationSampleActivity extends AppCompatActivity {

    private View animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_sample);
        animationView = findViewById(R.id.iv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animationView = null;
    }

    /**
     * 位移动画
     *
     * @param view View
     */
    public void translate(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 100, 0, 200);
        animation.setRepeatCount(0);
        animation.setFillAfter(true);
        animation.setDuration(3000);
        animationView.startAnimation(animation);
    }

    /**
     * 缩放动画
     *
     * @param view View
     */
    public void scale(View view) {
        ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0);
        animation.setRepeatCount(0);
        animation.setFillAfter(true);
        animation.setDuration(3000);
        animationView.startAnimation(animation);
    }

    /**
     * 缩放并位移
     * @param view View
     */
    public void scaleTranslate(View view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 100, 0, 200);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setRepeatCount(0);
        animationSet.setDuration(3000);
        animationSet.setFillEnabled(true);
        animationSet.setFillAfter(true);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationView.startAnimation(animationSet);

    }
}