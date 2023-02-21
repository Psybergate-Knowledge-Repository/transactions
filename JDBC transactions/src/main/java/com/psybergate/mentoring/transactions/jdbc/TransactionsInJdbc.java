package com.psybergate.mentoring.transactions.jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class TransactionsInJdbc {
    public static void main(String[] args) {
        SpringApplication.run(TransactionsInJdbc.class);
    }

    @Bean
    public DataSource postgresDataSource(){
        return DataSourceBuilder
                .create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/transactions?tcpKeepAlive=true")
                .username("postgres")
                .password("admin")
                .build();
    }

    @Bean
    public DataSource mySqlDataSource(){
        return DataSourceBuilder
                .create()
                .driverClassName("com.mysql.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/transactions?tcpKeepAlive=true")
                .username("root")
                .password("admin")
                .build();
    }
}
