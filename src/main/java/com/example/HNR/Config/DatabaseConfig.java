package com.example.HNR.Config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.HNR.Repository.SqlServer")
@EnableMongoRepositories(basePackages = "com.example.HNR.Repository.Mongodb")
@EntityScan(basePackages = "com.example.HNR.Model")
public class DatabaseConfig {
}
