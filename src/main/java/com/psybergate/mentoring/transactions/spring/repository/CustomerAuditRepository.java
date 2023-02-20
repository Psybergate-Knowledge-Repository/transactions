package com.psybergate.mentoring.transactions.spring.repository;

import com.psybergate.mentoring.transactions.spring.entity.CustomerAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerAuditRepository extends JpaRepository<CustomerAuditEntity, Long> {

    @Query("select cae from CustomerAuditEntity cae where cae.customerEmail = :email order by cae.createdDate desc ")
    List<CustomerAuditEntity> findByCustomerEmail(@Param("email") String email);

    @Modifying
    @Query(value = "insert customer_audit values (nextval('customer_audit_id_seq'), " +
            ":#{#audit.customerEmail}, " +
            ":#{#audit.modifiedBy}, " +
            ":#{#audit.modifiedDate}, " +
            ":#{#audit.customer}, " +
            ":#{#audit.createdDate}, " +
            ":#{#audit.lastModified})", nativeQuery = true)
    void saveWithError(@Param("audit") CustomerAuditEntity customerAuditEntity);
}
