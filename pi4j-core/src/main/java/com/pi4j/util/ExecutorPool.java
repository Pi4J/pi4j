package com.pi4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.*;

public class ExecutorPool {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorPool.class);

    private final Map<String, ExecutorService> executors;
    private final Map<String, ScheduledExecutorService> scheduledExecutors;

    public ExecutorPool() {
        this.executors = new ConcurrentHashMap<>();
        this.scheduledExecutors = new ConcurrentHashMap<>();
    }

    public ExecutorService getExecutor(String poolName) {
        if (poolName == null || poolName.isEmpty())
            throw new IllegalStateException("poolName must be set!");
        return this.executors.computeIfAbsent(poolName, p -> newCachedThreadPool(new NamedThreadPoolFactory(p)));
    }

    public ExecutorService getSingleThreadExecutor(String poolName) {
        if (poolName == null || poolName.isEmpty())
            throw new IllegalStateException("poolName must be set!");
        return this.executors.computeIfAbsent(poolName, p -> newSingleThreadExecutor(new NamedThreadPoolFactory(p)));
    }

    public ScheduledExecutorService getScheduledExecutor(String poolName) {
        if (poolName == null || poolName.isEmpty())
            throw new IllegalStateException("poolName must be set!");
        return this.scheduledExecutors.computeIfAbsent(poolName,
            p -> newScheduledThreadPool(4, new NamedThreadPoolFactory(p)));
    }

    public void destroy() {
        this.executors.forEach(this::shutdownExecutor);
        this.scheduledExecutors.forEach(this::shutdownExecutor);
    }

    private void shutdownExecutor(String name, ExecutorService executor) {
        logger.info("Shutting down executor pool {}", name);
        try {
            List<Runnable> tasks = executor.shutdownNow();
            if (!tasks.isEmpty()) {
                logger.warn("The following {} tasks were never started for executor {} :", tasks.size(), name);
                for (Runnable runnable : tasks) {
                    //noinspection StringConcatenationArgumentToLogCall avoid risk of multithreaded access to arbitrary objects via async logging
                    logger.warn("  " + runnable);
                }
            }

            if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                logger.error("Executor {} did not stop after 5s!", name);
        } catch (InterruptedException e) {
            logger.error("Was interrupted while shutting down tasks");
        }
    }

    private static class NamedThreadPoolFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String poolName;

        public NamedThreadPoolFactory(String poolName) {
            this.group = Thread.currentThread().getThreadGroup();
            this.poolName = poolName + "-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.poolName + this.threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
