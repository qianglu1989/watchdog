package global.redefine.watchdog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by QIANG on 2017/9/27
 * @author QIANG
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ExecutorConfig {

    @Value("${collection.executor.corePoolSize:20}")
    private int corePoolSize;

    @Value("${collection.executor.maxPoolSize:500}")
    private int maxPoolSize;

    @Value("${collection.executor.queueCapacity:10000}")
    private int queueCapacity;



    @Bean(name = "collection")
    public Executor kafkaConsumer() {

        return createExecutor(corePoolSize,maxPoolSize,queueCapacity,"collection-");
    }

    @Bean(name = "trace")
    public Executor trace() {

        return createExecutor(corePoolSize,maxPoolSize,queueCapacity,"trace-");
    }

    @Bean(name = "statement")
    public Executor statement() {

        return createExecutor(corePoolSize,maxPoolSize,queueCapacity,"statement-");
    }


    private Executor createExecutor(int corePoolSize ,int maxPoolSize,int queueCapacity,String threadName){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.setThreadNamePrefix(threadName);
        executor.initialize();
        return executor;
    }



}