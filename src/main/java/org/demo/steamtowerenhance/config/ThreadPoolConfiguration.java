package org.demo.steamtowerenhance.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfiguration {

    @Bean
    public CommonThreadPool commonThreadPool() {
        final LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();
        final ThreadFactory threadFactory = new CommonThreadFactory("commonThreadPool");
        return new CommonThreadPool(
                2,
                10,
                1,
                TimeUnit.MINUTES,
                blockingQueue,
                threadFactory);
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
