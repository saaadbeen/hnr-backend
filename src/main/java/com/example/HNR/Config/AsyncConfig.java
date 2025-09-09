package com.example.HNR.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Configurateur principal pour les tâches asynchrones (notifications, emails)
     */
    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);          // Nombre minimum de threads
        executor.setMaxPoolSize(20);          // Nombre maximum de threads
        executor.setQueueCapacity(100);       // Taille de la queue
        executor.setThreadNamePrefix("HNR-Async-");
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        log.info("Async task executor configured with core pool size: {}, max pool size: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());

        return executor;
    }

    /**
     * Executor spécifique pour l'envoi d'emails
     */
    @Bean(name = "emailExecutor")
    public Executor getEmailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("HNR-Email-");
        executor.setKeepAliveSeconds(30);
        executor.initialize();

        log.info("Email executor configured");

        return executor;
    }

    /**
     * Executor spécifique pour les événements de notifications
     */
    @Bean(name = "notificationExecutor")
    public Executor getNotificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("HNR-Notification-");
        executor.setKeepAliveSeconds(60);
        executor.initialize();

        log.info("Notification executor configured");

        return executor;
    }

    /**
     * Executor pour les tâches de nettoyage et maintenance
     */
    @Bean(name = "cleanupExecutor")
    public Executor getCleanupExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("HNR-Cleanup-");
        executor.setKeepAliveSeconds(300);
        executor.initialize();

        log.info("Cleanup executor configured");

        return executor;
    }
}