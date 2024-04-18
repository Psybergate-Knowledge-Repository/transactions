package com.psybergate.mentoring.transactions.spring.service.impl.proxies;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import com.psybergate.mentoring.transactions.spring.entity.CustomerAuditEntity;
import com.psybergate.mentoring.transactions.spring.entity.CustomerEntity;
import com.psybergate.mentoring.transactions.spring.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.util.List;

@RequiredArgsConstructor
public class CustomerServiceProxyUsingTransactionManager implements CustomerService {


    private final CustomerService customerService;
    private final PlatformTransactionManager transactionManager;


    @Override
    public void saveCustomerWithTransactionBoundary(Customer customer,
                                                    boolean simulateFailure) {
        final TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            customerService.saveCustomerWithTransactionBoundary(customer, simulateFailure);
            transactionManager.commit(transaction);
        } catch (Exception e) {
            transactionManager.rollback(transaction);
        }
    }


    @Override
    public void saveCustomerDelegateToTransactional(Customer customer,
                                                    boolean simulateFailure) {

    }

    @Override
    public void saveCustomerWithoutTransactionBoundary(Customer customer,
                                                       boolean simulateFailure) {

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
    public void saveCustomerWithCheckedExceptionThrown(Customer customer,
                                                       boolean simulateFailure) {

    }

    @Override
    public void saveCustomerWithUncheckedExceptionThrown(Customer customer,
                                                         boolean simulateFailure) {

    }

    @Override
    public long getRandomCustomerId() {
        return 0;
    }

}
