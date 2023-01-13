package com.psybergate.mentoring.transactions.spring.service;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import com.psybergate.mentoring.transactions.spring.entity.CustomerAuditEntity;
import com.psybergate.mentoring.transactions.spring.entity.CustomerEntity;
import com.psybergate.mentoring.transactions.spring.repository.CustomerAuditRepository;
import com.psybergate.mentoring.transactions.spring.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAuditRepository customerAuditRepository;

    @Override
    public void saveCustomerWithoutTransactionBoundary(final Customer customer,
                                                       final boolean simulateFailure) {
        final CustomerEntity customerEntity = new CustomerEntity(customer);
        customerRepository.save(customerEntity);
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

    @Override
    @Transactional
    public void saveCustomerWithCheckedExceptionThrown(final Customer customer,
                                                       final boolean simulateFailure) throws Exception {
        final CustomerEntity customerEntity = new CustomerEntity(customer);
        customerRepository.save(customerEntity);
        if (simulateFailure) throw new Exception("Checked exception");
        customerAuditRepository.save(new CustomerAuditEntity(customer.getName(), LocalDateTime.now(), customer.getEmail(), customer.toString()));
    }
}