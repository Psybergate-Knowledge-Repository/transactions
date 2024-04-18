package com.psybergate.mentoring.transactions.spring.service.impl;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import com.psybergate.mentoring.transactions.spring.entity.CustomerAuditEntity;
import com.psybergate.mentoring.transactions.spring.entity.CustomerEntity;
import com.psybergate.mentoring.transactions.spring.service.CustomerPropagationService;
import com.psybergate.mentoring.transactions.spring.service.CustomerService;
import com.psybergate.mentoring.transactions.spring.util.RandomGeneratorUtil;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@NoArgsConstructor
@SpringBootTest
public class PropagationTests {

    @Autowired
    private CustomerPropagationService customerPropagationService;

    @Autowired
    private CustomerService customerService;

      @Test
      public void propagationRequiredRollbackTest() {
          final Customer generatedCustomer = RandomGeneratorUtil.generateRandomCustomer();
          customerPropagationService.saveCustomerRequiredPropagation(generatedCustomer, true);
          List<CustomerAuditEntity> customerAudits = customerService.findAuditsByCustomerEmail(generatedCustomer.getEmail());
          final CustomerEntity customer = customerService.findCustomerByEmail(generatedCustomer.getEmail());
          if (customerAudits.isEmpty() || customer == null) fail("customer or audit was not saved");
          System.out.println(customerAudits.get(0));
          System.out.println(customer);
      }

}
