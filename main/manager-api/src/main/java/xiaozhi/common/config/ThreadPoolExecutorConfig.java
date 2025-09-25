package xiaozhi.common.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@EnableAsync
@Configuration
public class ThreadPoolExecutorConfig {

    /**
     * 默认线程池
     * <p>在 @Async 注解没指定线程池的时候使用</p>
     */
    @Bean(name = "aiExecutor")
    @Primary
    public ThreadPoolTaskExecutor aiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(1000);
        executor.setQueueCapacity(0);
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("aiExecutor-");
        executor.setRejectedExecutionHandler(new CustomerCallerRunsExecutionHandler());
        executor.initialize();
        return executor;
    }

    @Bean(value = "okhttpExecutor")
    public ExecutorService okhttpExecutor() {
        return new ThreadPoolExecutor(
                10,
                1000,
                30,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new CustomerCallerRunsExecutionHandler()
        );
    }

    private static class CustomerCallerRunsExecutionHandler extends ThreadPoolExecutor.CallerRunsPolicy {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            log.warn("rejected execution of task [{}] with executor [{}]", r, e);
            super.rejectedExecution(r, e);
        }
    }

    /**
     * 公共的定时任务执行器
     *
     * @return
     */
    @Bean(name = "commonScheduledExecutorService")
    public ScheduledExecutorService commonScheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(2,
                new ThreadFactoryBuilder().setNameFormat("commonScheduled-%d").build());
    }

}
