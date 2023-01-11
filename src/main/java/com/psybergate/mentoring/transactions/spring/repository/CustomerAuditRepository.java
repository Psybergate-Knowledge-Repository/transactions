package com.psybergate.mentoring.transactions.spring.repository;

import com.psybergate.mentoring.transactions.spring.entity.CustomerAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerAuditRepository extends JpaRepository<CustomerAuditEntity, Long> {

    @Query("select cae from CustomerAuditEntity cae where cae.customerEmail = :email order by cae.createdDate desc ")
    List<CustomerAuditEntity> findByCustomerEmail(@Param("email") String email);
}
