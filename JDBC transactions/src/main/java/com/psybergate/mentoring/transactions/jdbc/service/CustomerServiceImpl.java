package com.psybergate.mentoring.transactions.jdbc.service;

import com.psybergate.mentoring.transactions.jdbc.repository.JDBCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final JDBCRepository customerRepository;

    @Override
    public void saveCustomerWithoutTransactionBoundary(final Customer customer,
                                                       final boolean simulateFailure) {
        customerRepository.save(customer);
        if (simulateFailure) throw new RuntimeException("Simulated failure");
        customerAuditRepository.save(new CustomerAuditEntity(customer.getName(), LocalDateTime.now(), customer.getEmail(), customer.toString()));
    }

    @Override
    @Transactional
    public void saveCustomerWithTransactionBoundary(final Customer customer,
                                                    final boolean simulateFailure) {
        final CustomerEntity customerEntity = new CustomerEntity(customer);
        customerRepository.save(customerEntity);
        if (simulateFailure) throw new RuntimeException("Simulated failure");
        customerAuditRepository.save(new CustomerAuditEntity(customer.getName(), LocalDateTime.now(), customer.getEmail(), customer.toString()));
    }

    @Override
    public List<CustomerAuditEntity> findAuditsByCustomerEmail(final String email) {
        return customerAuditRepository.findByCustomerEmail(email);
    }

    @Override
    public CustomerEntity findCustomerByEmail(final String email) {
        return customerRepository.findByEmail(email);
    }
}