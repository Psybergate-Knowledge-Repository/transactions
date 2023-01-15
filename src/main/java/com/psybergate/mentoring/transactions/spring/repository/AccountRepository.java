package com.psybergate.mentoring.transactions.spring.repository;

import com.psybergate.mentoring.transactions.spring.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Query("select a from AccountEntity a where a.accountNumber = :accountNumber")
    AccountEntity findByAccountNumber(@Param("accountNumber") long accountNumber);
}
