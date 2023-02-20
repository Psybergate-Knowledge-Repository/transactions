package com.psybergate.mentoring.transactions.spring.service;

import com.psybergate.mentoring.transactions.spring.dto.Account;
import com.psybergate.mentoring.transactions.spring.entity.AccountEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface AccountService {
    @Transactional
    void saveAccount(Account account);

    AccountEntity findAccountByAccountNumber(long accountNumber);

    void debitAccountReadUncommittedLevel(long accountNumber,
                                          BigDecimal amount,
                                          long pauseSeconds,
                                          int tranIdentifier);

    void debitAccountWithoutTransactionDeclaration(long accountNumber,
                                                   BigDecimal amount,
                                                   long pauseSeconds,
                                                   int tranIdentifier,
                                                   boolean rollback);

    void debitAccountAndCreateHistory(long accountNumber,
                                      BigDecimal amount,
                                      int pauseSeconds,
                                      int tranIdentifier);
}
