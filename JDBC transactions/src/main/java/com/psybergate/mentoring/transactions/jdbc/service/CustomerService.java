package com.psybergate.mentoring.transactions.jdbc.service;

import com.psybergate.mentoring.transactions.jdbc.dto.Customer;
import com.psybergate.mentoring.transactions.jdbc.dto.CustomerAudit;

import java.sql.SQLException;
import java.util.List;

public interface CustomerService {

    void saveCustomerWithoutTransactionBoundary(Customer customer, final boolean simulateFailure) throws SQLException;

    void saveCustomerWithTransactionBoundary(Customer customer, final boolean simulateFailure) throws SQLException;

    List<CustomerAudit> findAuditsByCustomerEmail(String email) throws SQLException;

    Customer findCustomerByEmail(String email) throws SQLException;
}
