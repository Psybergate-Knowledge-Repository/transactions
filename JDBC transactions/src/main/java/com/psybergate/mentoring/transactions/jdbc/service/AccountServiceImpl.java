package com.psybergate.mentoring.transactions.jdbc.service;

import com.psybergate.mentoring.transactions.enums.TransactionColour;
import com.psybergate.mentoring.transactions.jdbc.dto.Account;
import com.psybergate.mentoring.transactions.jdbc.dto.AccountHistory;
import com.psybergate.mentoring.transactions.jdbc.exception.SimulateRollbackException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private static final String RETRIEVE_ACCOUNT_TEMPLATE = "select * from account where account_number = '%s'";
    private static final String RETRIEVE_ACCOUNT_HISTORY_TEMPLATE = "select * from account_history where account = '%s'";
    private static final String MYSQL_ACCOUNT_BALANCE_UPDATE_TEMPLATE = "update account set balance = %s where account_number = %s";
    private static final String MYSQL_ACCOUNT_NAME_UPDATE_TEMPLATE = "update account set name = '%s' where account_number = %s";
    public static final String MYSQL_ACCOUNT_HISTORY_INSERT_TEMPLATE = "insert into account_history(account, transaction_amount, opening_balance, closing_balance) values ('%s', '%s', '%s', '%s')";

    private static String createAccountUpdateBalanceStatement(final Account account) {
        return String.format(MYSQL_ACCOUNT_BALANCE_UPDATE_TEMPLATE,
                account.getBalance(),
                account.getAccountNumber());
    }

    private static String createAccountHistoryInsertStatement(final Account account, final BigDecimal amount) {
        return String.format(MYSQL_ACCOUNT_HISTORY_INSERT_TEMPLATE,
                account.getAccountNumber(),
                amount,
                account.getBalance().add(amount),
                account.getBalance());
    }

    private static String createAccountUpdateNameStatement(final Account account) {
        return String.format(MYSQL_ACCOUNT_NAME_UPDATE_TEMPLATE,
                account.getName(),
                account.getAccountNumber());
    }

    private static String createRetrieveAccountStatement(final long accountNumber) {
        return String.format(RETRIEVE_ACCOUNT_TEMPLATE,
                accountNumber);
    }

    private static String createRetrieveAccountHistoryStatement(final long accountNumber) {
        return String.format(RETRIEVE_ACCOUNT_HISTORY_TEMPLATE,
                accountNumber);
    }

    private static void waitForInterval(final long pauseSeconds) {
        try {
            Thread.sleep(pauseSeconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void print(final String template,
                              final int tranIdentifier,
                              final Object... args) {
        final String transactionColour = TransactionColour.getTransactionColour(tranIdentifier).getCode();
        final ArrayList<? super Object> templateArgs = new ArrayList<>();
        templateArgs.add(tranIdentifier);
        templateArgs.addAll(Arrays.asList(args));
        System.out.printf(transactionColour + template, templateArgs.toArray());
    }

    private final DataSource dataSource;

    public AccountServiceImpl(@Qualifier("mySqlDataSource") final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Account findAccountByAccountNumber(final long accountNumber,
                                              final Connection connection) {
        Account account = null;
        try (final Statement statement = connection.createStatement()) {
            final String retrieveAccountStatement = createRetrieveAccountStatement(accountNumber);
            ResultSet results = statement.executeQuery(retrieveAccountStatement);
            if (results.next()) {
                account = extractAccount(results);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    private List<AccountHistory> findAccountHistoryByAccountNumber(final long accountNumber, final Connection connection) {
        List<AccountHistory> accountHistory = new ArrayList<>();
        try (final Statement statement = connection.createStatement()) {
            final String retrieveAccountHistoryStatement = createRetrieveAccountHistoryStatement(accountNumber);
            ResultSet results = statement.executeQuery(retrieveAccountHistoryStatement);
            while (results.next()) {
                accountHistory.add(extractAccountHistory(results)) ;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accountHistory;
    }

    @Override
    public void debitAccountReadUncommittedLevel(final long accountNumber,
                                                 final BigDecimal amount,
                                                 final long pauseSeconds,
                                                 final int tranIdentifier,
                                                 final boolean rollback) {
        try (final Connection connection = dataSource.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                print("[%s] START TRANSACTION%n", tranIdentifier);
                Account account = findAccountByAccountNumber(accountNumber, connection);
                print("[%s] Balance before debit of %s: %s%n", tranIdentifier, amount, account.getBalance());
                account.setBalance(account.getBalance().subtract(amount));
                if (!rollback) waitForInterval(1);
                final String accountUpdateStatementWithNewBalance = createAccountUpdateBalanceStatement(account);
                statement.executeUpdate(accountUpdateStatementWithNewBalance);
                account = findAccountByAccountNumber(accountNumber, connection);
                print("[%s] Saved. New balance = %s%n", tranIdentifier, account.getBalance());
                waitForInterval(pauseSeconds);
                if (rollback) {
                    throw new SimulateRollbackException();
                }
                connection.commit();
                print("[%s] END TRANSACTION%n", tranIdentifier);
            } catch (Exception e) {
                connection.rollback();
                print("[%s] ROLLBACK TRANSACTION%n", tranIdentifier);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void debitAccountReadCommittedLevel(final long accountNumber,
                                               final BigDecimal amount,
                                               final long pauseSeconds,
                                               final int tranIdentifier,
                                               final boolean rollback) {
        try (final Connection connection = dataSource.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                print("[%s] START TRANSACTION%n", tranIdentifier);
                Account account = findAccountByAccountNumber(accountNumber, connection);
                print("[%s] Balance before debit of %s: %s%n", tranIdentifier, amount, account.getBalance());
                account.setBalance(account.getBalance().subtract(amount));
                if (!rollback) waitForInterval(1);
                final String accountUpdateStatementWithNewBalance = createAccountUpdateBalanceStatement(account);
                statement.executeUpdate(accountUpdateStatementWithNewBalance);
                account = findAccountByAccountNumber(accountNumber, connection);
                print("[%s] Saved. New balance = %s%n", tranIdentifier, account.getBalance());
                waitForInterval(pauseSeconds);
                if (rollback) {
                    throw new SimulateRollbackException();
                }
                connection.commit();
                print("[%s] END TRANSACTION%n", tranIdentifier);
            } catch (Exception e) {
                connection.rollback();
                print("[%s] ROLLBACK TRANSACTION%n", tranIdentifier);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void trackNameChanges(final long accountNumber,
                                 final long pauseSeconds,
                                 final int tranIdentifier) {
        try (final Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                print("[%s] START TRANSACTION%n", tranIdentifier);
                Account account = findAccountByAccountNumber(accountNumber, connection);
                print("[%s] Account name: %s%n", tranIdentifier, account.getName());
                waitForInterval(pauseSeconds);
                account = findAccountByAccountNumber(accountNumber, connection);
                print("[%s] Retrieved again. Account name: %s%n", tranIdentifier, account.getName());
                connection.commit();
                print("[%s] END TRANSACTION%n", tranIdentifier);
            } catch (Exception e) {
                connection.rollback();
                print("[%s] ROLLBACK TRANSACTION%n", tranIdentifier);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void changeAccountName(final long accountNumber,
                                  final String newAccountName,
                                  final int pauseSeconds,
                                  final int tranIdentifier) {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            print("[%s] START TRANSACTION%n", tranIdentifier);
            Account account = findAccountByAccountNumber(accountNumber, connection);
            print("[%s] Old name = %s before name change%n", tranIdentifier, account.getName(), newAccountName);
            account.setName(newAccountName);
            final String accountInsertStatementWithNewName = createAccountUpdateNameStatement(account);
            statement.executeUpdate(accountInsertStatementWithNewName);
            connection.commit();
            account = findAccountByAccountNumber(accountNumber, connection);
            print("[%s] END TRANSACTION%n", tranIdentifier);
            print("[%s] Saved and committed. New name = %s%n", tranIdentifier, account.getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void trackNameChangesUnderRepeatableRead(final long accountNumber,
                                                    final long pauseSeconds,
                                                    final int tranIdentifier) {
        try (final Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                print("[%s] START TRANSACTION%n", tranIdentifier);
                Account account = findAccountByAccountNumber(accountNumber, connection);
                print("[%s] Account name: %s%n", tranIdentifier, account.getName());
                waitForInterval(pauseSeconds);
                account = findAccountByAccountNumber(accountNumber, connection);
                print("[%s] Retrieved again. Account name: %s%n", tranIdentifier, account.getName());
                connection.commit();
                print("[%s] END TRANSACTION%n", tranIdentifier);
            } catch (Exception e) {
                connection.rollback();
                print("[%s] ROLLBACK TRANSACTION%n", tranIdentifier);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void debitAccountAndCreateHistory(final long accountNumber,
                                             final BigDecimal amount,
                                             final int pauseSeconds,
                                             final int tranIdentifier) {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            print("[%s] START TRANSACTION%n", tranIdentifier);
            List<AccountHistory> accountHistory = findAccountHistoryByAccountNumber(accountNumber, connection);
            Account account = findAccountByAccountNumber(accountNumber, connection);
            print("[%s] Account history before current debit = %s%n", tranIdentifier, accountHistory);
            account.setBalance(account.getBalance().subtract(amount));
            final String accountUpdateStatement = createAccountUpdateBalanceStatement(account);
            statement.executeUpdate(accountUpdateStatement);
            final String accountHistoryInsertStatement = createAccountHistoryInsertStatement(account, amount);
            statement.executeUpdate(accountHistoryInsertStatement);
            accountHistory = findAccountHistoryByAccountNumber(accountNumber, connection);
            print("[%s] Saved account history item. New account history = %s%n", tranIdentifier, accountHistory);
            waitForInterval(pauseSeconds);
            connection.commit();
            print("[%s] END TRANSACTION%n", tranIdentifier);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void trackAccountHistoryReadCommitted(final long accountNumber,
                                                 final long pauseSeconds,
                                                 final int tranIdentifier) {
        try (final Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                print("[%s] START TRANSACTION%n", tranIdentifier);
                List<AccountHistory> accountHistory = findAccountHistoryByAccountNumber(accountNumber, connection);
                print("[%s] Account history: %s%n", tranIdentifier, accountHistory);
                waitForInterval(pauseSeconds);
                accountHistory = findAccountHistoryByAccountNumber(accountNumber, connection);
                print("[%s] Retrieved again. Account history: %s%n", tranIdentifier, accountHistory);
                connection.commit();
                print("[%s] END TRANSACTION%n", tranIdentifier);
            } catch (Exception e) {
                connection.rollback();
                print("[%s] ROLLBACK TRANSACTION%n", tranIdentifier);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void trackAccountHistorySerializable(final long accountNumber,
                                                 final long pauseSeconds,
                                                 final int tranIdentifier) {
        try (final Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                print("[%s] START TRANSACTION%n", tranIdentifier);
                List<AccountHistory> accountHistory = findAccountHistoryByAccountNumber(accountNumber, connection);
                print("[%s] Account history: %s%n", tranIdentifier, accountHistory);
                waitForInterval(pauseSeconds);
                accountHistory = findAccountHistoryByAccountNumber(accountNumber, connection);
                print("[%s] Retrieved again. Account history: %s%n", tranIdentifier, accountHistory);
                connection.commit();
                print("[%s] END TRANSACTION%n", tranIdentifier);
            } catch (Exception e) {
                connection.rollback();
                print("[%s] ROLLBACK TRANSACTION%n", tranIdentifier);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Account extractAccount(final ResultSet results) {
        try {
            final LocalDateTime createdDate = results.getTimestamp("created_date").toLocalDateTime();
            final LocalDateTime lastModified = results.getTimestamp("last_modified").toLocalDateTime();
            final String name = results.getString("name");
            final BigDecimal balance = results.getBigDecimal("balance");
            final long accountNumber = results.getLong("account_number");
            final long customerId = results.getLong("customer_id");
            return new Account(createdDate,
                    lastModified,
                    name,
                    balance,
                    accountNumber,
                    customerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private AccountHistory extractAccountHistory(final ResultSet results) {
        try {
            final long accountNumber = results.getLong("account");
            final BigDecimal amount = results.getBigDecimal("transaction_amount");
            final BigDecimal openingBalance = results.getBigDecimal("opening_balance");
            final BigDecimal closingBalance = results.getBigDecimal("closing_balance");
            return new AccountHistory(accountNumber, amount, openingBalance, closingBalance);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetAccountName(final long accountNumber) throws SQLException {
        Account account;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            account = findAccountByAccountNumber(accountNumber, connection);
            account.setName("CHECK");
            final String accountInsertStatementWithNewName = createAccountUpdateNameStatement(account);
            statement.executeUpdate(accountInsertStatementWithNewName);
        }
    }


}
