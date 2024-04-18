package com.psybergate.mentoring.transactions.spring.service;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerPropagationService {
    @Transactional(propagation = Propagation.REQUIRED)
    void saveCustomerRequiredPropagation(Customer customer,
                                         boolean simulateFailure);

    @Transactional(propagation = Propagation.REQUIRED)
    void saveCustomerRequiresNewPropagation(Customer customer,
                                            boolean simulateFailure);
}
