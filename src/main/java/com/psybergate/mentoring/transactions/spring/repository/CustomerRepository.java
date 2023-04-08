package com.psybergate.mentoring.transactions.spring.repository;

import com.psybergate.mentoring.transactions.spring.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    CustomerEntity findByEmail(String email);

    @Transactional
    @Query("select c.id from CustomerEntity c")
    Set<Long> getAllIds();
}
