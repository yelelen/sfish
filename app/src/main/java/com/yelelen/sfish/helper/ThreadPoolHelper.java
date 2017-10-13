package com.yelelen.sfish.helper;

import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yelelen on 17-9-4.
 */

public class ThreadPoolHelper {
    private volatile static ThreadPoolHelper mInstance;
    private ThreadPoolExecutor mExecutor;

    private ThreadPoolHelper() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        int cores = Runtime.getRuntime().availableProcessors();
//        Log.e("ThreadPoolHelper -> ", cores + "");
        mExecutor = new ThreadPoolExecutor(cores, cores * 2,
                10, TimeUnit.SECONDS, queue, new SfishThreadFactory());
    }

    public static ThreadPoolHelper getInstance() {
        if (mInstance == null) {
            synchronized (ThreadPoolHelper.class) {
                if (mInstance == null) {
                    mInstance = new ThreadPoolHelper();
                }
            }
        }
        return mInstance;
    }

    public void start(Runnable runnable) {
        getInstance().mExecutor.execute(runnable);
    }

    public boolean isTerminated() {
        return getInstance().mExecutor.isTerminated();
    }

    class SfishThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r, "SfishThread");
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    }

}
