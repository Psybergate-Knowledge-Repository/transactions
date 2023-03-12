package com.psybergate.mentoring.transactions.jdbc.service;

import com.psybergate.mentoring.transactions.enums.TransactionColour;
import com.psybergate.mentoring.transactions.jdbc.dto.Account;
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

@Service
public class AccountServiceImpl implements AccountService {

    private static final String RETRIEVE_ACCOUNT_TEMPLATE = "select * from account where account_number = '%s'";

    private static final String MYSQL_ACCOUNT_INSERT_TEMPLATE = "insert into account(account_number, name, balance, created_date, last_modified, customer_id) values ('%s', '%s', '%s', '%s', '%s', '%s')";


    private static String createAccountInsertStatement(final Account account) {
        return String.format(MYSQL_ACCOUNT_INSERT_TEMPLATE,
                account.getAccountNumber(),
                account.getName(),
                account.getBalance(),
                account.getCreatedDate(),
                account.getLastModified(),
                account.getCustomerId());
    }

    private static void waitForInterval(final long pauseSeconds) {
        try {
            Thread.sleep(pauseSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    private static void sleep(final long pauseSeconds) {
        try {
            Thread.sleep(pauseSeconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private static String createRetrieveAccountStatement(final long accountNumber) {
        return String.format(RETRIEVE_ACCOUNT_TEMPLATE,
                accountNumber);
    }

    private final DataSource dataSource;

    public AccountServiceImpl(@Qualifier("mySqlDataSource") final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Account findAccountByAccountNumber(final long accountNumber) {
        Account account = null;
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            final String retrieveAuditStatement = createRetrieveAccountStatement(accountNumber);
            ResultSet results = statement.executeQuery(retrieveAuditStatement);
            if (results.next()) {
                account = extractAccount(results);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    @Override
    public void debitAccountReadUncommittedLevel(final long accountNumber,
                                                 final BigDecimal amount,
                                                 final long pauseSeconds,
                                                 final int tranIdentifier) {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            print("[%s] START TRANSACTION%n", tranIdentifier);
            Account account = findAccountByAccountNumber(accountNumber);
            print("[%s] Balance before debit of %s: %s%n", tranIdentifier, amount, account.getBalance());
            account.setBalance(account.getBalance().subtract(amount));
            final String accountInsertStatementWithNewBalance = createAccountInsertStatement(account);
            statement.executeUpdate(accountInsertStatementWithNewBalance);
            account = findAccountByAccountNumber(accountNumber);
            print("[%s] Saved. New balance = %s%n", tranIdentifier, account.getBalance());
            waitForInterval(pauseSeconds);
            account = findAccountByAccountNumber(accountNumber);
            print("[%s] Final balance check: %s%n", tranIdentifier, account.getBalance());
            print("[%s] END TRANSACTION%n", tranIdentifier);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void debitAccountWithoutTransactionDeclaration(final long accountNumber,
                                                          final BigDecimal amount,
                                                          final long pauseSeconds,
                                                          final int tranIdentifier,
                                                          final boolean rollback) {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_NONE);
            print("[%s] START TRANSACTION%n", tranIdentifier);
            Account account = findAccountByAccountNumber(accountNumber);
            print("[%s] Balance before debit of %s: %s%n", tranIdentifier, amount, account.getBalance());
            account.setBalance(account.getBalance().subtract(amount));
            final String accountInsertStatementWithNewBalance = createAccountInsertStatement(account);
            statement.executeUpdate(accountInsertStatementWithNewBalance);
            account = findAccountByAccountNumber(accountNumber);
            print("[%s] Saved. New balance = %s%n", tranIdentifier, account.getBalance());
            waitForInterval(pauseSeconds);
            account = findAccountByAccountNumber(accountNumber);
            print("[%s] Final balance check: %s%n", tranIdentifier, account.getBalance());
            print("[%s] END TRANSACTION%n", tranIdentifier);
            connection.commit();
        } catch (SQLException e) {
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
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            print("[%s] START TRANSACTION%n", tranIdentifier);
            Account account = findAccountByAccountNumber(accountNumber);
            print("[%s] Balance before debit of %s: %s%n", tranIdentifier, amount, account.getBalance());
            account.setBalance(account.getBalance().subtract(amount));
            final String accountInsertStatementWithNewBalance = createAccountInsertStatement(account);
            statement.executeUpdate(accountInsertStatementWithNewBalance);
            account = findAccountByAccountNumber(accountNumber);
            print("[%s] Saved. New balance = %s%n", tranIdentifier, account.getBalance());
            waitForInterval(pauseSeconds);
            account = findAccountByAccountNumber(accountNumber);
            print("[%s] Final balance check: %s%n", tranIdentifier, account.getBalance());
            print("[%s] END TRANSACTION%n", tranIdentifier);
            connection.commit();
        } catch (SQLException e) {
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


}
