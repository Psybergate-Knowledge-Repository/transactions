package com.psybergate.mentoring.transactions.spring.repository;

import com.psybergate.mentoring.transactions.spring.entity.AccountHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHistoryRepository extends JpaRepository<AccountHistoryEntity, Long> {
}
