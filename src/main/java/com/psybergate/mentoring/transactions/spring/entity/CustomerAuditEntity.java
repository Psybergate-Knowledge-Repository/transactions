package com.psybergate.mentoring.transactions.spring.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_audit")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CustomerAuditEntity extends BaseEntity{

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "customer_email")
    private String customerEmail;

    private String customer;
}
