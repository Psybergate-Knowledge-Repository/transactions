package com.psybergate.mentoring.transactions.spring.service.impl;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import com.psybergate.mentoring.transactions.spring.entity.CustomerEntity;
import com.psybergate.mentoring.transactions.spring.repository.CustomerRepository;
import com.psybergate.mentoring.transactions.spring.service.AuditPropagationService;
import com.psybergate.mentoring.transactions.spring.service.CustomerPropagationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerPropagationServiceImpl implements CustomerPropagationService {

    private final CustomerRepository customerRepository;
    private final AuditPropagationService auditPropagationService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCustomerRequiredPropagation(final Customer customer,
                                                final boolean simulateFailure) {
        final CustomerEntity customerEntity = new CustomerEntity(customer);
        customerRepository.save(customerEntity);
        auditPropagationService.saveAuditRequiredPropagation(customer, simulateFailure);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCustomerRequiresNewPropagation(final Customer customer,
                                                   final boolean simulateFailure) {
        final CustomerEntity customerEntity = new CustomerEntity(customer);
        customerRepository.save(customerEntity);
        auditPropagationService.saveAuditRequiresNewPropagation(customer, simulateFailure);
    }
}
