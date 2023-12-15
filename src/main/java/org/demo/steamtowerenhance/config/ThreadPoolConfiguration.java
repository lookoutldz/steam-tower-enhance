package org.demo.steamtowerenhance.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfiguration {

    @Value("${custom.thread-pool.core-pool-size:2}")
    private Integer corePoolSize;

    @Value("${custom.thread-pool.max-pool-size:64}")
    private Integer maxPoolSize;

    @Value("${custom.thread-pool.keep-alive-minutes:1}")
    private Integer keepAliveMinutes;

    @Value("${custom.thread-pool.queue-size:2147483647}")
    private Integer queueSize;

    @Bean
    public CommonThreadPool commonThreadPool() {
        final LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(queueSize);
        final ThreadFactory threadFactory = new CommonThreadFactory("commonThreadPool");
        return new CommonThreadPool(corePoolSize, maxPoolSize, keepAliveMinutes, TimeUnit.MINUTES, blockingQueue, threadFactory);
    }

    static class CommonThreadFactory implements ThreadFactory {
        private final String threadNamePrefix;

        CommonThreadFactory(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            final Thread newThread = new Thread(r);
            newThread.setName(threadNamePrefix + " - " + newThread.getId());
            return newThread;
        }
    }
}
