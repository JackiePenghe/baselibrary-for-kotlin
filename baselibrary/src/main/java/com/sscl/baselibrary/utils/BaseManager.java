package com.sscl.baselibrary.utils;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;


/**
 * 管理器
 *
 * @author jackie
 */
public class BaseManager {

    /*--------------------------------静态常量--------------------------------*/

    /**
     * Handler
     */
    private static final Handler HANDLER = new Handler();

    /**
     * 线程工厂
     */
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    };

    /**
     * 定时任务执行服务
     */
    private static final ScheduledExecutorService SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1, THREAD_FACTORY);

    /*--------------------------------getter--------------------------------*/

    @NonNull
    public static Handler getHandler() {
        return HANDLER;
    }

    @NonNull
    public static ThreadFactory getThreadFactory() {
        return THREAD_FACTORY;
    }

    @NonNull
    @SuppressWarnings("WeakerAccess")
    public static ThreadFactory newThreadFactory() {
        return new ThreadFactory() {
            /**
             * Constructs a new {@code Thread}.  Implementations may also initialize
             * priority, name, daemon status, {@code ThreadGroup}, etc.
             *
             * @param r a runnable to be executed by new thread instance
             * @return constructed thread, or {@code null} if the request to
             * create a thread is rejected
             */
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r);
            }
        };
    }

    @NonNull
    @SuppressWarnings("unused")
    public static ScheduledExecutorService getScheduledThreadPoolExecutor() {
        return SCHEDULED_THREAD_POOL_EXECUTOR;
    }

    @NonNull
    public static ScheduledExecutorService newScheduledExecutorService(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize, newThreadFactory());
    }
}
