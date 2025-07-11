package net.cactus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 任务执行器配置类
 * 提供文档转换等异步任务的执行器
 */
@Configuration
public class TaskExecutorConfig {

    /**
     * 配置文档转换任务执行器
     * 即使JODConverter被禁用，这个Bean也会被创建，避免依赖注入失败
     */
    @Bean("documentConversionTaskExecutor")
    public TaskExecutor documentConversionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("DocConvert-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
