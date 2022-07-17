package com.sscl.baselibrary.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory

/**
 * 管理器
 *
 * @author jackie
 */
object BaseManager {

    /*--------------------------------静态常量--------------------------------*/

    /**
     * Handler
     */
    val handler = Handler(Looper.getMainLooper())

    /**
     * 线程工厂
     */
    val threadFactory = ThreadFactory { r -> Thread(r) }

    fun newThreadFactory(): ThreadFactory {
        return ThreadFactory { r ->
            /**
             * Constructs a new `Thread`.  Implementations may also initialize
             * priority, name, daemon status, `ThreadGroup`, etc.
             *
             * @param r a runnable to be executed by new thread instance
             * @return constructed thread, or `null` if the request to
             * create a thread is rejected
             */
            /**
             * Constructs a new `Thread`.  Implementations may also initialize
             * priority, name, daemon status, `ThreadGroup`, etc.
             *
             * @param r a runnable to be executed by new thread instance
             * @return constructed thread, or `null` if the request to
             * create a thread is rejected
             */
            Thread(r)
        }
    }

    fun newScheduledExecutorService(corePoolSize: Int): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(corePoolSize, threadFactory)
    }

    fun newScheduledExecutorService(
        corePoolSize: Int,
        threadFactory: ThreadFactory?
    ): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(corePoolSize, threadFactory)
    }
}