package com.psybergate.mentoring.transactions.jdbc.service;

import com.psybergate.mentoring.transactions.jdbc.dto.Customer;
import com.psybergate.mentoring.transactions.jdbc.dto.CustomerAudit;
import com.psybergate.mentoring.transactions.jdbc.util.CustomerGeneratorUtil;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@NoArgsConstructor
@SpringBootTest
public class AtomicityTest {

    @Autowired
    private CustomerService customerService;


    @Test
    public void transactionalSuccessWithoutTransactionalDeclaration() throws SQLException {
        final Customer generatedCustomer = CustomerGeneratorUtil.generateRandomCustomer();
        customerService.saveCustomerWithoutTransactionBoundary(generatedCustomer, false);
        List<CustomerAudit> customerAudits = customerService.findAuditsByCustomerEmail(generatedCustomer.getEmail());
        final Customer customer = customerService.findCustomerByEmail(generatedCustomer.getEmail());
        //Expecting the customer and audit to be saved, since everything succeeded
        if (customerAudits.isEmpty() || customer == null) fail("customer or audit was not saved");
        System.out.println(customerAudits.get(0));
        System.out.println(customer);
    }

    /*There is no difference between transactional success with or without transactional declaration*/
    @Test
    public void transactionalSuccessWithTransactionalDeclaration() throws SQLException {
        final Customer generatedCustomer = CustomerGeneratorUtil.generateRandomCustomer();
        customerService.saveCustomerWithTransactionBoundary(generatedCustomer, false);
        List<CustomerAudit> customerAudits = customerService.findAuditsByCustomerEmail(generatedCustomer.getEmail());
        final Customer customer = customerService.findCustomerByEmail(generatedCustomer.getEmail());
        //Expecting the customer and audit to be saved, since everything succeeded
        if (customerAudits.isEmpty() || customer == null) fail("customer or audit was not saved");
        System.out.println(customerAudits);
        System.out.println(customer);
    }

    /*The difference becomes apparent when failure occurs after some DB statements*/
    @Test
    public void transactionalFailureWithoutTransactionalDeclaration() throws SQLException {
        final Customer generatedCustomer = CustomerGeneratorUtil.generateRandomCustomer();
        customerService.saveCustomerWithoutTransactionBoundary(generatedCustomer, true);
        List<CustomerAudit> customerAudits = customerService.findAuditsByCustomerEmail(generatedCustomer.getEmail());
        final Customer customer = customerService.findCustomerByEmail(generatedCustomer.getEmail());
        // Expecting customer to be saved, but not audit. This is problematic and illustrates the value of transactions
        if (!customerAudits.isEmpty() || customer == null) fail("customer was not saved or audit was saved");
        System.out.println(customerAudits);
        System.out.println(customer);
    }

    @Test
    public void transactionalFailureWithTransactionalDeclaration() throws SQLException {
        final Customer generatedCustomer = CustomerGeneratorUtil.generateRandomCustomer();
        customerService.saveCustomerWithTransactionBoundary(generatedCustomer, true);
        List<CustomerAudit> customerAudits = customerService.findAuditsByCustomerEmail(generatedCustomer.getEmail());
        final Customer customer = customerService.findCustomerByEmail(generatedCustomer.getEmail());
        // Expecting nothing to be saved. The transaction should be rolled back. This is what we want. All or nothing
        if (!customerAudits.isEmpty() || customer != null) fail("customer or audit was saved");
        System.out.println(customerAudits);
        System.out.println(customer);
    }

    @Test
    public void failureWithoutRollbackWhatHappens() {
        try {
            customerService.updateCustomerName("ridwaanomar7@gmail.com", "NEWNAME!", true);
        } catch (Exception e) {
            // Squash for demo purposes
        }
    }

}