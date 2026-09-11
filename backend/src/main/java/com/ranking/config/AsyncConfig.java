package com.ranking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "rankingRefreshExecutor")
    public Executor rankingRefreshExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); //스레드 수
        executor.setMaxPoolSize(2); //최대 스레드 수
        executor.setQueueCapacity(10); //스레드풀일때 대기큐 수
        executor.setThreadNamePrefix("ranking-refresh-"); //스레드 prefix
        executor.initialize();
        return executor;
    }
}
