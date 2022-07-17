package com.sscl.basesample.activities.sample

import com.sscl.basesample.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.TranslateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.AnimationSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.View

/**
 * 动画测试
 *
 * @author pengh
 */
class AnimationSampleActivity : AppCompatActivity() {
    private var animationView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.com_sscl_basesample_activity_animation_sample)
        animationView = findViewById(R.id.iv)
    }

    override fun onDestroy() {
        super.onDestroy()
        animationView = null
    }

    /**
     * 位移动画
     *
     * @param view View
     */
    fun translate(view: View?) {
        val animation = TranslateAnimation(0f, 100f, 0f, 200f)
        animation.repeatCount = 0
        animation.fillAfter = true
        animation.duration = 3000
        animationView!!.startAnimation(animation)
    }

    /**
     * 缩放动画
     *
     * @param view View
     */
    fun scale(view: View?) {
        val animation = ScaleAnimation(1f, 0f, 1f, 0f)
        animation.repeatCount = 0
        animation.fillAfter = true
        animation.duration = 3000
        animationView!!.startAnimation(animation)
    }

    /**
     * 缩放并位移
     * @param view View
     */
    fun scaleTranslate(view: View?) {
        val translateAnimation = TranslateAnimation(0f, 100f, 0f, 200f)
        val scaleAnimation = ScaleAnimation(1f, 0f, 1f, 0f)
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(translateAnimation)
        animationSet.repeatCount = 0
        animationSet.duration = 3000
        animationSet.isFillEnabled = true
        animationSet.fillAfter = true
        animationSet.interpolator = AccelerateDecelerateInterpolator()
        animationView!!.startAnimation(animationSet)
    }
}