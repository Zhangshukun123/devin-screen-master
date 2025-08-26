package com.diwen.liliao.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created By  tian on 2020/4/11
 * Describe:   线程池
 */
public class ThreadPoolUtils {
    private static ExecutorService threadPool = null;
    public static synchronized ExecutorService getInstance() {
        if (threadPool == null) {
            synchronized (ExecutorService.class) {
                if (threadPool == null) {
                    // threadPool = Executors.newCachedThreadPool();
                    threadPool = Executors.newCachedThreadPool();
                }
            }
        }
        return threadPool;
    }
    
}
