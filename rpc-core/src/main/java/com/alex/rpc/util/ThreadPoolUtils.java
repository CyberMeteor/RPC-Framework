package com.alex.rpc.util;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public final class ThreadPoolUtils {
    private static final Map<String, ExecutorService> THREAD_POOL_CACHE = new ConcurrentHashMap<>();
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    private static final int CPU_INTENSIVE_NUM = CPU_NUM + 1;
    private static final int IO_INTENSIVE_NUM = CPU_NUM * 2;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 60;
    private static final int DEFAULT_QUEUE_SIZE = 128;

    public static ExecutorService createCpuIntensiveThreadPool(String poolName) {
        return createThreadPool(CPU_INTENSIVE_NUM, poolName);
    }

    public static ExecutorService createIoIntensiveThreadPool(String poolName) {
        return createThreadPool(IO_INTENSIVE_NUM, poolName);
    }

    public static ExecutorService createThreadPool(
            int corePoolSize,
            String poolName
    ) {
        return createThreadPool(corePoolSize, corePoolSize, poolName);
    }

    public static ExecutorService createThreadPool(
            int corePoolSize,
            int maxPoolSize,
            String poolName
    ) {
        return createThreadPool(corePoolSize, maxPoolSize, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_QUEUE_SIZE, poolName);
    }

    public static ExecutorService createThreadPool(
            int corePoolSize,
            int maxPoolSize,
            long keepAliveTime,
            int queueSize,
            String poolName
    ) {
        return createThreadPool(corePoolSize, maxPoolSize, keepAliveTime, queueSize, poolName, false);
    }

    public static ExecutorService createThreadPool(
            int corePoolSize,
            int maxPoolSize,
            long keepAliveTime,
            int queueSize,
            String poolName,
            boolean isDaemon
    ) {
        if (THREAD_POOL_CACHE.containsKey(poolName)) {
            return THREAD_POOL_CACHE.get(poolName);
        }

         ExecutorService executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize),
                 createThreadFactory(poolName, isDaemon)
        );

        THREAD_POOL_CACHE.put(poolName, executorService);
        return executorService;
    }

    public static ThreadFactory createThreadFactory(String poolName) {
        return createThreadFactory(poolName, false);
    }

    public static ThreadFactory createThreadFactory(String poolName, boolean isDaemon) {
        ThreadFactoryBuilder threadFactoryBuilder = ThreadFactoryBuilder.create()
                .setDaemon(isDaemon);

        if (StrUtil.isBlank(poolName)) {
            return threadFactoryBuilder.build();
        }

        return threadFactoryBuilder.setNamePrefix(poolName).build();
    }

    public static void shutdownAll() {
        THREAD_POOL_CACHE.entrySet().parallelStream()
                .forEach(entry -> {
                    String poolName = entry.getKey();
                    ExecutorService executorService = entry.getValue();

                    executorService.shutdown();

                    log.info("start shutdown thread pool: {}", poolName);

                    try {
                        if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                            log.info("{} thread pool has been shutdown", poolName);
                        }  else {
                            log.info("{} thread pool is not shutdown in 10 seconds, forcing it to shutdown", poolName);
                            executorService.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        log.error("Exceptions occurred when stopping {} thread pool ", poolName);
                        executorService.shutdownNow();
                    }
                });
    }
}
