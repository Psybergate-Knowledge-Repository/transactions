package com.psybergate.mentoring.transactions.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class Account {
    private long accountNumber;

    private String name;

    private BigDecimal balance;

    private long customerId;
}
