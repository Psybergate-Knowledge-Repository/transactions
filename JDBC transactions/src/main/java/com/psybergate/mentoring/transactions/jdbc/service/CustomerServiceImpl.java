package com.psybergate.mentoring.transactions.jdbc.service;

import com.psybergate.mentoring.transactions.jdbc.dto.Customer;
import com.psybergate.mentoring.transactions.jdbc.dto.CustomerAudit;
import com.psybergate.mentoring.transactions.jdbc.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    public static final String CUSTOMER_INSERT_TEMPLATE = "insert into customer values (nextval('customer_id_seq'), '%s', '%s', '%s', '%s', '%s', '%s')";

    public static final String AUDIT_INSERT_TEMPLATE = "insert into customer_audit values (nextval('customer_audit_id_seq'), '%s', '%s', '%s', '%s', '%s', '%s')";

    private static final String RETRIEVE_AUDIT_TEMPLATE = "select * from customer_audit where email = %s";

    private static final String RETRIEVE_CUSTOMER_TEMPLATE = "select * from customer where email = %s";

    private static String createCustomerInsertStatement(final Customer customer) {
        return String.format(CUSTOMER_INSERT_TEMPLATE,
                customer.getName(),
                customer.getSurname(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCreatedDate(),
                customer.getLastModified());
    }

    private static String createAuditInsertStatement(final Customer customer) {
        return String.format(AUDIT_INSERT_TEMPLATE,
                customer.getEmail(),
                customer.getName(),
                LocalDateTime.now(),
                customer,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    private static String createRetrieveAuditStatement(final String email) {
        return String.format(RETRIEVE_AUDIT_TEMPLATE,
                email);
    }

    private static String createRetrieveCustomerStatement(final String email) {
        return String.format(RETRIEVE_CUSTOMER_TEMPLATE,
                email);
    }

    private final DataSource dataSource;

    @Override
    public void saveCustomerWithoutTransactionBoundary(final Customer customer,
                                                       final boolean simulateFailure) throws SQLException {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            final String customerInsertStatement = createCustomerInsertStatement(customer);
            final String auditInsertStatement = createAuditInsertStatement(customer);
            statement.executeQuery(customerInsertStatement);
            if (simulateFailure) throw new RuntimeException("Simulated failure");
            statement.executeQuery(auditInsertStatement);
        }
    }

    @Override
    @Transactional
    public void saveCustomerWithTransactionBoundary(final Customer customer,
                                                    final boolean simulateFailure) throws SQLException {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            final String customerInsertStatement = createCustomerInsertStatement(customer);
            final String auditInsertStatement = createAuditInsertStatement(customer);
            statement.executeQuery(customerInsertStatement);
            if (simulateFailure) throw new RuntimeException("Simulated failure");
            statement.executeQuery(auditInsertStatement);
            connection.commit();
        }
    }

    @Override
    public List<CustomerAudit> findAuditsByCustomerEmail(final String email) throws SQLException {
        final List<CustomerAudit> audits;
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            final String retrieveAuditStatement = createRetrieveAuditStatement(email);
            ResultSet results = statement.executeQuery(retrieveAuditStatement);
            audits = extractAudits(results);
        }
        return audits;
    }

    @Override
    public Customer findCustomerByEmail(final String email) throws SQLException {
        final Customer customer;
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            final String retrieveAuditStatement = createRetrieveCustomerStatement(email);
            ResultSet results = statement.executeQuery(retrieveAuditStatement);
            customer = extractCustomer(results);
        }
        return customer;
    }

    private Customer extractCustomer(final ResultSet results) throws SQLException {
        final LocalDateTime createdDate = DateUtil.toLocalDateTime(results.getDate("created_date"));
        final LocalDateTime lastModified = DateUtil.toLocalDateTime(results.getDate("last_modified"));
        final String name = results.getString("name");
        final String surname = results.getString("surname");
        final String email = results.getString("email");
        final String phoneNumber = results.getString("phone_number");
        return new Customer(createdDate,
                lastModified,
                name,
                surname,
                email,
                phoneNumber);
    }

    private List<CustomerAudit> extractAudits(final ResultSet results) throws SQLException {
        final List<CustomerAudit> audits = new ArrayList<>();
        while (results.next()){
            final String modifiedBy = results.getString("modified_by");
            final LocalDateTime modifiedDate = DateUtil.toLocalDateTime(results.getDate("modified_date"));
            final String customerEmail = results.getString("customer_email");
            final String customer = results.getString("customer");
            final LocalDateTime createdDate = DateUtil.toLocalDateTime(results.getDate("created_date"));
            final LocalDateTime lastModified = DateUtil.toLocalDateTime(results.getDate("last_modified"));

            final CustomerAudit audit = new CustomerAudit(modifiedBy,
                    modifiedDate,
                    customerEmail,
                    customer,
                    createdDate,
                    lastModified);
            audits.add(audit);
        }
        return audits;
    }
}