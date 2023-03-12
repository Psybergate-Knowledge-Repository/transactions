package com.psybergate.mentoring.transactions.jdbc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Account {

    private LocalDateTime createdDate;

    private LocalDateTime lastModified;

    private long accountNumber;

    private String name;

    private BigDecimal balance;

    private long customerId;

    public Account(final LocalDateTime createdDate,
                   final LocalDateTime lastModified,
                   final String name,
                   final BigDecimal balance,
                   final long accountNumber,
                   final long customerId) {
        this.createdDate = createdDate;
        this.lastModified = lastModified;
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
        this.customerId = customerId;

    }
}
