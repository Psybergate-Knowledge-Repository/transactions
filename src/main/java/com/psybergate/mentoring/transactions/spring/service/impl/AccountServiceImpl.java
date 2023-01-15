package com.psybergate.mentoring.transactions.spring.service.impl;

import com.psybergate.mentoring.transactions.spring.dto.Account;
import com.psybergate.mentoring.transactions.spring.entity.AccountEntity;
import com.psybergate.mentoring.transactions.spring.repository.AccountRepository;
import com.psybergate.mentoring.transactions.spring.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

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

}
