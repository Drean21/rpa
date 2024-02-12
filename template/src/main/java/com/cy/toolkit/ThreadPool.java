package com.cy.toolkit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPool 自实现简易线程池
 *
 * @author Liang Zhaoyuan
 * @version 2024/02/09 14:52
 **/
@Configuration
public class ThreadPool {
    /**
     * 根据cpu的数量动态的配置核心线程数和最大线程数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 核心线程数 = CPU核心数 + 1
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 9;
    /**
     * 线程池最大线程数 = CPU核心数 * 5 + 1
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 9 + 1;
    /**
     * 非核心线程闲置时间 = 超时1s
     */
    private static final int KEEP_ALIVE = 2;

    /**
     * 线程池的对象
     */
    private static ThreadPoolExecutor executor;


    private static ThreadPool instance;

    /**
     * 获取单例的线程池管理对象
     *
     * @return
     */
    public synchronized static ThreadPool getInstance() {
        if (instance == null) {
            instance = new ThreadPool();
        }
        return instance;
    }


    /**
     * 获取单例的线程池对象
     *
     * @return
     */
    public synchronized static ThreadPoolExecutor getSingleExecutorService() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100000),
                    threadFactory, new ThreadPoolExecutor.DiscardPolicy());
        }
        return executor;
    }


    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("rpaWorkThread" + "-%d")
            .setDaemon(true).build();

    /**
     * 使用线程池，线程池中线程的创建完全是由线程池自己来维护的，我们不需要创建任何的线程
     * 我们所需要做的事情就是往这个池子里面丢一个又一个的任务
     *
     * @param r
     */
    public void execute(Thread r) {
        if (executor == null) {
            /**
             * corePoolSize:核心线程数
             * maximumPoolSize：线程池所容纳最大线程数(workQueue队列满了之后才开启)
             * keepAliveTime：非核心线程闲置时间超时时长
             * unit：keepAliveTime的单位
             * workQueue：等待队列，存储还未执行的任务
             * threadFactory：线程创建的工厂
             * handler：异常处理机制
             */
            executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100000),
                    threadFactory, new ThreadPoolExecutor.DiscardPolicy());
        }
        try {
            /**
             *  把一个任务丢到了线程池中
             */
            executor.execute(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel(Runnable r) {
        if (r != null) {
            /**
             * 把任务移除等待队列
             */
            executor.getQueue().remove(r);
        }
    }

    public void shutdown() {
        if (this.executor != null) {
            executor.shutdown();
        }
    }

}