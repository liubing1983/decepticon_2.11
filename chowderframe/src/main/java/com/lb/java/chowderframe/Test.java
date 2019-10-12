package com.lb.java.chowderframe;

import com.lb.java.chowderframe.utils.GuavaCacheUtils;
import com.lb.java.chowderframe.utils.ZookeeperUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import java.lang.String;
import java.lang.StringBuilder;

public class Test {

    // 线程池
    private Executor executor = Executors.newSingleThreadExecutor();

    // 初始化zk
     static ZookeeperUtils zku = new ZookeeperUtils();

    // guava cache
    private static GuavaCacheUtils<String, String> cacheUtils;

    /**
     * 异步执行zk节点监控
     */
    FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
        public Integer call() throws InterruptedException {
            // 执行zk节点监控
            zku.PathChildrenCacheWatcher(cacheUtils);
            return Integer.valueOf(0);
        }
    });

    /**
     * 启动监控过程
     */
    public void execute() {
        // 通过线程池启动一个额外的线程加载Watching过程
        executor.execute(task);
    }


    public  static void  main(String[] args) throws  Exception{
        Test t = new Test();
        // 启动缓存
        cacheUtils = new GuavaCacheUtils();
        // 使用另一个线程启动zk watcher
        t.execute();

        // 模拟
        for(int i = 0 ; i < 10000 ; i++){
            System.out.println( cacheUtils.getCallableCache("port"));
            Thread.sleep(5000);
        }
    }
}
