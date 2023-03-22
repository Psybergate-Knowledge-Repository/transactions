package com.psybergate.mentoring.transactions.jdbc.dto;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class AccountHistory {

    private long account;

    private BigDecimal transactionAmount;

    private BigDecimal openingBalance;

    private BigDecimal closingBalance;

}
