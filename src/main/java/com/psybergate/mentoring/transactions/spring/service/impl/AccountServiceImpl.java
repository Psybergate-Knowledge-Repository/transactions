package com.psybergate.mentoring.transactions.spring.service.impl;

import com.psybergate.mentoring.transactions.spring.dto.Account;
import com.psybergate.mentoring.transactions.spring.entity.AccountEntity;
import com.psybergate.mentoring.transactions.spring.entity.AccountHistoryEntity;
import com.psybergate.mentoring.transactions.spring.enums.TransactionColour;
import com.psybergate.mentoring.transactions.spring.repository.AccountHistoryRepository;
import com.psybergate.mentoring.transactions.spring.repository.AccountRepository;
import com.psybergate.mentoring.transactions.spring.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

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
            throw new RuntimeException(e);
        }
    }

    private final AccountRepository accountRepository;

    private AccountHistoryRepository accountHistoryRepository;

    @Override
    @Transactional
    public void saveAccount(final Account account) {
        final AccountEntity accountEntity = new AccountEntity(account);
        accountRepository.save(accountEntity);
    }

    @Override
    public AccountEntity findAccountByAccountNumber(final long accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void debitAccountReadUncommittedLevel(final long accountNumber,
                                                 final BigDecimal amount,
                                                 final long pauseSeconds,
                                                 final int tranIdentifier) {
        print("[%s] START TRANSACTION%n", tranIdentifier);
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber);
        print("[%s] Balance before debit of %s: %s%n", tranIdentifier, amount, account.getBalance());
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        account = accountRepository.findByAccountNumber(accountNumber);
        print("[%s] Saved. New balance = %s%n", tranIdentifier, account.getBalance());
        try {
            Thread.sleep(pauseSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        account = accountRepository.findByAccountNumber(accountNumber);
        print("[%s] Final balance check: %s%n", tranIdentifier, account.getBalance());
        print("[%s] END TRANSACTION%n", tranIdentifier);
    }

    @Override
    public void debitAccountWithoutTransactionDeclaration(final long accountNumber,
                                                          final BigDecimal amount,
                                                          final long pauseSeconds,
                                                          final int tranIdentifier,
                                                          final boolean rollback) {
        print("[%s] START TRANSACTION%n", tranIdentifier);
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber);
        final BigDecimal openingBalance = account.getBalance();
        print("[%s] Balance before debit of %s: %s%n", tranIdentifier, amount, openingBalance);
        account.setBalance(openingBalance.subtract(amount));
        if (!rollback) sleep(1);
        accountRepository.save(account);
        account = accountRepository.findByAccountNumber(accountNumber);
        print("[%s] Saved. New balance = %s%n", tranIdentifier, account.getBalance());
        sleep(pauseSeconds);
        if (rollback) {
            account.setBalance(openingBalance);
            accountRepository.saveAndFlush(account);
        }
        account = accountRepository.findByAccountNumber(accountNumber);
        print("[%s] Final balance check: %s%n", tranIdentifier, account.getBalance());
        print("[%s] END TRANSACTION%n", tranIdentifier);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void debitAccountAndCreateHistory(final long accountNumber,
                                             final BigDecimal amount,
                                             final int pauseSeconds,
                                             final int tranIdentifier) {
        print("[%s] START TRANSACTION%n", tranIdentifier);
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber);
        final BigDecimal openingBalance = account.getBalance();
        print("[%s] Balance before debit of %s: %s%n", tranIdentifier, amount, openingBalance);
        account.setBalance(openingBalance.subtract(amount));
        accountRepository.save(account);
        account = accountRepository.findByAccountNumber(accountNumber);
        print("[%s] Saved. New balance = %s%n", tranIdentifier, account.getBalance());
        try {
            Thread.sleep(pauseSeconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        account = accountRepository.findByAccountNumber(accountNumber);
        accountHistoryRepository.save(new AccountHistoryEntity(accountNumber, amount, openingBalance, account.getBalance()));
        print("[%s] Final balance check: %s%n", tranIdentifier, account.getBalance());
        print("[%s] END TRANSACTION%n", tranIdentifier);
    }


}
