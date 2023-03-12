package com.psybergate.mentoring.transactions.jdbc.service;


import com.psybergate.mentoring.transactions.jdbc.dto.Account;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface AccountService {

    Account findAccountByAccountNumber(long accountNumber);

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
