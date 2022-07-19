package com.sscl.baselibrary.utils

import android.content.Context
import android.speech.tts.TextToSpeech.OnInitListener
import kotlin.jvm.Volatile
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

/**
 * @author LiangYaLong
 * 描述:         语音播报
 * 时间:     2021/3/25
 * 版本:     1.0
 */
object TTsUtils {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 初始化监听器
     */
    interface OnInitListener {
        /**
         * 初始化成功
         */
        fun initSucceed()

        /**
         * 初始化失败
         */
        fun initFailed()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    private var tts: TextToSpeech? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    fun init(context: Context, onInitListener: OnInitListener?) {
        tts = TextToSpeech(context) {
            val result = tts?.setLanguage(Locale.CHINA)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "当前TTS不支持中文")
                onInitListener?.initFailed()
                tts = null
            } else {
                Log.e("TTS", "TTS初始化成功")
                onInitListener?.initSucceed()
            }
        }
    }

    /**
     * 语音播报
     *
     * @param text 语音播报内容
     * @param add  是否为队列模式
     */
    fun speakText(text: String, add: Boolean) {
        if (add) {
            tts?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
        } else {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}