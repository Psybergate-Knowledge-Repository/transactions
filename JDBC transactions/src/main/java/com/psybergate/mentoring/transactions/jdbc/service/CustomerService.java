package com.psybergate.mentoring.transactions.jdbc.service;

import com.psybergate.mentoring.transactions.jdbc.dto.Customer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerService {

    void saveCustomerWithoutTransactionBoundary(Customer customer, final boolean simulateFailure);

    @Transactional
    void saveCustomerWithTransactionBoundary(Customer customer, final boolean simulateFailure);

    List<CustomerAudit> findAuditsByCustomerEmail(String email);

    Customer findCustomerByEmail(String email);
}
