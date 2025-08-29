package com.example.HNR.Config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.HNR.Repository",
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                classes = {com.example.HNR.Repository.UserRepository.class}
        )
)
@EnableMongoRepositories(
        basePackages = "com.example.HNR.Repository",
        includeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                classes = {com.example.HNR.Repository.UserRepository.class}
        )
)
@EntityScan(basePackages = "com.example.HNR.Model")
public class DatabaseConfig {
    // Configuration automatique par Spring Boot
}