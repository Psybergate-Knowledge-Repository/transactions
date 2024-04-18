package com.psybergate.mentoring.transactions.spring.service.impl;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import com.psybergate.mentoring.transactions.spring.entity.CustomerAuditEntity;
import com.psybergate.mentoring.transactions.spring.entity.CustomerEntity;
import com.psybergate.mentoring.transactions.spring.repository.CustomerAuditRepository;
import com.psybergate.mentoring.transactions.spring.service.AuditPropagationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditPropagationServiceImpl implements AuditPropagationService {

    private final CustomerAuditRepository customerAuditRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAuditRequiredPropagation(final Customer customer,
                                             final boolean simulateFailure) {
        saveAudit(customer, simulateFailure);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditRequiresNewPropagation(final Customer customer,
                                                final boolean simulateFailure) {
        saveAudit(customer, simulateFailure);
    }

    private void saveAudit(final Customer customer,
                           final boolean simulateFailure) {
        if (simulateFailure)
            customerAuditRepository.saveWithError(
                    new CustomerAuditEntity(customer.getName(), LocalDateTime.now(), customer.getEmail(),
                                            customer.toString()));
        customerAuditRepository.save(
                new CustomerAuditEntity(customer.getName(), LocalDateTime.now(), customer.getEmail(),
                                        customer.toString()));
    }
}
