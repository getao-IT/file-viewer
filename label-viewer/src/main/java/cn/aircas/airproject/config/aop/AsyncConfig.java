package cn.aircas.airproject.config.aop;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程配置类
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Override
    @Bean("taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 配置核心线程数
        executor.setCorePoolSize(10);
        // 配置最大线程数
        executor.setMaxPoolSize(10);
        // 配置队列大小
        executor.setQueueCapacity(9999);
        // 配置线程池中的线程名称前缀
        executor.setThreadNamePrefix("airmessage-thread-");
        // rejection-policy： 配置线程池中的任务已经达到max size时，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程中执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }
}
