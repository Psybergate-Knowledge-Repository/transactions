package com.psybergate.mentoring.transactions.jdbc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class AccountHistory {

    private long account;

    private BigDecimal transactionAmount;

    private BigDecimal openingBalance;

    private BigDecimal closingBalance;

}
