package com.psybergate.mentoring.transactions.jdbc.service;

import com.psybergate.mentoring.transactions.jdbc.dto.Customer;
import com.psybergate.mentoring.transactions.jdbc.dto.CustomerAudit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    public static final String CUSTOMER_INSERT_TEMPLATE = "insert into customer values (nextval('customer_id_seq'), '%s', '%s', '%s', '%s', '%s', '%s')";

    public static final String MYSQL_CUSTOMER_INSERT_TEMPLATE = "insert into customer(name, surname, email, phone_number, created_date, last_modified) values ('%s', '%s', '%s', '%s', '%s', '%s')";

    public static final String AUDIT_INSERT_TEMPLATE = "insert into customer_audit values (nextval('customer_audit_id_seq'), '%s', '%s', '%s', '%s', '%s', '%s')";

    public static final String MYSQL_AUDIT_INSERT_TEMPLATE = "insert into customer_audit(customer_email, modified_by, modified_date, customer, created_date, last_modified) values ('%s', '%s', '%s', '%s', '%s', '%s')";

    public static final String PROBLEMATIC_AUDIT_INSERT_TEMPLATE = "insert customer_audit values (nextval('customer_audit_id_seq'), '%s', '%s', '%s', '%s', '%s', '%s')";

    public static final String PROBLEMATIC_MYSQL_AUDIT_INSERT_TEMPLATE = "insert customer_audit(customer_email, modfied_by, modified_date, customer, created_date, last_modified) values ('%s', '%s', '%s', '%s', '%s', '%s')";
    private static final String RETRIEVE_AUDIT_TEMPLATE = "select * from customer_audit where customer_email = '%s'";

    private static final String RETRIEVE_CUSTOMER_TEMPLATE = "select * from customer where email = '%s'";

    private static final String MYSQL_CUSTOMER_UPDATE_NAME_TEMPLATE = "update customer set name = '%s' where email = '%s'";

    private static String createCustomerInsertStatement(final Customer customer) {
        return String.format(MYSQL_CUSTOMER_INSERT_TEMPLATE,
                customer.getName(),
                customer.getSurname(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCreatedDate(),
                customer.getLastModified());
    }

    private static String createCustomerUpdateNameStatement(Customer customer) {
        return String.format(MYSQL_CUSTOMER_UPDATE_NAME_TEMPLATE,
                customer.getName(),
                customer.getEmail());
    }

    private static String createAuditInsertStatement(final Customer customer, final boolean simulateFailure) {
        final String template = simulateFailure ? PROBLEMATIC_MYSQL_AUDIT_INSERT_TEMPLATE : MYSQL_AUDIT_INSERT_TEMPLATE;
        return String.format(template,
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

    public CustomerServiceImpl(@Qualifier("mySqlDataSource") final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveCustomerWithoutTransactionBoundary(final Customer customer,
                                                       final boolean simulateFailure) throws SQLException {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            final String customerInsertStatement = createCustomerInsertStatement(customer);
            final String auditInsertStatement = createAuditInsertStatement(customer, simulateFailure);
            statement.executeUpdate(customerInsertStatement);
            statement.executeUpdate(auditInsertStatement);
        }
    }

    @Override
    public void saveCustomerWithTransactionBoundary(final Customer customer,
                                                    final boolean simulateFailure) {
        try (final Connection connection = dataSource.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                connection.setAutoCommit(false);
                final String customerInsertStatement = createCustomerInsertStatement(customer);
                final String auditInsertStatement = createAuditInsertStatement(customer, simulateFailure);
                statement.executeUpdate(customerInsertStatement);
                statement.executeUpdate(auditInsertStatement);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        Customer customer = null;
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            final String retrieveCustomerStatement = createRetrieveCustomerStatement(email);
            ResultSet results = statement.executeQuery(retrieveCustomerStatement);
            if (results.next()) {
                customer = extractCustomer(results);
            }
        }
        return customer;
    }

    @Override
    public void updateCustomerName(final String customerEmail,
                                   final String name,
                                   final boolean simulateFailure) {
        try {
            final Connection connection = dataSource.getConnection();
            final Statement statement = connection.createStatement();
            connection.setAutoCommit(false);
            final Customer customer = findCustomerByEmail(customerEmail);
            customer.setName(name);
            final String customerInsertStatement = createCustomerUpdateNameStatement(customer);
            final String auditInsertStatement = createAuditInsertStatement(customer, simulateFailure);
            statement.executeUpdate(customerInsertStatement);
            statement.executeUpdate(auditInsertStatement);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Customer extractCustomer(final ResultSet results) throws SQLException {
        final LocalDateTime createdDate = results.getTimestamp("created_date").toLocalDateTime();
        final LocalDateTime lastModified = results.getTimestamp("last_modified").toLocalDateTime();
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
        while (results.next()) {
            final String modifiedBy = results.getString("modified_by");
            final LocalDateTime modifiedDate = results.getTimestamp("modified_date").toLocalDateTime();
            final String customerEmail = results.getString("customer_email");
            final String customer = results.getString("customer");
            final LocalDateTime createdDate = results.getTimestamp("created_date").toLocalDateTime();
            final LocalDateTime lastModified = results.getTimestamp("last_modified").toLocalDateTime();

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