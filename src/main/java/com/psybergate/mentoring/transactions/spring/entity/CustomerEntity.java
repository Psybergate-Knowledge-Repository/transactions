package com.psybergate.mentoring.transactions.spring.entity;

import com.psybergate.mentoring.transactions.spring.dto.Customer;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name = "customer",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
@NoArgsConstructor
@ToString
public class CustomerEntity extends BaseEntity{

    private String name;

    private String surname;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    public CustomerEntity(final Customer customer) {
         this.name = customer.getName();
         this.surname = customer.getSurname();
         this.email = customer.getEmail();
         this.phoneNumber = customer.getPhoneNumber();
    }
}
