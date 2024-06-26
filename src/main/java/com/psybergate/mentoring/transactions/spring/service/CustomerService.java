package com.psybergate.mentoring.transactions.spring.service;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import com.psybergate.mentoring.transactions.spring.entity.CustomerAuditEntity;
import com.psybergate.mentoring.transactions.spring.entity.CustomerEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerService {

    void saveCustomerWithoutTransactionBoundary(Customer customer, final boolean simulateFailure);

    void saveCustomerDelegateToTransactional(Customer customer,
                                             boolean simulateFailure);

    @Transactional
    void saveCustomerWithTransactionBoundary(Customer customer, final boolean simulateFailure);

    List<CustomerAuditEntity> findAuditsByCustomerEmail(String email);

    CustomerEntity findCustomerByEmail(String email);

    void saveCustomerWithCheckedExceptionThrown(Customer customer, final boolean simulateFailure) throws Exception;

    @Transactional
    void saveCustomerWithUncheckedExceptionThrown(Customer customer,
                                                  boolean simulateFailure) throws Exception;

    long getRandomCustomerId();

}
