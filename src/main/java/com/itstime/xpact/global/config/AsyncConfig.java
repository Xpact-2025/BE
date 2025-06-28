package com.itstime.xpact.global.config;

import com.itstime.xpact.global.exception.CustomException;
import com.itstime.xpact.global.exception.ErrorCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;

@Configuration
@EnableAsync
public class AsyncConfig {

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 30;
    private static final int QUEUE_CAPACITY = 100;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("Async-");
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setRejectedExecutionHandler(rejectedExecutionHandler());
        executor.initialize();
        return executor;
    }
}
