package com.psybergate.mentoring.transactions.spring.service.impl;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import com.psybergate.mentoring.transactions.spring.entity.CustomerAuditEntity;
import com.psybergate.mentoring.transactions.spring.entity.CustomerEntity;
import com.psybergate.mentoring.transactions.spring.service.CustomerService;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@RequiredArgsConstructor
public class CustomerServiceProxyUsingJdbcTransactions implements CustomerService {

    private final CustomerService customerService;
    private final DataSource dataSource;


    @Override
    public void saveCustomerWithTransactionBoundary(Customer customer, boolean simulateFailure) {
        try (final Connection connection = dataSource.getConnection()) {
            try  {
                connection.setAutoCommit(false);
                customerService.saveCustomerWithTransactionBoundary(customer, simulateFailure);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void saveCustomerDelegateToTransactional(Customer customer, boolean simulateFailure) {

    }

    @Override
    public void saveCustomerWithoutTransactionBoundary(Customer customer, boolean simulateFailure) {

    }

    @Override
    public List<CustomerAuditEntity> findAuditsByCustomerEmail(String email) {
        return null;
    }

    @Override
    public CustomerEntity findCustomerByEmail(String email) {
        return null;
    }

    @Override
    public void saveCustomerWithCheckedExceptionThrown(Customer customer, boolean simulateFailure) throws Exception {

    }

    @Override
    public void saveCustomerWithUncheckedExceptionThrown(Customer customer, boolean simulateFailure) throws Exception {

    }

    @Override
    public long getRandomCustomerId() {
        return 0;
    }
}
