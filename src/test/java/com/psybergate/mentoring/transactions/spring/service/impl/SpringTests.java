package com.psybergate.mentoring.transactions.spring.service.impl;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import com.psybergate.mentoring.transactions.spring.entity.CustomerAuditEntity;
import com.psybergate.mentoring.transactions.spring.entity.CustomerEntity;
import com.psybergate.mentoring.transactions.spring.service.CustomerService;
import com.psybergate.mentoring.transactions.spring.util.RandomGeneratorUtil;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@NoArgsConstructor
@SpringBootTest
public class SpringTests {

    @Autowired
    private CustomerService customerService;

    @Test
    public void invokingTransactionalMethodFromWithinTheSameClass() {
        final Customer generatedCustomer = RandomGeneratorUtil.generateRandomCustomer();
        try {
            customerService.saveCustomerDelegateToTransactional(generatedCustomer, true);
        } catch (Exception e) {
            //Simulated failure
        }
        List<CustomerAuditEntity> customerAudits = customerService.findAuditsByCustomerEmail(generatedCustomer.getEmail());
        final CustomerEntity customer = customerService.findCustomerByEmail(generatedCustomer.getEmail());
        // Expecting nothing to be saved. The transaction should be rolled back. This is what we want. All or nothing
        if (!customerAudits.isEmpty() || customer != null) fail("customer or audit was saved");
        System.out.println(customerAudits);
        System.out.println(customer);
    }

}