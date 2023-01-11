package com.psybergate.mentoring.transactions.spring;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class TransactionsInSpring {
    public static void main(String[] args) {
        SpringApplication.run(TransactionsInSpring.class);
    }

    @Bean
    public DataSource dataSource(){
        return DataSourceBuilder
                .create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/transactions?tcpKeepAlive=true")
                .username("postgres")
                .password("admin")
                .build();
    }
}
