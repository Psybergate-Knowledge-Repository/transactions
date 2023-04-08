package com.psybergate.mentoring.transactions.spring;

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
                .driverClassName("com.mysql.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/transactions?tcpKeepAlive=true")
                .username("root")
                .password("admin")
                .build();
    }
}
