package com.psybergate.mentoring.transactions.spring.service;

import com.psybergate.mentoring.transactions.spring.dto.Account;
import com.psybergate.mentoring.transactions.spring.entity.AccountEntity;
import org.springframework.transaction.annotation.Transactional;

public interface AccountService {
    @Transactional
    void saveAccount(Account account);

    AccountEntity findAccountByAccountNumber(long accountNumber);
}
