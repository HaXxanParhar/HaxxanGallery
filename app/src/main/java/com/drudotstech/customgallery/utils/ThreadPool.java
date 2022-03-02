package com.drudotstech.customgallery.utils;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/********** Developed by Drudots Technology **********
 * Created by : usman on 06/08/2021 at 3:14 PM
 ******************************************************/


public class ThreadPool {

    private static ThreadPoolExecutor threadPoolExecutor;
    private OnCompletedCallback callback;

    public ThreadPool(OnCompletedCallback callback) {
        this.callback = callback;
    }

    /**
     * call this method in the Application class to initialize the thread pool
     */
    public static void initThreadPool() {

        // Sets the amount of time an idle thread waits before terminating
        final int KEEP_ALIVE_TIME = 1;
        // Sets the Time Unit to seconds
        final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        /*
         * Gets the number of available cores
         * (not always the same as the maximum number of cores)
         */
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
        // Creates a thread pool manager
        threadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                workQueue);
    }

    public static ThreadPoolExecutor getInstance() {
        if (threadPoolExecutor == null)
            initThreadPool();

        return threadPoolExecutor;
    }


    public interface OnCompletedCallback {
        void onCompleted(HashMap<String, Object> hashMap);
    }


}
