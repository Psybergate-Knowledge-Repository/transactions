package com.psybergate.mentoring.transactions.jdbc.service;


import com.psybergate.mentoring.transactions.jdbc.dto.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public interface AccountService {

    Account findAccountByAccountNumber(long accountNumber, final Connection connection);

    void debitAccountReadUncommittedLevel(long accountNumber,
                                          BigDecimal amount,
                                          long pauseSeconds,
                                          int tranIdentifier, final boolean rollback);

    void debitAccountReadCommittedLevel(long accountNumber,
                                        BigDecimal amount,
                                        long pauseSeconds,
                                        int tranIdentifier,
                                        boolean rollback);

    void trackNameChanges(long accountNumber,
                          long pauseSeconds,
                          int tranIdentifier);

    void trackNameChangesUnderRepeatableRead(long accountNumber,
                                             long pauseSeconds,
                                             int tranIdentifier);

    void debitAccountAndCreateHistory(long accountNumber,
                                      BigDecimal amount,
                                      int pauseSeconds,
                                      int tranIdentifier);

    void changeAccountName(long accountNumber, String newAccountName, int pauseSeconds, int tranIdentifier);

    void trackAccountHistoryReadCommitted(long accountNumber,
                                          long pauseSeconds,
                                          int tranIdentifier);

    void trackAccountHistorySerializable(long accountNumber,
                                         long pauseSeconds,
                                         int tranIdentifier);

    void resetAccountName(final long l) throws SQLException;

}
