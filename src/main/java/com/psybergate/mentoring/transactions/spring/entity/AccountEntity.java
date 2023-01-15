package com.psybergate.mentoring.transactions.spring.entity;

import com.psybergate.mentoring.transactions.spring.dto.Account;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "account",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"account_number"})})
@NoArgsConstructor
@ToString
public class AccountEntity extends BaseEntity {

    @Column(name = "account_number")
    private long accountNumber;

    private String name;

    private BigDecimal balance;

    @Column(name = "customer_id")
    private long customerId;

    public AccountEntity(final Account account) {
        accountNumber = account.getAccountNumber();
        name = account.getName();
        balance = account.getBalance();
        customerId = account.getCustomerId();
    }
}
