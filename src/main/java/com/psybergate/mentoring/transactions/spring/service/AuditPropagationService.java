package com.psybergate.mentoring.transactions.spring.service;

import com.psybergate.mentoring.transactions.spring.dto.Customer;

public interface AuditPropagationService {
    void saveAuditRequiredPropagation(Customer customer,
                                      boolean simulateFailure);

    void saveAuditRequiresNewPropagation(Customer customer,
                                         boolean simulateFailure);
}
