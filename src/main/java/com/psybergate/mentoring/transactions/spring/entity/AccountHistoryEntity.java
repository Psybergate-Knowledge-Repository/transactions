package com.psybergate.mentoring.transactions.spring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class AccountHistoryEntity extends BaseEntity {

    private long account;

    @Column(name = "transaction_amount")
    private BigDecimal transactionAmount;

    @Column(name = "opening_balance")
    private BigDecimal openingBalance;

    @Column(name = "closing_balance")
    private BigDecimal closingBalance;

}
